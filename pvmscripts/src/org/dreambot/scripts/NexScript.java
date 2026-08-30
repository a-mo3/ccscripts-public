package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.MiniQuest;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.gwd.GWDBoltPreference;
import org.dreambot.behaviour.method.gwd.RingPreference;
import org.dreambot.behaviour.method.gwd.nex.NexNodes;
import org.dreambot.behaviour.method.gwd.nex.enter.EnterNexFight;
import org.dreambot.behaviour.method.gwd.nex.frozendoor.FrozenDoorBranch;
import org.dreambot.behaviour.method.gwd.nex.kc.GotoNexKCArea;
import org.dreambot.behaviour.method.gwd.nex.kc.TickGetNexKC;
import org.dreambot.behaviour.method.gwd.zammy.KillZammy;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.MuleOffItem;
import org.dreambot.behaviour.misc.RechargeBlowpipe;
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
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.NexSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;
import org.dreambot.webnodes.GWDNodes;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class NexScript extends PseudoScript implements ItemContainerListener, SpawnListener {
    FractalRoot<NexSettings> tree = new FractalRoot<>(new NexSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    Area MONEY_ZONE = new Area(1652, 3784, 1697, 3751);

    @Override
    public void onArgs(String... args) {
    }


    @Override
    public void init() {
        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));
        GWDNodes.init();
        NexNodes.init();
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
                ItemID.TOOTH_HALF_OF_KEY,
                ItemID.LOOP_HALF_OF_KEY,
                ItemID.SNAPE_GRASS,
                ItemID.RUNE_SQ_SHIELD,
                ItemID.RUNE_2H_SWORD,
                ItemID.RUNE_SPEAR,
                ItemID.RUNE_BATTLEAXE,
                ItemID.DRAGON_SPEAR,
                ItemID.UNICORN_HORN,
                ItemID.SKILLS_NECKLACE,
                ItemID.SARADOMIN_HILT,
                ItemID.SARADOMIN_SWORD,
                ItemID.SARADOMINS_LIGHT,
                // todo ACB
                ItemID.SUPER_DEFENCE3,
                ItemID.MAGIC_POTION3,
                ItemID.SARADOMIN_BREW3,
                ItemID.ADAMANT_PLATEBODY,
                ItemID.RUNE_KITESHIELD,
                ItemID.RUNE_PLATESKIRT,
                ItemID.DIAMOND,
                ItemID.LAW_RUNE,
                ItemID.GRIMY_RANARR_WEED,
                ItemID.RANARR_SEED,
                ItemID.MAGIC_SEED,

                ItemID.GRIMY_RANARR_WEED,
                ItemID.GRIMY_IRIT_LEAF,
                ItemID.GRIMY_KWUARM,
                ItemID.GRIMY_LANTADYME,
                ItemID.GRIMY_DWARF_WEED,
                ItemID.GRIMY_AVANTOE,
                ItemID.GRIMY_CADANTINE,
                ItemID.GRIMY_GUAM_LEAF,
                ItemID.GRIMY_HARRALANDER,
                ItemID.BLOOD_RUNE,
                ItemID.CHAOS_RUNE,
                ItemID.RUNE_DAGGER_PP,
                ItemID.SILVER_ORE,
                ItemID.AVIAN_HEAD,
                ItemID.ADAMANTITE_BAR,
                ItemID.BLACK_MASK,
                ItemID.AMULET_OF_GLORY,
                ItemID.RING_OF_WEALTH,
                ItemID.SHARK,
                ItemID.LOBSTER,
                ItemID.RUNE_CHAINBODY,
                ItemID.RUNE_PLATESKIRT,
                ItemID.ANTI_POSION_3,
                ItemID.DRAGON_BONES,
                ItemID.PRAYER_POTION4,
                ItemID.RUNITE_LIMBS,
                ItemID.NATURE_RUNE,
                ItemID.MITHRIL_PLATESKIRT,
                ItemID.MITHRIL_PLATEBODY,
                ItemID.ADAMANT_PLATEBODY,
                ItemID.ADAMANT_PLATELEGS,
                ItemID.ADAMANT_PLATESKIRT,
                ItemID.LAW_RUNE,
                ItemID.UNCUT_SAPPHIRE,
                ItemID.UNCUT_RUBY,
                ItemID.UNCUT_EMERALD,
        };
        MuleOff.muleOffItems = new MuleOffItem[]{
                new MuleOffItem(ItemID.ARMADYL_CROSSBOW, () -> true, tree.getSettings().loadout.name().contains("ACB") ? 1 : 0)
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        RingPreference.ringPreference = tree.getSettings().ringPreference;
        GWDBoltPreference.boltPreference = tree.getSettings().boltPreference;

        tree.setSimpleName("cCNex");


        tree.addChildren(
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),


                new GetMembershipBranch().setSimpleName("Get Membership"),

                // this varbit is NEX_STORY_NPC from runelite, its when you speak to whatever her name is to get
                // access to prison
                new FrozenDoorBranch(() -> PlayerSettings.getBitValue(13182) < 1),

                // after getting KC setting up for the fight
                new RechargeBlowpipe().setSimpleName("Toxic blowpipe charge"),
                new EnterNexFight(tree.getSettings().loadout).setSimpleName("Fight setup"),

                //
                new GotoNexKCArea(() -> !GotoNexKCArea.KC_AREA.contains(Players.getLocal())),
                new TickGetNexKC(() -> true)

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

        if (Bank.isCached()) {
            if (OwnedItems.count(ItemID.ECUMENICAL_KEY) == 3) {
                keyLock = true;
            }

            // y check is important or you would use it on the door and then teleport out
            if (!OwnedItems.contains(ItemID.ECUMENICAL_KEY) && Players.getLocal().getY() < 3500) {
                keyLock = false;
            }
        }

        if (!nodesCut && PaidQuest.EADGARS_RUSE.isFinished()) {
            nodesCut = true;
            Logger.info("Cutting nodes after eadgars ruse ");
            Area cutNodesArea = new Area(2790, 3652, 2936, 3599);
            WebFinder wf = WebFinder.getWebFinder();
            List<AbstractWebNode> dragonNodes = wf.getAll().stream().filter(x -> cutNodesArea.contains(x.getTile())).collect(Collectors.toList());
            dragonNodes.forEach(wf::removeNode);
        }

//        if (AVIANS.contains(Players.getLocal())) hasLootInBag = true;

        if (hasLootInBag) {
            if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null) {
                Logger.info("Emptying looting bags");
                new EmptyLootingBagEvent().executed();
                hasLootInBag = false;
            }
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


        // wildy gwd boulder thats a npc for some reason
        NPC boulderObstacle = (NPCs.closest(x -> x.getName().equals("<col=00ffff>Boulder</col>")));
        if (Skills.getBoostedLevel(Skill.STRENGTH) >= 60 && boulderObstacle != null && boulderObstacle.distance() < 5 && boulderObstacle.getX() < Players.getLocal().getX()) {
            Logger.info("Moving boulder");
            boulderObstacle.interact("Move");
            Antiban.sleepUntil(() -> boulderObstacle.getX() > Players.getLocal().getX(), 4400);
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
//                "Zil GP (Actual) " + KillZammy.earnedGP,
//                "Zil GP (EV) " + deathCounter * 220_000,
//                "Zil kills " + deathCounter
        };
    }

    @Override
    public String getScriptName() {
        return "cCNex";
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
