package org.dreambot.behaviour.method.huey;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.behaviour.method.huey.mainfight.*;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickSetCombatIndex;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.scriptdata.HueycoatlSettings;

import java.util.Arrays;
import java.util.function.Supplier;

public class HueyMainBranch extends TickFractal {
    public HueyMainBranch(Supplier<Boolean> acceptCondition, HueycoatlSettings settings) {
        // we pass down loadout for mage only mechanics
        super(acceptCondition);
        setSimpleName("Huey main");

        // add nodes to huey coatl main
        Tile[] path = {
                new Tile(1528, 3282, 0),
                new Tile(1527, 3277, 0),
                new Tile(1524, 3273, 0),
                new Tile(1518, 3270, 0),
                new Tile(1512, 3270, 0),
                new Tile(1510, 3273, 0),
                new Tile(1511, 3281, 0)
        };

        WebFinder wf = WebFinder.getWebFinder();
        Arrays.stream(path).forEach(wf::createAndAddNode);

        addChildren(
                new HueySpec(settings.useBurningClawsSpec).setSimpleName("Spec"),
                new HueyToggleRun().setSimpleName("Toggle run"),
//                new HueyAttackStyles(settings).setSimpleName("Switch to str"),
                new TickSetCombatIndex()
                        .addWeapon(ItemID.SARADOMIN_SWORD, 2),

                new HueySetAutocast(settings.loadout).setSimpleName("Set Autocast"),
                new HueyPotionDecision().setSimpleName("Pot up"),
                new HueyTailPrayDecision(settings.loadout, settings.safePray).setSimpleName("Main prayer decision"),
                new HueyEatDecision().setSimpleName("Eat"),
                new HueyLoot().setSimpleName("Loot"),
                HueyLightningWatch.getInstance().setSimpleName("Lightning"),
                // dodge wave
                new HueyWaveDodge(settings.loadout).setSimpleName("Wave dodge"),
                // attack huey, this will get into the area
                new HueyMainAttack(settings.loadout).setSimpleName("Heuy main attack")
        );
    }
}
