package org.dreambot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsSettings;
import org.dreambot.analytics.models.BanReport;
import org.dreambot.analytics.models.HeartBeat;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.input.mouse.MouseSettings;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.WebNodeType;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.LoginListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.method.mta.alchemy.AlchemyRoomMTA;
import org.dreambot.behaviour.method.mta.enchant.EnchantRoomMTA;
import org.dreambot.behaviour.method.mta.graveyard.GraveyardRoomMTA;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.AutoProggy;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.AnyValidChildrenFractal;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scripts.*;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;
import org.dreambot.settings.ui.PaintButton;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * doing this to reduce the amount of commits hashtag has to read recompile and approve
 * only for scripts that have similar codebases
 */
@ScriptManifest(category = Category.MONEYMAKING, name = "PVMScripts", author = "cc", version = 0.1)
public class PvmMain extends AbstractScript implements LoginListener {
    PseudoScript activeScript = null;
    Map<String, Supplier<PseudoScript>> scriptMap = new HashMap<>();
    public static String scriptName;
    // make params globally accessible so settings can be checked from any script
    public static String[] qsParams = new String[0];

    public static Fractal universalTasks = new AnyValidChildrenFractal();

    @Override
    public void onStart(String... params) {
        // allow users to swap one ID for another by using reflection to change itemids,
        // i basically always use the item from here for everything thing, so logic should persist
        Pattern pattern = Pattern.compile("(\\d+):(\\d+)");
        for (String input : params) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                int first = Integer.parseInt(matcher.group(1));
                int second = Integer.parseInt(matcher.group(2));
                Logger.info("From: " + first + " To: " + second);

                for (Field field : ItemID.class.getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        int value = field.getInt(null);
                        if (value == first) {
                            Logger.info("Successfully swapped " + first + " " + second);
                            field.setInt(null, second);
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        }

        qsParams = params;
        analyticsToggle.setLabel(AnalyticsSettings.isAnalyticsOn() ? "Analytics On" : "Analytics Off");
        analyticsToggle.setBorderColor(AnalyticsSettings.isAnalyticsOn() ? Color.GREEN : Color.RED);
        analyticsToggle.setOnClick(m -> {
            analyticsToggle.setBorderColor(AnalyticsSettings.toggleAnalytics() ? Color.GREEN : Color.RED);
            analyticsToggle.setLabel(AnalyticsSettings.isAnalyticsOn() ? "Analytics On" : "Analytics Off");
        });

        if (params == null || params.length == 0) return;
        selectScript(params[0]);
        activeScript.onStart(params);
    }

    @Override
    public void onStart() {
        analyticsToggle.setLabel(AnalyticsSettings.isAnalyticsOn() ? "Analytics On" : "Analytics Off");
        analyticsToggle.setBorderColor(AnalyticsSettings.isAnalyticsOn() ? Color.GREEN : Color.RED);
        analyticsToggle.setOnClick(m -> {
            analyticsToggle.setBorderColor(AnalyticsSettings.toggleAnalytics() ? Color.GREEN : Color.RED);
            analyticsToggle.setLabel(AnalyticsSettings.isAnalyticsOn() ? "Analytics On" : "Analytics Off");
        });
        selectScript(null);
        activeScript.onStart();
    }

    @Override
    public void onExit() {
        if (activeScript.forceMenuManip) {
            MouseSettings.setSpeed(previousMouseSpeed);
            Menu.toggleMenuManipulation(wasMenuManipOn);
            Walking.toggleNoClickWalk(wasNoClickWalk);
        }
        activeScript.onExit();
    }

    private void selectScript(String param) {
        scriptMap.put("gargoyle", Gargoyle::new);
        scriptMap.put("wyrm", Wyrms::new);
        scriptMap.put("sulphur", SulphurNagua::new);
        scriptMap.put("kurask", KuraskScript::new);
        scriptMap.put("avian", AviansiesScript::new);
        scriptMap.put("spectre", SpectresScript::new);
        scriptMap.put("gray", GrayChinchompasScript::new);
        scriptMap.put("blackchin", BlackChinchompasScript::new);
        scriptMap.put("redchin", RedChinchompasScript::new);
        scriptMap.put("green", GreenDragonScript::new);
        scriptMap.put("revena", RevenantsScript::new);
        scriptMap.put("turoth", TurothScript::new);
        scriptMap.put("killerwatt", KillerwattScript::new);
        scriptMap.put("arceuustele", ArceuusTeleTabScript::new);
        scriptMap.put("pohtele", PohTeleTabsScript::new);
        scriptMap.put("zombiepirate", ZombiePiratesScript::new);
        scriptMap.put("blastfurnace", BlastFurnaceScript::new);
        scriptMap.put("bluedragon", BlueDragonScript::new);
        scriptMap.put("sun", SunlightAntelopeScript::new);
        scriptMap.put("rogueschest", RoguesChestScript::new);
        scriptMap.put("magetraining", MTAScript::new);
        scriptMap.put("nightmarezone", NMZScript::new);
        scriptMap.put("masterfarmer", MasterFarmersScript::new);
        scriptMap.put("knights", ArdyKnightsScript::new);
        scriptMap.put("paladin", ArdyKnightsScript::new);
        scriptMap.put("puropuro", PuroPuroScript::new);
        scriptMap.put("reddrag", RedDragonScript::new);
        scriptMap.put("fletch", FletchingScript::new);
        scriptMap.put("lavadragon", LavaDragonScript::new);
        scriptMap.put("barrows", BarrowsScript::new);
        scriptMap.put("undead", UndeadDruidsScript::new);
        scriptMap.put("gemstone", GemStoneCrabScript::new);
        scriptMap.put("mixology", MixologyScript::new);
        scriptMap.put("tanner", TannerScript::new);
        scriptMap.put("fanatic", ChaosFanaticScript::new);
        scriptMap.put("minnow", MinnowsScript::new);
        scriptMap.put("motherlode", MotherlodeScript::new);
        scriptMap.put("tutorial", TutorialScript::new);
        scriptMap.put("amethyst", AmethystScript::new);
        scriptMap.put("imp", ImpScript::new);

        scriptMap.put("gryphon", GryphonScript::new);
        scriptMap.put("frost", FrostDragonScript::new);
        scriptMap.put("aquanite", AquaniteScript::new);
        scriptMap.put("clay", FtpClay::new);
        scriptMap.put("dump", BankDumpScript::new);
        scriptMap.put("wildernessagil", WildernessAgilityScript::new);
        scriptMap.put("earthorb", EarthOrberScript::new);
        scriptMap.put("airorb", EarthOrberScript::new);
        // fast mouse / menu manip required scripts
        scriptMap.put("vetion", () -> new VetionScript().forceMenuManip(90, 100));
        scriptMap.put("callisto", () -> new CallistoScript().forceMenuManip(90, 100));
        scriptMap.put("venenatis", () -> new VenenatisScript().forceMenuManip(90, 100));

        scriptMap.put("crazy", () -> new CrazyArchScript().forceMenuManip(90, 100));
        scriptMap.put("lizard", () -> new LizardShamenScript().forceMenuManip(90, 100));
        scriptMap.put("corp", () -> new CorpScript().forceMenuManip(90, 100));
        scriptMap.put("huey", () -> new HueycoatlScript().forceMenuManip(90, 100));
        scriptMap.put("moonsof", () -> new MoonsOfPerilScript().forceMenuManip(90, 100));
        scriptMap.put("scurrius", () -> new ScurriusScript().forceMenuManip(90, 100));
        scriptMap.put("artio", () -> new ArtioScript().forceMenuManip(90, 100));
        scriptMap.put("calvarion", () -> new CalvarionScript().forceMenuManip(90, 100));
        scriptMap.put("spindel", () -> new SpindelScript().forceMenuManip(90, 100));
        scriptMap.put("zilyana", () -> new ZilyanaScript().forceMenuManip(90, 100));
        scriptMap.put("phosani", () -> new PhosaniScript().forceMenuManip(90, 100));
        scriptMap.put("kree", () -> new KreearraScript().forceMenuManip(90, 100));
        scriptMap.put("zam", () -> new ZammyScript().forceMenuManip(90, 100));
        scriptMap.put("bandos", () -> new BandosScript().forceMenuManip(90, 100));
        scriptMap.put("gorilla", () -> new DemonicGorillaScript().forceMenuManip(90, 100));
        scriptMap.put("nex", () -> new NexScript().forceMenuManip(90, 100));

        scriptName = ScriptManager.getScriptManager().getCurrentScript().getSDNName().toLowerCase();

        // all the bonding and such all tasks need, along with antiban breaks and anti ban training
        universalTasks.setSimpleName("Pre task");


        scriptMap.forEach((name, supp) -> {
            if (scriptName.contains(name)) {
                activeScript = supp.get();
            }
        });

        if (activeScript == null) {
            Logger.warn("SCRIPT WAS NOT ADDED TO LIST " + scriptName);
        }

        if (ScriptManager.getScriptManager().getCurrentScript().getScriptId() <= 0) {
            // PENIS (so i can ctrl f here)
//            if (activeScript == null) activeScript = new PuroPuroScript();
            log("Setting pvm testing");
            activeScript = new PvmTesting();
        }


        Logger.info(param);
        if (param != null && ScriptManager.getScriptManager().getCurrentScript().getScriptId() < 0) { // if im running locally
            scriptMap.forEach((name, supp) -> {
                if (name.contains(param)) {
                    Logger.info(String.format("Selected %s from %s", name, param));
                    activeScript = supp.get();
                }
            });
            return;
        }

        // make later so settings is saved properly
        universalTasks.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ReactionSettingsFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("Lamp handler"),
                new EmptyDeathsCoffer().setSimpleName("Death coffer")
        );

        // check if script is a forced menu manip script and cache state
        if (activeScript.forceMenuManip) {
            previousMouseSpeed = MouseSettings.getSpeed();
            wasMenuManipOn = Menu.isMenuManipulationActive();
            wasNoClickWalk = Walking.isNoClickWalkEnabled();

            MouseSettings.setSpeed(activeScript.forceMouseSpeed);
            Menu.toggleMenuManipulation(true);
            Walking.toggleNoClickWalk(true);
        }

        // aviansies will try and use this
        BankLocation.blacklist(BankLocation.WOODCUTTING_GUILD_DUNGEON);
        if (!Menu.isMenuManipulationActive()) {
            Alerts.addAlert(6_000, Color.YELLOW, "Menu Manipulation on is recommend, dreambot VIP is required.");
        }
    }

