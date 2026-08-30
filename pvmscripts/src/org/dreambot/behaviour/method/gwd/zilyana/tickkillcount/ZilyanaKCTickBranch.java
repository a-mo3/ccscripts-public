package org.dreambot.behaviour.method.gwd.zilyana.tickkillcount;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.behaviour.method.gwd.zilyana.tickzilyanafight.KillZilyanaPotionDecision;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.ZilyanaSettings;

import java.util.function.Supplier;

public class ZilyanaKCTickBranch extends TickFractal {
    public ZilyanaKCTickBranch(Supplier<Boolean> acceptCondition, ZilyanaSettings settings) {
        super(acceptCondition);
        setSimpleName("Get zil kc");

        this.inventoryLoadout = settings.loadout.inventoryLoadout;
        this.equipmentLoadout = settings.loadout.equipmentLoadout;
        this.loadoutCondition = () -> (ItemVariants.SARADOMIN_BREW.getItem() == null || ItemVariants.SUPER_RESTORE.getItem() == null)
                || Players.getLocal().getZ() == 0
                || Players.getLocal().getY() < 4000;

        addChildren(
                new GoToZilKCDecision(),
                new ZilyanaKCTickPrayer().setSimpleName("Pray flick"),
                // should be pretty much the same as the fight
                new KillZilyanaPotionDecision().setSimpleName("Potion decision"),
                new ZilyanaKCAttack().setSimpleName("Attack Zil KC")
        );
    }
}
