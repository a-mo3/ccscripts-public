package org.dreambot.behaviour.training.magic;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
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
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Accessors(chain = true)
public class F2PMultipleSpots extends Fractal {
    List<String> mobs = Arrays.asList(
            "Cow",
            "Ram",
            "Lesser demon"
    );
    Area[] areas = new Area[]{
            new Area(3021, 3319, 3041, 3314), // south falador cows
            new Area(3248, 3269, 3252, 3255), // lummy cows
            new Area(3108, 3162, 3111, 3159, 2), // demon
            new Area(3197, 3280, 3204, 3277) // lummy cows near sheep
    };

    Area selectedArea = areas[ShuffleFractal.getLoginValue() % areas.length];
    @Setter
    int defenceTarget = 0;

    public F2PMultipleSpots(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR).setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 13)
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE).setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 13)
        ;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.MIND_RUNE, 1, 1600)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 35)
                .addItem(ItemID.CHAOS_RUNE, 1, 1600)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 35)
                .addItem(ItemID.AIR_RUNE, 4, 7000).setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 13)
                .setStrict(true)
        ;

    }

    @Override
    public int onLoop() {
        if (!selectedArea.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(selectedArea.getCenter());
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

        if (Players.getLocal().isInCombat()) {
            return ReactionGenerator.getNormal();
        }

        NPC cow = NPCs.closest(x -> x.canAttack() && x.distance() < 10 && mobs.contains(x.getName()) && x.hasAction("Attack"));
        Logger.info("Mob: " + cow);
        if (cow != null) {
            cow.interact("Attack");
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 2400);
        }
        return ReactionGenerator.getNormal();
    }

    private Spell getSpell() {
        Spell[] allowed = new Spell[]{
                Normal.WIND_STRIKE,
                Normal.FIRE_STRIKE,
                Normal.FIRE_BOLT
        };

        return Arrays.stream(allowed).filter(Magic::canCast).reduce((f, s) -> s).orElse(null);
    }
}