    boolean wasMenuManipOn = false;
    boolean wasNoClickWalk = false; // if it needs menu manip it needs no click walk
    int previousMouseSpeed = 50;

    /*
    After a hop there is a loading gamestate 25, the client will return empty inventory and equipment when this happens
    after a hop lets not loop because the script can trigger events that shouldnt happen, causes double buying and other things
     */
    Timer hopTimer = new Timer(1600);

    // send analytic heatbeats every 15 minutes
    Timer heartbeatTimer = new Timer(15 * 60 * 1_000);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Executor executor = Executors.newSingleThreadExecutor();

    // when pickpocketing you dont want to open coin pouches every time
    public static List<Area> coinAllowedAreas = new ArrayList<>();

    // Minigame paint that stops the scripts execution and records mouse path
    public static boolean isMouseTraining = false;

    ConcurrentLinkedQueue<Point> mouseTrail = new ConcurrentLinkedQueue<>();
    private Point mouseTarget = null;
    int mouseTrainingProgress = 0;
    int mouseTrainingCap = 30;


    // we set to friends only mode, this is for any party mode script (huey, corp in future)
    // this counter exists to make sure it doesnt brick everything if this is wrong
    int attemptToSetFriends = 0;

    Timer analyticsTimer = new Timer(15 * 60 * 1000);
    final String analURL = "/heartbeat";
    boolean failedAuthCheck = false;

