package org.dreambot.behaviour.quests.icegloves;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
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
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class KillIceQueen extends Fractal {
    public KillIceQueen(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemVariants.PRAYER_POTION, 3, 3)
                .setEnabledCondition(() -> ItemVariants.PRAYER_POTION.getItem() == null)
                .addItem(ItemID.LOBSTER, 1, 10)
                .addItem(ItemID.CAMELOT_TELEPORT, 1, 4)
                .addItem(ItemID.MIND_RUNE, 300)
                .setEnabledCondition(() -> BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 50 || !Inventory.contains(ItemID.MIND_RUNE))
                .addItem(ItemID.AIR_RUNE, 600)
                .setEnabledCondition(() -> BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 50 || !Inventory.contains(ItemID.AIR_RUNE))
                .addItem(ItemID.BRONZE_PICKAXE)
        ;

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
        ;
    }

    Area ICE_QUEEN = new Area(2859, 9963, 2871, 9959);

    @Override
    public int onLoop() {
        if (Widgets.isOpen()) {
            log("Closing banks");
            Widgets.closeAll();
        }

        GroundItem gloves = GroundItems.closest(ItemID.ICE_GLOVES);
        if (gloves != null) {
            gloves.interact("Take");
            return ReactionGenerator.getNormal();
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) < 3) {
            log("Drink prayer pot");
            Inventory.interact(ItemVariants.PRAYER_POTION.getItem(), "Drink");
            Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) > 3, 1200);
        }

        NPC npc = NPCs.closest(x -> x.getName().toLowerCase().contains("ice"));
        Prayers.toggle(Players.getLocal().isInCombat() || npc != null && npc.distance() < 5, Prayer.PROTECT_FROM_MELEE);

        if (Skills.getBoostedLevel(Skill.HITPOINTS) < 10) {
            log("Eat Lobster");
            Inventory.interact(ItemID.LOBSTER, "Eat");
            Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.HITPOINTS) > 10, 1200);
        }

        if (Combat.isAutoRetaliateOn()) {
            log("Turn off auto retaliate");
            Combat.toggleAutoRetaliate(false);
            return ReactionGenerator.getNormal();
        }

        if (!Magic.isAutocasting() || Magic.getAutocastSpell() != Normal.FIRE_STRIKE) {
            log("Set to fire strike");
            Magic.setAutocastSpell(Normal.FIRE_STRIKE);
            return ReactionGenerator.getNormal();
        }


        if (!ICE_QUEEN.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(ICE_QUEEN);
            return ReactionGenerator.getNormal();
        }

        NPC iceQueen = NPCs.closest("Ice queen");
        if (iceQueen != null && !Players.getLocal().isInteracting(iceQueen)) {
            log("Attack ice queen");
            iceQueen.interact("Attack");
        }
        return ReactionGenerator.getNormal();
    }
}
