package org.dreambot.behaviour.training;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
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
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Accessors(chain = true)
public class DistributedRangeTraining extends Fractal {
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

    public DistributedRangeTraining(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.SHORTBOW)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 20)
                .addItem(EquipmentSlot.WEAPON, ItemID.WILLOW_SHORTBOW)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 30 && Skills.getRealLevel(Skill.RANGED) >= 20)
                .addItem(EquipmentSlot.WEAPON, ItemID.YEW_SHORTBOW)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 50 && Skills.getRealLevel(Skill.RANGED) >= 40)
                .addItem(EquipmentSlot.WEAPON, ItemID.MAGIC_SHORTBOW)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 50)
                // dont need armour for training its all safespots

                .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.IRON_ARROW, 1, 2000))
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 20)

                .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.MITHRIL_ARROW, 1, 2000))
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 40 && Skills.getRealLevel(Skill.RANGED) >= 20)

                .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.RUNE_ARROW, 1, 2000))
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 40)
        ;

//        this.inventoryLoadout = new InventoryLoadout()
//                .addItem(ItemID.CHAOS_RUNE, 1, 1600)
//                .addItem(ItemID.AIR_RUNE, 1, 7000).setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 13)
//                .setStrict(true)
//        ;
    }

    @Override
    public int onLoop() {
        if (!selectedArea.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(selectedArea.getCenter());
            return ReactionGenerator.getQuick();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
            return ReactionGenerator.getNormal();
        }


        if (Skills.getRealLevel(Skill.DEFENCE) < defenceTarget) {
            if (Widgets.isOpen()) Widgets.closeAll();
            if (Combat.getCombatStyle() != CombatStyle.RANGED_DEFENCE) {
                Logger.info("Setting to range defence");
                Combat.setCombatStyle(CombatStyle.RANGED_DEFENCE);
                return ReactionGenerator.getNormal();
            }
        } else {
            if (Widgets.isOpen()) Widgets.closeAll();
            if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
                Logger.info("Setting to range rapid");
                Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
                return ReactionGenerator.getNormal();
            }
        }

        if (Players.getLocal().isInCombat()) {
            return ReactionGenerator.getNormal();
        }

        NPC cow = NPCs.closest(x -> x.canAttack() && x.distance() < 10 && mobs.contains(x.getName()) && x.hasAction("Attack"));
        if (cow != null) {
            cow.interact("Attack");
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 2400);
            return ReactionGenerator.getNormal();
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
