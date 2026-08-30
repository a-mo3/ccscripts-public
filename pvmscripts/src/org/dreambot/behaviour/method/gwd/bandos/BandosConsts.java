package org.dreambot.behaviour.method.gwd.bandos;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.BandosSettings;
import org.dreambot.scripts.BandosScript;
import org.dreambot.settings.fractalsettings.SettingsRepository;

import java.util.Arrays;
import java.util.List;

public class BandosConsts {
    public static final String BANDOS = "General Graardor";

    public static final String RANGE_MINION_NAME = "Sergeant Grimspike";
    public static final String MAGIC_MINION_NAME = "Sergeant Steelwill";
    public static final String MELEE_MINION_NAME = "Sergeant Strongstack";

    public static final int RANGE_ATTACK_ANIMATION = 7073;
    public static final int MAGIC_ATTACK_ANIMATION = 7071;
    public static final int MELEE_ATTACK_ANIMATION = 6154;

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
            .addItem(ItemID.HAMMER)
            .setRefill(6)
            .addItem(ItemID.RUNE_DART, 600)
            .setEnabledCondition(() -> !Inventory.contains(ItemID.RUNE_DART))
            .addItem(ItemID.SARADOMIN_BREW4, () -> SettingsRepository.findInstanceOf(new BandosSettings()).brewQuantity)
            .setRefill(50)
            .addItem(ItemID.SUPER_RESTORE4, () -> SettingsRepository.findInstanceOf(new BandosSettings()).restoreQuantity)
            .setRefill(50)
            .addItem(ItemID.PRAYER_POTION4, () -> SettingsRepository.findInstanceOf(new BandosSettings()).prayerQuantity)
            .setRefill(50)
            .addItem(ItemID.STAMINA_POTION4, () -> SettingsRepository.findInstanceOf(new BandosSettings()).staminaQuantity)
            .setRefill(40)
            .addItem(ItemID.ECUMENICAL_KEY)
            .setEnabledCondition(() -> OwnedItems.contains(ItemID.ECUMENICAL_KEY))
            .addItem(ItemID.RUNE_POUCH)
            .setEnabledCondition(() -> Players.getLocal().getY() < 3500 && !OwnedItems.contains(ItemID.TROLLHEIM_TELEPORT))
            .addItem(ItemID.TROLLHEIM_TELEPORT, () -> OwnedItems.count(ItemID.TROLLHEIM_TELEPORT))
            .setEnabledCondition(() -> Players.getLocal().getY() < 3500 && OwnedItems.contains(ItemID.TROLLHEIM_TELEPORT))
            // for BTP
            .addItem(ItemID.NATURE_RUNE, 100)
            .setEnabledCondition(() -> BandosScript.shouldUseBonesToPeaches && OwnedItems.contains(ItemID.TROLLHEIM_TELEPORT))
            .addItem(ItemID.MUD_BATTLESTAFF)
            .setEnabledCondition(() -> BandosScript.shouldUseBonesToPeaches && OwnedItems.contains(ItemID.TROLLHEIM_TELEPORT));

    public static final InventoryLoadout brewBlowpipeLoadout = new InventoryLoadout(brewInv)
            .removeItems(x -> x.getItemId() == ItemID.RUNE_DART)
            .addItem(ItemVariants.BLOWPIPE);

    public static final InventoryLoadout guthansBrewBlowpipeLoadout = new InventoryLoadout(brewBlowpipeLoadout)
            .addItem(ItemVariants.GUTHANS_CHEST)
            .addItem(ItemVariants.GUTHANS_SKIRT)
            .addItem(ItemVariants.GUTHANS_HELM)
            .addItem(ItemVariants.GUTHANS_SPEAR);

    public static List<Integer> primaryWeapons = Arrays.asList(
            ItemID.BOW_OF_FAERDHINEN,
            ItemID.DRAGON_CROSSBOW,
            ItemID.ARMADYL_CROSSBOW
    );

    public static List<Integer> secondaryWeapons = Arrays.asList(
            ItemID.TOXIC_BLOWPIPE,
            ItemID.RUNE_DART
    );


    /**
     * the fixed loadout we use in nex, that only aims for 1 kc for we being a fixed high brew count
     */
    public static InventoryLoadout specialNexBrewInv = new InventoryLoadout()
            .addItem(ItemID.HAMMER)
            .setRefill(6)
            .addItem(ItemID.RUNE_DART, 600)
            .setEnabledCondition(() -> !Inventory.contains(ItemID.RUNE_DART))
            .addItem(ItemID.SARADOMIN_BREW4, 15)
            .setRefill(50)
            .addItem(ItemID.SUPER_RESTORE4, 6)
            .setRefill(50)
            .addItem(ItemID.STAMINA_POTION4, () -> 2)
            .setRefill(40)
            .addItem(ItemID.ECUMENICAL_KEY)
            .setEnabledCondition(() -> OwnedItems.contains(ItemID.ECUMENICAL_KEY))
            .addItem(ItemID.RUNE_POUCH)
            .setEnabledCondition(() -> Players.getLocal().getY() < 3500 && !OwnedItems.contains(ItemID.TROLLHEIM_TELEPORT))
            .addItem(ItemID.TROLLHEIM_TELEPORT, () -> OwnedItems.count(ItemID.TROLLHEIM_TELEPORT))
            .setEnabledCondition(() -> Players.getLocal().getY() < 3500 && OwnedItems.contains(ItemID.TROLLHEIM_TELEPORT))
            // for BTP
            .addItem(ItemID.NATURE_RUNE, 100)
            .setEnabledCondition(() -> BandosScript.shouldUseBonesToPeaches && OwnedItems.contains(ItemID.TROLLHEIM_TELEPORT))
            .addItem(ItemID.MUD_BATTLESTAFF)
            .setEnabledCondition(() -> BandosScript.shouldUseBonesToPeaches && OwnedItems.contains(ItemID.TROLLHEIM_TELEPORT));
}
