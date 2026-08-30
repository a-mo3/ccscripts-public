package org.dreambot.analytics;

import com.google.gson.Gson;
import okhttp3.*;
import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
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

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Everything is accessed through executors to maintain thread safety when events can be triggered by listeners
 */
public class Reporter implements GameTickListener, LoginListener {
    // do all the reading and writing of analytics changes to this so we dont blow up with concurrency issues
    // most of the data is densities, so it doesnt even matter about the execution order
    static ExecutorService analyticsExecutor = Executors.newSingleThreadExecutor();
    // single thread that listens to mouse pos and records it every 50ms
    static ExecutorService mouseWatchExecutor = Executors.newSingleThreadExecutor();
    static final Timer heartbeatTimer = new Timer(15 * 1000 * 60);
    final int heartbeatLength = 15;
    static int[][] mousePosDensities = new int[100][100];
    static int[][] mouseClickDensities = new int[100][100];
    // when a click happens, the time until the heartBeat timer completes
    static List<Integer> relativeClickTimes = new LinkedList<>();

    Map<Integer, Integer> regionDensities = new HashMap<>();
    // how many times a script have activated certain script paths
    public static Map<String, Integer> fractalDensities = new HashMap<>();
    int msSpentBreaking;

    OkHttpClient okHttpClient = new OkHttpClient();

    private Reporter(boolean saveLocally) {
        // get tick event and login event
        Client.getInstance().addEventListener(this);
        // set the mouse algo that tracks clicks
        Mouse.setMouseAlgorithm(new AnalyticMouseAlgo());
        mouseWatchExecutor.submit(() -> {
            RandomManager r = Client.getInstance().getRandomManager();
            BreakSolver s = r == null ? null : r.getBreakSolver();
            while (ScriptManager.getScriptManager().isRunning() || ScriptManager.getScriptManager().isPaused()) {
                // the main check for sending analytics
                this.isValid(saveLocally);

                if (s != null && s.isBreakRunning()) {
                    msSpentBreaking += 50;
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
        int[] percentiles = getPercentile(Client.getViewportWidth(), Client.getViewportHeight(), p.x, p.y);
        analyticsExecutor.execute(() -> {
            // can be max 900,000 ms
            relativeClickTimes.add((int) heartbeatTimer.remaining());
            mouseClickDensities[percentiles[0]][percentiles[1]]++;
        });
    }

    public static void reportMousePos(Point p) {
        int[] percentiles = getPercentile(Client.getViewportWidth(), Client.getViewportHeight(), p.x, p.y);
        analyticsExecutor.execute(() -> {
            mousePosDensities[percentiles[0]][percentiles[1]]++;
        });
    }

    public static int[] getPercentile(int screenWidth, int screenHeight, double x, double y) {
        if (screenWidth <= 0 || screenHeight <= 0) {
            throw new IllegalArgumentException("Screen width and height must be positive.");
        }

        int widthPercent = (int) Math.round((x / screenWidth) * 100);
        int heightPercent = (int) Math.round((y / screenHeight) * 100);

        return new int[]{widthPercent, heightPercent};
    }


    public boolean isValid(boolean saveLocally) {
        if (heartbeatTimer.finished()) {
            heartbeatTimer.reset();
            // build heartbeat model
            Logger.log(Color.PINK, "Sending analytics");
            // we are accessing so do that from the thread
            analyticsExecutor.execute(() -> {
                MouseData mousePos = toMouseData(mousePosDensities);
                MouseData mouseClick = toMouseData(mouseClickDensities);
                String email = AccountManager.getAccountUsername();
                // if username isnt an email, dont track it
                // email domain is tracked to track bans on certain abused providers, see heartbeat.proto
                // hash of it so no one can do domain takeover or anything
                if (!email.contains("@")) {
                    email = "Penis.";
                } else {
                    email = hashStringSHA256(email.substring(email.indexOf("@") + 1));
                }

                HeartBeat heartBeat = HeartBeat.newBuilder()
                        .setMousePos(mousePos)
                        .setMouseClicks(mouseClick)
                        .putAllExecuteCounts(fractalDensities)
                        .putAllRegionDensities(regionDensities)
                        .setIsCovert(Instance.isCovertEnabled())
                        .addAllRelativeClickTime(relativeClickTimes)

                        .setTime(System.currentTimeMillis())
                        .setAccountHash(AccountManager.getAccountHash())
                        .setDreambotUser(Client.getForumUser().getUsername())
                        .setDreambotToken(Client.getForumUser().getAuthenticationCode())

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

                // reset all the state
                relativeClickTimes = new LinkedList<>();
                msSpentBreaking = 0;
                mousePosDensities = new int[100][100];
                mouseClickDensities = new int[100][100];
                regionDensities = new HashMap<>();
                fractalDensities = new HashMap<>();


                // save locally first
                // enabled setting checked at the start of this method
                if (saveLocally) {
                    try (FileOutputStream fos = new FileOutputStream(System.getProperty("scripts.path") + "/analytics/" +
                            AccountManager.getAccountHash() + "-" + System.currentTimeMillis() + ".bin")) {
                        heartBeat.writeTo(fos);
                    } catch (IOException e) {
                        Logger.info("Failed to write heartbeat " + e.getMessage());
                    }
                }

                //  send to server
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/x-protobuf"),
                        heartBeat.toByteArray()
                );

                Request request = new Request.Builder()
                        .post(body)
                        .url("/heartbeat/v1")
                        .build();

                try (Response response = okHttpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        log("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    log("Analytics exception " + e.getMessage());
                }
            });
        }
        return false; // never go onto onloop here and therefore never stop the script tree
    }

    private int getWealth() {
        int bankVal = Bank.getBankHistoryCache()
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(Item::getLivePrice).sum();

        int invVal = Inventory.all()
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(Item::getLivePrice).sum();

        int equipVal = Equipment.all()
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(Item::getLivePrice).sum();
        return equipVal + invVal + bankVal;
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
                throw new IllegalArgumentException("Jagged array not allowed");
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

    private static void log(String s) {
        Logger.log(Color.PINK, "[Analytics] - " + s);
    }
}
