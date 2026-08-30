package org.dreambot.behaviour.method.artio;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.behaviour.method.spindel.LootSpindel;
import org.dreambot.behaviour.method.spindel.range.RangeSpindelBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.LootingBag;

import java.util.Arrays;
import java.util.List;

public class ExitArtioWithLoot extends Fractal {
    final int maxLoot;
    final int maxKillsPerTrip;

    public ExitArtioWithLoot(int maxLoot, int maxKillsPerTrip) {
        this.maxLoot = maxLoot;
        this.maxKillsPerTrip = maxKillsPerTrip;
    }

    @Override
    public boolean isValid() {
        return Combat.isInWild() && (inventoryValue() >= maxLoot
                || (RangeSpindelBranch.killsThisTrip >= maxKillsPerTrip && GroundItems.closest(LootSpindel.lootFilter) == null));
    }

    public static List<Integer> ignoredIds = Arrays.asList(
            ItemID.VIGGORAS_CHAINMACE,
            ItemID.URSINE_CHAINMACE,
            ItemID.WEBWEAVER_BOW,
            ItemID.CRAWS_BOW,
            ItemID.BLIGHTED_MANTA_RAY,
            ItemID.BLIGHTED_KARAMBWAN,
            ItemID.SARACHNIS_CUDGEL
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

    @Override
    public int onLoop() {
        return LeaveArtio.leaveArtio();
    }
}
