package org.dreambot.behaviour.method.spindel;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.scriptdata.CalvarionSettings;
import org.dreambot.scriptdata.SpindelSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class LootSpindel extends Fractal {


    // the loot we pick up
    public static final int[] PICKABLE_LOOT = new int[]{
            ItemID.DRAGON_2H_SWORD,
            ItemID.DRAGON_PICKAXE,
            ItemID.SUPER_RESTORE4,
            ItemID.VOIDWAKER_GEM,
            ItemID.FANGS_OF_VENENATIS,
            ItemID.TREASONOUS_RING,

            ItemID.PRAYER_POTION3,
            ItemID.PRAYER_POTION2,
            ItemID.PRAYER_POTION1,

            ItemID.STAMINA_POTION3,
            ItemID.STAMINA_POTION2,
            ItemID.STAMINA_POTION1,

            ItemID.RANGING_POTION3,
            ItemID.RANGING_POTION2,
            ItemID.RANGING_POTION1,

            ItemID.DARK_CRAB,
            ItemID.SUPER_RESTORE4,
            ItemID.RUNE_PICKAXE,
            ItemID.RUNE_KNIFE,
            ItemID.RUNE_2H_SWORD,
            ItemID.CHAOS_RUNE,
            ItemID.BLOOD_RUNE,
            ItemID.DEATH_RUNE,
            ItemID.DIAMOND_BOLTS_E,
            ItemID.CANNONBALL,
            ItemID.UNCUT_DIAMOND,
            ItemID.GOLD_ORE,
            ItemID.MAGIC_LOGS,
            ItemID.LIMPWURT_ROOT,
            ItemID.ONYX_BOLT_TIPS,
            ItemID.RED_SPIDERS_EGGS,
            ItemID.UNCUT_DRAGONSTONE,
            ItemID.UNCUT_RUBY,
            ItemID.GRIMY_SNAPDRAGON,
            ItemID.UNICORN_HORN,
            ItemID.ANTIDOTE4_5952,
            ItemID.MAGIC_SEED,
            ItemID.PALM_TREE_SEED,
            ItemID.BLIGHTED_SUPER_RESTORE4,
            ItemID.COMBAT_POTION2,
    };

    public static final List<Integer> droppables = Arrays.asList(
            ItemID.VIAL,
            ItemID.BLIGHTED_MANTA_RAY
    );

    private static boolean shouldUseLootingBag() {
        return ScriptManager.getScriptManager().getCurrentScript().getSDNName().toLowerCase().contains("spinde")
                ? SettingsRepository.findInstanceOf(new SpindelSettings()).useLootingBag : SettingsRepository.findInstanceOf(new CalvarionSettings()).useLootingBag;
    }

    public static Filter<GroundItem> lootFilter = x -> (x.getId() == ItemID.LOOTING_BAG_CLOSED && shouldUseLootingBag())
            || x.getAmount() * x.getItem().getLivePrice() > 2500 || Arrays.stream(PICKABLE_LOOT).anyMatch(i -> i == x.getId());

    @Override
    public boolean isValid() {
        GroundItem loot = GroundItems.closest(lootFilter);
        return loot != null;
    }

    @Override
    public int onLoop() {
        GroundItem loot = GroundItems.closest(lootFilter);
        if (loot == null || !loot.exists()) {
            Logger.info("No loot");
            return ReactionGenerator.getQuick();
        }

        if (Inventory.isFull()) {
            if (Inventory.contains(x -> droppables.contains(x.getId()))) {
                Logger.info("Drop something to make space for loot");
                Inventory.drop(x -> droppables.contains(x.getId()));
            } else {
                Logger.info("Nothing to drop and a full inv, time to go");
                return SpindelAntiPk.leaveSpindel();
            }
            return ReactionGenerator.getQuick();
        }

        loot.interact("Take");
        return ReactionGenerator.getQuick();
    }
}
