package org.dreambot.behaviour.quests.trollstronghold;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
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
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.VarbitRequirement;
import org.dreambot.fractals.util.VarplayerRequirement;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class CellKeys extends Fractal {
    public CellKeys(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SHARK, 1, 10)
                .addItem(ItemVariants.PRAYER_POTION, 3, 3)
                .setEnabledCondition(() -> ItemVariants.PRAYER_POTION.getItem() == null)
                .addItem(ItemID.CHAOS_RUNE, 1, 250)
                .addItem(ItemID.AIR_RUNE, 4, 500)
        ;

        setEquipmentLoadout(new EquipmentLoadout()
                .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE));
    }

    Area ROCK_THROW_AREA = new Area(
            new Tile(2879, 3692, 0),
            new Tile(2884, 3696, 0),
            new Tile(2894, 3701, 0),
            new Tile(2904, 3697, 0),
            new Tile(2907, 3702, 0),
            new Tile(2901, 3710, 0),
            new Tile(2876, 3700, 0));

    Tile t = new Tile(2832, 10078, 0);

    VarbitRequirement freedEadgar = new VarbitRequirement(0, 1);
    VarplayerRequirement freedGodric = new VarplayerRequirement(317, 40);
    int eadgarGateID = 3765;
    int godricGateID = 3767;

    @Override
    public int onLoop() {
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


        log(t.distance() + " t dist");
        if (t.distance() > 12 || Players.getLocal().getZ() == 2) {
            log("walk");
            Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
            if (Walking.shouldWalk()) {
                Walking.walk(t);
            }
            return ReactionGenerator.getNormal();
        }

        // free eadgar
        if (freedEadgar.isNotComplete()) {
            log("Free eadgar");
            // kill edgars troll, Berry
            if (!Inventory.contains(ItemID.CELL_KEY_2)) {
                log("Get cell key 2");
                GroundItem key = GroundItems.closest(ItemID.CELL_KEY_2);
                if (key != null) {
                    log("Take cell key 2");
                    key.interact("Take");
                    return ReactionGenerator.getNormal();
                }

                NPC berry = NPCs.closest("Berry");
                if (berry == null) {
                    log("Cant find berry");
                    return ReactionGenerator.getNormal();
                }

                Character tgt = Players.getLocal().getInteractingCharacter();
                if (tgt == null || !tgt.equals(berry)) {
                    log("Attack berry");
                    berry.interact("Attack");
                }
                return ReactionGenerator.getNormal();
            }
            // open eadgars cell door

            GameObject gate = GameObjects.closest(eadgarGateID);
            if (gate != null) {
                log("Open eadgar gate");
                gate.interact("Unlock");
            }
            return ReactionGenerator.getNormal();
        }
        Prayers.toggle(Players.getLocal().isInCombat(), Prayer.PROTECT_FROM_MELEE);

        // freee godric
        if (!freedGodric.check()) {
            log("Free godric");
            // kill edgars troll, Berry
            if (!Inventory.contains(ItemID.CELL_KEY_1)) {
                log("Get cell key 1");
                GroundItem key = GroundItems.closest(ItemID.CELL_KEY_1);
                if (key != null) {
                    log("Take cell key 1");
                    key.interact("Take");
                    return ReactionGenerator.getNormal();
                }

                NPC twig = NPCs.closest("Twig");
                if (twig == null) {
                    log("Cant find twig");
                    return ReactionGenerator.getNormal();
                }

                Character tgt = Players.getLocal().getInteractingCharacter();
                if (tgt == null || !tgt.equals(twig)) {
                    log("Attack berry");
                    twig.interact("Attack");
                }
                return ReactionGenerator.getNormal();
            }
            // open eadgars cell door

            GameObject gate = GameObjects.closest(godricGateID);
            if (gate != null) {
                log("Open eadgar gate");
                gate.interact("Unlock");
            }
            return ReactionGenerator.getNormal();

        }

        return ReactionGenerator.getNormal();
    }
}
