package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
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
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.method.antipk.AntiPkLeaveBosses;
import org.dreambot.behaviour.method.calvarion.CalvarionConfigurePrayerFlicks;
import org.dreambot.behaviour.method.calvarion.GetMoneyForFees;
import org.dreambot.behaviour.method.spindel.ExitWithLoot;
import org.dreambot.behaviour.method.spindel.RechargeWildyWeapon;
import org.dreambot.behaviour.method.tickantipk.TickAntiPKBranch;
import org.dreambot.behaviour.method.vetion.GoToVetion;
import org.dreambot.behaviour.method.vetion.InitVetionConnection;
import org.dreambot.behaviour.method.vetion.LeaveVetion;
import org.dreambot.behaviour.method.vetion.tickvetion.TickVetionBranch;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.TurnInLootKeys;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.behaviour.wilddiary.EasyWildernessDiary;
import org.dreambot.behaviour.wilddiary.HardWildernessDiary;
import org.dreambot.behaviour.wilddiary.MediumWildernessDiary;
import org.dreambot.comms.impl.callisto.CallistoClient;
import org.dreambot.comms.impl.vetion.VetionClient;
import org.dreambot.comms.impl.vetion.messages.VetionTeamState;
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
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.VetionSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class VetionScript extends PseudoScript implements ItemContainerListener, ChatListener, GameStateListener {
    FractalRoot<VetionSettings> tree = new FractalRoot<>(new VetionSettings(), getScriptName());
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
            // todo vetion loadouts
        }
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        MuleOff.LOOT = new int[]{

                ItemID.RUNE_FULL_HELM,
                ItemID.ADAMANT_PLATEBODY,
                ItemID.RUNE_MED_HELM,
                ItemID.RUNE_SQ_SHIELD,
                ItemID.RUNE_CHAINBODY,
                ItemID.RUNE_BATTLEAXE,
                ItemID.CHAOS_RUNE,
                ItemID.DEATH_RUNE,
                ItemID.LAW_RUNE,
                ItemID.RUNE_ARROW,
                ItemID.ADAMANT_ARROW,

                ItemID.OBSIDIAN_PLATELEGS,
                ItemID.OBSIDIAN_PLATEBODY,
                ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD,

                ItemID.MYSTIC_ROBE_TOP,
                ItemID.MYSTIC_ROBE_BOTTOM,
                ItemID.MYSTIC_FIRE_STAFF,
                ItemID.MYSTIC_WATER_STAFF,

                ItemID.STAMINA_POTION1,
                ItemID.STAMINA_POTION2,
                ItemID.STAMINA_POTION3,

                ItemID.SUPER_COMBAT_POTION3,
                ItemID.SUPER_COMBAT_POTION2,
                ItemID.SUPER_COMBAT_POTION1,


                ItemID.AMULET_OF_GLORY_UNCHARGED,
                ItemID.RING_OF_WEALTH,
                ItemID.SKILLS_NECKLACE,
                ItemID.DRAGON_2H_SWORD,
                ItemID.DRAGON_PICKAXE,
                ItemID.SKULL_OF_VETION,
                ItemID.RING_OF_THE_GODS,
                ItemID.VOIDWAKER_BLADE,

                ItemID.DARK_CRAB,
                ItemID.SUPER_RESTORE4,

                ItemID.RUNE_PICKAXE,
                ItemID.ANCIENT_STAFF,
                ItemID.RUNE_2H_SWORD,

                ItemID.CHAOS_RUNE,
                ItemID.DEATH_RUNE,
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

                ItemID.GRIMY_RANARR_WEED,
                ItemID.GRIMY_DWARF_WEED,
                ItemID.GRIMY_SNAPDRAGON,
                ItemID.GRIMY_TOADFLAX,
                ItemID.RUNE_KNIFE,
                ItemID.RUNE_DART,
                ItemID.WILDERNESS_CRABS_TELEPORT
        };

        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        Logger.info("Init");
        tree.setSimpleName("cCVetion");

        Area FEROX = new Area(3124, 3646, 3156, 3616);

        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ScoutFractal(),
                new ReactionSettingsFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("Lamp handler"),
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new EmptyDeathsCoffer().setSimpleName("Empty coffer"),

                // melee mode training
                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.ATTACK) < tree.getSettings().atkTarget)
                        .setSimpleName("Melee training"),

                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < Math.max(tree.getSettings().prayerTarget, 43))
                        .setSimpleName("Prayer, min43"),

                // todo magic training
                new EasyWildernessDiary().setSimpleName("Easy"),
                new MediumWildernessDiary().setSimpleName("Medium"),

                new MuleOff().setSimpleName("Mule off")
                        .setPrependLogic(() -> {
                            if (!SpecialWalker.leaveAvasRoom()) return true;
                            new DecantPotionEvent("blighted super", "prayer potion", "super combat").executed();
                            return false;
                        }),


                new RechargeWildyWeapon(ItemID.VIGGORAS_CHAINMACE_U, ItemID.VIGGORAS_CHAINMACE, rechargedExitDanger, 350)
                        .setSimpleName("Recharge viggoras")
                        .setAcceptCondition(() -> tree.getSettings().loadout.name().contains("VIG")
                                && (OwnedItems.contains(ItemID.VIGGORAS_CHAINMACE_U) || needsToRecharge)),

                new GetMoneyForFees().setSimpleName("Get money for fees"),
