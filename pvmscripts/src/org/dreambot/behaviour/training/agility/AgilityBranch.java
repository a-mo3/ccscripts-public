package org.dreambot.behaviour.training.agility;


import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.function.Supplier;

public class AgilityBranch extends Fractal {
    public AgilityBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Agility");
        addChildren(
                new PickupMOG(),
//                new GnomeLeaf(),
                new GnomeFractal()
                        .setEquipmentLoadout(new EquipmentLoadout().setStrict(true))
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemVariants.RING_OF_WEALTH)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                        )
                        .setSimpleName("Gnome"),
                new DraynorFractal()
                        .setSimpleName("Draynor")
                        .setEquipmentLoadout(new EquipmentLoadout().setStrict(true))
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemVariants.RING_OF_WEALTH)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                        ),
                new RestlessGhost().setSimpleName("Restless ghost"),
                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                new PriestInPeril().setSimpleName("Priest in peril"),
                new CanifisFractal(() -> Skills.getRealLevel(Skill.AGILITY) < 60)
                        .setSimpleName("Canifis")
                        .setEquipmentLoadout(new EquipmentLoadout().setStrict(true))
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemVariants.RING_OF_WEALTH)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                        ),
                // todo add falador @ 50 once node problem is sorted
                new SeersAgilityFractal(() -> true)
                        .setSimpleName("Seers")
                        .setEquipmentLoadout(new EquipmentLoadout().setStrict(true))
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemVariants.RING_OF_WEALTH)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                        )
        );
    }

    private static Area DRAYNOR = new Area(3072, 3286, 3109, 3234, 1);

    public static boolean onDraynorCourse() {
        DRAYNOR.setZ(1);
        if (DRAYNOR.contains(Players.getLocal())) return true;
        DRAYNOR.setZ(2);
        if (DRAYNOR.contains(Players.getLocal())) return true;
        DRAYNOR.setZ(3);
        if (DRAYNOR.contains(Players.getLocal())) return true;
        return false;
    }

    private static Area CANAFIS = new Area(3464, 3515, 3522, 3461);

    public static boolean onCanifisCourse() {
        CANAFIS.setZ(1);
        if (CANAFIS.contains(Players.getLocal())) return true;
        CANAFIS.setZ(2);
        if (CANAFIS.contains(Players.getLocal())) return true;
        CANAFIS.setZ(3);
        if (CANAFIS.contains(Players.getLocal())) return true;
        return false;
    }
}
