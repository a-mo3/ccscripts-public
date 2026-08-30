package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.GameStateListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.quests.eaglespeak.EaglesPeak;
import org.dreambot.behaviour.training.hunter.*;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.RedChinSettings;
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

public class RedChinchompasScript extends PseudoScript implements ItemContainerListener, ChatListener, GameStateListener {
    FractalRoot<RedChinSettings> tree = new FractalRoot<>(new RedChinSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    static Timer lastWorldHop = new Timer(3000);
    boolean wasHopping = false;
    boolean disableOnMembers = false;

    @Override
    public void onArgs(String... args) {
        if (Arrays.stream(args).anyMatch(x -> x.contains("membership"))) disableOnMembers = true;
    }

    Area CHINS = new Area(3155, 3775, 3160, 3769);

    @Override
    public void init() {
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
        tree.setSimpleName("cCRedChins");
        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));

        MuleOff.LOOT = new int[]{
                ItemID.BLACK_CHINCHOMPA,
                ItemID.RED_CHINCHOMPA,
                ItemID.CHINCHOMPA,
        };

        ScriptManager scriptManager = Client.getInstance().getScriptManager();
        boolean useGray = tree.getSettings().trainWithGray && scriptManager.hasSDNScript(1914);
        final Area SMALL_AREA_RED = new Area(2557, 2917, 2559, 2915, 0);

        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

//                new CopperLongtails(() -> true).setSimpleName("Copper"),
                new EaglesPeak(() -> Skills.getRealLevel(Skill.HUNTER) >= 27 && !PaidQuest.EAGLES_PEAK.isFinished()).setSimpleName("Eagles Peak"),
                new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < 53).setSimpleName("Hunter"),

                new GenericChinCatching(() -> useGray && Skills.getRealLevel(Skill.HUNTER) < 63,
                        GrayChinSpot.ISLE_OF_SOULS.getCenter(), !tree.getSettings().crash)
                        .setSimpleName("Gray chins"),
                new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < 63).setSimpleName("Hunter"),
                new EnsureLeftFalconry().setSimpleName("Ensure left falconry"),

                new MuleOff().setSimpleName("Mule off"),
                new Fractal(() -> tree.getSettings().varlamoreSpot)
                        .setSimpleName("Varlamore spot")
                        .addChildren(
                                new ChildrenOfTheSun().setSimpleName("COS"),
                                new GenericChinCatching(() -> true,
                                        new Tile(1316, 3170),
                                        !tree.getSettings().crash)
                                        .setSimpleName("Varlamore Red chins")
                        ),

                new GenericChinCatching(() -> true,
                        SMALL_AREA_RED.getCenter(),
                        !tree.getSettings().crash)
                        .setSimpleName("Red chins")

        );

//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (Worlds.getCurrent().isMembers() && disableOnMembers) return -1;
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
                "Hunter level " + Skills.getRealLevel(Skill.HUNTER)
        };
    }

    @Override
    public String getScriptName() {
        return "cCRedChinFarm";
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
        Logger.info("Inv added");
        if (Bank.isOpen()) return;
        if (runtime.elapsed() < 20_000) return;
        if (Arrays.stream(MuleOff.LOOT).noneMatch(x -> x == item.getId())) return;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("Inv changed");
        if (Bank.isOpen()) return;
        if (runtime.elapsed() < 20_000) return;
        if (Arrays.stream(MuleOff.LOOT).noneMatch(x -> x == incoming.getId())) return;
//        if (CHINS.getCenter().distance() > 10) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        Logger.info("Inv swap");
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
}