//                // same prayers as calv
                new InitVetionConnection().setSimpleName("Establish comms"),
                new TurnInLootKeys().setSimpleName("Turn in loot key"),
                new TickAntiPKBranch(
                        // todo pk cycle lock here, so it will complete run away even after it gaps
                        () -> (VetionClient.getFirstPker() != null && !FEROX.contains(CallistoClient.getFirstPker())) || TickAntiPKBranch.lock,
                        tree.getSettings().runMode,
                        VetionClient::getPkers
                ),
                new CalvarionConfigurePrayerFlicks().setSimpleName("Set up quick prayers"),
                new LeaveVetion(tree.getSettings().exitLootValue).setSimpleName("Leave vetion"),
                new Fractal(() -> !Combat.isInWild() && !tree.getSettings().loadout.isFulfilled())
                        .setInventoryLoadout(tree.getSettings().loadout.getInventoryLoadout())
                        .setEquipmentLoadout(tree.getSettings().loadout.getEquipmentLoadout())
                        .setSimpleName("Get loadout"),
                new GoToVetion().setSimpleName("Go to Vet'ion"),
//
                new TickVetionBranch(() -> true, tree.getSettings()).setSimpleName("Fight Vet'ion")
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
        // delete 2/3 of the
        int calc = Calculations.random(escapeCavePaths.size());
        Logger.info("Del calc  " + calc);
        Area delPaths = escapeCavePaths.get(calc);
        List<AbstractWebNode> delNodes = wf.getAll().stream().filter(x -> delPaths.contains(x.getTile()))
                .collect(Collectors.toList());
        delNodes.forEach(wf::removeNode);
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
                    .ifPresent(x -> VetionClient.reportPker(x.getName(), tree.getSettings().pkWebhook));
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

        VetionTeamState state = VetionClient.getState();
        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
                "Game state " + Client.getGameStateID(),
                String.format("Inv loot value %s / %s", df.format(invValue + LootingBag.value()), df.format(tree.getSettings().exitLootValue)),
                "Deaths " + deathCount,
                "Opps " + (state == null ? "-" : state.getOpps().size() + " - " + state.getOpps()),
                "Team " + (state == null ? "-" : state.getTeamId() + " W" + state.getWorld())
        };
    }

    @Override
    public String getScriptName() {
        return "cCVetionScript";
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
            Character atkingMe = Players.getLocal().getCharacterInteractingWithMe();
            if (atkingMe != null && Combat.isInWild() && atkingMe instanceof Player) deathCount++;
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
        VetionClient.closeConnection();
    }
}
