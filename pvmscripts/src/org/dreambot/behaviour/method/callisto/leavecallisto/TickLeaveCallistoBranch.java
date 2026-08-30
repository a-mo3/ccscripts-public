package org.dreambot.behaviour.method.callisto.leavecallisto;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.behaviour.method.spindel.LootSpindel;
import org.dreambot.behaviour.misc.tickcombat.decisions.GenericTickEat;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.LootingBag;

import java.util.Arrays;
import java.util.List;

/**
 * this is triggered when out of food, or loot value is time to leave
 * this is not anti pk leaving
 */
public class TickLeaveCallistoBranch extends TickFractal {
    static List<Integer> food = Arrays.asList(
            ItemID.BLIGHTED_ANGLERFISH, ItemID.BLIGHTED_MANTA_RAY
    );

    public TickLeaveCallistoBranch(int maxLoot, CallistoLeaveMode leaveMode) {
        super(() -> (inventoryValue() >= maxLoot)
                || (Combat.isInWild() && Inventory.count(x -> food.contains(x.getId())) < 4
                && GroundItems.closest(LootSpindel.lootFilter) == null));
        setSimpleName("Leave Callisto");

        addChildren(
                new TickDrinkPotions()
                        .addPotion(ItemVariants.BLIGHTED_SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() < 10),

                new GenericTickEat().setAllowEat(() -> leaveMode != CallistoLeaveMode.SUICIDE),

                new TickDrinkPotions()
                        .addPotion(ItemVariants.STAMINA_POTION, () -> Walking.getRunEnergy() < 5)
                        .addPotion(ItemVariants.ANTI_DOTE_PP, () -> Combat.isPoisoned() || Combat.isEnvenomed()),

                new SuicideLeaveCallisto(leaveMode == CallistoLeaveMode.SUICIDE),
                new TickLeaveCallistoDecision()

        );
    }


    public static List<Integer> ignoredIds = Arrays.asList(
            ItemID.VIGGORAS_CHAINMACE,
            ItemID.URSINE_CHAINMACE,
            ItemID.WEBWEAVER_BOW,
            ItemID.CRAWS_BOW,
            ItemID.BLIGHTED_MANTA_RAY,
            ItemID.BLIGHTED_KARAMBWAN,
            ItemID.SARACHNIS_CUDGEL,
            ItemID.SARADOMIN_SWORD
    );

    public static int inventoryValue() {
        return Inventory.all()
                .stream()
                .mapToInt(x -> {
                    if (x == null) return 0;
                    if (ignoredIds.contains(x.getId())) return 0;
                    return (x.getLivePrice() + 1) * x.getAmount();
                })
                .sum() + LootingBag.value()
                ;
    }
}
