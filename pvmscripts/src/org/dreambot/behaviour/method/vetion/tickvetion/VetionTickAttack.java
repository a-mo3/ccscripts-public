package org.dreambot.behaviour.method.vetion.tickvetion;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.spindel.AntiCrashWildyBosses;
import org.dreambot.behaviour.method.vetion.VetionData;
import org.dreambot.discordwebhook.DiscordSettings;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PVMUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VetionTickAttack extends TickDecision {
    public VetionTickAttack() {
        setSimpleName("vetion attack");
        specCostMap.put(ItemID.GRANITE_MAUL, 60);
        specCostMap.put(ItemID.DRAGON_MACE, 25);
        specCostMap.put(ItemID.SARADOMIN_SWORD, 100);
    }

    // itemid, spec % cost
    Map<Integer, Integer> specCostMap = new HashMap<>();

    List<Integer> regulars = Arrays.asList(
            ItemID.SARACHNIS_CUDGEL,
            ItemID.GLACIAL_TEMOTLI,
            ItemID.SARADOMIN_SWORD,
            ItemID.ZOMBIE_AXE,
            ItemID.URSINE_CHAINMACE,
            ItemID.VIGGORAS_CHAINMACE
    );

    @Override
    public boolean evaluate() {
        if (!Walking.isRunEnabled() && Walking.getRunEnergy() >= 5) Walking.toggleRun();
        NPC hound = NPCs.closest(x -> x.getName().contains("hound"));
        Character target = Players.getLocal().getInteractingCharacter();
        if (target != null) {
            // check if hound or calvarion.
            String tgtName = target.getName();
            if (tgtName == null) {
                log("Non null target with a null name?");
                return true;
            }

            if (VetionData.VETION_NAME.equals(tgtName)) {
                log("Targeting Calvarion");
                if (hound != null) {
                    log("Attack a hound " + hound);
                    hound.interact("Attack");
                    return true;
                }
                return true;
            }

            if (tgtName.toLowerCase().contains("hound")) {
                log("Targeting hound");
                return true;
            }
        }

        // attack
        NPC calv = NPCs.closest(VetionData.VETION_NAME);

        if (hound != null) {
            log("Attack a hound " + hound);
            hound.interact("Attack");
            return true;
        }

        if (calv != null) {
            // dont care if other people are attacking, we're doing this in teams
//            Character fightingCalv = calv.getCharacterInteractingWithMe();
//            if (fightingCalv != null && !fightingCalv.equals(Players.getLocal())) {
//                log("Someone else is attacking vetion");
//                AntiCrashWildyBosses.hasToLeave = true;
//                return true;
//            }

            Item specWeapon = Inventory.get(x -> specCostMap.getOrDefault(x.getId(), 101) <= Combat.getSpecialPercentage());
            if (specWeapon == null) specWeapon = Equipment.get(x -> specCostMap.getOrDefault(x.getId(), 101) <= Combat.getSpecialPercentage());
            if (specWeapon == null) {
                // no spec available, ensure we are using normal
                log("Equip regular");
                if (!Combat.isSpecialActive()) Inventory.interact(x -> regulars.contains(x.getId()));
            } else {
                // equip spec weapon, then cast sec
                log("Spec");
                Inventory.interact(x -> specCostMap.containsKey(x.getId()));
                Combat.toggleSpecialAttack(true);
            }

            // if lightning is present dont attack unless you are on attackable tile, as to not move into the way
            boolean lightnings = GraphicsObjects.closest(x -> VetionData.isVetionAttack(x.getId())) != null;
            if (lightnings) {
                if (Arrays.stream(PVMUtil.attackableTiles(calv, 2)).anyMatch(x -> x.equals(Players.getLocal().getTile())))
                    calv.interact("Attack");
            } else {
                calv.interact("Attack");
            }
            return true;
        }

        return false;
    }
}
