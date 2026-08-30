package org.dreambot.behaviour.method.corp.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.event.impl.ExperienceEvent;
import org.dreambot.api.script.listener.ExperienceListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.method.corp.CorpClient;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

/**
 * Should only spec if you have a dragon warhammer / bgs
 * if you only have one we should always spec with it
 * if you have both we should spec with whatever the team is relying on, or bgs
 * relying on meaning the spec count is <3 for dhw and <400dmg dealt with bgs
 * we will track the spec hit here for bgs damage
 */
public class CorpSpecDecision extends TickDecision implements ExperienceListener {
    public CorpSpecDecision() {
        Client.getInstance().addEventListener(this);
        setSimpleName("Corp spec");
    }


    @Override
    public boolean evaluate() {
        if (Combat.getSpecialPercentage() < 50) return false;

        if (Combat.getSpecialPercentage() >= 55 && Inventory.contains(ItemID.DARK_BOW)) {
            log("Doing dark bow spec");
            Inventory.interact(ItemID.DARK_BOW);
            Inventory.interact(ItemID.RUNE_ARROW);
            Combat.toggleSpecialAttack(true);
            return false;
        }

        if (CorpClient.getDHWSpecsLanded() < 3) {
            log("Need to land another dhw spec");
            if (Inventory.contains(ItemID.DRAGON_WARHAMMER)) {
                log("Equip DHW");
                Inventory.interact(ItemID.DRAGON_WARHAMMER);
                Combat.toggleSpecialAttack(true);
                return false;
            }
            if (Equipment.contains(ItemID.DRAGON_WARHAMMER)) {
                log("DWH spec again");
                Combat.toggleSpecialAttack(true);
                return false;
            }
        }

        if (CorpClient.getBGSDamageDelt() < 250) {
            if (Inventory.contains(ItemID.BANDOS_GODSWORD)) {
                log("Equip BGS");
                Inventory.interact(ItemID.BANDOS_GODSWORD);
                Combat.toggleSpecialAttack(true);
                return false;
            }

            if (Equipment.contains(ItemID.BANDOS_GODSWORD)) {
                log("BGS spec");
                Combat.toggleSpecialAttack(true);
                return false;
            }
        }

        return false;
    }

    @Override
    public void onGained(ExperienceEvent event) {
        if (event.getSkill() != Skill.HITPOINTS) return;
        if (Equipment.contains(ItemID.DRAGON_WARHAMMER)) {
            log("Recording a DWH hit");
            CorpClient.recordDWHHit();
        }

        if (Equipment.contains(ItemID.BANDOS_GODSWORD)) {
            log("Recording a bgs hit " + event.getChange());
            CorpClient.recordBGSHit((int) (event.getChange() * 0.75));
        }
    }
}
