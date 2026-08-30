package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.clan.chat.ClanChat;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.GameStateListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.friends.JoinClanChat;
import org.dreambot.behaviour.method.antipk.AntiPkLeaveBosses;
import org.dreambot.behaviour.method.callisto.CallistoData;
import org.dreambot.behaviour.method.callisto.CallistoLoadout;
import org.dreambot.behaviour.method.callisto.GoToCallisto;
import org.dreambot.behaviour.method.callisto.InitCallistoConnection;
import org.dreambot.behaviour.method.callisto.leavecallisto.TickLeaveCallistoBranch;
import org.dreambot.behaviour.method.callisto.tickcallisto.TickCallistoBranch;
import org.dreambot.behaviour.method.calvarion.GetMoneyForFees;
import org.dreambot.behaviour.method.spindel.ExitWithLoot;
import org.dreambot.behaviour.method.spindel.RechargeWildyWeapon;
import org.dreambot.behaviour.method.tickantipk.TickAntiPKBranch;
import org.dreambot.behaviour.method.vetion.WildernessRunMode;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.MuleOffItem;
import org.dreambot.behaviour.misc.RechargeTrident;
import org.dreambot.behaviour.misc.TurnInLootKeys;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.behaviour.wilddiary.EasyWildernessDiary;
import org.dreambot.behaviour.wilddiary.HardWildernessDiary;
import org.dreambot.behaviour.wilddiary.MediumWildernessDiary;
import org.dreambot.comms.impl.callisto.CallistoClient;
import org.dreambot.comms.impl.callisto.CallistoComms;
import org.dreambot.comms.impl.callisto.messages.CallistoTeamState;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.discordwebhook.scouter.ScoutFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.DecantPotionEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.*;
import org.dreambot.scriptdata.CallistoSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CallistoScript extends PseudoScript implements ItemContainerListener, ChatListener, GameStateListener {
    FractalRoot<CallistoSettings> tree = new FractalRoot<>(new CallistoSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    int deathCount = 0;

    // for getting out of wild when your need to recharge whatever wildy weapon
    Supplier<Boolean> rechargedExitDanger = () -> {
        if (Combat.isInWild()) {
            AntiPkLeaveBosses.leaveBosses();
            return true;
        }
        return !SpecialWalker.leaveAvasRoom();
    };
    // set whenever you get the no charges left message
    // todo find inital charge state and manage it well
    boolean needsToRecharge = false;
    boolean hasLootInBag = true;

    @Override
    public void onArgs(String... args) {
        for (String arg : args) {
            Arrays.stream(CallistoLoadout.values())
                    .filter(x -> x.name().equalsIgnoreCase(arg))
                    .findAny().ifPresent(x -> tree.getSettings().loadout = x);

            Arrays.stream(WildernessRunMode.values())
                    .filter(x -> x.name().equalsIgnoreCase(arg))
                    .findAny().ifPresent(x -> tree.getSettings().runMode = x);

        }
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        MuleOff.LOOT = new int[]{
                ItemID.RED_DRAGON_LEATHER,
                ItemID.RED_DRAGONHIDE,
                ItemID.COCONUT,
                ItemID.MAHOGANY_LOGS,
                ItemID.STAMINA_POTION3,
                ItemID.RUNE_KITESHIELD,
                ItemID.SOUL_RUNE,
                ItemID.RUNE_PLATEBODY,
                ItemID.MYSTIC_EARTH_STAFF,
                ItemID.TYRANNICAL_RING,
                ItemID.CLAWS_OF_CALLISTO,
                ItemID.RANARR_SEED,
                ItemID.SNAPDRAGON_SEED,


                ItemID.GRIMY_RANARR_WEED,
                ItemID.GRIMY_DWARF_WEED,
                ItemID.GRIMY_SNAPDRAGON,
                ItemID.GRIMY_TOADFLAX,
                ItemID.RUNE_KNIFE,
                ItemID.RUNE_DART,
                ItemID.WILDERNESS_CRABS_TELEPORT,
                ItemID.AMULET_OF_GLORY_UNCHARGED,
                ItemID.RING_OF_WEALTH,
                ItemID.SKILLS_NECKLACE,
                ItemID.DRAGON_2H_SWORD,
                ItemID.DRAGON_PICKAXE,
                ItemID.VOIDWAKER_HILT,
                ItemID.RING_OF_THE_GODS,
                ItemID.VOIDWAKER_BLADE,

                ItemID.DARK_CRAB,
                ItemID.SUPER_RESTORE4,

                ItemID.SUPER_COMBAT_POTION3,
                ItemID.SUPER_COMBAT_POTION2,
                ItemID.SUPER_COMBAT_POTION1,

                ItemID.ANTIDOTE3,
                ItemID.ANTIDOTE2,
                ItemID.ANTIDOTE1,


                ItemID.RUNE_PICKAXE,
                ItemID.ANCIENT_STAFF,
                ItemID.RUNE_2H_SWORD,

                ItemID.BLOOD_RUNE,
                ItemID.CANNONBALL,

                ItemID.GOLD_ORE,
                ItemID.LIMPWURT_ROOT,
                ItemID.MAGIC_LOGS,
                ItemID.OAK_PLANK,
                ItemID.WINE_OF_ZAMORAK,
                ItemID.UNCUT_RUBY,
                ItemID.UNCUT_DIAMOND,
                ItemID.DRAGON_BONES,
                ItemID.UNCUT_DRAGONSTONE,
                ItemID.MORT_MYRE_FUNGUS,
                ItemID.GRIMY_RANARR_WEED,

                ItemID.SANFEW_SERUM4,
                ItemID.SUPERCOMPOST,
                ItemID.YEW_SEED,
                ItemID.MAGIC_SEED,
                ItemID.PALM_TREE_SEED,
                ItemID.BLIGHTED_ANGLERFISH,
                ItemID.BLIGHTED_KARAMBWAN,
                ItemID.COMBAT_BRACELET,
        };

        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        MuleOff.muleOffItems = new MuleOffItem[]{
                new MuleOffItem(ItemID.MYSTIC_ROBE_TOP, () -> true, 3),
                new MuleOffItem(ItemID.MYSTIC_ROBE_BOTTOM, () -> true, 3),
                new MuleOffItem(ItemID.DEATH_RUNE, () -> true, 1000),
                new MuleOffItem(ItemID.CHAOS_RUNE, () -> true, 1000)
        };

        Area FEROX = new Area(3124, 3646, 3156, 3616);
        Logger.info("Init");
        tree.setSimpleName("cCCallisto");
        CallistoComms.forceWorld = tree.getSettings().forceWorld;

        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ScoutFractal(),
                new ReactionSettingsFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("Lamp handler"),
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Empty coffer"),

                new PrayerBranch(() -> Skill.PRAYER.getLevel() < Math.max(tree.getSettings().prayerTarget, 43))
                        .setSimpleName("Prayer training"),
                // melee mode training
                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.HITPOINTS) < tree.getSettings().hpTarget
                        || Skills.getRealLevel(Skill.MAGIC) < tree.getSettings().magicTarget)
                        // todo defence target
                        .setSimpleName("magic training"),

                new EasyWildernessDiary().setSimpleName("Easy"),
                new MediumWildernessDiary().setSimpleName("Medium"),

                // todo avas and range training

                new RechargeWildyWeapon(ItemID.CRAWS_BOW_U, ItemID.CRAWS_BOW, rechargedExitDanger,
                        350).setSimpleName("Recharge Craws")
                        .setAcceptCondition(() -> tree.getSettings().loadout.name().contains("BOW")
                                && (OwnedItems.contains(ItemID.CRAWS_BOW_U) || needsToRecharge)),

                new RechargeWildyWeapon(ItemID.WEBWEAVER_BOW_U, ItemID.WEBWEAVER_BOW, rechargedExitDanger,
                        350).setSimpleName("Recharge Webweaver")
                        .setAcceptCondition(() -> tree.getSettings().loadout.name().contains("BOW")
                                && (OwnedItems.contains(ItemID.WEBWEAVER_BOW_U) || needsToRecharge)),

                new RechargeWildyWeapon(ItemID.ACCURSED_SCEPTRE_U, ItemID.ACCURSED_SCEPTRE, rechargedExitDanger,
                        350).setSimpleName("Recharge Accursed")
                        .setAcceptCondition(() -> tree.getSettings().loadout.name().contains("SCEP")
                                && (OwnedItems.contains(ItemID.ACCURSED_SCEPTRE_U) || needsToRecharge)),


                new MuleOff().setSimpleName("Mule off")
                        .setPrependLogic(() -> {
                            if (!SpecialWalker.leaveAvasRoom()) return true;
                            new DecantPotionEvent("blighted super", "prayer potion", "super combat").executed();
                            return false;
                        }),

                new GetMoneyForFees().setSimpleName("Get money for fees"),
//                // same prayers as calv
                new InitCallistoConnection().setSimpleName("Establish comms"),
                new TurnInLootKeys(),
                new TickAntiPKBranch(
                        // ignore pker in ferox or else you are in a loop of exit -> run away/fight
                        () -> (Combat.isInWild() && CallistoClient.getFirstPker() != null && !FEROX.contains(CallistoClient.getFirstPker())) || TickAntiPKBranch.lock,
                        tree.getSettings().runMode,
                        CallistoClient::getPkers
                ),

                new JoinClanChat(() -> tree.getSettings().clanChat != null
                        && !tree.getSettings().clanChat.isEmpty()
                        && !ClanChat.inChat(tree.getSettings().clanChat),
                        tree.getSettings().clanChat),

                new RatConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MISSILES, tree.getSettings().loadout.isRange() ? PVMUtil.getBestRangePray() : PVMUtil.getBestMagePray()}),

                new RechargeTrident().setSimpleName("Recharge trident"),
                new TickLeaveCallistoBranch(tree.getSettings().exitLootValue, tree.getSettings().leaveMode).setSimpleName("Leave Callisto"),

                new Fractal(() -> !Combat.isInWild() && !tree.getSettings().loadout.isFulfilled())
                        .setInventoryLoadout(tree.getSettings().loadout.getInventoryLoadout())
                        .setEquipmentLoadout(tree.getSettings().loadout.getEquipmentLoadout())
                        .setSimpleName("Get loadout"),

                new GoToCallisto().setSimpleName("Go to Callisto"),
