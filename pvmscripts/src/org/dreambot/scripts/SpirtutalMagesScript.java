package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.gwd.*;
import org.dreambot.behaviour.method.gwd.zammy.KillZammy;
import org.dreambot.behaviour.misc.*;
import org.dreambot.behaviour.misc.tickcombat.GenericCombatBranch;
import org.dreambot.behaviour.quests.deathplateau.DeathPlateau;
import org.dreambot.behaviour.quests.druidicritual.DruidicRitual;
import org.dreambot.behaviour.quests.dwarfcannon.DwarfCannon;
import org.dreambot.behaviour.quests.eadgarsruse.EadgarsRuse;
import org.dreambot.behaviour.quests.trollstronghold.TrollStronghold;
import org.dreambot.behaviour.training.herblore.HerbloreBranch;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.nmz.CollectHerbBoxes;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.behaviour.wilddiary.EasyWildernessDiary;
import org.dreambot.behaviour.wilddiary.MediumWildernessDiary;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.SpirtutalMagesSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;
import org.dreambot.webnodes.GWDNodes;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class SpirtutalMagesScript extends PseudoScript implements ItemContainerListener, SpawnListener {
    FractalRoot<SpirtutalMagesSettings> tree = new FractalRoot<>(new SpirtutalMagesSettings(), getScriptName());
    @Override
    public void onArgs(String... args) {
    }


    @Override
    public void init() {
        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));
        GWDNodes.init();
        Client.getInstance().addEventListener(this);

        // blacklist tile where rocks are
        LocalPathFinder lp = LocalPathFinder.getLocalPathFinder();
        lp.addBlacklistedTile(new Tile(2901, 3680, 0));
        lp.addBlacklistedTile(new Tile(2902, 3680, 0));
        lp.addBlacklistedTile(new Tile(2908, 3682, 0));
        lp.addBlacklistedTile(new Tile(2909, 3683, 0));
        lp.addBlacklistedTile(new Tile(2871, 3671, 0));
        lp.addBlacklistedTile(new Tile(2870, 3671, 0));

        MuleOff.LOOT = new int[]{
                ItemID.DRAGON_BOOTS
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        Area ZAM_MAGES = new Area(
                new Tile(2886, 5355, 2),
                new Tile(2907, 5344, 2),
                new Tile(2914, 5333, 2),
                new Tile(2922, 5337, 2),
                new Tile(2933, 5337, 2),
                new Tile(2940, 5350, 2),
                new Tile(2935, 5361, 2),
                new Tile(2913, 5365, 2),
                new Tile(2887, 5369, 2),
                new Tile(2881, 5361, 2));

        tree.setSimpleName("cCSpiritualMage");
        tree.addChildren(
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),

                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

                new CollectHerbBoxes().setSimpleName("Herb box"),

                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 61)
                        .setSimpleName("61 Magic for TP & Alch"),

                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < Math.max(43, tree.getSettings().prayerTarget))
                        .setSimpleName("Prayer (43 min)"),

                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("No 330"),

                new DwarfCannon().setSimpleName("Dwarf cannon"),

                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Get off 330"),

                new ConfigurableMeleeTraining(() ->  Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().preSlayerCombatTarget)
                        .setSimpleName("pre Slayer Sandcrabs"),

                new DruidicRitual().setSimpleName("Drudic ritual"),

                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 83).setSimpleName("Slayer until 83"),
                new ConfigurableMeleeTraining(() ->  Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().postSlayerCombatTarget)
                        .setSimpleName("Post Slayer Sandcrabs"),

                new DeathPlateau().setSimpleName("Death plateau"),
                new TrollStronghold().setSimpleName("Troll stronghold"),
                new HerbloreBranch(() -> Skills.getRealLevel(Skill.HERBLORE) < 31, false),
                new EadgarsRuse().setSimpleName("Eadgars ruse"),

                new PlaceRopes(false).setSimpleName("Place GWD rope"),
                new GWDRechargeAtFerox().setSimpleName("Use Ferox pool"),

                // dont mule off while in gwd
                new Fractal(() -> Players.getLocal().getY() < 4000 && !Combat.isInWild() && (MuleOff.timer == null || MuleOff.timer.finished()))
                        .addChildren(
                                new MuleOff().setSimpleName("Mule Off")
                        )
                        .setSimpleName("Safe mule off"),
                GenericCombatBranch.builder()
                        .area(ZAM_MAGES)

                        .mobFilter(x -> ZAM_MAGES.contains(x)
                                && x.getName().equals("Undead Druid")
                                && x.getHealthPercent() > 0
                                && (!x.isInCombat() || x.isInteracting(Players.getLocal())))
                        .prayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MAGIC, PVMUtil.getBestMeleePray()})

                        .build()
                        .init()


        );
