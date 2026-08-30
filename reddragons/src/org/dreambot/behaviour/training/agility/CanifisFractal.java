package org.dreambot.behaviour.training.agility;


import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.fractals.Fractal;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class CanifisFractal extends Fractal {
    final Area DRAYNOR = new Area(3074, 3285, 3117, 3235);
    final Area CANIFIS = new Area(3466, 3511, 3520, 3462);

    final Area START_AREA = new Area(3504, 3490, 3508, 3486);
    final Area FIRST_ROOF = new Area(3503, 3498, 3511, 3488, 2);
    final Area SECOND_ROOF = new Area(3495, 3507, 3504, 3503, 2);
    final Area THIRD_ROOF = new Area(3485, 3505, 3493, 3498, 2);
    final Area FOURTH_ROOF = new Area(3472, 3501, 3480, 3490, 3);
    final Area FIFTH_ROOF = new Area(3476, 3487, 3484, 3481, 2);
    final Area SIXTH_ROOF = new Area(3486, 3479, 3504, 3468, 3);
    final Area FINAL_ROOF = new Area(3508, 3483, 3516, 3474, 2);


//    EquipmentLoadout teleports = new EquipmentLoadout()
//            .addAmulet(ItemVariant.SKILLS_NECKLACE.minCharges(2))
//            .addRing(ItemVariant.RING_OF_WEALTH.minCharges(2)).setStrict(false)
//            .addHands(ItemVariant.COMBAT_BRACELET.minCharges(2));

    List<AgilityStage> stages = Arrays.asList(
            new AgilityStage(
                    () -> START_AREA.contains(Players.getLocal()),
                    () -> FIRST_ROOF.contains(Players.getLocal()),
                    () -> GameObjects.closest("Tall tree"),
                    "Climb"
            ),
            new AgilityStage(
                    () -> FIRST_ROOF.contains(Players.getLocal()),
                    () -> SECOND_ROOF.contains(Players.getLocal()),
                    () -> GameObjects.closest("Gap"),
                    "Jump"
            ),
            new AgilityStage(
                    () -> SECOND_ROOF.contains(Players.getLocal()),
                    () -> THIRD_ROOF.contains(Players.getLocal()),
                    () -> GameObjects.closest(14845), // a gap but the other one is closer probably
                    "Jump"
            ),
            new AgilityStage(
                    () -> THIRD_ROOF.contains(Players.getLocal()),
                    () -> FOURTH_ROOF.contains(Players.getLocal()),
                    () -> GameObjects.closest(14848),
                    "Jump"
            ),
            new AgilityStage(
                    () -> FOURTH_ROOF.contains(Players.getLocal()),
                    () -> FIFTH_ROOF.contains(Players.getLocal()),
                    () -> GameObjects.closest("Gap"),
                    "Jump"
            ),
            new AgilityStage(
                    () -> FIFTH_ROOF.contains(Players.getLocal()),
                    () -> SIXTH_ROOF.contains(Players.getLocal()),
                    () -> GameObjects.closest("Pole-vault"),
                    "Vault"
            ),
            new AgilityStage(
                    () -> SIXTH_ROOF.contains(Players.getLocal()),
                    () -> FINAL_ROOF.contains(Players.getLocal()),
                    () -> GameObjects.closest("Gap"),
                    "Jump"
            ),
            new AgilityStage(
                    () -> FINAL_ROOF.contains(Players.getLocal()),
                    () -> Players.getLocal().getZ() == 0,
                    () -> GameObjects.closest("Gap"),
                    "Jump"
            )

    );

    @Override
    public boolean isValid() {
        return Skills.getRealLevel(Skill.AGILITY) < 99;
    }

    @Override
    public int onLoop() {
//        if (!teleports.isFulfilled()) {
//            Log.info("Withdrawing canifis loadout");
//            teleports.setStrict(true);
//            new EquipmentLoadoutEvent(teleports)
//                    .setBuyRemainder(true)
//                    .execute();
//            return ReactionGenerator.getNormal();
//        }

        Log.info("Smeep");
        if (AreaUtils.containsIgnorePlane(DRAYNOR, Players.getLocal().getTile())) {
            Log.info("Exit draynor");
            if (Walking.shouldWalk(8)) Walking.walk(BankLocation.GRAND_EXCHANGE.getTile());
            return ReactionGenerator.getNormal();
        }

        if (!AreaUtils.containsIgnorePlane(CANIFIS, Players.getLocal().getTile())) {
            if (!SpecialWalker.MORYTANIA.contains(Players.getLocal())) {
                Log.info("Speical walking morytania");
                SpecialWalker.enterMorytania();
                return ReactionGenerator.getNormal();
            }
            Log.info("walk to start of canifis");
            if (Walking.shouldWalk(8)) Walking.walk(START_AREA.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (Players.getLocal().getZ() == 0 && !START_AREA.contains(Players.getLocal())) {
            Log.info("walk to start of canifis (2)");
            if (Walking.shouldWalk(8)) Walking.walk(START_AREA.getCenter());
            return ReactionGenerator.getNormal();
        }

        for (AgilityStage stage : stages) {
            if (stage.isValid()) return stage.onLoop();
        }
        return ReactionGenerator.getNormal();
    }
}