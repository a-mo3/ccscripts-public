package org.dreambot.behaviour.method.venenatis.tickvenenatis;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.venenatis.VenenatisData;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.List;

public class VenenatisTickAttack extends TickDecision {
    public VenenatisTickAttack() {
        setSimpleName("Attack (no web)");
    }

    List<Integer> switchBack = Arrays.asList(
            ItemID.GLACIAL_TEMOTLI,
            ItemID.SARACHNIS_CUDGEL,
            ItemID.SARADOMIN_SWORD,
            ItemID.VIGGORAS_CHAINMACE,
            ItemID.URSINE_CHAINMACE
    );
    Filter<Item> reequip = x -> switchBack.contains(x.getId());

    @Override
    public boolean evaluate() {
        NPC venenatis = NPCs.closest(VenenatisData.VENENATIS_NAME);
        if (venenatis == null) return false;

        Projectile p = Projectiles.closest(VenenatisData.WEB_PROJECTILE);
        GameObject web = GameObjects.closest(x -> VenenatisData.isWeb(x.getId()));
        if (p != null || web != null) return false;

        log("No web attack");

        if (!Walking.isRunEnabled() && Walking.getRunEnergy() >= 5) Walking.toggleRun();


        if (!Equipment.contains(reequip) && Inventory.contains(reequip)) {
            log("Re equip");
            Inventory.interact(reequip);
        }

        Character target = Players.getLocal().getInteractingCharacter();
        if (!venenatis.equals(target)) {
            log("Attack venenatis");
            venenatis.interact();
        }
        return true;
    }
}
