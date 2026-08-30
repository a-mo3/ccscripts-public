package org.dreambot.generics;

import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Casts a spell, by itself or on an entity / Item
 */
public class GenericCastSpell extends IronFractal {
    final Spell spell;
    final Supplier<Entity> entitySupplier;
    final Supplier<Item> itemSupplier;

    public GenericCastSpell(BooleanSupplier acceptCondition, Spell spell, Supplier<Entity> entitySupplier) {
        super(acceptCondition);
        this.spell = spell;
        this.entitySupplier = entitySupplier;
        this.itemSupplier = null;
        setSimpleName("Cast " + spell);
    }

    public GenericCastSpell(BooleanSupplier acceptCondition, Spell spell) {
        super(acceptCondition);
        this.spell = spell;
        this.entitySupplier = null;
        this.itemSupplier = null;
        setSimpleName("Cast " + spell);
    }

    @Override
    protected int onLoop() {
        if (!Magic.canCast(spell)) {
            log("Cant cast " + spell);
            return sleep();
        }

        if (entitySupplier != null) {
            Entity e = entitySupplier.get();
            log("Casting " + spell + " on entity " + e);
            if (e != null) Magic.castSpellOn(spell, e);
            return sleep();
        }

        if (itemSupplier != null) {
            Item i = itemSupplier.get();
            log("Casting " + spell + " on item " + i);
            if (i != null) Magic.castSpellOn(spell, i);
            return sleep();
        }
        log("Casting spell " + spell);
        Magic.castSpell(spell);
        return sleep();
    }
}
