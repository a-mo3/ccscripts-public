package org.dreambot.behaviour.method.tickantipk;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class TickAttackDecision extends TickDecision {
    final Supplier<Player> topEnemy;
    public TickAttackDecision(Supplier<Player> topEnemy) {
        setSimpleName("Attack Decision");
        this.topEnemy = topEnemy;
    }

    List<Integer> normalWeapons = Arrays.asList(
            ItemID.SARACHNIS_CUDGEL,
            ItemID.GLACIAL_TEMOTLI,
            ItemID.DUAL_MACUAHUITL,
            ItemID.SARADOMIN_SWORD
    );
    Filter<Item> reequip = x -> normalWeapons.contains(x.getId());

    @Override
    public boolean evaluate() {
        if (Combat.isSpecialActive() && Players.getLocal().getInteractingCharacter() != null) return false;

        if (Inventory.contains(reequip) && !Equipment.contains(reequip)) {
            log("Re equip");
            Inventory.interact(reequip);
        }

        Player p = topEnemy.get();
        Character target = Players.getLocal().getInteractingCharacter();
        if (p != null && !p.equals(target)) p.interact();
        return false;
    }
}
