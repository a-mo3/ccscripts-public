package org.dreambot.behaviour.method.greendragon;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.HitSplatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.misc.SmartLootEvent;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.scriptdata.GDKSettings;
import org.dreambot.scripts.GreenDragonScript;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Setter
public class KillGreenDragons extends Fractal implements HitSplatListener {
    Area dragonLoc = new Area(2966, 3626, 2988, 3604);
    Timer walkLog = new Timer(1000 * 20);
    @Getter
    static List<Integer> foodIds = Arrays.asList(
            ItemID.BLIGHTED_MANTA_RAY,
            ItemID.BLIGHTED_KARAMBWAN,
            ItemID.JUG_OF_WINE,
            ItemID.LOBSTER
    );

    Area[] meleeAreas = new Area[]{
            new Area(2966, 3626, 2988, 3604),
            new Area(3127, 3719, 3155, 3692),
            new Area(3322, 3707, 3350, 3662),
    };


    public KillGreenDragons(Supplier<Boolean> acceptCondition, Area loc) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);
        dragonLoc = loc;
    }

    public KillGreenDragons(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);
    }


    public static Timer lastDragonAtk = new Timer(1600);

    private final Supplier<CombatStyle> styleSupplier = () -> {
        int atk = Skills.getRealLevel(Skill.ATTACK);
        int str = Skills.getRealLevel(Skill.STRENGTH);
        if (atk < 70 && atk < str) {
            return CombatStyle.ATTACK;
        }
        return Equipment.contains(ItemID.ABYSSAL_WHIP) ? CombatStyle.SHARED : CombatStyle.STRENGTH;
    };

    private Supplier<Spell> spellSupplier = null;

    private Supplier<Spell> bestWaterSpell = () -> {
        List<Spell> waterSpells = Arrays.asList(
                Normal.WATER_BLAST,
                Normal.WATER_BOLT,
                Normal.WATER_STRIKE,
                Normal.WATER_WAVE,
                Normal.WATER_SURGE
        );
        return waterSpells.stream()
                .filter(Magic::canCast)
                .findFirst().orElse(null);
    };

    List<Spell> waterSpells = Arrays.asList(
            Normal.WATER_BLAST,
            Normal.WATER_BOLT,
            Normal.WATER_STRIKE,
            Normal.WATER_WAVE,
            Normal.WATER_SURGE
    );

    public boolean hasResourcesToCastSpell() {
        if (spellSupplier != null) {
            return waterSpells.stream().anyMatch(Magic::canCast);
        }
        return true;
    }

    public KillGreenDragons setWaterMagic() {
        spellSupplier = bestWaterSpell;
        return this;
    }

    Timer sinceLastInCombat = new Timer(5000);

    @Override
    public int onLoop() {
        if (Players.getLocal().isInCombat()) sinceLastInCombat.reset();
        dragonLoc = SettingsRepository.findInstanceOf(new GDKSettings()).location.area;
        if (spellSupplier != null) {
            // magic mode cast water
            Magic.setAutocastSpell(spellSupplier.get());
        } else {
            CombatStyle s = styleSupplier.get();
            if (Combat.getCombatStyle() != s) {
                Combat.setCombatStyle(s);
            }
        }

        if (!dragonLoc.contains(Players.getLocal())) {
            if (walkLog.finished()) {
                Logger.info("Walking to dragons");
                walkLog.reset();
            }
            if (Walking.shouldWalk(8)) Walking.walk(dragonLoc);
            return ReactionGenerator.getQuick();
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) > 0 && SettingsRepository.findInstanceOf(new GDKSettings()).prayMelee) {
            PrayerUtils.toggle(lastDragonAtk.finished(), Prayer.PROTECT_FROM_MELEE);
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
        }

        if (Skills.getBoostedLevel(Skill.HITPOINTS) <= SettingsRepository.findInstanceOf(new GDKSettings()).eatAbove && Combat.getHealthPercent() != 100) {
            Logger.info("Eating shark");
            Inventory.interact(x -> foodIds.contains(x.getId()), Inventory.contains(ItemID.JUG_OF_WINE) ? "Drink" : "Eat");
            return ReactionGenerator.getQuick();
        }


        int energy = Walking.getRunEnergy();
        if (energy < 23) {
            Item stamina = ItemVariants.STAMINA_POTION.getItem();
            if (stamina != null) {
                Logger.info("Drinking stamina");
                stamina.interact("Drink");
                Sleep.sleepUntil(Walking::isStaminaActive,
                        1400);
            }
        }

        Item antiFire = ItemVariants.ANTI_FIRE_POTION.getItem();
        if (antiFire != null && PlayerSettings.getBitValue(3981) < 3) {
            Logger.info("Drinking antifire");
            antiFire.interact("Drink");
            return ReactionGenerator.getQuick();
        }

        Item prayerPot = ItemVariants.BLIGHTED_SUPER_RESTORE.getItem();
        if (prayerPot != null && Skills.getBoostedLevel(Skill.PRAYER) < 1) {
            Logger.info("Drinking prayer");
            prayerPot.interact("Drink");
            return ReactionGenerator.getQuick();
        }

        // str and atk boost
        int strBoost = Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel();
        if (strBoost < 2) {
            Item strPot = ItemVariants.STRENGTH_POTION.getItem();
            if (strPot != null) {
                log("Drink str pot");
                strPot.interact();
            }
        }

        int atkBoost = Skill.ATTACK.getBoostedLevel() - Skill.ATTACK.getLevel();
        if (atkBoost < 2) {
            Item atkPot = ItemVariants.ATTACK_POTION.getItem();
            if (atkPot != null) {
                log("Drink atk pot");
                atkPot.interact();
            }
        }

        int rngBoost = Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel();
        if (rngBoost < 3) {
            Item rngPot = ItemVariants.RANGE_POTION.getItem();
            if (rngPot != null) {
                log("Drink range potion");
                rngPot.interact();
            }
        }


        if (Dialogues.inDialogue()) {
            Dialog.solve();
            return ReactionGenerator.getQuick();
        }

        Supplier<List<GroundItem>> lootSupplier = () -> GroundItems.all(
                x -> (ItemVariants.LOOTING_BAG.contains(x.getId())) || x.getAmount() * LivePrices.get(x.getId()) > SettingsRepository.findInstanceOf(new GDKSettings()).minLootValue
                        && dragonLoc.contains(x)
                        && (!foodIds.contains(x.getId()) || Inventory.getEmptySlots() > 1)
        );
        if (!lootSupplier.get().isEmpty()) {
            Logger.info("Loot event: " + new SmartLootEvent(lootSupplier,
                    ItemID.JUG, ItemID.BLIGHTED_MANTA_RAY, ItemID.LOBSTER, ItemID.JUG_OF_WINE).executed());
            GreenDragonScript.hasLootInBag = true;
            return ReactionGenerator.getQuick();
        }

        if (Players.getLocal().isInCombat()) {
            return ReactionGenerator.getQuick();
        }

        NPC dragon = NPCs.closest(x -> x.getName().equals("Green dragon") && dragonLoc.contains(x) && !x.isInCombat());
        if (dragon == null && sinceLastInCombat.finished() && SettingsRepository.findInstanceOf(new GDKSettings()).avoidCompetition) {
            Logger.info("No free dragon hopping worlds");
            WorldHopper.hopWorld(Worlds.getRandomWorld(w -> w.isMembers() && w.getMinimumLevel() < Skills.getTotalLevel() && w.isNormal()));
            return ReactionGenerator.getQuick();
        }


        int boosted = Skills.getBoostedLevel(Skill.STRENGTH) - Skills.getRealLevel(Skill.STRENGTH);
        if (boosted < SettingsRepository.findInstanceOf(new GDKSettings()).minBoost) {
            Item boostPot = ItemVariants.COMBAT_POTION.getItem();
            if (boostPot != null) {
                Logger.info("Drinking boost");
                boostPot.interact("Drink");
                Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.STRENGTH) - Skills.getRealLevel(Skill.STRENGTH) > SettingsRepository.findInstanceOf(new GDKSettings()).minBoost,
                        1400);
            }
        }

        if (dragon != null && dragon.interact("Attack")) {
            Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 2000);
        }
        return ReactionGenerator.getQuick();
    }
//
//    @Override
//    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
//        if (npc.isInteracting(Players.getLocal())) {
//            Logger.info("NPC Animation " + animation);
//            lastDragonAtk.reset();
//        }
//    }

    @Override
    public void onHitSplatAdded(Entity entity, int type, int damage, int id, int special, int gameCycle) {
        if (entity.equals(Players.getLocal())) {
            lastDragonAtk.reset();
        }
    }
}
