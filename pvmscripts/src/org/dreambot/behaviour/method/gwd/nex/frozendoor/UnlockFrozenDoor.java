package org.dreambot.behaviour.method.gwd.nex.frozendoor;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class UnlockFrozenDoor extends Fractal {
    public UnlockFrozenDoor(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Go unlock door");


        equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                .addItem(EquipmentSlot.LEGS, ItemID.ZAMORAK_CHAPS)
                .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
                .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                ;

        inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.LAW_RUNE, 10, 20)
                .addItem(ItemID.FIRE_RUNE, 10, 20)
                .addItem(ItemID.SHARK, 1, 12)
                .setRefill(50)
                .addItem(ItemID.FROZEN_KEY_26356).enabledIfOwned()
                .addItem(ItemVariants.PRAYER_POTION, 1, 2)
                ;
    }

    Area DOOR = new Area(2881, 5282, 2886, 5277, 2);

    public static final Area ROCK_THROW_AREA = new Area(
            new Tile(2879, 3692, 0),
            new Tile(2884, 3696, 0),
            new Tile(2894, 3701, 0),
            new Tile(2904, 3697, 0),
            new Tile(2907, 3702, 0),
            new Tile(2901, 3710, 0),
            new Tile(2876, 3700, 0));

    @Override
    public int onLoop() {
//        if (!Bank.isCached()) {
//            Logger.info("Get cache");
//            if (Bank.open()) Bank.updateCache();
//            return ReactionGenerator.getNormal();
//        }

        if (Skill.PRAYER.getBoostedLevel() <= 2) ItemVariants.PRAYER_POTION.interact("Drink");
        if (Skill.HITPOINTS.getBoostedLevel() <= 30) Inventory.interact(ItemID.SHARK);

        Player lp = Players.getLocal();
        if (ROCK_THROW_AREA.contains(lp)) {
            log("Prot missle for troll rocks");
            Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
        } else if (lp.getCharactersInteractingWithMe().stream().anyMatch(x -> x.distance() < 3 && x.getName().toLowerCase().contains("wolf"))) {
            log("Pray against wolf");
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
        } else {
            PrayerUtils.disableAll();
        }

        if (Dialogues.inDialogue() || Client.isInCutscene() || Client.isDynamicRegion()) {
            log("Dialogues");
            Dialog.solve();
            return ReactionGenerator.getNormal();
        }

        if (!DOOR.contains(Players.getLocal())) {
            log("Go to door");
            if (Walking.shouldWalk()) Walking.walk(DOOR);
        } else {
            ObjectUtil.interact("Frozen Door");
        }

        return ReactionGenerator.getNormal();
    }
}
