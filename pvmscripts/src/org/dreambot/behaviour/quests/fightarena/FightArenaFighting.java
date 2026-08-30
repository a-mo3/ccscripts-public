package org.dreambot.behaviour.quests.fightarena;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.map.Region;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.NPCUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class FightArenaFighting extends Fractal {

    public FightArenaFighting(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Fight");

        this.setInventoryLoadout(new InventoryLoadout()
                // dont need mins here because we wont consume until a later fractal
                .addItem(ItemID.AIR_RUNE, 4, 500)
                .addItem(ItemID.CHAOS_RUNE, 1, 150)
                .addItem(ItemVariants.PRAYER_POTION, 1, 8)
                .addItem(ItemID.SHARK, 1, 10)
        );
        this.setEquipmentLoadout(new EquipmentLoadout()
//                .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                        .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                        .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                        .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                        .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
        );
    }

    Tile safespot = new Tile(2598, 3162, 0);

    @Override
    public int onLoop() {
        if (Magic.getAutocastSpell() != Normal.FIRE_BOLT) Magic.setAutocastSpell(Normal.FIRE_BOLT);

        NPC hengrad = NPCs.closest("Hengrad");
        if (!Client.isDynamicRegion() && hengrad != null && hengrad.distance() < 7) {
            log("Hengrad");
            if (Dialogues.inDialogue()) {
                log("Talk to hengrad");
                Dialog.solve();
                return ReactionGenerator.getNormal();
            }

            hengrad.interact("Talk-to");
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            return ReactionGenerator.getNormal();
        }

        if (Combat.getHealthPercent() < 50) {
            log("Eat");
            Inventory.interact(ItemID.SHARK);
            return ReactionGenerator.getNormal();
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) < 10) {
            ItemVariants.PRAYER_POTION.getItem().interact("Drink");
            return ReactionGenerator.getNormal();
        }

        boolean inMeleeRange = Players.getLocal().getCharactersInteractingWithMe()
                .stream().anyMatch(x -> x.distance() < 2);
        Prayers.toggle(inMeleeRange, Prayer.PROTECT_FROM_MELEE);

        // walk to jaunt to recover if not in instance

        if (!safespot.equals(Players.getLocal().getTile()) && !Client.isDynamicRegion()) {
            if (Walking.shouldWalk()) Walking.walkExact(safespot);
            return ReactionGenerator.getQuick();
        }

        if (Client.isDynamicRegion()) {
            if (Dialogues.inDialogue()) {
                log("Dialog");
                Dialog.solve("");
                return ReactionGenerator.getNormal();
            }

            Tile instanceSafe = Region.toInstance(safespot).get(0);
            if (instanceSafe != null && !instanceSafe.equals(Players.getLocal().getTile())) {
                log("Walking to isntance safe");
                Walking.walkExact(instanceSafe);
                return ReactionGenerator.getNormal();
            }
        }

        Character mob = Players.getLocal().getCharacterInteractingWithMe();
        Character tgt = Players.getLocal().getInteractingCharacter();
        if (mob != null && (tgt == null || !tgt.equals(mob))) {
            log("Attacking mob");
            mob.interact("Attack");
        }

        if (mob == null && tgt == null) {
            log("Attack boss");
            NPC boss = NPCs.closest(x -> x.hasAction("Attack"));
            if (boss != null) boss.interact();
        }

        return ReactionGenerator.getNormal();
    }
}
