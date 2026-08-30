package org.dreambot.behaviour.method.bluedragons;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.misc.SmartLootEvent;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.CombatMode;
import org.dreambot.scriptdata.BlueDragonSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

import static org.dreambot.behaviour.method.bluedragons.GoToBlueDragons.getBestWaterSpell;

/**
 * kill & loot blue dragons, return to bank when out of food or potions
 */
public class KillBlueDragon extends Fractal {
    public static final Area BLUE_DRAGON_AREA = new Area(2889, 9815, 2926, 9791);
    List<Integer> loot = Arrays.asList(
            ItemID.BLUE_DRAGONHIDE,
            ItemID.DRAGON_BONES,
            ItemID.RUNE_DAGGER,
            ItemID.NATURE_RUNE,
            ItemID.LAW_RUNE,
            ItemID.ADAMANT_FULL_HELM
    );

    Filter<GroundItem> lootFilter = x -> loot.contains(x.getId()) || x.getItem().getLivePrice() > 1500;

    Integer[] dropIds = new Integer[]{
            ItemID.VIAL,
            ItemID.LOBSTER
    };

    @Override
    public boolean isValid() {
        return BLUE_DRAGON_AREA.contains(Players.getLocal());
    }

    @Override
    public int onLoop() {
        if (shouldLeave()) {
            slowLog("Go to ferox pool");
            if (Walking.shouldWalk()) Walking.walk(GoToBlueDragons.FEROX_POOL);
            return ReactionGenerator.getNormal();
        }

        Spell best = getBestWaterSpell();
        if (SettingsRepository.findInstanceOf(new BlueDragonSettings()).getCombatMode() == CombatMode.MAGIC && !best.equals(Magic.getAutocastSpell())) {
            log("Set autocast " + getBestWaterSpell());
            Magic.setAutocastSpell(getBestWaterSpell());
            return ReactionGenerator.getNormal();
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) < 5) {
            Item i = ItemVariants.PRAYER_POTION.getItem();
            if (i != null) {
                log("Drinking prayer " + i);
                i.interact("Drink");
                Sleep.sleepUntil(() -> Skills.getRealLevel(Skill.PRAYER) > 5, 1200);
            }
        }

        Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);

        if (SettingsRepository.findInstanceOf(new BlueDragonSettings()).useCombatPrayers) {
            CombatMode mode = SettingsRepository.findInstanceOf(new BlueDragonSettings()).getCombatMode();
            switch (mode) {
                case MAGIC:
                    Prayers.toggle(true, getBestMagePray());
                    break;
                case RANGED:
                    Prayers.toggle(true, getBestRangePray());
                    break;
                case MELEE:
                    Prayers.toggle(true, Prayer.ULTIMATE_STRENGTH);
            }
        }

        if (!hasAntifireProt()) {
            Item i = ItemVariants.ANTI_FIRE_POTION.getItem();
            if (i != null) {
                log("Drinking antifire " + i);
                i.interact("Drink");
                Sleep.sleepUntil(this::hasAntifireProt, 1200);
            }
        }

        if (lootFilter != null && (!Inventory.isFull() || Inventory.contains(dropIds))) {
            List<GroundItem> loot = GroundItems.all(x -> lootFilter.match(x) && BLUE_DRAGON_AREA.contains(x));
            if (!loot.isEmpty()) {
                if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 10) Walking.toggleRun();
                new SmartLootEvent(() -> GroundItems.all(x -> lootFilter.match(x) && BLUE_DRAGON_AREA.contains(x)), dropIds)
                        .executed();
                return ReactionGenerator.getNormal();
            }
        }

        Player lp = Players.getLocal();
        Character target = lp.getInteractingCharacter();
        Character hittingMe = lp.getCharacterInteractingWithMe();
        if (target != null) {
            // already attacking
            slowLog("Already in combat");
            return ReactionGenerator.getNormal();
        }

        if (hittingMe != null && hittingMe.getName().toLowerCase().contains("dragon")) {
            log("Attack dragon attacking me");
            hittingMe.interact();
            Sleep.sleepUntil(() -> lp.getInteractingCharacter() != null, 2400);
            return ReactionGenerator.getNormal();
        }

        NPC dragon = NPCs.closest(x -> x.getName().equals("Blue dragon") && !x.isInCombat());
        if (dragon != null) {
            log("Attack dragon");
            dragon.interact("Attack");
            Sleep.sleep(1400);
        }
        return ReactionGenerator.getNormal();
    }

    private boolean hasAntifireProt() {
        return PlayerSettings.getBitValue(3981) >= 3;
    }

    private boolean shouldLeave() {
        // no prayer potion
        if (Skills.getBoostedLevel(Skill.PRAYER) < 5 && ItemVariants.PRAYER_POTION.getItem() == null) return true;
        if (Combat.getHealthPercent() < 60 && !Inventory.contains(ItemID.LOBSTER)) return true;
        if (!hasAntifireProt() && ItemVariants.ANTI_FIRE_POTION.getItem() == null) return true;
        if (Inventory.isFull() && !Inventory.contains(ItemID.LOBSTER, ItemID.VIAL)) return true;
        // this means you are out of runes.
        if (SettingsRepository.findInstanceOf(new BlueDragonSettings()).getCombatMode() == CombatMode.MAGIC && getBestWaterSpell() == null)
            return true;
        return false;
    }

    public static final int RIGOUR_UNLOCKED = 5451;
    public static final int AUGURY_UNLOCKED = 5452;

    public static Prayer getBestMagePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        if (lvl >= 77 && PlayerSettings.getBitValue(AUGURY_UNLOCKED) == 1) return Prayer.AUGURY;
        if (lvl >= 45) return Prayer.MYSTIC_MIGHT;
        return Prayer.MYSTIC_LORE;
    }

    public static Prayer getBestRangePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        if (lvl >= 74 && PlayerSettings.getBitValue(RIGOUR_UNLOCKED) == 1) return Prayer.RIGOUR;
        if (lvl >= 44) return Prayer.EAGLE_EYE;
        return Prayer.HAWK_EYE;
    }
}
