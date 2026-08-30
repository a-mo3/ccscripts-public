package org.dreambot.analytics.impl;

import com.google.gson.Gson;
import com.sun.jna.platform.win32.Advapi32Util;
import okhttp3.*;
import org.dreambot.analytics.HeartBeat;
import org.dreambot.analytics.MouseData;
import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.ForumUser;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.input.mouse.MouseSettings;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomManager;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.GameTickListener;
import org.dreambot.api.script.listener.LoginListener;
import org.dreambot.api.utilities.AccountManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.core.Instance;
import org.dreambot.fractals.IronFractal;
import org.dreambot.gui.settings.SettingFractal;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Sends heartbeats every X minutes
 * maintains mouse and click positions
 * region densities (map of region, ticks in region)
 */
public class AnalyticsReporter extends SettingFractal<AnalyticsSettings> implements GameTickListener, LoginListener {
    // 21 / 05 2026 we need a new executor because relying on onloop doesnt work for breaks and logout script breaks
    // i am afraid of this thread dying
    // this does the actual checking
    static ExecutorService workExecutor = Executors.newSingleThreadExecutor();

    static AtomicReference<Thread> analT = new AtomicReference<>();
    // do all the reading and writing of analytics changes to this so we dont blow up with concurrency issues
    // most of the data is densities, so it doesnt even matter about the execution order
    static ExecutorService analyticsExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "Analytics thread");
        analT.set(t);
        return t;
    });
    // single thread that listens to mouse pos and records it every 50ms
    static ExecutorService mouseWatchExecutor = Executors.newSingleThreadExecutor();

    static final Timer heartbeatTimer = new Timer(15 * 1000 * 60);
    final int heartbeatLength = 15;
    static int[][] mousePosDensities = new int[100][100];
    static int[][] mouseClickDensities = new int[100][100];
    // when a click happens, the time until the heartBeat timer completes
    // this implicitly tracks mouse path
    static List<Integer> relativeClickTimes = new LinkedList<>();

    Map<Integer, Integer> regionDensities = new HashMap<>();
    // how many times a script have activated certain script paths
    public static Map<String, Integer> fractalDensities = new HashMap<>();
    int msSpentBreaking;

    OkHttpClient okHttpClient = new OkHttpClient();

    public AnalyticsReporter() {
        super(() -> false);
        setSimpleName("Analytics");

//        try {
//            IronFractal.mouseFeatureFlag = AnalyticsReporter.fetchFeatureFlag();
//            Logger.info("Fetched mouse feature " + IronFractal.mouseFeatureFlag);
//        } catch (IOException ex) {
//            Logger.error("failed to fetch mouse feature");
//        }

        log("Checking analytics " + getSettings().enabled);
        if (!getSettings().enabled) return;
        workExecutor.submit(() -> {
            try {
                while (ScriptManager.getScriptManager().isRunning() || ScriptManager.getScriptManager().isPaused()) {
                    work();
                    Thread.sleep(30_000);
                }
            } catch (Exception e) {
                log(e.toString());
                e.printStackTrace();
            }
        });

        log("Registering analytics");
        Client.getInstance().addEventListener(this);
        // set the mouse algo that tracks clicks
        Mouse.setMouseAlgorithm(new AnalyticMouseAlgo());
        mouseWatchExecutor.execute(() -> {
            RandomManager r = Client.getInstance().getRandomManager();
            BreakSolver s = r == null ? null : r.getBreakSolver();
            while (ScriptManager.getScriptManager().isRunning() || ScriptManager.getScriptManager().isPaused()) {
                if (s != null && s.isBreakRunning()) {
                    analyticsExecutor.execute(() -> msSpentBreaking += 50);
                } else {
                    reportMousePos(Mouse.getPosition());
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void reportFractalExecute(String path) {
        analyticsExecutor.execute(() -> fractalDensities.put(path, fractalDensities.getOrDefault(path, 0) + 1));
    }

    public static void reportClick(Point p) {
        final int[] percentiles = getPercentile(Client.getViewportWidth(), Client.getViewportHeight(), p.x, p.y);
        final int remaining = Math.toIntExact(heartbeatTimer.remaining());
        analyticsExecutor.execute(() -> {
//            // can be max 900,000 ms
//            relativeClickTimes.add(remaining);
            mouseClickDensities[percentiles[0]][percentiles[1]]++;
        });
    }

    public static void reportMousePos(Point p) {
        int[] percentiles = getPercentile(Client.getViewportWidth(), Client.getViewportHeight(), p.x, p.y);
        analyticsExecutor.execute(() -> mousePosDensities[percentiles[0]][percentiles[1]]++);
    }

    public static int[] getPercentile(int screenWidth, int screenHeight, double x, double y) {
        if (screenWidth <= 0 || screenHeight <= 0) {
            Logger.info("negative percentiles");
            return new int[]{-1, -1};
        }

        int widthPercent = (int) Math.round((x / screenWidth) * 100);
        int heightPercent = (int) Math.round((y / screenHeight) * 100);

        return new int[]{widthPercent, heightPercent};
    }


    @Override
    public boolean isValid() {
        if (!getSettings().enabled) return false;
        return false; // never go onto onloop here and therefore never stop the script tree
    }

    private int getWealth() {
        int bankVal = Bank.getBankHistoryCache()
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(i -> i.getLivePrice() * i.getAmount())
                .sum();

        int invVal = Inventory.all()
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(i -> i.getLivePrice() * i.getAmount())
                .sum();

        int equipVal = Equipment.all()
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(i -> i.getLivePrice() * i.getAmount())
                .sum();
        return equipVal + invVal + bankVal;
    }

    @Override
    public String settingName() {
        return "analytics.json";
    }

    @Override
    public AnalyticsSettings defaultSettings() {
        return new AnalyticsSettings(); // off by default
    }

    @Override
    public void onServerTick() {
        // every tick track region we're in.
        if (!Client.isLoggedIn()) return;
        int regionId = Players.getLocal().getRegionId();
        int count = regionDensities.getOrDefault(regionId, 0) + 1;
        analyticsExecutor.execute(() -> {
            regionDensities.put(regionId, count);
        });
    }


    public static MouseData toMouseData(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        MouseData.Builder builder = MouseData.newBuilder()
                .setRows(rows)
                .setCols(cols);

        // Flatten in row-major order
        for (int r = 0; r < rows; r++) {
            if (grid[r].length != cols) {
                Logger.info("Jagged array when marshalling mouse data");
                return null;
            }
            for (int c = 0; c < cols; c++) {
                builder.addMouseDensity(grid[r][c]);
            }
        }

        return builder.build();
    }

    public static String hashStringSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes());

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "Hash error " + e.getMessage();
        }
    }

    public static void shutDown() {
        analyticsExecutor.shutdown();
        mouseWatchExecutor.shutdown();
    }

    // removed 3/07, mouse flags set to a b c, test is moving to mouse algos
//    public static IdleMouseFeatureFlag fetchFeatureFlag() throws IOException {
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url("/flags/mouse/" + AccountManager.getAccountHash())
//                .get()
//                .addHeader("User-Agent", "insomnia/11.1.0")
//                .build();
//
//        Response response = client.newCall(request).execute();
//        return new Gson().fromJson(response.body().string(), IdleMouseFeatureFlag.class);
//    }

    @Override
    public void onLoginResponse(int response) {
        // i just think this is the ban response
        log("Login resopnse " + response);
        if (response == 4) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("/ban/" + AccountManager.getAccountHash())
                    .post(RequestBody.create(null, new byte[0]))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "insomnia/11.1.0")
                    .build();

            log("Reporting ban");
            try (Response c = client.newCall(request).execute()) {
                log("Success");
            } catch (IOException e) {
                log("Failed to report ban " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    void work() {

        if (heartbeatTimer.finished()) {
            heartbeatTimer.reset();
            // build heartbeat model
            Logger.log(Color.PINK, "Sending analytics " + analyticsExecutor.isShutdown() + " " + analyticsExecutor.isTerminated());
            Thread t = analT.get();
            Logger.log(Color.PINK, "A Thread " + t.getState() + " " + t.isAlive());
            Arrays.stream(t.getStackTrace()).forEach(Logger::log);
            // we are accessing so do that from the thread
            Future f = analyticsExecutor.submit(() -> {
                log("Marshalling heartbeat");
                MouseData mousePos = toMouseData(mousePosDensities);
                MouseData mouseClick = toMouseData(mouseClickDensities);
                String email = AccountManager.getAccountUsername();
                // if username isnt an email, dont track it
                // email domain is tracked to track bans on certain abused providers, see heartbeat.proto
                // hash of it so no one can do domain takeover or anything
                log("Encode email");
                if (email == null || !email.contains("@") || email.endsWith("@")) {
                    email = "Penis.";
                } else {
                    int index = email.indexOf("@") + 1;
                    if (index < email.length()) email = hashStringSHA256(email.substring(index));
                }

                log("Grab forum user");
                ForumUser forumUser = Client.getForumUser();

                log("Constructing heartbeat");
                HeartBeat heartBeat = HeartBeat.newBuilder()
                        .setMousePos(mousePos)
                        .setMouseClicks(mouseClick)
                        .putAllExecuteCounts(fractalDensities)
                        .putAllRegionDensities(regionDensities)
                        .setIsCovert(Instance.isCovertEnabled())
                        .addAllRelativeClickTime(relativeClickTimes)

                        .setTime(System.currentTimeMillis())
                        .setAccountHash(AccountManager.getAccountHash())
                        .setDreambotUser(forumUser == null ? "NullUser" : forumUser.getUsername())
                        .setDreambotToken(forumUser == null ? "NullToken" : forumUser.getAuthenticationCode())

                        .setScriptName(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                        .setHeartBeatLength(heartbeatLength)
                        .setNoClickWalk(Walking.isNoClickWalkEnabled())
                        .setMenuManip(Menu.isMenuManipulationActive())
                        .setTimeBreaking(msSpentBreaking)
                        .setIsUsingBeta(Instance.isBetaActive())
                        .setIsMember(Client.isMembers())
                        .setIsCovert(Instance.isCovertEnabled())
                        .setMouseSpeed(MouseSettings.getSpeed())
                        .setEmailDomain(email)
                        .setIronmanState(PlayerSettings.getBitValue(1777))

                        .setWealth(getWealth())
                        .setAttack(Skill.ATTACK.getExperience())
                        .setStrength(Skill.STRENGTH.getExperience())
                        .setDefence(Skill.DEFENCE.getExperience())
                        .setRanged(Skill.RANGED.getExperience())
                        .setPrayer(Skill.PRAYER.getExperience())
                        .setMagic(Skill.MAGIC.getExperience())
                        .setRunecraft(Skill.RUNECRAFTING.getExperience())
                        .setConstruction(Skill.CONSTRUCTION.getExperience())
                        .setHitpoints(Skill.HITPOINTS.getExperience())
                        .setAgility(Skill.AGILITY.getExperience())
                        .setHerblore(Skill.HERBLORE.getExperience())
                        .setThieving(Skill.THIEVING.getExperience())
                        .setCrafting(Skill.CRAFTING.getExperience())
                        .setFletching(Skill.FLETCHING.getExperience())
                        .setSlayer(Skill.SLAYER.getExperience())
                        .setHunter(Skill.HUNTER.getExperience())
                        .setMining(Skill.MINING.getExperience())
                        .setSmithing(Skill.SMITHING.getExperience())
                        .setFishing(Skill.FISHING.getExperience())
                        .setCooking(Skill.COOKING.getExperience())
                        .setFiremaking(Skill.FIREMAKING.getExperience())
                        .setWoodcutting(Skill.WOODCUTTING.getExperience())
                        .setFarming(Skill.FARMING.getExperience())
                        .setSailing(Skill.SAILING.getExperience())
                        .build();
                log("Resetting state");

                // reset all the state
                relativeClickTimes = new LinkedList<>();
                msSpentBreaking = 0;
                mousePosDensities = new int[100][100];
                mouseClickDensities = new int[100][100];
                regionDensities = new HashMap<>();
                fractalDensities = new HashMap<>();


                // save locally first
                // enabled setting checked at the start of this method
                try (FileOutputStream fos = new FileOutputStream(System.getProperty("scripts.path") + "/ccanalytics/" +
                        AccountManager.getAccountHash() + "-" + System.currentTimeMillis() + ".bin")) {
                    log("Write to fos");
                    heartBeat.writeTo(fos);
                } catch (IOException e) {
                    Logger.info("Failed to write heartbeat " + e.getMessage());
                }

                //  send to server
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/x-protobuf"),
                        heartBeat.toByteArray()
                );

                Request request = new Request.Builder()
                        .post(body)
                        // arbscouter was a project i abandoned and just had the url and server already setup.
                        .url("/heartbeat/v1")
                        .build();

                log("Firing request");
                try (Response response = okHttpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        log("Unexpected code " + response);
                    } else {
                        log("Success code " + response);
                    }
                } catch (IOException e) {
                    log("Analytics exception " + e.getMessage());
                }
            });


            try {
                log("Waiting for analytics to submit");
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                Logger.info("Analytics error " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
