package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.lizardmen.*;
import org.dreambot.behaviour.method.pirates.RechargeAtFerox;
import org.dreambot.behaviour.misc.ImbueMSB;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.RefillRosewoodBlowpipe;
import org.dreambot.behaviour.quests.ClientOfKourend;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.XMarksTheSpot;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.range.ConfigurableRangeTraining;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.TimedShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.events.EventExitCondition;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.fractals.util.RatConfigureQuickPrayers;
import org.dreambot.scriptdata.LizardmenSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;

public class LizardShamenScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<LizardmenSettings> tree = new FractalRoot<>(new LizardmenSettings(), getScriptName());

    @Override
    public void onArgs(String... args) {
        for (String arg : args) {
            try {
                tree.getSettings().room = LizardRoom.valueOf(arg);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCLizardmenFarm");

        if (tree.getSettings().room == LizardRoom.RANDOM)
            tree.getSettings().room = LizardRoom.values()[Calculations.random(0, 3)];

        MuleOff.LOOT = new int[]{
                ItemID.RUNE_SQ_SHIELD,
                ItemID.RUNITE_BAR,
                ItemID.DRAGON_SPEAR,
                ItemID.LOOP_HALF_OF_KEY,
                ItemID.RANGING_POTION2,
                ItemID.TOOTH_HALF_OF_KEY,
                ItemID.SHIELD_LEFT_HALF,
                ItemID.RUNE_2H_SWORD,

                ItemID.AMULET_OF_GLORY_UNCHARGED,
                ItemID.RING_OF_WEALTH,
                ItemID.SKILLS_NECKLACE,

                ItemID.DRAGON_WARHAMMER,

                ItemID.RUNE_MED_HELM,
                ItemID.EARTH_BATTLESTAFF,
                ItemID.MYSTIC_EARTH_STAFF,
                ItemID.RUNE_WARHAMMER,
                ItemID.RUNE_CHAINBODY,
                ItemID.RED_DHIDE_VAMBRACES,

                ItemID.AIR_RUNE,
                ItemID.CHAOS_RUNE,
                ItemID.DEATH_RUNE,

                ItemID.XERICIAN_FABRIC,
                ItemID.IRON_ORE,
                ItemID.COAL,
                ItemID.RUNITE_ORE,

                ItemID.GRIMY_KWUARM,
                ItemID.GRIMY_CADANTINE,
                ItemID.GRIMY_DWARF_WEED,
                ItemID.GRIMY_LANTADYME,

                ItemID.RANARR_SEED,
                ItemID.SNAPDRAGON_SEED,
                ItemID.TORSTOL_SEED,
                ItemID.WATERMELON_SEED,
                ItemID.WILLOW_SEED,
                ItemID.MAHOGANY_PLANK,
                ItemID.MAPLE_SEED,
                ItemID.YEW_SEED,
                ItemID.PAPAYA_TREE_SEED,
                ItemID.MAGIC_SEED,
                ItemID.PALM_TREE_SEED,
                ItemID.DRAGONFRUIT_TREE_SEED,
                ItemID.CELASTRUS_SEED,
                ItemID.REDWOOD_TREE_SEED,

        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        EntranceWebNode lizardDwelling = new EntranceWebNode(1312, 3686, 0, "Lizard dwelling", "Enter");
        // entrance near node  1312, 10086
        WebFinder wf = WebFinder.getWebFinder();
        lizardDwelling.addOutgoingConnections(wf.getNearest(new Tile(1312, 10068), 15));
        lizardDwelling.addDualConnections(wf.getNearest(lizardDwelling, 30));
        wf.addWebNodes(lizardDwelling);

        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Mystical barrier", "pass"));

        AbstractResponseEvent.addGlobalExitCondition(new EventExitCondition(() -> {
            if ((Combat.isEnvenomed() || Combat.isPoisoned()) && ItemVariants.ANTI_DOTE_PP.getItem() != null) {
                Logger.info("Universal antidote");
                if (Widgets.isOpen()) Widgets.closeAll();
                ItemVariants.ANTI_DOTE_PP.interact("Drink");
            }
            return false;
        }, "antidote"));

        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ReactionSettingsFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new EmptyDeathsCoffer().setSimpleName("Death handler"),
                new LampHandler().setSimpleName("lamp handler"),
//
                new GetMembershipBranch().setSimpleName("Getting membership."),

                new TimedShuffleFractal(40, 208)
                        .addChildren(
                                new PrayerBranch(() -> Skill.PRAYER.getLevel() < Math.max(tree.getSettings().prayerTarget, 43))
                                        .setSimpleName("Prayer training"),
                                new ConfigurableMeleeTraining(() -> Combat.getCombatLevel() < 20
                                        || Skills.getRealLevel(Skill.HITPOINTS) < 10)
                                        .setSimpleName("Melee training"),
                                new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                new ConfigurableRangeTraining(() -> Skills.getRealLevel(Skill.HITPOINTS) < 30 || Skills.getRealLevel(Skill.RANGED) < 30)
//                                        .setDefenceTarget(settings.defenceTarget)
                                        .setSimpleName("Range Sandcrabs"),
                                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Fire making for slayer")
                        ),
//                 combat / range training

                new Fractal(() -> !PaidQuest.ANIMAL_MAGNETISM.isFinished() && Skills.getRealLevel(Skill.RANGED) >= 30 && tree.getSettings().rangeTarget >= 30)
                        .setSimpleName("Get Avas")
                        .addChildren(
                                new XMarksTheSpot().setSimpleName("X marks the spot"),
                                new ClientOfKourend().setSimpleName("Client of Kourend"),
                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                new RestlessGhost().setSimpleName("Restless ghost"),
                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                new PriestInPeril().setSimpleName("Priest in peril"),
                                new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                        ),
                new ConfigurableRangeTraining(() -> tree.getSettings().loadout.mode == Skill.RANGED && Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget,
                        Math.max(tree.getSettings().defTarget, 40))
                        .setSimpleName("Range training"),


                new ImbueMSB(),
                new MuleOff().setSimpleName("Mule off"),

                // no med packs they removed that from the game
                new GetOff330().setSimpleName("Off 330"),
                new CombatRing(() -> !OwnedItems.containsAll(
                        ItemID.SHAYZIEN_HELM_5,
                        ItemID.SHAYZIEN_BODY_5,
                        ItemID.SHAYZIEN_GREAVES_5,
                        ItemID.SHAYZIEN_BOOTS_5,
                        ItemID.SHAYZIEN_GLOVES_5
                )),

                new RatConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MISSILES, PVMUtil.getBestRangePray()}),
                new RechargeAtFerox().setSimpleName("Ferox recharge stats"),
                new RefillRosewoodBlowpipe().setSimpleName("Rosewood refill"),
                new GotoLizardShaman(() -> !Inventory.contains(ItemID.SHARK, ItemID.CHILLI_POTATO)
                        || (Equipment.isSlotEmpty(EquipmentSlot.ARROWS) && !Equipment.contains(ItemID.ROSEWOOD_BLOWPIPE)) // todo if i add blowpipe check for that here
                        || !tree.getSettings().room.area.contains(Players.getLocal()), tree.getSettings()),
                new LizardmenBranch(() -> true, tree.getSettings()).setSimpleName("Kill lizardmen")
        );
    }


    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) tree.run();

        if (ClientSettings.isLevelUpInterfaceEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Disable level up message");
            ClientSettings.toggleLevelUpInterface(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.areFoodSupplyPilesOnDeathEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Disable level up message");
            ClientSettings.toggleFoodSupplyPilesOnDeath(false);
            return ReactionGenerator.getNormal();
        }

        if (!Client.isLoggedIn()) return ReactionGenerator.getNormal();
        if (Client.getGameStateID() == 45) return ReactionGenerator.getNormal();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "In combat " + Players.getLocal().isInCombat(),
                "Tick " + Client.getGameTick(),
        };
    }

    @Override
    public String getScriptName() {
        return "cCLizardmenFarm";
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
    public void onInventoryItemAdded(Item item) {
        if (Bank.isOpen()) return;
        if (!tree.getSettings().room.area.contains(Players.getLocal())) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (Bank.isOpen()) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) return;
        if (!tree.getSettings().room.area.contains(Players.getLocal())) return;
        grossGp += incoming.getLivePrice() * quantity;
    }

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        if (!tree.getSettings().room.area.contains(Players.getLocal())) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    int deathCount = 0;

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            deathCount++;
        }
    }

    @Override
    public void onScriptPaint(Graphics g) {
        g.setColor(Color.white);
        for (Tile tile : tree.getSettings().room.tiles) {
            if (tile.distance() > 10) return; // return because all tiles are close
            g.drawPolygon(tile.getPolygon());
        }

        g.setColor(Color.BLUE);
        KillLizardmen.jumpTiles.forEach((t, ti) -> {
            if (t.distance() < 10) g.drawPolygon(t.getPolygon());
        });
    }
}
