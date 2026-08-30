package org.dreambot.behaviour.method.revs.behaviour;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.WebNodeType;
import org.dreambot.api.methods.walking.web.node.impl.teleports.MagicTeleport;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class ExitRevs extends Fractal {
    final int maxLoot;
    final int maxKillsPerTrip;

    public ExitRevs(int maxLoot, int maxKillsPerTrip) {
        this.maxLoot = maxLoot;
        this.maxKillsPerTrip = maxKillsPerTrip;
    }

    @Getter
    @Setter
    private static boolean forceLeave;

    @Override
    public boolean isValid() {
        if (!Combat.isInWild()) forceLeave = false;
        return forceLeave || (Combat.isInWild() && (inventoryValue() >= maxLoot));
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
                    return (x.getLivePrice()) * x.getAmount();
                })
                .sum() + LootingBag.value()
                ;
    }

    @Override
    public int onLoop() {
        WebFinder.getWebFinder().disableTeleport(MagicTeleport.LUMBRIDGE_HOME_TELEPORT);
        // todo void this check if you have hard wilderness diaries
        if (Players.getLocal().isInCombat()) WebFinder.getWebFinder().disableWebNodeType(WebNodeType.TELEPORT_NODE);

        Walking.walk(BankLocation.EDGEVILLE);
        WebFinder.getWebFinder().enableWebNodeType(WebNodeType.TELEPORT_NODE);
        WebFinder.getWebFinder().enableTeleport(MagicTeleport.LUMBRIDGE_HOME_TELEPORT);
        return ReactionGenerator.getQuick();
    }
}
