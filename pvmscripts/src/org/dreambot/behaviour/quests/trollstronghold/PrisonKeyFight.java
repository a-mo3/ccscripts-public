package org.dreambot.behaviour.quests.trollstronghold;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class PrisonKeyFight extends Fractal {
    public PrisonKeyFight(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SHARK, 1, 10)
                .addItem(ItemVariants.PRAYER_POTION, 1, 3)
                .addItem(ItemID.CHAOS_RUNE, 1, 250)
                .addItem(ItemID.AIR_RUNE, 4, 500)
                .addItem(ItemID.PRISON_KEY)
                .setEnabledCondition(() -> OwnedItems.contains(ItemID.PRISON_KEY))
        ;

        setEquipmentLoadout(new EquipmentLoadout()
                .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE));

        setSimpleName("Get P key");
    }

    Tile GENERALS_LOCATION = new Tile(2830, 10086, 2);
    Area ROCK_THROW_AREA = new Area(
            new Tile(2879, 3692, 0),
            new Tile(2884, 3696, 0),
            new Tile(2894, 3701, 0),
            new Tile(2904, 3697, 0),
            new Tile(2907, 3702, 0),
            new Tile(2901, 3710, 0),
            new Tile(2876, 3700, 0));

    @Override
    public int onLoop() {
        if (Widgets.isOpen()) {
            Widgets.closeAll();
        }
        Prayers.toggle(ROCK_THROW_AREA.contains(Players.getLocal()), Prayer.PROTECT_FROM_MISSILES);

        if (Combat.getHealthPercent() < 50) {
            log("Eat");
            Inventory.interact(ItemID.SHARK);
            return 600;
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) < 10) {
            ItemVariants.PRAYER_POTION.getItem().interact("Drink");
        }

        if (Magic.getAutocastSpell() != Normal.FIRE_BOLT) {
            log("Set fire bolt");
            Magic.setAutocastSpell(Normal.FIRE_BOLT);
            return ReactionGenerator.getNormal();
        }

        GroundItem key = GroundItems.closest(ItemID.PRISON_KEY);
        if (key != null) {
            log("Taking key");
            key.interact("Take");
            return ReactionGenerator.getNormal();
        }

        boolean attackedByGeneral = Players.getLocal()
                .getCharactersInteractingWithMe()
                .stream().anyMatch(x -> "Troll general".equals(x.getName()));
        Prayers.toggle(attackedByGeneral || GENERALS_LOCATION.distance() < 8, Prayer.PROTECT_FROM_MELEE);
        if (!attackedByGeneral) {
            // todo maybe atk general, hes agressive
            if (!Combat.isAutoRetaliateOn()) {
                log("Toggle auto reta");
                Combat.toggleAutoRetaliate(true);
            }

            // walk to his loc
            log("Going to general");
            if (Walking.shouldWalk()) Walking.walk(GENERALS_LOCATION);
            return ReactionGenerator.getNormal();
        }


        return ReactionGenerator.getNormal();
    }
}
