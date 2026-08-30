package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.Antiban;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.misc.AdvStandardCombat;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.RefillRosewoodBlowpipe;
import org.dreambot.behaviour.misc.tickcombat.GenericCombatBranch;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.animalmagnetism.util.LeaveAvaRoom;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
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
import org.dreambot.behaviour.wilddiary.EasyWildernessDiary;
import org.dreambot.behaviour.wilddiary.HardWildernessDiary;
import org.dreambot.behaviour.wilddiary.MediumWildernessDiary;
import org.dreambot.discordwebhook.AutoProggy;
import org.dreambot.discordwebhook.scouter.ScoutFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllEquipmentEvent;
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.pktrie.PKTrie;
import org.dreambot.scriptdata.AvianSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class AviansiesScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<AvianSettings> tree = new FractalRoot<>(new AvianSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    final Area AVIANS = new Area(
            new Tile(3022, 10143, 0),
            new Tile(3033, 10149, 0),
            new Tile(3040, 10158, 0),
            new Tile(3032, 10166, 0),
            new Tile(3022, 10168, 0),
            new Tile(3008, 10157, 0));
    public int deathCount = 0;

    private AvianSettings getSettings() {
        return SettingsRepository.getSetting(getScriptName(), new AvianSettings());
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.AMULET_OF_GLORY,
                ItemID.ADAMANTITE_BAR
        };

        Logger.info("Init");
        tree.setSimpleName("cCAvians");
        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));

        MuleOff.LOOT = new int[]{
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
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        new EasyWildernessDiary();
        new MediumWildernessDiary(); // create this to add all the needed webnodes

        tree.addChildren(
                new GetMembershipBranch().setSimpleName("Get Membership"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < 60)
                        .setStyleSupplier(() -> {
                            int atk = Skills.getRealLevel(Skill.ATTACK);
                            int str = Skills.getRealLevel(Skill.STRENGTH);
                            int def = Skills.getRealLevel(Skill.DEFENCE);
                            if (atk >= 30 && def >= 30) return CombatStyle.STRENGTH;
                            if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                            if (atk <= def) return CombatStyle.ATTACK;
                            return CombatStyle.DEFENCE;
                        })
                        .setSimpleName("Melee training"),
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < getSettings().prayerTarget).setSimpleName("get 45 prayer"),
                new GetOff330(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel()),
                new ConfigurableRangeTraining(() -> Skills.getRealLevel(Skill.RANGED) < getSettings().rangeTarget, 40)
//                        .setDefenceTarget(40)
                        .setSimpleName("Range training")
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                            }
                            return false;
                        }),


                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Burn logs need it for slayer"),
                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                new RestlessGhost().setSimpleName("Restless ghost"),
                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                new PriestInPeril().setSimpleName("Priest in peril"),
                new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                new AnimalMagnetismBranch().setSimpleName("Animal Magnetism"),

                new HardWildernessDiary(() -> tree.getSettings().doHardWildernessDiary).setSimpleName("Hard diary"),
                new GetMoreAvas().setSimpleName("More avas"),

                new LeaveAvaRoom().setSimpleName("Leave avas"),

                new MuleOff()
                        .setSimpleName("Mule off"),

                new RefillRosewoodBlowpipe(),

                GenericCombatBranch.builder()
                        .dropSupplier(() -> Inventory.get(ItemID.JUG_OF_WINE, ItemID.JUG, ItemID.LOBSTER, ItemID.BLIGHTED_MANTA_RAY, ItemID.SWORDFISH))
                        .area(AVIANS)
                        .mobFilter(x -> x.getName().toLowerCase().contains("avian") && AVIANS.contains(x) && x.getLevel() <= 92)

                        .prayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MISSILES, PVMUtil.getBestRangePray()})
                        .flickPrayers(true)

                        .lootFilter(x -> (ItemVariants.LOOTING_BAG.contains(x.getId()) || x.getId() == ItemID.ECUMENICAL_KEY || AVIANS.contains(x))
                                && x.getId() != ItemID.BONES
                                && (x.getItem().getLivePrice() * x.getAmount()) > getSettings().minLootValue
                        )

                        .addPotion(ItemVariants.RANGE_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3)
                        .addPotion(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 3)
                        .addPotion(ItemVariants.STRENGTH_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 5)

                        .addPotion(ItemVariants.BLIGHTED_SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() < 10)

                        .style(CombatStyle.RANGED_RAPID)
                        .build()
                        .init()

                        .setLoadoutCondition(() -> !Combat.isInWild() || !Inventory.contains(ItemID.JUG_OF_WINE, ItemID.LOBSTER, ItemID.BLIGHTED_MANTA_RAY))

                        .setInventoryLoadout(getSettings().loadout.inventoryLoadout)
                        .setEquipmentLoadout(getSettings().loadout.equipmentLoadout)

                        .setSimpleName("Avian (flick)")
                        .setAcceptCondition(() -> tree.getSettings().flick),

                // only kill the lower level avians
                new AdvStandardCombat(() -> true, AVIANS,
                        () -> NPCs.closest(x -> x.getName().toLowerCase().contains("avian") && AVIANS.contains(x) && x.getLevel() <= 92),
                        ItemID.SWORDFISH)

                        .setFlickBoostTiming(600, Prayer.EAGLE_EYE)
                        .setStyleSupplier(() -> Skills.getRealLevel(Skill.DEFENCE) >= getSettings().defTarget ? CombatStyle.RANGED_RAPID : CombatStyle.RANGED_DEFENCE)
                        .setLootFilter(x -> (ItemVariants.LOOTING_BAG.contains(x.getId()) || x.getId() == ItemID.ECUMENICAL_KEY || AVIANS.contains(x))
                                && x.getId() != ItemID.BONES
                                && (x.getItem().getLivePrice() * x.getAmount()) > getSettings().minLootValue
                        )

                        .setInventoryLoadout(getSettings().loadout.inventoryLoadout)
                        .setEquipmentLoadout(getSettings().loadout.equipmentLoadout)

                        .setPrependLogic(() -> {
                            if (Equipment.count(ItemID.RUNE_DART) > 220) new BankAllEquipmentEvent().execute();
                            if (Equipment.count(ItemID.ADAMANT_DART) > 220) new BankAllEquipmentEvent().execute();

                            if (!SpecialWalker.leaveAvasRoom()) return true;

                            if (AVIANS.contains(Players.getLocal()) && Skills.getBoostedLevel(Skill.RANGED) <= Skills.getRealLevel(Skill.RANGED) + 5) {
                                Item pot = ItemVariants.RANGE_POTION.getItem();
                                if (pot != null && pot.interact("Drink")) {
                                    Antiban.sleepUntil(() -> Skills.getBoostedLevel(Skill.RANGED) > Skills.getRealLevel(Skill.RANGED) + 5, 1400);
                                    return true;
                                }
                            }
                            return false;
                        })
                        .setSimpleName("Avians")
        );

