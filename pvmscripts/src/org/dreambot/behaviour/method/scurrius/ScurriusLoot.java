package org.dreambot.behaviour.method.scurrius;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.scriptdata.ScurriusSettings;
import org.dreambot.scripts.ScurriusScript;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class ScurriusLoot extends TickDecision implements ItemContainerListener {
    static boolean hasRegistered = false;
    final boolean dropForLoot ;
    public ScurriusLoot(boolean dropForLoot) {
        this.dropForLoot = dropForLoot;
        if (!hasRegistered) {
            // only register this listner 1 time or you'll triple count gp
            hasRegistered = true;
            Client.getInstance().addEventListener(this);
        }
        setSimpleName("Loot scurr");
    }
// loot scurrius and also alch items

    Set<Integer> lootables = new HashSet<>(Arrays.asList(
            ItemID.RUNE_FULL_HELM,
            ItemID.ADAMANT_PLATEBODY,
            ItemID.RUNE_MED_HELM,
            ItemID.RUNE_SQ_SHIELD,
            ItemID.RUNE_CHAINBODY,
            ItemID.RUNE_BATTLEAXE,
            ItemID.CHAOS_RUNE,
            ItemID.DEATH_RUNE,
            ItemID.LAW_RUNE,
            ItemID.RUNE_ARROW,
            ItemID.COINS_995,
            ItemID.ADAMANT_ARROW,
            ItemID.PRAYER_POTION4
    ));

    Set<Integer> alchables = new HashSet<>(Arrays.asList(
            ItemID.RUNE_FULL_HELM,
            ItemID.ADAMANT_PLATEBODY,
            ItemID.RUNE_MED_HELM,
            ItemID.RUNE_SQ_SHIELD,
            ItemID.RUNE_CHAINBODY,
            ItemID.RUNE_BATTLEAXE
    ));


    // seems to count down in minutes 0 when you can eat from food
    public static final int RAT_BOSS_PLAYER_EAT_FROM_FOOD_PILE = 4078;

    @Override
    public boolean evaluate() {
        if (Inventory.contains(ItemID.VIAL)) {
            Inventory.dropAll(ItemID.VIAL);
            log("Drop all vials");
        }

        GroundItem spine = GroundItems.closest(ItemID.SCURRIUS_SPINE);
        if (spine != null) {
            log("Grab spine drop");
            if (Inventory.isFull()) {
                Inventory.drop(ItemID.SHARK);
            }

            spine.interact("Take");
            return false;
        }

        if (Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY) && Inventory.contains(x -> alchables.contains(x.getId()))) {
            log("Alching");
            Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY, Inventory.get(x -> alchables.contains(x.getId())));
            return true;
        }

        GroundItem bestLoot = GroundItems.all(x -> lootables.contains(x.getItem().getId()))
                .stream()
                .max(Comparator.comparingInt(x -> (x.getItem().getLivePrice() + 1) * x.getAmount()))
                .orElse(null);
        if (bestLoot != null && (dropForLoot || !Inventory.isFull())) {
            if (Inventory.isFull()) {
                log("Drop a shark for loot");
                Inventory.drop(ItemID.SHARK);
            }
            log("Take best loot " + bestLoot);
            bestLoot.interact("Take");
            return true;
        }

        log("No loot left");
        int missingHP = Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel();
        if (missingHP >= 15 && PlayerSettings.getConfig(RAT_BOSS_PLAYER_EAT_FROM_FOOD_PILE) == 0) {
            log("lets eat some cheese");
            ObjectUtil.interact("Food Pile");
        }
        return false;
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        if (!Client.isDynamicRegion() || !alchables.contains(item.getId())) return;
        log("Adding " + item.getName() + " " + item.getHighAlchValue());
        ScurriusScript.grossGp += item.getHighAlchValue();
    }
}
