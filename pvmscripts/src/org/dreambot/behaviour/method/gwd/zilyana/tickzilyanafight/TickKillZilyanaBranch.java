package org.dreambot.behaviour.method.gwd.zilyana.tickzilyanafight;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.ZilyanaSettings;

import java.util.function.Supplier;

public class TickKillZilyanaBranch extends TickFractal {
    public TickKillZilyanaBranch(Supplier<Boolean> acceptCondition, ZilyanaSettings settings) {
        super(acceptCondition);
        setSimpleName("Kill zil");
        addChildren(
                new GetIntoZilyanaFight(settings).setSimpleName("Go to zil"),
                new KillZilyanaPrayerDecision().setSimpleName("Zil prayer"),
                // potion is how we eat
                new KillZilyanaPotionDecision().setSimpleName("Zil potions"),
                new KillZillAttackDecision().setSimpleName("Zilyana attack"),
                new ZilyanaAntiCrash(settings).setSimpleName("Anti crash"),
                new ZilyanaWalkDecision().setSimpleName("Walk"),
                new ZilyanaGuards().setSimpleName("Guards"),
                new ZilyanaLootDecision().setSimpleName("Loot zil")
        );

        this.paintArraySupplier = () -> {
            NPC zil = NPCs.closest("Commander Zilyana");
            NPC starlight = NPCs.closest("Starlight");
            return new String[]{
                    String.format("B: %d R: %d P: %d S: %d", ItemVariants.SARADOMIN_BREW.getInventoryCount(),
                            ItemVariants.SUPER_RESTORE.getInventoryCount(),
                            ItemVariants.PRAYER_POTION.getInventoryCount(),
                            ItemVariants.STAMINA_POTION.getInventoryCount()
                    ),
                    "Bree cycle: " + KillZilyanaPrayerDecision.breeTickTiming,
                    "Growler Cycle: " + KillZilyanaPrayerDecision.growlerTickTiming,
                    "Current " + Client.getGameTick() % 5,
                    "Ping " + Worlds.getCurrent().getPing(),
                    String.format("Zil dist %.2f Ser: %.2f", zil == null ? 0 : zil.distance(), zil == null ? 0 : zil.getServerTile().distance()),
                    String.format("star dist %.2f Ser: %.2f", starlight == null ? 0 : starlight.distance(), starlight == null ? 0 : starlight.getServerTile().distance()),
            };
        };
    }
}