    @Override
    public int onLoop() {
        if (analyticsTimer.finished()) {
            analyticsTimer.reset();
            if (AnalyticsSettings.isAnalyticsOn()) {
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, new GsonBuilder().setPrettyPrinting().create().toJson(new HeartBeat()));
                Request request = new Request.Builder()
                        .url(analURL)
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("User-Agent", "ccscript")
                        .build();

                new Thread(() -> {
                    try (Response response = client.newCall(request).execute();) {
                        int code = response.code();
                        Logger.info("Analytics res code " + code);
                        if (code == 400) failedAuthCheck = true;
                    } catch (IOException e) {
                        Logger.error("Analytics req failed");
                    }
                }).start();
            }
        }

        if (attemptToSetFriends < 10 && PlayerSettings.getBitValue(13674) != 1) {
            log("Setting chat to friends");
            WidgetChild friendButton = Widgets.get(x -> x.hasAction("Private: Show friends"));
            if (!Widgets.isOpen() && friendButton != null) {
                friendButton.interact("Private: Show friends");
                attemptToSetFriends++;
            }

        }
        if (isMouseTraining) {
            return 50;
        }
        // sort of wasteful to do this in every script btu lowkey needed
        Player lp = Players.getLocal();
        if (EnchantRoomMTA.ENCHANT_ARENA.contains(lp)
                || AlchemyRoomMTA.ALCHEMY_ROOM.contains(lp)
                || GraveyardRoomMTA.GRAVE_ROOM.contains(lp)
        ) {
            WebFinder.getWebFinder().disableWebNodeType(WebNodeType.TELEPORT_NODE);
        } else {
            WebFinder.getWebFinder().enableWebNodeType(WebNodeType.TELEPORT_NODE);
        }

