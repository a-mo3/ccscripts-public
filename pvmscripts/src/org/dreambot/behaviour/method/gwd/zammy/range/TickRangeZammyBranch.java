package org.dreambot.behaviour.method.gwd.zammy.range;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.gwd.zammy.EnterZammyRoom;
import org.dreambot.behaviour.method.gwd.zammy.melee.TickZammyEat;
import org.dreambot.behaviour.method.gwd.zammy.TickZammyPrayer;
import org.dreambot.behaviour.method.gwd.zammy.ZammyCounters;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.ZammySettings;

import java.util.function.Supplier;

public class TickRangeZammyBranch extends TickFractal {

    public static int rotationIndex = 0;

    public TickRangeZammyBranch(Supplier<Boolean> acceptCondition, ZammySettings settings) {
        super(acceptCondition);
        setSimpleName("Zammy range");

        paintArraySupplier = () -> {

            NPC zam = NPCs.closest(ZammyCounters.ZAMMY_NAME);
            return new String[]{
                    "Zammy counter: " + ZammyCounters.zamCounter,
                    "Range counter: " + ZammyCounters.rangeCounter,
                    "Melee counter: " + ZammyCounters.meleeCounter,
                    "Magic counter: " + ZammyCounters.magicCounter,
                    "Our counter: " + ZammyCounters.ourCounter,
                    "Zam TT dist " + (zam == null ? "-" : zam.getTrueTile().distance()),
                    "Zam dist " + (zam == null ? "-" : zam.distance()),
                    "Tick " + Client.getGameTick(),
                    "Cycle " + Client.getGameCycle(),
            };
        };

        addChildren(
                new ZammyCounters().setSimpleName("Counters"),
                new EnterZammyRoom().setSimpleName("Enter zam"),

                new TickZammyPrayer().setSimpleName("Zam prayers"),

                new TickDrinkPotions()
                        .addPotion(ItemVariants.STAMINA_POTION, () -> Walking.getRunEnergy() < 5)
                        .addPotion(ItemVariants.ANTI_DOTE_PP, () -> Combat.isPoisoned() || Combat.isEnvenomed())
                        .addPotion(ItemVariants.SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() < 10),

                new TickZammyEat().setSimpleName("Eat"),
                new DropItemOnTile(),

                new ZammyRangeAttackDecision().setSimpleName("Attack"),
                new TickZammyLoot(),
                new TickZammyKillGuards().setSimpleName("Kill guards")
        );
    }
}
