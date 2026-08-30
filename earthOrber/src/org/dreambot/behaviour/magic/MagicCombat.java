package org.dreambot.behaviour.magic;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

public class MagicCombat extends Fractal {
    final Area COW_SAFESPOT = new Area(3248, 3264, 3252, 3255);
    final Area COWS = new Area(3251, 3271, 3262, 3255);
    int hpTarget;
    int defenceTarget;

    public MagicCombat(int hpTarget, int defenceTarget) {
        super(() -> Skills.getRealLevel(Skill.HITPOINTS) < hpTarget || Skills.getRealLevel(Skill.DEFENCE) < defenceTarget);
        this.defenceTarget = defenceTarget;
        this.hpTarget = hpTarget;
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
        ;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.CHAOS_RUNE, 1, 1600)
                .setStrict(true)
        ;


        this.paintArraySupplier = () -> new String[]{
                "Magic: " + Skills.getRealLevel(Skill.MAGIC),
                "HP: " + Skills.getRealLevel(Skill.HITPOINTS),
                "Defence: " + Skills.getRealLevel(Skill.DEFENCE)
        };
    }

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            Dialogues.continueDialogue();
            return ReactionGenerator.getNormal();
        }

        if (!COW_SAFESPOT.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(COW_SAFESPOT);
            return ReactionGenerator.getNormal();
        }

        if (Skills.getRealLevel(Skill.DEFENCE) < defenceTarget) {
            if (!Magic.isAutocastDefensive()) {
                Magic.setDefensiveAutocastSpell(Normal.WIND_BOLT);
                return ReactionGenerator.getNormal();
            }
        } else {
            if (!Magic.isAutocasting()) {
                Magic.setAutocastSpell(Normal.WIND_BOLT);
                return ReactionGenerator.getNormal();
            }
        }

        if (Players.getLocal().isInCombat()) {
            return ReactionGenerator.getNormal();
        }

        NPC cow = NPCs.closest(x -> COWS.contains(x) && x.getName().equals("Cow") && x.hasAction("Attack"));
        if (cow != null) {
            cow.interact("Attack");
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 2400);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
