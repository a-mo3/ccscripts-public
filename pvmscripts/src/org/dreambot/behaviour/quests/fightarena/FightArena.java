package org.dreambot.behaviour.quests.fightarena;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.training.magic.MagicBranch;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PVMUtil;

// we are using this mainly for NMZ, so its okay to require prayer for it instead of just safespots imo
public class FightArena extends Fractal {
    public FightArena() {
        super(() -> !PaidQuest.FIGHT_ARENA.isFinished());

        this.paintArraySupplier = () -> new String[]{
                "State " + getState()
        };

        setSimpleName("Fight Arena");
        addChildren(
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43).setSimpleName("Min 43 prayer"),
                new MagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 35).setSimpleName("Min 35 magic"),

                new TalkToFractal(() -> getState() == 0, new Tile(2565, 3199, 0), () -> NPCs.closest("Lady Servil"))
                        .setDialogueOptions("Yes.", "help")
                        .setInventoryLoadout(new InventoryLoadout()
                                // dont need mins here because we wont consume until a later fractal
                                .addItem(ItemID.AIR_RUNE, 500)
                                .addItem(ItemID.CHAOS_RUNE, 150)
                                .addItem(ItemID.COINS_995, 5)
                                .addItem(ItemVariants.PRAYER_POTION, 8, 8)
                                .addItem(ItemID.SHARK, 10)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                                .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                        )
                        .setSimpleName("Start"),

                new TalkToFractal(() -> !Equipment.containsAll(ItemID.KHAZARD_HELMET, ItemID.KHAZARD_ARMOUR),
                        new Tile(2613, 3189, 0),
                        () -> GameObjects.closest("Chest"))
                        .setInteraction("Search")
                        .setPrependLogic(() -> {
                            if (Inventory.contains(ItemID.KHAZARD_ARMOUR, ItemID.KHAZARD_HELMET)) {
                                log("Equipping khazard kit");
                                Equipment.equip(EquipmentSlot.CHEST, ItemID.KHAZARD_ARMOUR);
                                Equipment.equip(EquipmentSlot.HAT, ItemID.KHAZARD_HELMET);
                                return true;
                            }

                            if (Inventory.isFull()) {
                                log("inv full drop cheapest");
                                PVMUtil.dropCheapest();
                            }
                            return false;
                        })
                        .setSimpleName("Khazard kit"),

                new TalkToFractal(() -> getState() == 2, new Tile(2615, 3143, 0), () -> NPCs.closest("Head guard"))
                        .setSimpleName("Guard"),

                new TalkToFractal(() -> getState() == 3 && !Inventory.contains(ItemID.KHALI_BREW),
                        new Tile(2567, 3140, 0), () -> NPCs.closest("Khazard barman"))
                        .setDialogueOptions("like a Khali brew")
                        .setSimpleName("Buy brew"),

                new TalkToFractal(() -> getState() == 3, new Tile(2615, 3143, 0), () -> NPCs.closest("Head guard"))
                        .setSimpleName("Give brew to Guard"),

                new TalkToFractal(() -> getState() < 6, new Tile(2617, 3167, 0), () -> GameObjects.closest(80))// 80 is the lil neighbours cell
                        .setInteraction("Open")
                        .setSimpleName("Free lil neighbour"),

                new TalkToFractal(() -> getState() >= 12, new Tile(2565, 3199, 0), () -> NPCs.closest("Lady Servil"))
                        .setSimpleName("Finish"),

                new FightArenaFighting(() -> true)
        );
    }

    private int getState() {
        return PaidQuest.FIGHT_ARENA.getConfigValue();
    }
}
