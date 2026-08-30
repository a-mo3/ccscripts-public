package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.GameStateListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.eaglespeak.EaglesPeak;
import org.dreambot.behaviour.training.hunter.*;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.discordwebhook.scouter.ScoutFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.BlackChinSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class BlackChinchompasScript extends PseudoScript implements ItemContainerListener, ChatListener, GameStateListener {
    FractalRoot<BlackChinSettings> tree = new FractalRoot<>(new BlackChinSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    static Timer lastWorldHop = new Timer(3000);
    boolean wasHopping = false;
    boolean disableOnMembers = false;
    int deathCount = 0;

    @Override
    public void onArgs(String... args) {
        if (Arrays.stream(args).anyMatch(x -> x.contains("membership"))) disableOnMembers = true;
    }

    Area CHINS = new Area(3155, 3775, 3160, 3769);

    @Override
    public void init() {
        Client.getInstance().getRandomManager().disableSolver(RandomEvent.LOGIN);
        Client.getInstance().getRandomManager().registerSolver(new CustomLoginHandler("CUSTOM_LOGIN"));
        Client.getInstance().getRandomManager().enableSolver("CUSTOM_LOGIN");
        // AbstractResponseEvent.addGlobalExitCondition(new EventExitCondition(() -> !lastWorldHop.finished(), "RECENT_WORLD_HOP"));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2357, 3465, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2349, 3470, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2337, 3473, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2326, 3477, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2320, 3485, 0));
        WebFinder.getWebFinder().createAndAddNode(new Tile(2320, 3490, 0));

        Area eaglesPeakForest = new Area(
                new Tile(2314, 3489, 0),
                new Tile(2322, 3499, 0),
                new Tile(2332, 3505, 0),
                new Tile(2347, 3500, 0),
                new Tile(2353, 3490, 0),
                new Tile(2361, 3492, 0),
                new Tile(2357, 3505, 0),
                new Tile(2345, 3512, 0),
                new Tile(2331, 3513, 0),
                new Tile(2317, 3501, 0));

        LocalPathFinder l = LocalPathFinder.getLocalPathFinder();
        for (Tile t : eaglesPeakForest.getTiles()) {
            l.addBlacklistedTile(t);
        }


        // i think these are black chin wilderness related but ill leave them in there because we dont need wildy for this and if im wrong i dont wanna break anything
        Area dragonsBeHere = new Area(
                new Tile(3117, 3747, 0),
                new Tile(3188, 3744, 0),
                new Tile(3186, 3683, 0),
                new Tile(3179, 3656, 0),
                new Tile(3136, 3655, 0),
                new Tile(3116, 3702, 0));
        WebFinder wf = WebFinder.getWebFinder();
        List<AbstractWebNode> dragonNodes = wf.getAll().stream().filter(x -> dragonsBeHere.contains(x.getTile())).collect(Collectors.toList());
        dragonNodes.forEach(wf::removeNode);

        Client.getInstance().addEventListener(this);


        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.AMULET_OF_GLORY,
                ItemID.ADAMANTITE_BAR
        };

        Logger.info("Init");
        tree.setSimpleName("cCBlackChins");
        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));

        MuleOff.LOOT = new int[]{
                ItemID.BLACK_CHINCHOMPA,
                ItemID.RED_CHINCHOMPA,
                ItemID.CHINCHOMPA,
                ItemID.AMULET_OF_GLORY_UNCHARGED,
                ItemID.RING_OF_WEALTH
        };

        ScriptManager scriptManager = Client.getInstance().getScriptManager();
        boolean useGray = tree.getSettings().trainWithGray && scriptManager.hasSDNScript(1914);
        boolean useRed = tree.getSettings().trainWithRed && (scriptManager.hasSDNScript(1671) || scriptManager.hasSDNScript(1670));
        final Area SMALL_AREA_RED = new Area(2557, 2917, 2559, 2915, 0);

        Tile[] chinSpots = {
                new Tile(3145, 3774),
                new Tile(3156, 3772),
                new Tile(3137, 3782),
        };

        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

