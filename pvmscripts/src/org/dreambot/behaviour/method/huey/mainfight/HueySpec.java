package org.dreambot.behaviour.method.huey.mainfight;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.huey.HueyData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.List;

public class HueySpec extends TickDecision {
    final boolean shouldSpec;

    public HueySpec(boolean shouldSpec) {
        this.shouldSpec = shouldSpec;
        setSimpleName("Special attack");
    }

    List<Integer> weapon = Arrays.asList(
            ItemID.SARACHNIS_CUDGEL,
            ItemID.SARADOMIN_SWORD,
            ItemID.DUAL_MACUAHUITL
    );

    @Override
    public boolean evaluate() {
        if (shouldSpec && Combat.getSpecialPercentage() >= 30) {
            // can spec, switch to claws and spec
            if (Inventory.contains(ItemID.BURNING_CLAWS)) {
                Inventory.interact(ItemID.BURNING_CLAWS);
            }
            Combat.toggleSpecialAttack(true);
        } else {
            // if in tail phase we want to use glacials if they're in inv, because of the 1-9 clamping on damage rolls when crush
            NPC tail = NPCs.closest("Hueycoatl tail");
            if (HueyData.isInHueyFight() && tail != null) {
                if (Inventory.contains(ItemID.GLACIAL_TEMOTLI)) {
                    log("Equip glacial temotli");
                    Equipment.equip(EquipmentSlot.WEAPON, ItemID.GLACIAL_TEMOTLI);
                }

                if (Inventory.contains(ItemID.DUAL_MACUAHUITL)) {
                    log("Equip dual macs");
                    Equipment.equip(EquipmentSlot.WEAPON, ItemID.DUAL_MACUAHUITL);
                }
                return false;
            }
            // cannot spec, switch back to main weapon
            if (!Equipment.contains(x -> weapon.contains(x.getId()))) {
                log("Equip regular weapon");
                Inventory.interact(x -> weapon.contains(x.getId()));
            }
        }
        return false;
    }
}
