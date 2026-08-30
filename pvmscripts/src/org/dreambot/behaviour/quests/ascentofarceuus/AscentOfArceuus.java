package org.dreambot.behaviour.quests.ascentofarceuus;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.quests.ClientOfKourend;
import org.dreambot.behaviour.quests.XMarksTheSpot;
import org.dreambot.behaviour.training.hunter.HunterBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.VarbitRequirement;

public class AscentOfArceuus extends Fractal {
    public AscentOfArceuus() {
        super(() -> !PaidQuest.THE_ASCENT_OF_ARCEUUS.isFinished());
        ArceuusNodes.init();

        // 5:53:19 pm: Varbit 7859: 0 -> 1
        VarbitRequirement checkedGrave = new VarbitRequirement(7859, 1);
        VarbitRequirement foundTrack1 = new VarbitRequirement(7860, 1);
        VarbitRequirement foundTrack2 = new VarbitRequirement(7861, 1);
        VarbitRequirement foundTrack3 = new VarbitRequirement(7862, 1);
        VarbitRequirement foundTrack4 = new VarbitRequirement(7863, 1);
        VarbitRequirement foundTrack5 = new VarbitRequirement(7864, 1);
        setSimpleName("Ascent Of Arceuus");

        this.paintArraySupplier = () -> new String[]{
                "State " + getState()
        };
        addChildren(
                // this quest is reasonably doable with 10hp
                new XMarksTheSpot().setSimpleName("X marks the spot"),
                new ClientOfKourend().setSimpleName("Client of Kourend"),
                new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < 12).setSimpleName("12 Hunter"),

                new TalkToFractal(() -> getState() == 0, new Tile(1698, 3742, 0), () -> NPCs.closest("Mori"))
                        .setDialogueOptions("Yes", "can I do to help", "should let someone know", "Of course")
                        .setSimpleName("Start @ Mori"),

                new TalkToFractal(() -> getState() == 1, new Tile(1620, 3673, 1), () -> NPCs.closest("Councillor Andrews"))
                        .setDialogueOptions("Death")
                        .setSimpleName("Talk to Andrews"),

                new TalkToFractal(() -> getState() == 2, new Tile(1698, 3742, 0), () -> NPCs.closest("Mori"))
                        .setDialogueOptions("do now?")
                        .setSimpleName("Return to Mori"),

                // todo enter tower of magic and kill the jaunts states 3-4
                new KillTorturedSouls(() -> getState() <= 4),

                new TalkToFractal(() -> getState() <= 6, new Tile(1580, 3821, 1), () -> NPCs.closest("Lord Trobin Arceuus"))
                        .setSimpleName("Talk to Trobin"),


                new TalkToFractal(() -> getState() == 7, new Tile(1312, 10211, 0), () -> NPCs.closest("Kaal-Ket-Jor"))
                        .setDoReachCheck(false)
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.SKILLS_NECKLACE)
                        )
                        .setSimpleName("Talk to Kaal Ket Jor"),

                // do tracking 8-9
                new Fractal(() -> getState() <= 9)
                        .addChildren(
                                new TalkToFractal(checkedGrave::isNotComplete, new Tile(1348, 3738), () -> GameObjects.closest("Ancient Grave"))
                                        .setDoReachCheck(false)
                                        .setInteraction("Inspect")
                                        .setSimpleName("Grave"),

                                new TalkToFractal(foundTrack1::isNotComplete, new Tile(1335, 3743), () -> GameObjects.closest(34622))
                                        .setInteraction("Inspect")
                                        .setSimpleName("Track 1"),

                                new TalkToFractal(foundTrack2::isNotComplete, new Tile(1317, 3750), () -> GameObjects.closest(34623))
                                        .setInteraction("Inspect")
                                        .setSimpleName("Track 2"),

                                // same id and close might need tile check
                                new TalkToFractal(foundTrack3::isNotComplete, new Tile(1305, 3750),
                                        () -> GameObjects.closest(x -> x.getId() == 34623 && x.getX() == 1305))
                                        .setInteraction("Inspect")
                                        .setSimpleName("Track 3"),

                                new TalkToFractal(foundTrack4::isNotComplete, new Tile(1288, 3751, 0), () -> GameObjects.closest(34621))
                                        .setInteraction("Inspect")
                                        .setSimpleName("Track 4"),

                                new TalkToFractal(foundTrack5::isNotComplete, new Tile(1286, 3738, 0), () -> GameObjects.closest(34624))
                                        .setInteraction("Inspect")
                                        .setSimpleName("Track 5"),

                                new KillTrappedSoul(() -> true)
                        )
                        .setSimpleName("Tracking"),

                // todo kill trapped soul 10
                new KillTrappedSoul(() -> getState() == 10),

                new TalkToFractal(() -> getState() == 11, new Tile(1312, 10211, 0), () -> NPCs.closest("Kaal-Ket-Jor"))
                        .setDoReachCheck(false)
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.SKILLS_NECKLACE)
                        )
                        .setSimpleName("Talk to Kaal Ket Jor"),

//                new TalkToFractal(() -> getState() == 12, new Tile(1710, 3882, 0), () -> GameObjects.closest(34626))
//                        .setInteraction("Inspect")
//                        .setSimpleName("Search rock"),
                new SearchRocks(() -> getState() == 12).setSimpleName("Search rocks"),

                new TalkToFractal(() -> getState() == 13, new Tile(1580, 3821, 1), () -> NPCs.closest("Lord Trobin Arceuus"))
                        .setSimpleName("Finish @ Trobin")

        );
    }

    private int getState() {
        return PaidQuest.THE_ASCENT_OF_ARCEUUS.getConfigValue();
    }
}
