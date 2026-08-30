package org.dreambot.behaviour.training.magic;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * fire spell on ice giants (100%) weakness
 */
@Accessors(chain = true)
public class MageIceGiants extends Fractal {
    List<String> mobs = Arrays.asList(
            "Ice giant",
            "Ice warrior"
    );
    Area ICE_DUNGEON = new Area(3038, 9599, 3067, 9563);

    @Setter
    int defenceTarget = 0;

    public MageIceGiants(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Ice dungeon");

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR).setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 13)
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE).setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 13)
                .setStrict(true)
        ;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SWORDFISH, 1, 22)
                .setRefill(250)
                .addItem(ItemID.MIND_RUNE, 1, 1600)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 35)
                .addItem(ItemID.CHAOS_RUNE, 1, 1600)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 35 && Skills.getRealLevel(Skill.MAGIC) < 59)
                .addItem(ItemID.DEATH_RUNE, 1, 1600)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 59 && Skills.getRealLevel(Skill.MAGIC) < 75)
                .addItem(ItemID.BLOOD_RUNE, 1, 1600)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 75)
                .addItem(ItemID.AIR_RUNE, 6, 7000).setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 13)
//                .setStrict(true)
        ;
    }

    @Override
    public int onLoop() {
        if (Skills.getBoostedLevel(Skill.HITPOINTS) <= 7 || missingHealth() > 14) {
            Inventory.interact(ItemID.SWORDFISH, "Eat");
            return ReactionGenerator.getNormal();
        }

        if (!ICE_DUNGEON.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(ICE_DUNGEON.getCenter());
            return ReactionGenerator.getQuick();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
            return ReactionGenerator.getQuick();
        }
        if (Skills.getRealLevel(Skill.DEFENCE) < defenceTarget) {
            if (!Magic.isAutocastDefensive()) {
                Magic.setDefensiveAutocastSpell(getSpell());
                return ReactionGenerator.getNormal();
            }
        } else {
            if (!Magic.isAutocasting()) {
                Magic.setAutocastSpell(getSpell());
                return ReactionGenerator.getNormal();
            }
        }

        GroundItem loot = GroundItems.closest(x -> x.getItem().isStackable());
        if (!Inventory.isFull() && loot != null) {
            loot.interact("Take");
            Sleep.sleepUntil(() -> !loot.exists(), 800);
            return ReactionGenerator.getNormal();
        }

        if (Players.getLocal().isInCombat()) {
            return ReactionGenerator.getNormal();
        }

        NPC npc = NPCs.closest(x -> x.canAttack() && x.distance() < 10 && mobs.contains(x.getName()) && x.hasAction("Attack"));
        Logger.info("Mob: " + npc);
        if (npc != null) {
            npc.interact("Attack");
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 2400);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }

    private Spell getSpell() {
        Spell[] allowed = new Spell[]{
                Normal.WIND_STRIKE,
                Normal.FIRE_STRIKE,
                Normal.FIRE_BOLT,
                Normal.FIRE_BLAST,
                Normal.FIRE_WAVE,
        };

        Spell sp = Arrays.stream(allowed).filter(Magic::canCast).reduce((f, s) -> s).orElse(null);
        Logger.info("Spell " + sp);
        return sp;
    }

    private int missingHealth() {
        return Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
    }
}