//
                new TickCallistoBranch(() -> true, tree.getSettings()).setSimpleName("Fight Callisto")
        );
//        new AIAntiban();

        WebFinder wf = WebFinder.getWebFinder();
        Area mageBankEntrance = new Area(3086, 3961, 3099, 3955);
        List<AbstractWebNode> dragonNodes = wf.getAll().stream().filter(x -> mageBankEntrance.contains(x.getTile())).collect(Collectors.toList());
        dragonNodes.forEach(wf::removeNode);
        // remove nodes that make it path through green dragons
        Area feroxThroughDragons = new Area(3116, 3718, 3148, 3644);
        List<AbstractWebNode> badNodes = wf.getAll().stream().filter(x -> feroxThroughDragons.contains(x.getTile())).collect(Collectors.toList());
        badNodes.forEach(wf::removeNode);

        Area wildernesChaosTempleNodes = new Area(3218, 3637, 3259, 3586);
        badNodes = wf.getAll().stream().filter(x -> wildernesChaosTempleNodes.contains(x.getTile())).collect(Collectors.toList());
        badNodes.forEach(wf::removeNode);
        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));

        // AbstractResponseEvent.addGlobalExitCondition(new EventExitCondition(() -> !lastWorldHop.finished(), "RECENT_WORLD_HOP"));

        List<Area> escapeCavePaths = Arrays.asList(
                new Area(3350, 10288, 3393, 10254), // right 2 paths
                new Area(3323, 10289, 3370, 10259), // left 2 paths
                // side 2 paths
                new Area(3326, 10297, 3340, 10263)
                        .withArea(new Area(3372, 10300, 3390, 10271))
        );
        // todo one of these breaks exiting i think
