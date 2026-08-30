package org.dreambot.behaviour.training.agility;


import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class DraynorFractal extends Fractal {
//    EquipmentLoadout teleports = new EquipmentLoadout()
//            .addAmulet(ItemVariant.SKILLS_NECKLACE.minCharges(2))
//            .addRing(ItemVariant.RING_OF_WEALTH.minCharges(2))
//            .addHands(ItemVariant.COMBAT_BRACELET.minCharges(2));

    private final Area DRAYNOR = new Area(3070, 3293, 3115, 3232);
    private final Area COURSE_START = new Area(3103, 3276, 3106, 3282, 0);
    private final Area FIRST_ROOF = new Area(3096, 3276, 3102, 3282, 3);
    private final Area SECOND_ROOF = new Area(3093, 3272, 3087, 3278, 3);
    private final Area THIRD_ROOF = new Area(3095, 3264, 3088, 3269, 3);
    private final Area FOURTH_ROOF = new Area(3087, 3256, 3088, 3262, 3);
    private final Area FIFTH_ROOF = new Area(3095, 3253, 3087, 3255, 3);
    private final Area FINAL_ROOF = new Area(3096, 3255, 3101, 3262, 3);

    List<AgilityStage> stages = Arrays.asList(
            new AgilityStage(
                    () -> Players.getLocal().getZ() == 0,
                    () -> Players.getLocal().getZ() == 3,
                    () -> GameObjects.closest("Rough wall"),
                    "Climb"
            ),
            new AgilityStage(
                    () -> FIRST_ROOF.contains(Players.getLocal()),
                    () -> SECOND_ROOF.contains(Players.getLocal()) && !Players.getLocal().isMoving(),
                    () -> GameObjects.closest("Tightrope"),
                    "Cross"
            ),
            new AgilityStage(
                    () -> SECOND_ROOF.contains(Players.getLocal()),
                    () -> THIRD_ROOF.contains(Players.getLocal()),
                    () -> GameObjects.closest("Tightrope"),
                    "Cross"
            ),
            new AgilityStage(
                    () -> THIRD_ROOF.contains(Players.getLocal()),
                    () -> FOURTH_ROOF.contains(Players.getLocal()),
                    () -> GameObjects.closest("Narrow wall"),
                    "Balance"
            ),
            new AgilityStage(
                    () -> FOURTH_ROOF.contains(Players.getLocal()),
                    () -> FIFTH_ROOF.contains(Players.getLocal()),
                    () -> GameObjects.closest("Wall"),
                    "Jump-up"
            ),
            new AgilityStage(
                    () -> FIFTH_ROOF.contains(Players.getLocal()),
                    () -> FINAL_ROOF.contains(Players.getLocal()),
                    () -> GameObjects.closest("Gap"),
                    "Jump"
            ),
            new AgilityStage(
                    () -> FINAL_ROOF.contains(Players.getLocal()),
                    () -> Players.getLocal().getZ() == 0,
                    () -> GameObjects.closest("Crate"),
                    "Climb-down"
            )
    );

    public DraynorFractal(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    public DraynorFractal() {
    }

    @Override
    public boolean isValid() {
        if (this.acceptCondition != null) return acceptCondition.get();
        return Skills.getRealLevel(Skill.AGILITY) < 40;
    }

    @Override
    public int onLoop() {
//        if (!teleports.isFulfilled()) {
//            new EquipmentLoadoutEvent(teleports)
//                    .setBuyRemainder(true)
//                    .execute();
//            return ReactionGenerator.getNormal();
//        }

        if (!AreaUtils.containsIgnorePlane(DRAYNOR, Players.getLocal().getTile())) {
            if (Walking.shouldWalk(8)) Walking.walk(COURSE_START.getCenter());
            return ReactionGenerator.getNormal();
        }

        Sleep.sleep(300, 1900);
        for (AgilityStage stage : stages) {
            if (stage.isValid()) return stage.onLoop();
        }
        return ReactionGenerator.getNormal();
    }
}
