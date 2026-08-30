package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.analytics.impl.AnalyticsSettings;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.randoms.RandomManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.behaviour.combat.GenericCombat;
import org.dreambot.behaviour.firemaking.FireMakingDTO;
import org.dreambot.behaviour.fishing.FishingDTO;
import org.dreambot.behaviour.fishing.FishingMode;
import org.dreambot.behaviour.fuckingaround.LogoutBreak;
import org.dreambot.behaviour.fuckingaround.WalkAbout;
import org.dreambot.behaviour.goldfarming.KillCowsForHide;
import org.dreambot.behaviour.mining.MiningDTO;
import org.dreambot.behaviour.mining.MiningMode;
import org.dreambot.behaviour.quest.RomeoAndJuliet;
import org.dreambot.behaviour.quest.SheepShearer;
import org.dreambot.behaviour.quest.WitchsPotion;
import org.dreambot.behaviour.quest.XMarksTheSpot;
import org.dreambot.behaviour.quest.cooksassistant.CooksAssistant;
import org.dreambot.behaviour.smithing.SmithingBar;
import org.dreambot.behaviour.smithing.SmithingDTO;
import org.dreambot.behaviour.smithing.SmithingMode;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.behaviour.woodcutting.TreeType;
import org.dreambot.behaviour.woodcutting.WoodcuttingDTO;
import org.dreambot.fractals.AnyValidChildrenFractal;
import org.dreambot.fractals.IronFractal;
import org.dreambot.gui.settings.SettingsRepository;
import org.dreambot.loadouts.behavior.RestockStackFractal;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.utility.PaintUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class IronBuilder extends PseudoScript {
    @Override
    protected String scriptName() {
        return "Builder";
    }

    @Override
    public void init(IronFractal tree, String[] args) {
        Menu.toggleMenuManipulation(Calculations.random(0, 2) == 1);
        Walking.toggleNoClickWalk(Calculations.random(0, 2) == 1);

        tree.setSimpleName("Iron builder");
        chooseBehaviour(-1, tree, args);
        Logger.info("Behaviour choosen");

        // turn on analytics in beta, everyone getting authed knows it for data collection
        // and theres no gui in beta to turn it on yourself
        AnalyticsSettings s = SettingsRepository.getSetting("analytics.json", new AnalyticsSettings().setEnabled(true));
        Logger.info("Analytics settings " + s);
        if (s == null || !s.enabled) {
            Logger.info("Beta enabling analytics");
            SettingsRepository.serializeToFile(new AnalyticsSettings().setEnabled(true), "analytics.json");
        }
    }

    private void chooseBehaviour(int code, IronFractal scriptTree, String[] args) {
        // i actually dont need any server sided decision for the first week or 2
        IronFractal mining = new AnyValidChildrenFractal().addChildren(
                new MiningDTO()
                        .setLevel(Calculations.random(-10, 30))
                        .setBankOre(true)
                        .setMode(MiningMode.CLAY)
                        .toFractal(),
                new MiningDTO()
                        .setLevel(Calculations.random(15, 55))
                        .setBankOre(Calculations.random(2) == 1)
                        .setMode(MiningMode.BRONZE)
                        .toFractal(),
                new MiningDTO()
                        .setLevel(Calculations.random(16, 55))
                        .setBankOre(Calculations.random(2) == 1)
                        .setMode(MiningMode.COPPER)
                        .toFractal(),
                new MiningDTO()
                        .setLevel(Calculations.random(15, 55))
                        .setBankOre(Calculations.random(2) == 1)
                        .setMode(MiningMode.TIN)
                        .toFractal(),
                new MiningDTO()
                        .setLevel(Calculations.random(30, 101))
                        .setBankOre(Calculations.random(2) == 1)
                        .setMode(MiningMode.IRON)
                        .toFractal(),
                new MiningDTO()
                        .setLevel(Calculations.random(30, 101))
                        .setBankOre(Calculations.random(2) == 1)
                        .setMode(MiningMode.COAL)
                        .toFractal()
        ).setSimpleName("Mining");


        IronFractal firemaking = new AnyValidChildrenFractal().addChildren(
                new FireMakingDTO()
                        .setCollectAshes(false)
                        .setLevel(Calculations.random(30, 99))
                        .toFractal()
        ).setSimpleName("Firemaking");

        IronFractal fishing = new AnyValidChildrenFractal().addChildren(
                new FishingDTO()
                        .setMode(FishingMode.SMALL_NET)
                        .setBankAll(Calculations.random(4) == 1)
                        .setSellAll(Calculations.random(10) == 1)
                        .setLevelTaget(Calculations.random(20, 50))
                        .toFractal(),
                // todo rod and cage
                new FishingDTO()
                        .setMode(FishingMode.FLY)
                        .setBankAll(Calculations.random(4) == 1)
                        .setSellAll(Calculations.random(10) == 1)
                        .setLevelTaget(Calculations.random(30, 75))
                        .toFractal()
        ).setSimpleName("Fishing");

        IronFractal woodcutting = new AnyValidChildrenFractal().addChildren(
                new WoodcuttingDTO()
                        .setLevel(Calculations.random(30, 85))
                        .setBank(Calculations.random(2) == 1)
                        .toFractal(),
                new WoodcuttingDTO()
                        .setLevel(Calculations.random(60, 101))
                        .setTreeType(TreeType.WILLOW)
                        .setBank(false)
                        .toFractal(),
                new WoodcuttingDTO()
                        .setLevel(101)
                        .setTreeType(TreeType.YEW)
                        .setBank(Calculations.random(2) == 1)
                        .toFractal()
        ).setSimpleName("Woodcutting");

        IronFractal combat = new AnyValidChildrenFractal().addChildren(
                new GenericCombat(() -> Combat.getCombatLevel() < Calculations.random(1, 35),
                        new Area(3170, 3303, 3184, 3289),
                        x -> "Chicken".equals(x.getName()))
                        .setLootFilter(x -> x.getId() == ItemID.FEATHER)
                        .setRunAwayThreshold(2)
                        .setRestLocation(new Area(3167, 3282, 3173, 3276))
                        .setSimpleName("Kill chicken")
        ).setSimpleName("Combat");

        IronFractal smithing = new AnyValidChildrenFractal().addChildren(
                new SmithingDTO()
                        .setMode(SmithingMode.BARS)
                        .setBar(SmithingBar.BRONZE)
                        .setTarget(Calculations.random(15, 40))
                        .toFractal(),
                new SmithingDTO()
                        .setMode(SmithingMode.BARS)
                        .setBar(SmithingBar.IRON)
                        .setTarget(Calculations.random(30, 82))
                        .toFractal(),
                new SmithingDTO()
                        .setMode(SmithingMode.BARS)
                        .setBar(SmithingBar.STEEL)
                        .setTarget(Calculations.random(0, 99))
                        .toFractal()
        ).setSimpleName("Combat");

        shuffleTree = new AnyValidChildrenFractal().addChildren(
                combat,
                mining,
                firemaking,
                new XMarksTheSpot(() -> !FreeQuest.X_MARKS_THE_SPOT.isFinished()),
                new SheepShearer(() -> !FreeQuest.SHEEP_SHEARER.isFinished()),
                new RomeoAndJuliet(() -> !FreeQuest.ROMEO_AND_JULIET.isFinished()),
                new CooksAssistant(() -> !FreeQuest.COOKS_ASSISTANT.isFinished()),
                new WitchsPotion(() -> !FreeQuest.WITCHS_POTION.isFinished()),
                fishing,
                smithing,
                woodcutting
        ).setSimpleName("B").shuffle();

        if (args.length >= 1) {
            goldFarmingTree = new IronFractal(() -> Quests.getQuestPoints() >= 10 && Skills.getTotalLevel() >= 100)
                    .setSimpleName("Gold Farming");
            // parse args for allowed gold farming tasks
            for (String arg : args) {
                switch (arg) {
                    case "cow":
                        goldFarmingTree.addChildren(new KillCowsForHide(() -> true));
                    case "iron":
                        goldFarmingTree.addChildren(
                                // todo prereq levels
                                new MiningDTO()
                                        .setLevel(15)
                                        .setBankOre(Calculations.random(2) == 1)
                                        .setMode(MiningMode.BRONZE)
                                        .toFractal(),
                                new MiningDTO()
                                        .setLevel(101)
                                        .setBankOre(Calculations.random(2) == 1)
                                        .setMode(MiningMode.IRON)
                                        .toFractal()
                        );
                    case "clay":
                        goldFarmingTree.addChildren(
                                new MiningDTO()
                                        .setLevel(101)
                                        .setBankOre(true)
                                        .setMode(MiningMode.CLAY)
                                        .toFractal()
                        );
                    case "coal":
                        goldFarmingTree.addChildren(
                                new MiningDTO()
                                        .setLevel(30)
                                        .setBankOre(Calculations.random(2) == 1)
                                        .setMode(MiningMode.BRONZE)
                                        .toFractal(),
                                new MiningDTO()
                                        .setLevel(101)
                                        .setBankOre(Calculations.random(2) == 1)
                                        .setMode(MiningMode.COAL)
                                        .toFractal()
                        );
                    case "yew":
                        goldFarmingTree.addChildren(
                                new WoodcuttingDTO()
                                        .setLevel(60)
                                        .setBank(true)
                                        .toFractal(),
                                new WoodcuttingDTO()
                                        .setLevel(101)
                                        .setTreeType(TreeType.YEW)
                                        .setBank(true)
                                        .toFractal()
                        );
                    case "bronzeBar":
                        goldFarmingTree.addChildren(
                                new SmithingDTO()
                                        .setMode(SmithingMode.BARS)
                                        .setBar(SmithingBar.BRONZE)
                                        .setTarget(101)
                                        .toFractal()
                        );
                    case "ironBar":
                        goldFarmingTree.addChildren(
                                new SmithingDTO()
                                        .setMode(SmithingMode.BARS)
                                        .setBar(SmithingBar.BRONZE)
                                        .setTarget(15)
                                        .toFractal(),
                                new SmithingDTO()
                                        .setMode(SmithingMode.BARS)
                                        .setBar(SmithingBar.IRON)
                                        .setTarget(1000)
                                        .toFractal()
                        );
                    case "steelBar":
                        goldFarmingTree.addChildren(
                                new SmithingDTO()
                                        .setMode(SmithingMode.BARS)
                                        .setBar(SmithingBar.BRONZE)
                                        .setTarget(30)
                                        .toFractal(),
                                new SmithingDTO()
                                        .setMode(SmithingMode.BARS)
                                        .setBar(SmithingBar.STEEL)
                                        .setTarget(1000)
                                        .toFractal()
                        );
                    default:
                        continue;
                }
            }

        }

        // true = break for this 10 mins, false = dont
        Logger.info("abbc" + antiBanBreakCount);
        final int PLAY_TIME_VARCINT = 526;
//        breakFractal = new IronFractal(() -> shouldBreak((int) (runtime.elapsed() / (600_000))));
//        breakFractal.setSimpleName("Fuck about");
//        breakFractal.addChildren(
//                new LogoutBreak(() -> true).setSimpleName("Logout"),
//                new WalkAbout(() -> true),
//                new IronFractal(() -> true).setSimpleName("Idle")
//        );


        // disable breaks
        Client.getInstance().getRandomManager().disableSolver(RandomEvent.BREAK);


        scriptTree.addChildren(
//                breakFractal,
                new IronFractal(() -> {
                    Client.getInstance().getRandomManager().enableSolver(RandomEvent.LOGIN);
                    return false;
                }).setSimpleName("Ensure logged in"),
                goldFarmingTree,
                shuffleTree
//                new FireMakingDTO()
//                        .setLevel(99)
//                        .toFractal().setSimpleName("Fallback firemaking")
        );
    }

    IronFractal breakFractal = new IronFractal(() -> false);
    Timer breakShuffleTimer = new Timer(1000L * 60 * Calculations.random(60, 220));

    int antiBanBreakCount = Calculations.random(121);
    boolean[] noobArray = noobArray(antiBanBreakCount);

    private boolean shouldBreak(int period) {
        if (period > 120) return false;
        return noobArray[period];
    }

    /**
     * generate an array
     * @return 120 boolean array
     */
    boolean[] noobArray(int i) {
        if (i > 120) i = 120;
        boolean[] result = new boolean[120];
        // Start with first i positions set to true
        for (int j = 0; j < i; j++) {
            result[j] = true;
        }
        // Shuffle the array so trues are evenly/randomly distributed
        Random random = new Random();
        for (int j = result.length - 1; j > 0; j--) {
            int k = random.nextInt(j + 1);

            // swap
            boolean temp = result[j];
            result[j] = result[k];
            result[k] = temp;
        }
        Logger.info(Arrays.toString(result));
        return result;
    }

    IronFractal goldFarmingTree = new IronFractal(() -> false);
    Timer goldFarmShuffleTimer = new Timer(1000L * 60 * Calculations.random(180, 500));
    // to prevent any account getting stuck for the lifetime of the account, shuffle every 60 to 220 minutes
//    Timer shuffleTimer = new Timer(1000L * 60 * Calculations.random(60, 220));
    IronFractal shuffleTree;
    Timer shuffleTimer = new Timer(1000L * 60 * Calculations.random(60, 220));

    @Override
    public boolean onLoop() {
        if (breakShuffleTimer.finished()) {
            Logger.info("Shuffle breaks behaviour");
            breakShuffleTimer.reset();
            breakFractal.shuffle();
        }

        if (goldFarmShuffleTimer.finished()) {
            Logger.info("Shuffle gold farming behaviour");
            goldFarmShuffleTimer = new Timer(1000L * 60 * Calculations.random(180, 500));
            goldFarmingTree.shuffle();
        }

        if (shuffleTimer.finished()) {
            Logger.info("Shuffle behaviour");
            shuffleTimer = new Timer(1000L * 60 * Calculations.random(60, 220));
            shuffleTree.shuffle();
        }
        return false;
    }

    Timer runtime = new Timer();

    @Override
    public void onPaint(Graphics g) {
//        button.paintButton(g);
        PaintUtil.paint(g, new String[]{
                runtime.formatTime(),
                IronFractal.decisionPath.toString() + " " + TutorialTree.tutState(),
                String.valueOf(RestockStackFractal.getRestockTasks().size()),
                Arrays.toString(RestockStackFractal.getRestockTasks().toArray()),
                IronFractal.mouseFeatureFlag.toString(),
                "Period " + runtime.elapsed() / (60 * 1000 * 10) + " " + antiBanBreakCount
        });
    }
}