//        new AIAntiban();
        new EasyWildernessDiary();
        new MediumWildernessDiary(); // create this to add all the needed webnodes
    }

    boolean hasLootInBag = true;
    boolean nodesCut;
    // set true when you own 3 keys, set false when you are out of keys (& low Y axis), false will force avians
    boolean keyLock = false;

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (Client.getGameStateID() == 45) return ReactionGenerator.getQuick();
        if (!Client.isLoggedIn()) return ReactionGenerator.getQuick();
        if (!nodesCut && PaidQuest.EADGARS_RUSE.isFinished()) {
            nodesCut = true;
            Logger.info("Cutting nodes after eadgars ruse ");
            Area cutNodesArea = new Area(2790, 3652, 2936, 3599);
            WebFinder wf = WebFinder.getWebFinder();
            List<AbstractWebNode> dragonNodes = wf.getAll().stream().filter(x -> cutNodesArea.contains(x.getTile())).collect(Collectors.toList());
            dragonNodes.forEach(wf::removeNode);
        }

        if (ClientSettings.areItemPilesOnDeathEnabled()) {
            if (Bank.isOpen()) Bank.close();
            Logger.info("Disabling item piles on death");
            ClientSettings.toggleItemPilesOnDeath(false);
            return ReactionGenerator.getNormal();
        }

        if (!ChangeAlchWarning.setHighAlchWarning(500_000)) {
            Logger.info("Setting alch warning price");
            return ReactionGenerator.getNormal();
        }

        return tree.run();
    }

    Timer runtime = new Timer();
    int avianGP = 0;
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
                "target: " + target,
                "Avian GP: " + avianGP,
                "Zil GP (Actual) " + KillZammy.earnedGP,
                "Zil GP (EV) " + deathCounter * 220_000,
                "Zil kills " + deathCounter
        };
    }

    @Override
    public String getScriptName() {
        return "cCSpiritualMageFarm";
    }

    @Override
    public int getMoneyMade() {
        return avianGP + KillZammy.earnedGP;
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
        if (!Combat.isInWild()) return;
        avianGP += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!Combat.isInWild()) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        avianGP += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (!Combat.isInWild()) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;
        avianGP += (incoming.getLivePrice() + 1) * quantity;
    }

    int deathCounter = 0;

    @Override
    public void onNpcDespawn(NPC npc) {
        if (npc == null || npc.getName() == null) return;
        if (npc.getName().equals("Commander K'ril Tsutaroth")) {
            Logger.info("kril death");
            deathCounter++;
        }
    }

    // door to altar tiles
    final Tile a1 = new Tile(2920, 5329, 2);
    final Tile a2 = new Tile(2926, 5329, 2);
    final Tile a3 = new Tile(2933, 5329, 2);
    // altar to door
    final Tile d1 = new Tile(2936, 5329, 2);
    final Tile d2 = new Tile(2930, 5329, 2);
    final Tile d3 = new Tile(2923, 5329, 2);

    @Override
    public void onScriptPaint(Graphics g) {
        g.setColor(Color.blue);
        g.drawPolygon(a1.getPolygon());
        g.drawPolygon(a2.getPolygon());
        g.drawPolygon(a3.getPolygon());

        g.setColor(Color.green);
        g.drawPolygon(d1.getPolygon());
        g.drawPolygon(d2.getPolygon());
        g.drawPolygon(d3.getPolygon());

        g.setColor(Color.GRAY);
        g.drawPolygon(Players.getLocal().getServerTile().getPolygon());
    }
}
