package org.dreambot.behaviour.method.gwd.zammy;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.map.Area;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.ZammySettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;

import java.util.Arrays;
import java.util.List;

public class ZammyConsts {
    public static final List<Integer> DROP_TABLE = Arrays.asList(
            ItemID.SARADOMIN_SWORD,
            ItemID.SARADOMINS_LIGHT,
            ItemID.ARMADYL_CROSSBOW,
            ItemID.GODSWORD_SHARD_1,
            ItemID.GODSWORD_SHARD_2,
            ItemID.GODSWORD_SHARD_3,
            ItemID.ADAMANT_PLATEBODY,
            ItemID.RUNE_DART,
            ItemID.RUNE_KITESHIELD,
            ItemID.RUNE_PLATESKIRT,
            ItemID.PRAYER_POTION4,
            ItemID.SARADOMIN_BREW3,
            ItemID.SUPER_RESTORE4,
            ItemID.COINS_995,
            ItemID.DIAMOND,
            ItemID.RANARR_SEED,
            ItemID.LAW_RUNE,
            ItemID.MAGIC_SEED,
            ItemID.GRIMY_RANARR_WEED
    );

    public static final List<Integer> ALCH_ITEMS = Arrays.asList(
            ItemID.ADAMANT_PLATEBODY,
            ItemID.RUNE_KITESHIELD,
            ItemID.RUNE_PLATESKIRT,
            ItemID.RUNE_2H_SWORD,
            ItemID.RUNE_BATTLEAXE,
            ItemID.RUNE_SQ_SHIELD,
            ItemID.RUNE_SWORD
    );

    public static InventoryLoadout brewInv = new InventoryLoadout()
            .addItem(ItemID.RUNE_DART, 50)
            .setEnabledCondition(() -> !Inventory.contains(ItemID.RUNE_DART))
            .addItem(ItemID.SARADOMIN_BREW4, 8)
            .setRefill(50)
            .addItem(ItemID.SUPER_RESTORE4, 10)
            .setRefill(50)
            .addItem(ItemID.STAMINA_POTION4, 6)
            .setRefill(40)
            .addItem(ItemID.ANTIDOTE4_5952, 2)
            .setRefill(40)
            .addItem(ItemID.RUNE_POUCH)
            ;

    public static final InventoryLoadout brewBlowpipeLoadout = new InventoryLoadout(brewInv)
            .removeItems(x -> x.getItemId() == ItemID.RUNE_DART)
            .addItem(ItemVariants.BLOWPIPE);

    public static final InventoryLoadout guthansBrewBlowpipeLoadout = new InventoryLoadout(brewBlowpipeLoadout)
            .addItem(ItemVariants.GUTHANS_CHEST)
            .addItem(ItemVariants.GUTHANS_SKIRT)
            .addItem(ItemVariants.GUTHANS_HELM)
            .addItem(ItemVariants.GUTHANS_SPEAR)
            ;

    public static List<Integer> primaryWeapons = Arrays.asList(
            ItemID.BOW_OF_FAERDHINEN,
            ItemID.DRAGON_CROSSBOW,
            ItemID.ARMADYL_CROSSBOW,
            ItemID.ODIUM_WARD
    );

    public static List<Integer> secondaryWeapons = Arrays.asList(
            ItemID.TOXIC_BLOWPIPE,
            ItemID.RUNE_DART
    );

}