//        // delete 2/3 of the
//        int calc = Calculations.random(escapeCavePaths.size());
//        Logger.info("Del calc  " + calc);
//        Area delPaths = escapeCavePaths.get(calc);
//        List<AbstractWebNode> delNodes = wf.getAll().stream().filter(x -> delPaths.contains(x.getTile()))
//                .collect(Collectors.toList());
//        delNodes.forEach(wf::removeNode);
    }


    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();

        if (ClientSettings.isSkullPreventionActive()) {
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            Logger.info("Disable skull prev");
            ClientSettings.toggleSkullPrevention(false);
            return ReactionGenerator.getNormal();
        }

        // todo looting bag handle
        if (Combat.isInWild() && Players.getLocal().isInCombat()) {
            Players.getLocal().getCharactersInteractingWithMe().stream()
                    .filter(Objects::nonNull) // just do be safe
                    .filter(x -> x instanceof Player)
                    .findFirst()
                    .ifPresent(x -> CallistoClient.reportPker(x.getName(), tree.getSettings().pkWebhook));
        }

        // dont run the tree while hopping worlds because equipment state will make you do loadouts you shouldnt
        if (!Client.isLoggedIn()) return ReactionGenerator.getNormal();
        if (Client.getGameStateId() == 45) return ReactionGenerator.getNormal();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    Timer cacheTime = new Timer(5 * 1000);
    int invValue = -1;

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

        // todo something here for melee mode
        if (cacheTime.finished()) {
            cacheTime.reset();
            invValue = ExitWithLoot.inventoryValue();
        }

        CallistoTeamState state = CallistoClient.getState();
        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
                String.format("Inv loot value %s / %s", df.format(invValue + LootingBag.value()), df.format(tree.getSettings().exitLootValue)),
                "Loot bag value " + df.format(LootingBag.value()),
                "Opps " + (state == null ? "-" : state.getOpps().size() + " - " + state.getOpps()),
                "Team " + (state == null ? "-" : state.getTeamId() + " W" + state.getWorld())
        };
    }

    @Override
    public String getScriptName() {
        return "cCCallistoFarm";
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

    @Override
    public void onScriptPaint(Graphics g) {
        NPC call = NPCs.closest(CallistoData.CALLISTO_NAME);
        if (call != null) {
            g.setColor(Color.green);
            g.drawPolygon(call.getTile().getPolygon());

            g.setColor(Color.MAGENTA);
            g.drawPolygon(call.getServerTile().translate(2, 2).getPolygon());
        }

        Player lp = Players.getLocal();

        g.setColor(Color.white);
        g.drawPolygon(lp.getTile().getPolygon());
        g.setColor(Color.black);
        g.drawPolygon(lp.getServerTile().getPolygon());
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
        Logger.info("item added");
        if (!Combat.isInWild()) return;
        if (Bank.isOpen()) return;
        if (ExitWithLoot.ignoredIds.contains(item.getId())) return;
//        if (.SPINDEL_CHASM.contains(Players.getLocal())) return;
        ;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("item changed");
        if (Bank.isOpen()) return;
        if (!Combat.isInWild()) return;
        if (ExitWithLoot.ignoredIds.contains(incoming.getId())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        Logger.info("item swapped");
        if (!Combat.isInWild()) return;
        if (Bank.isOpen()) return;
        if (ExitWithLoot.ignoredIds.contains(incoming.getId())) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            deathCount++;
        }

        if (message.getMessage().toLowerCase().contains("not enough revenant ether")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("has run out of revenant")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("chainmace is out of charges")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("giving it a total of")) {
            needsToRecharge = false;
        }
    }

    static Timer lastWorldHop = new Timer(3000);
    boolean wasHopping = false;

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossGp += item.getLivePrice() * item.getAmount();
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
    public void onExit() {
        CallistoClient.closeConnection();
    }
}
