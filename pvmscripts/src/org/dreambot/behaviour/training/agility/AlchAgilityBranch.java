package org.dreambot.behaviour.training.agility;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.magic.MagicBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.function.Supplier;

public class AlchAgilityBranch extends Fractal {
    Timer alchTime = new Timer(2800);

    public AlchAgilityBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Alch-Agility");
        addChildren(
                new MagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 55).setSimpleName("Get 55"),
                new PickupMOG(),
                new Fractal(() -> alchTime.finished() && Inventory.contains(ItemID.RUNE_ARROW) && Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY))
                        .setSimpleName("Alch")
                        .setPrependLogic(() -> {
                            if (Widgets.isOpen()) Widgets.closeAll();
                            Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY, Inventory.get(ItemID.RUNE_ARROW));
                            alchTime.reset();
                            return false;
                        }),
                new GnomeFractal()
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemVariants.RING_OF_WEALTH)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.NATURE_RUNE, 1, 1000)
                                .addItem(ItemID.RUNE_ARROW, 1, 1000)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                        )
                        .setSimpleName("Gnome"),
                new DraynorFractal()
                        .setSimpleName("Draynor")
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemVariants.RING_OF_WEALTH)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.NATURE_RUNE, 1, 1000)
                                .addItem(ItemID.RUNE_ARROW, 1, 1000)
                        ),
                new RestlessGhost().setSimpleName("Restless ghost"),
                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                new PriestInPeril().setSimpleName("Priest in peril"),
                new CanifisFractal(() -> Skills.getRealLevel(Skill.AGILITY) < 60)
                        .setSimpleName("Canifis")
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemVariants.RING_OF_WEALTH)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.NATURE_RUNE, 1, 1000)
                                .addItem(ItemID.RUNE_ARROW, 1, 1000)
                        ),
                // todo add falador @ 50 once node problem is sorted
                new SeersAgilityFractal(() -> true)
                        .setSimpleName("Seers")
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemVariants.RING_OF_WEALTH)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.NATURE_RUNE, 1, 1000)
                                .addItem(ItemID.RUNE_ARROW, 1, 1000)
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
