package org.dreambot.behaviour.method.spindel.tickspindel;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;

import java.util.Arrays;
import java.util.List;

public class TickSpindelMeleeAttack extends TickDecision implements AnimationListener {
    public TickSpindelMeleeAttack() {
        setSimpleName("Range attack spindel");
        Client.getInstance().addEventListener(this);
    }

    List<Integer> meleeWeapons = Arrays.asList(
            ItemID.VIGGORAS_CHAINMACE,
            ItemID.URSINE_CHAINMACE,
            ItemID.SARACHNIS_CUDGEL
    );


    @Override
    public boolean evaluate() {
        // switch back to melee weapon after handling spiders
        if (!Equipment.contains(x -> meleeWeapons.contains(x.getId()))) {
            log("Switch to melee weapon");
            Equipment.equip(EquipmentSlot.WEAPON, x -> meleeWeapons.contains(x.getId()));
            return true;
        }

        if (Combat.getCombatStyle() != CombatStyle.STRENGTH) {
            // str on cudgel or chainmace is crush, using crush is important
            log("Set combat style to crush");
            Combat.setCombatStyle(CombatStyle.STRENGTH);
            return true;
        }

        NPC spindel = NPCs.closest("Spindel");
        if (spindel == null) {
            log("Cant attack, cant find spindel");
            return false;
        }

        Character tgt = Players.getLocal().getInteractingCharacter();
        if (!spindel.equals(tgt)) {
            log("Attack spindel");
            spindel.interact("Attack");
        }
        return false;
    }
}
