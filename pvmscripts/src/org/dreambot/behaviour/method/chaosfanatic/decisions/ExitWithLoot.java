package org.dreambot.behaviour.method.chaosfanatic.decisions;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.scriptdata.ChaosFanaticSettings;

import java.util.Arrays;
import java.util.List;

public class ExitWithLoot extends TickDecision {

    final ChaosFanaticSettings settings;

    public ExitWithLoot(ChaosFanaticSettings settings) {
        super();
        this.settings = settings;
    }

    @Override
    public boolean evaluate() {
        boolean shouldLeave = !(inventoryValue() < settings.leaveAmount && Inventory.contains(ItemID.BLIGHTED_MANTA_RAY));
        // prayer check
        if (Skill.PRAYER.getBoostedLevel() == 0 && ItemVariants.BLIGHTED_SUPER_RESTORE.getItem() == null && ItemVariants.PRAYER_POTION.getItem() == null)
            shouldLeave = true;
        if (Equipment.isSlotEmpty(EquipmentSlot.ARROWS) && !Inventory.contains(ItemID.AMETHYST_ARROW,
                ItemID.MITHRIL_BOLTS,
                ItemID.RUNE_ARROW,
                ItemID.DIAMOND_BOLTS_E))
            shouldLeave = true;
        if (!shouldLeave) {
            // todo loot here incase we just dropped our last manta ray
            return false;
        }
//
//        if (settings.suicideToBank) {
//            log("Kill self");
//
//            NPC fanatic = NPCs.closest("Chaos Fanatic");
//            // attack
//            if (fanatic == null) {
//                log("No fanatic");
//                return true;
//            }
//
//            Character target = Players.getLocal().getInteractingCharacter();
//            if (target == null) {
//                log("Attack fanatic");
//                fanatic.interact();
//            }
//
//            return true;
//        }
//
        // todo anti pk protections

        log("Over leave item value, leave or increase your setting " + settings.leaveAmount + "  " + inventoryValue());
        if (Combat.isInWild()) {
            log("In combat run home");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
        }
        return true;
    }

    public static List<Integer> ignoredIds = Arrays.asList(
            ItemID.ACCURSED_SCEPTRE,
            ItemID.VIGGORAS_CHAINMACE,
            ItemID.URSINE_CHAINMACE,
            ItemID.WEBWEAVER_BOW,
            ItemID.CRAWS_BOW,
            ItemID.BLIGHTED_MANTA_RAY,
            ItemID.BLIGHTED_KARAMBWAN,
            ItemID.SARACHNIS_CUDGEL
    );

    public static int inventoryValue() {
        return Inventory.all()
                .stream()
                .mapToInt(x -> {
                    if (x == null) return 0;
                    if (ignoredIds.contains(x.getId())) return 0;
                    return (x.getLivePrice()) * x.getAmount();
                })
                .sum() + LootingBag.value()
                ;
    }
}
