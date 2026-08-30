package org.dreambot.fractals.util;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;

import java.util.*;
import java.util.function.Predicate;

public class PVMUtil {
    public static Item getCheapest() {
        return Inventory.all()
                .stream()
                .filter(Objects::nonNull)
                .filter(Item::isTradable)
                .min(Comparator.comparingInt(Item::getLivePrice))
                .orElse(null);
    }

    public static Item getCheapestExcept(Integer... keep) {
        List<Integer> l = new ArrayList<>(Arrays.asList(keep));
        return getCheapestExcept(x -> x != null && l.contains(x.getId()));
    }

    public static Item getCheapestExcept(Predicate<Item> except) {
        return Inventory.all()
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> !except.test(x))
                .filter(Item::isTradable)
                .min(Comparator.comparingInt(Item::getLivePrice))
                .orElse(null);
    }

    public static void dropCheapest() {
        if (!Inventory.isFull())
            Logger.info("Dropping cheapest even though inventory is not full Slot empty: " + Inventory.getEmptySlots());
        Item cheapest = Inventory.all()
                .stream()
                .filter(Objects::nonNull)
                .filter(Item::isTradable)
                .min(Comparator.comparingInt(Item::getLivePrice))
                .orElse(null);
        Logger.info("Drop cheapest item " + cheapest);
        if (cheapest != null) Inventory.drop(cheapest.getId());
    }

    public static Tile[] attackableTiles(NPC npc, int size) {
        Tile southWest = npc.getTrueTile();
        Tile northEast = new Tile(southWest.getX() + size, southWest.getY() + size, southWest.getZ());
        List<Tile> attackable = new ArrayList<>();

        // loop from south to north, adding a eastern and western tile every time
        for (int i = southWest.getY(); i <= northEast.getY(); i++) {
            attackable.add(new Tile(northEast.getX() + 1, i, npc.getZ()));
            attackable.add(new Tile(southWest.getX() - 1, i, npc.getZ()));
        }
        // do the same for east
        for (int i = southWest.getX(); i <= northEast.getX(); i++) {
            attackable.add(new Tile(i, northEast.getY() + 1, npc.getZ()));
            attackable.add(new Tile(i, southWest.getY() - 1, npc.getZ()));
        }

        return attackable.toArray(attackable.toArray(new Tile[0]));
    }

    public static final int RIGOUR_UNLOCKED = 5451;
    public static final int AUGURY_UNLOCKED = 5452;

    public static Prayer getBestMagePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        if (lvl >= 77 && PlayerSettings.getBitValue(AUGURY_UNLOCKED) == 1) return Prayer.AUGURY;
        if (lvl >= 45) return Prayer.MYSTIC_MIGHT;
        return Prayer.MYSTIC_LORE;
    }

    public static Prayer getBestRangePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        if (lvl >= 74 && PlayerSettings.getBitValue(RIGOUR_UNLOCKED) == 1) return Prayer.RIGOUR;
        if (lvl >= 44) return Prayer.DEADEYE.isUnlocked() ? Prayer.DEADEYE : Prayer.EAGLE_EYE;
        return Prayer.HAWK_EYE;
    }

    public static Prayer getBestMeleePray() {
        if (isPietyUnlocked()) return Prayer.PIETY;
        return Prayer.ULTIMATE_STRENGTH;
    }

    public static boolean isPietyUnlocked() {
        return Skill.PRAYER.getLevel() >= 70 && PlayerSettings.getBitValue(3909) == 8 && Skill.DEFENCE.getLevel() >= 70;
    }

}
