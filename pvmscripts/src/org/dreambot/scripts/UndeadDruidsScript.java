package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.tickcombat.GenericCombatBranch;
import org.dreambot.behaviour.quests.ClientOfKourend;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.XMarksTheSpot;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.animalmagnetism.util.LeaveAvaRoom;
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
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.UndeadDruidSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.util.Arrays;
import java.util.Comparator;

public class UndeadDruidsScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<UndeadDruidSettings> tree = new FractalRoot<>(new UndeadDruidSettings(), getScriptName());

    Area UNDEAD_DRUID_AREA = new Area(1807, 9972, 1813, 9959);

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCUndeadDruids");

        MuleOff.LOOT = new int[]{
                ItemID.AIR_BATTLESTAFF,
                ItemID.EARTH_BATTLESTAFF,

                ItemID.AIR_RUNE,
                ItemID.EARTH_RUNE,
                ItemID.BLOOD_RUNE,
                ItemID.CHAOS_RUNE,
                ItemID.COSMIC_RUNE,
                ItemID.DEATH_RUNE,
                ItemID.NATURE_RUNE,
                ItemID.MUD_RUNE,
                ItemID.LAW_RUNE,

                ItemID.GRIMY_GUAM_LEAF,
                ItemID.GRIMY_MARRENTILL,
                ItemID.GRIMY_TARROMIN,
                ItemID.GRIMY_HARRALANDER,
                ItemID.RANARR_WEED,
                ItemID.GRIMY_IRIT_LEAF,
                ItemID.GRIMY_AVANTOE,
                ItemID.GRIMY_KWUARM,
                ItemID.GRIMY_CADANTINE,
                ItemID.GRIMY_LANTADYME,
                ItemID.GRIMY_DWARF_WEED,

                ItemID.SNAPE_GRASS_SEED,
                ItemID.RANARR_SEED,
                ItemID.SNAPDRAGON_SEED,
                ItemID.CADANTINE_SEED,
                ItemID.DWARF_WEED_SEED,
                ItemID.TORSTOL_SEED,

                ItemID.EYE_OF_NEWT,
                ItemID.POTATO_CACTUS,
                ItemID.WHITE_BERRIES,
                ItemID.WINE_OF_ZAMORAK,

                ItemID.GRIMY_RANARR_WEED,
                ItemID.AMULET_OF_GLORY_UNCHARGED,
                ItemID.SKILLS_NECKLACE,
                ItemID.GRUBBY_KEY,


                ItemID.RANGING_POTION3,
                ItemID.RANGING_POTION2,
                ItemID.RANGING_POTION1,

                ItemID.PRAYER_POTION3,
                ItemID.PRAYER_POTION2,
                ItemID.PRAYER_POTION1,
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;


        EntranceWebNode staircaseEnter = new EntranceWebNode(new Tile(1669, 3567, 0), "Staircase", "Climb-down");

        EntranceWebNode staircaseExit = new EntranceWebNode(new Tile(1798, 9967, 0), "Staircase", "Climb-up");

        BasicWebNode inside = new BasicWebNode(1799, 9970);

        WebFinder wf = WebFinder.getWebFinder();
        wf.getNearest(staircaseEnter, 15).addDualConnections(staircaseEnter);
        staircaseExit.addDualConnections(staircaseEnter);
        inside.addDualConnections(staircaseExit);
        wf.addWebNodes(staircaseEnter, staircaseExit, inside);

        // add path to druids
        Tile[] path = {
                new Tile(1800, 9974, 0),
                new Tile(1801, 9976, 0),
                new Tile(1803, 9978, 0),
                new Tile(1806, 9978, 0),
                new Tile(1809, 9977, 0),
                new Tile(1810, 9975, 0),
                new Tile(1810, 9971, 0),
                new Tile(1810, 9967, 0),
                new Tile(1810, 9962, 0)
        };
        for (Tile tile : path) {
            wf.createAndAddNode(tile);
        }


        tree.addChildren(
                new ReactionSettingsFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("Lamp handler"),

                new GetMembershipBranch().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new EmptyDeathsCoffer().setSimpleName("Empty grave"),

                // prayer training
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget)
                        .setSimpleName("Prayer training: " + tree.getSettings().prayerTarget),

                new Fractal(() -> !PaidQuest.ANIMAL_MAGNETISM.isFinished() && Skills.getRealLevel(Skill.RANGED) >= 30 && tree.getSettings().rangeTarget >= 30)
                        .setSimpleName("Get Avas")
                        .addChildren(
                                new ConfigurableMeleeTraining(() -> Combat.getCombatLevel() < 20
                                        || Skills.getRealLevel(Skill.HITPOINTS) < 10)
                                        .setSimpleName("Melee training"),

                                new XMarksTheSpot().setSimpleName("X marks the spot"),
                                new ClientOfKourend().setSimpleName("Client of Kourend"),
                                new ConfigurableRangeTraining(() -> Skills.getRealLevel(Skill.HITPOINTS) < 30 || Skills.getRealLevel(Skill.RANGED) < 30)
//                                        .setDefenceTarget(settings.defenceTarget)
                                        .setSimpleName("Range Sandcrabs"),
                                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Fire making for slayer"),
                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                new RestlessGhost().setSimpleName("Restless ghost"),
                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                new PriestInPeril().setSimpleName("Priest in peril"),
                                new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                        ),
                new LeaveAvaRoom().setSimpleName("Ava room"),
                // combat / range training
                new ConfigurableRangeTraining(() -> tree.getSettings().loadout.mode == Skill.RANGED && Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget)
                        .setSimpleName("Range training"),

                new ConfigurableMeleeTraining(() -> tree.getSettings().loadout.mode == Skill.ATTACK && Skills.getRealLevel(Skill.ATTACK) < tree.getSettings().attackTarget)
                        .setSimpleName("Melee training"),


                // x marks to spot to access kourend
                new XMarksTheSpot().setSimpleName("X marks spot"),

                new MuleOff().setSimpleName("Mule off"),
                new GetMoreAvas().setSimpleName("Get more ava"),

                new Fractal(() -> (!UNDEAD_DRUID_AREA.contains(Players.getLocal()) && !tree.getSettings().loadout.isFulfilled())
                        || (!Inventory.contains(ItemID.LOBSTER) || (ItemVariants.PRAYER_POTION.getItem() == null && Skill.PRAYER.getBoostedLevel() == 0)))
                        .setInventoryLoadout(tree.getSettings().loadout.inventoryLoadout)
                        .setEquipmentLoadout(tree.getSettings().loadout.equipmentLoadout)
                        .setPrependLogic(() -> {
                            PrayerUtils.disableAll();
                            return false;
                        })
                        .setSimpleName("Get loadout"),

                GenericCombatBranch.builder()
                        .dropSupplier(() -> Inventory.get(ItemID.LOBSTER))
                        .area(UNDEAD_DRUID_AREA)
                        .mobFilter(x -> UNDEAD_DRUID_AREA.contains(x)
                                && x.getName().equals("Undead Druid")
                                && x.getHealthPercent() > 0
                                && (!x.isInCombat() || x.isInteracting(Players.getLocal())))
                        .prayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MAGIC, tree.getSettings().loadout.mode == Skill.RANGED ? PVMUtil.getBestRangePray() : PVMUtil.getBestMeleePray()})

                        .flickPrayers(tree.getSettings().shouldFlick)

                        .addPotion(ItemVariants.RANGE_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3)
                        .addPotion(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 3)
                        .addPotion(ItemVariants.STRENGTH_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 5)

                        .lootFilter(
                                x -> UNDEAD_DRUID_AREA.contains(x)
                                        && (x.getItem().getLivePrice() * x.getAmount() > Math.max(PVMUtil.getCheapest().getLivePrice(), 1000) || x.getItem().isNoted())
                        )

                        // when ranged get at least a 1 tile gap from druid
                        .extraFightLogic(tree.getSettings().loadout.mode == Skill.RANGED ? () -> {
                            Character currentTarget = Players.getLocal().getInteractingCharacter();
                            if (currentTarget != null && currentTarget.getSurroundingArea(1).contains(Players.getLocal())) {
                                Logger.info("Get gap from undead druid");
                                Area meleeDistance = currentTarget.getSurroundingArea(1);
                                Area ourRadius = Players.getLocal().getSurroundingArea(2);
                                if (Walking.shouldWalk())
                                    Arrays.stream(ourRadius.getTiles()).filter(x -> !meleeDistance.contains(x))
                                            .min(Comparator.comparingDouble(Tile::distance))
                                            .ifPresent(Walking::walkExact);
                                return true;
                            }
                            return false;
                        } : null)


                        .build()
                        .init()
                        .setSimpleName("Undead Druids")
                        .setAcceptCondition(() -> true)
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
        return "cCUndeadDruidFarm";
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
        if (!UNDEAD_DRUID_AREA.contains(Players.getLocal())) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!UNDEAD_DRUID_AREA.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) return;

        grossGp += incoming.getLivePrice() * quantity;
    }

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossGp += item.getLivePrice() * item.getAmount();
    }

    int deathCount = 0;

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            deathCount++;
        }
    }
}
