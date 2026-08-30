package org.dreambot.behaviour.method.bluedragons;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.spindel.GoToSpindel;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.CombatMode;
import org.dreambot.scriptdata.BlueDragonSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

import static org.dreambot.behaviour.method.bluedragons.KillBlueDragon.BLUE_DRAGON_AREA;

/**
 * recharge stats at ferox enclave
 * falador -> blue dragon pipe (use summer pie if needed)
 */
public class GoToBlueDragons extends Fractal {
    public static final Area FEROX_POOL = new Area(3127, 3638, 3130, 3633);
    // entrance where pipe is
    public static final Area TAVERLEY_DUNGEON_START = new Area(2881, 9802, 2887, 9793);
    public static final Area WHOLE_FALADOR = new Area(2873, 3420, 3069, 3303);

    public GoToBlueDragons() {
        this.inventoryLoadout = SettingsRepository.findInstanceOf(new BlueDragonSettings()).loadout.getInventoryLoadout();
        this.equipmentLoadout = SettingsRepository.findInstanceOf(new BlueDragonSettings()).loadout.getEquipmentLoadout();
    }

    @Override
    public int onLoop() {
        Spell best = getBestWaterSpell();
        if (SettingsRepository.findInstanceOf(new BlueDragonSettings()).getCombatMode() == CombatMode.MAGIC && !best.equals(Magic.getAutocastSpell())) {
            if (Widgets.isOpen()) Widgets.closeAll();
            log("Set autocast " + getBestWaterSpell());
            Magic.setAutocastSpell(getBestWaterSpell());
            return ReactionGenerator.getNormal();
        }

        if (!areStatusFull()) {
            if (!FEROX_POOL.contains(Players.getLocal())) {
                slowLog("Go to ferox pool");
                if (Walking.shouldWalk()) Walking.walk(FEROX_POOL);
                return ReactionGenerator.getNormal();
            }

            GameObject rejPool = GameObjects.closest("Pool of Refreshment");
            if (rejPool != null && !Players.getLocal().isMoving()) {
                rejPool.interact("Drink");
                Sleep.sleepUntil(GoToSpindel::areStatusFull, 2300);
            }
            return ReactionGenerator.getQuick();
        }

        // walker should handle falador tp
        if (!TAVERLEY_DUNGEON_START.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(TAVERLEY_DUNGEON_START);
            return ReactionGenerator.getNormal();
        }

        if (Skills.getRealLevel(Skill.AGILITY) < 65) {
            Logger.warn("You don't have the min agility level required (65 + 5(Summer Pie))");
        }

        // proc antifire
        if (!hasAntifireProt()) {
            Item i = ItemVariants.ANTI_FIRE_POTION.getItem();
            if (i == null) {
                log("Failed to find antifire. going back to bank");
                new BankAllInventoryEvent().execute(); // todo better handle if this ever even occurs
                return ReactionGenerator.getNormal();
            }
            log("Drinking antifire " + i);
            i.interact("Drink");
            Sleep.sleepUntil(this::hasAntifireProt, 2600);
        }

        if (Skills.getBoostedLevel(Skill.AGILITY) < 70) {
            log("Eating summer pie");
            Inventory.interact(x -> x.getName().toLowerCase().contains("summer"));
            Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.AGILITY) >= 70, 2400);
            return ReactionGenerator.getNormal();
        }

        GameObject pipe = GameObjects.closest("Obstacle pipe");
        if (pipe == null) {
            log("Failed to find pipe");
            return ReactionGenerator.getNormal();
        }

        pipe.interact();
        Sleep.sleepUntil(() -> BLUE_DRAGON_AREA.contains(Players.getLocal()), 2000);
        return ReactionGenerator.getNormal();
    }

    private boolean hasAntifireProt() {
        return PlayerSettings.getBitValue(3981) >= 3;
    }

    private boolean areStatusFull() {
        if (Combat.isPoisoned() || Combat.isEnvenomed()) return false;
        if (Skills.getBoostedLevel(Skill.HITPOINTS) < Skills.getRealLevel(Skill.HITPOINTS)) return false;
        if (Skills.getBoostedLevel(Skill.PRAYER) < Skills.getRealLevel(Skill.PRAYER)) return false;
        return true;
    }

    private static List<Spell> waterSpells = Arrays.asList(
            Normal.WATER_SURGE,
            Normal.WATER_WAVE,
            Normal.WATER_BLAST,
            Normal.WATER_BOLT,
            Normal.WATER_STRIKE
    );

    public static Spell getBestWaterSpell() {
        return waterSpells.stream().filter(Magic::canCast).findFirst().orElse(null);
    }
}
