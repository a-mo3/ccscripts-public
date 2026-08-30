package org.dreambot.behaviour.method.gwd.bandos.tickbandosfight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.gwd.bandos.BandosConsts;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.BandosSettings;

import java.util.function.Supplier;

public class TickKillBandosBranch extends TickFractal {
    public TickKillBandosBranch(Supplier<Boolean> acceptCondition, BandosSettings settings) {
        super(acceptCondition);
        setSimpleName("Kill Bandos");
        addChildren(
                new BandosAntiCrash(settings).setSimpleName("Anti crash"),
                new GetIntoBandosFight(settings).setSimpleName("Go to bandos"),
                new KillBandosPrayerDecision().setSimpleName("bandos prayer"),
                // potion is how we eat
                new KillBandosPotionDecision().setSimpleName("bandos potions"),
                new BandosWalkDecision().setSimpleName("Walk & atk"),
                new BandosExpensiveLootDecision().setSimpleName("Expensive Loot bandos"),
                new BandosLootDecision().setSimpleName("Loot bandos"),
                new BandosGuards().setSimpleName("Guards")
        );

        this.paintArraySupplier = () -> {
            NPC bandos = NPCs.closest(BandosConsts.BANDOS);

            return new String[]{
                    "Range Cycle " + KillBandosPrayerDecision.rangeMinionTiming,
                    "Magic Cycle " + KillBandosPrayerDecision.magicMinionTiming,
                    "Bandos Dist " + (bandos == null ? 0 : bandos.distance()),
                    BandosWalkDecision.targetTile == null ? " - " : BandosWalkDecision.targetTile.toString(),
                    "Target " + Players.getLocal().getInteractingCharacter(),
                    "First enter " + BandosWalkDecision.firstEnter,
                    "First lap  " + BandosWalkDecision.firstLap,
                    "Play atk " + BandosWalkDecision.lastPlayerAttack + " " + (Client.getGameTick() - BandosWalkDecision.lastPlayerAttack),
                    "Consume - ",
                    "Bandos spawn - ",
                    "Bandos Atk Skip " + BandosWalkDecision.hasBandosAttacked + " " + (bandos == null ? " " : bandos.getAnimation()),
                    ""
            };
        };
    }
}