//        new AIAntiban();
    }

    boolean hasLootInBag = false;
    boolean hasLoadedTrie = false;
    Timer trieRefresh = new Timer(60 * 1000 * 45);
    boolean shouldHop = false;

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (!Combat.isInWild() && !Bank.isCached()) {
            if (!SpecialWalker.leaveAvasRoom()) return ReactionGenerator.getNormal();
            if (Bank.isOpen()) Bank.close();
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        }

        if (trieRefresh.finished() || !hasLoadedTrie) {
            PKTrie.refreshPkerList();
            trieRefresh.reset();
            hasLoadedTrie = true;
        }


        if (Inventory.contains(ItemID.LOOTING_BAG_CLOSED)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Opening looting bag");
            Inventory.interact(ItemID.LOOTING_BAG_CLOSED, "Open");
            Antiban.sleepUntil(() -> !Inventory.contains(ItemID.LOOTING_BAG_OPENED), 1800);
            return ReactionGenerator.getNormal();
        }

        NPC boulderObstacle = (NPCs.closest(x -> x.getName().equals("<col=00ffff>Boulder</col>")));
        if (boulderObstacle != null && boulderObstacle.distance() < 5 && boulderObstacle.getX() < Players.getLocal().getX()) {
            Logger.info("Moving boulder");
            boulderObstacle.interact("Move");
            Antiban.sleepUntil(() -> boulderObstacle.getX() > Players.getLocal().getX(), 4400);
            return ReactionGenerator.getNormal();
        }

        if (Combat.isInWild()) {
            Player pker = Players.closest(x -> canAttackMe(x) && PKTrie.checkString(x.getName()));
            if (pker != null) {
                Logger.info("Pker - tp out");
                shouldHop = true;
                if (Equipment.contains(ItemVariants.AMULET_OF_GLORY.getIds())) {
                    Logger.info("TP to edgeville - antipk");
                    Equipment.interact(EquipmentSlot.AMULET, "Edgeville");
                    return ReactionGenerator.getNormal();
                }
            }
        }

        if (shouldHop && !Combat.isInWild()) {
            WorldHopper.hopWorld(Worlds.getRandomWorld(w ->
                    w.isNormal() && w.isMembers() && w.getMinimumLevel() == 0
            ));

            shouldHop = false;
        }

        if (AVIANS.contains(Players.getLocal())) hasLootInBag = true;

        if (hasLootInBag) {
            if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null) {
                Logger.info("Emptying looting bags");
                new EmptyLootingBagEvent().executed();
                hasLootInBag = false;
            }
        }
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    public static boolean canAttackMe(Player threat) {
        if (threat.getName().equals(Players.getLocal().getName())) return false;
        int threatLvl = threat.getLevel();
        int mylvl = Combat.getCombatLevel();
        int wildernessLvl = Combat.getWildernessLevel();
        return threatLvl >= (mylvl - wildernessLvl) && threatLvl <= (wildernessLvl + mylvl);
    }

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
                "cCAvians: " + runtime.formatTime(),
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Time Until Mule off: " + muleOff,
                String.format("Earned %s (%s / hr))", df.format(grossGp), df.format(runtime.getHourlyRate(grossGp))),
                "target: " + target,
                "inCombat " + Players.getLocal().isInCombat(),
                "interactingWith " + Players.getLocal().getInteractingCharacter(),
                "interactingWithMe " + Players.getLocal().getCharacterInteractingWithMe(),
                "Deaths: " + deathCount,
                "Keys: " + OwnedItems.count(ItemID.ECUMENICAL_KEY) // todo delete or rate limit
        };
    }

    @Override
    public String getScriptName() {
        return "cCAviansiesFarm";
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
        Logger.info("item added");
        if (!Combat.isInWild()) return;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("item changed");
        if (!Combat.isInWild()) return;
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
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;
        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onScriptPaint(Graphics g) {
//        Ents.lastAttackedFrom.forEach(x -> g.drawPolygon(x.getPolygon()));
    }

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            shouldHop = true;
            deathCount++;
        }
    }
}