//                new CopperLongtails(() -> true).setSimpleName("Copper"),
                new EaglesPeak(() -> Skills.getRealLevel(Skill.HUNTER) >= 27 && !PaidQuest.EAGLES_PEAK.isFinished())
                        .setSimpleName("Eagles Peak"),
                new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < 53)
                        .setSimpleName("Hunter"),

                new GenericChinCatching(() -> useGray && Skills.getRealLevel(Skill.HUNTER) < 63,
                        GrayChinSpot.ISLE_OF_SOULS.getCenter(), !tree.getSettings().crash)
                        .setSimpleName("Gray chins"),
                new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < 63).setSimpleName("Hunter"),

                new GenericChinCatching(() -> useRed && Skills.getRealLevel(Skill.HUNTER) < 73,
                        SMALL_AREA_RED.getCenter(), !tree.getSettings().crash)
                        .setSimpleName("Red chins"),
                new GenericChinCatching(() -> useGray && Skills.getRealLevel(Skill.HUNTER) < 73,
                        GrayChinSpot.ISLE_OF_SOULS.getCenter(), !tree.getSettings().crash)
                        .setSimpleName("Gray chins"),
                new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < 73).setSimpleName("Hunter"),
                new EnsureLeftFalconry().setSimpleName("Ensure left falconry"),

                new Fractal(() -> (Combat.isInWild() && !Inventory.contains(ItemID.BOX_TRAP))
                        || Inventory.count(ItemID.BLACK_CHINCHOMPA) >= tree.getSettings().chinLimit)
                        .setInventoryLoadout(new InventoryLoadout().setStrict(true))
                        .setSimpleName("Bank"),
                new MuleOff().setSimpleName("Mule off"),
                new BlackChinAntiPkNode().setSimpleName("Anti pk"),
                new GetBoxTraps().setSimpleName("Manually get box trap"),
                new GenericChinCatching(() -> true, chinSpots[Calculations.random(0, chinSpots.length)], !tree.getSettings().crash)
                        .setPrependLogic(() -> {
                            if (Camera.getPitch() < 280 && tree.getSettings().forceCameraUp && !Menu.isMenuManipulationActive()) {
                                Logger.info("Pitch camera up");
                                Camera.rotateToPitch(383);
                            }
                            return false;
                        })
                        .setInventoryLoadout(new InventoryLoadout()
                                        .addItem(ItemID.BOX_TRAP, 10, 16)
                                        .setEnabledCondition(() -> !Combat.isInWild())
                                        .addItem(ItemID.BLIGHTED_MANTA_RAY, 6).setRefill(200)
                                        .setEnabledCondition(() -> !Combat.isInWild())
                                        .addItem(ItemVariants.GAMES_NECKLACE)
                                        .addItem(ItemVariants.SKILLS_NECKLACE)
                                        .setEnabledCondition(() -> !Combat.isInWild() && Players.getLocal().getY() < 3600)
                                        .setStrictSupplier(() -> !Combat.isInWild())
//                                .strictIgnore(ItemID.BLACK_CHINCHOMPA)
                        ) // include corp lair
                        // after world hopping theres a sort period where it will do the loadout but it shouldn't because
                        // you are actually in wild. break events in this case
                        .setEventBreakCondition(Combat::isInWild)
                        .setSimpleName("Black chins")
        );

//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (Worlds.getCurrent().isMembers() && disableOnMembers) return -1;
        if (!Bank.isCached() && !Combat.isInWild()) {
            Logger.info("Get cache");
            if (Bank.isOpen()) Bank.updateCache();
            Bank.open();
            return ReactionGenerator.getNormal();
        }

        if (Combat.isAutoRetaliateOn()) {
            Logger.info("Turn off auto retaliate");
            if (Widgets.isOpen()) Widgets.closeAll();
            Combat.toggleAutoRetaliate(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isLevelUpInterfaceEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Disable level up message");
            ClientSettings.toggleLevelUpInterface(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Disable world hop confirmation");
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getNormal();
        }

        if (!Client.isLoggedIn()) return ReactionGenerator.getNormal();
        if (Client.getGameStateID() == 45) return ReactionGenerator.getQuick();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public String[] getPaintInfo() {

        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        Player local = Players.getLocal();
        String target = "";
        if (local != null) {
            Character tgt = local.getInteractingCharacter();
            if (tgt != null) target = tgt.getName();
        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Hunter level " + Skills.getRealLevel(Skill.HUNTER),
                "Deaths: " + deathCount,
                "Avoids: " + BlackChinAntiPkNode.avoidCount,
                "TB'd " + CombatUtil.get().isTeleblocked()
        };
    }

    @Override
    public String getScriptName() {
        return "cCBlackChinFarm";
    }

    @Override
    public int getMoneyMade() {
        return grossGp;
    }

    @Override
    public Timer getRuntime() {
        return runtime;
    }

    @Override
    public long getMuleOffTime() {
        return MuleOff.timer == null ? 0 : MuleOff.timer.remaining();
    }

    @Override
    public Fractal getFractal() {
        return tree;
    }

    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        DecimalFormat format = new DecimalFormat("00");
        return String.format("%s:%s:%s",
                format.format(hours),
                format.format(minutes),
                format.format(seconds));
    }


    public void onInventoryItemAdded(Item item) {
        if (Bank.isOpen()) return;
        if (runtime.elapsed() < 20_000) return;
        if (Arrays.stream(MuleOff.LOOT).noneMatch(x -> x == item.getId())) return;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (Bank.isOpen()) return;
        if (runtime.elapsed() < 20_000) return;
        if (Arrays.stream(MuleOff.LOOT).noneMatch(x -> x == incoming.getId())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (Bank.isOpen()) return;
        if (runtime.elapsed() < 20_000) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (Arrays.stream(MuleOff.LOOT).noneMatch(x -> x == incoming.getId())) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onScriptPaint(Graphics g) {
        try {
            for (BoxTrapState s : GenericChinCatching.boxTrapStates) {
                g.setColor(s.isMine() ? Color.GREEN : Color.YELLOW);
                if (s.getOwner() == BoxTrapState.Owner.SOMEONE_ELSE) g.setColor(Color.RED);
                if (s.getTile().distance() > 16) continue;
                g.drawPolygon(s.getTile().getPolygon());
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onGameStateChange(GameState gameState) {
        if (gameState == GameState.HOPPING) {
            Logger.info("World hop");
            wasHopping = true;
            return;
        }

        if (wasHopping && gameState == GameState.LOGGED_IN) {
            Logger.info("Reset last world hop timer");
            lastWorldHop.reset();
            wasHopping = false;
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            deathCount++;
        }
    }
}