        if (Client.getGameState() == GameState.HOPPING) hopTimer.reset();
        if ((coinAllowedAreas.isEmpty()
                || coinAllowedAreas.stream().noneMatch(x -> x.contains(Players.getLocal()))) &&
                Inventory.contains("Coin pouch")) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Inventory.interact("Coin pouch"); // stack is open-all single is open
        }


        if (Inventory.contains(8890) && !AlchemyRoomMTA.ALCHEMY_ROOM.contains(Players.getLocal())) {
            log("Drop fake gp");
            if (Widgets.isOpen()) Widgets.closeAll();
            Inventory.dropAll(8890);
        }

        if (Worlds.getCurrent().isLeagueWorld()) {
            Logger.info("Is in leagues world - getting off");
            WorldHopper.hopWorld(Worlds.getRandomWorld(w -> w.isNormal() && w.isMembers() && w.getMinimumLevel() < Skills.getTotalLevel()));
        }

        // dont run the tree while hopping worlds because equipment state will make you do loadouts you shouldnt
        if (!hopTimer.finished() && (Inventory.isEmpty() && Equipment.isEmpty())) {
            Logger.info("Halt loop because of recent hop");
            return ReactionGenerator.getQuick();
        }

        if (Client.getGameStateID() == 10) {
            log("Blocking script execution for login screen state");
            return ReactionGenerator.getNormal();
        }
        if (activeScript == null) {
            scriptName = ScriptManager.getScriptManager().getCurrentScript().getSDNName().toLowerCase();
            log("Failed to run script " + scriptName);
            return -1;
        }

        if (universalTasks.isValid()) return universalTasks.run();
        return activeScript.onLoop();
    }

    Color DISCORD_LIGHT_GRAY_OPAQUE = new Color(66, 69, 73, 157);
    Font TRAINING_FONT = new Font("SansSerif", Font.BOLD, 72);


    Timer mouseCalc = new Timer(50);
    long mouseTimeStamp = System.currentTimeMillis();

    PaintButton analyticsToggle = new PaintButton();

    @Override
    public void onPaint(Graphics graphics) {
//        analyticsToggle.paintButton(graphics);
        // todo add this back when i add breaks
//        AntibanFractal.paintBreaks((Graphics2D) graphics);


        if (isMouseTraining) {
            // math
            if (mouseCalc.finished()) {
                mouseCalc.reset();
                if (mouseTrainingProgress >= mouseTrainingCap) {
                    isMouseTraining = false;
                    Client.getInstance().setMouseInputEnabled(false);
                    CamelMouse.modify();
                    log("Mouse Modified!");
                    mouseTrainingProgress = 0;
                }

                if (mouseTarget == null)
                    mouseTarget = new Point(Calculations.random(50, Client.getViewportWidth() - 50), Calculations.random(50, Client.getViewportHeight() - 50));

                if (mouseTarget.distance(Mouse.getPosition()) < 25) {
                    mouseTarget = new Point(Calculations.random(50, Client.getViewportWidth() - 50), Calculations.random(50, Client.getViewportHeight() - 50));
                    Logger.info("Mouse move time: " + (System.currentTimeMillis() - mouseTimeStamp));
                    mouseTimeStamp = System.currentTimeMillis();
                    mouseTrainingProgress++;
                }

                mouseTrail.add(Mouse.getPosition());

                if (mouseTrail.size() >= 50) mouseTrail.poll();
            }

            graphics.setColor(DISCORD_LIGHT_GRAY_OPAQUE);
            graphics.fillRect(0, 0, Client.getViewportWidth(), Client.getViewportHeight());

            graphics.setFont(TRAINING_FONT);
            graphics.setColor(Color.WHITE.darker());
            FontMetrics metrics = graphics.getFontMetrics();
            String msg = "Mouse over the ball!";
            graphics.drawString(msg, (Client.getViewportWidth() - metrics.stringWidth(msg)) / 2, Client.getViewportHeight() - 40);

            graphics.fillRect(50, 20,
                    (int) ((Client.getViewportWidth() - 100) * ((double) mouseTrainingProgress / mouseTrainingCap)), 40);


            graphics.setColor(Color.GREEN.darker());
            if (mouseTarget != null) graphics.fillOval(mouseTarget.x, mouseTarget.y, 25, 25);
            Point lastPoint = null;
            for (Point mousePoint : mouseTrail) {
                if (lastPoint == null) {
                    lastPoint = mousePoint;
                    continue;
                }
                graphics.drawLine(lastPoint.x, lastPoint.y, mousePoint.x, mousePoint.y);
                lastPoint = mousePoint;
            }

            return;
        }

        try {
            if (activeScript != null) activeScript.onPaint(graphics);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onLoginResponse(int response) {
        /**
         * from jdocs:
         * Parameters:
         * response - -3: connection timed out 3: invalid credentials 4: banned
         */
        if (response == 4 && AnalyticsSettings.isAnalyticsOn()) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, new GsonBuilder().setPrettyPrinting().create().toJson(new BanReport()));
            Request request = new Request.Builder()
                    .url(analURL + "/ban")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "ccscript")
                    .build();

            new Thread(() -> {
                try (Response r = client.newCall(request).execute();) {
                    int code = r.code();
                    Logger.info("Analytics ban res code " + code);
                    if (code == 400) failedAuthCheck = true;
                } catch (IOException e) {
                    Logger.error("Analytics ban req failed");
                }
            }).start();
        }
    }
}
