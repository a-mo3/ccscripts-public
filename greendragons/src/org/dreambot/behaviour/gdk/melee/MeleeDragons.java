package org.dreambot.behaviour.gdk.melee;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
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
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.SmartLootEvent;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.List;
import java.util.function.Supplier;

public class MeleeDragons extends Fractal implements HitSplatListener {
    Area fortressDragon = new Area(2966, 3626, 2988, 3604);
    Timer walkLog = new Timer(1000 * 20);
    boolean hasSet;

    Area[] meleeAreas = new Area[]{
            new Area(2966, 3626, 2988, 3604),
            new Area(3127, 3719, 3155, 3692),
            new Area(3322, 3707, 3350, 3662),
    };

    public MeleeDragons(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);

        if (!hasSet && ScriptSettings.getSettingsData().multipleMeleeAreas) {
            fortressDragon = meleeAreas[ShuffleFractal.getLoginValue() % meleeAreas.length];
            hasSet = true;
        }
    }

    public static Timer lastDragonAtk = new Timer(1600);

    private Supplier<CombatStyle> styleSupplier = () -> {
        if (ScriptSettings.getSettingsData().onlyStab) return CombatStyle.STRENGTH;
        int atk = Skills.getRealLevel(Skill.ATTACK);
        int str = Skills.getRealLevel(Skill.STRENGTH);
        int def = Skills.getRealLevel(Skill.DEFENCE);
        int limit = ScriptSettings.getSettingsData().combatLimit;
        if (def + 9 < atk || (atk >= limit && str >= limit)) {
            return CombatStyle.DEFENCE;
        }
        if (atk + 9 <= str || def >= atk || (str >= limit) || (atk < 60 && str >= 50) || (atk < 70 && str >= 60)) {
            return CombatStyle.ATTACK;
        }
        if (atk >= str) return CombatStyle.STRENGTH;
        return Combat.getCombatStyle();
    };


    @Override
    public int onLoop() {
        CombatStyle s = styleSupplier.get();
        if (Combat.getCombatStyle() != s) {
            Combat.setCombatStyle(s);
        }

        if (!fortressDragon.contains(Players.getLocal())) {
            if (walkLog.finished()) {
                Logger.info("Walking to dragons");
                walkLog.reset();
            }
            if (Walking.shouldWalk(8)) Walking.walk(fortressDragon);
            return ReactionGenerator.getQuick();
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) > 0 && ScriptSettings.getSettingsData().prayerMelee) {
            Prayers.toggle(lastDragonAtk.finished(), Prayer.PROTECT_FROM_MELEE);
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
        }

        if (Inventory.contains(ScriptSettings.getFoodId()) && Skills.getBoostedLevel(Skill.HITPOINTS) <= ScriptSettings.getSettingsData().eatAbove && Combat.getHealthPercent() != 100) {
            Logger.info("Eating shark");
            Inventory.interact(ScriptSettings.getFoodId(), "Eat");
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

        Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
        if (prayerPot != null && Skills.getBoostedLevel(Skill.PRAYER) < 1) {
            Logger.info("Drinking prayer");
            prayerPot.interact("Drink");
            return ReactionGenerator.getQuick();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
            return ReactionGenerator.getQuick();
        }

        Supplier<List<GroundItem>> lootSupplier = () -> GroundItems.all(
                x -> (ItemVariants.LOOTING_BAG.contains(x.getID())) || x.getAmount() * LivePrices.get(x.getID()) > ScriptSettings.getSettingsData().minLootValue
                        && fortressDragon.contains(x)
                        && (x.getID() != ScriptSettings.getFoodId() || Inventory.getEmptySlots() > 1)
        );
        if (!lootSupplier.get().isEmpty()) {
            Logger.info("Loot event: " + new SmartLootEvent(lootSupplier, ScriptSettings.getFoodId()).executed());
            return ReactionGenerator.getQuick();
        }

        if (Players.getLocal().isInCombat()) {
            return ReactionGenerator.getQuick();
        }

        NPC dragon = NPCs.closest(x -> x.getName().equals("Green dragon") && fortressDragon.contains(x) && !x.isInCombat());
        if (dragon == null && ScriptSettings.getSettingsData().avoidCompetition) {
            Logger.info("No free dragon hopping worlds");
            WorldHopper.hopWorld(Worlds.getRandomWorld(w -> w.isMembers() && w.getMinimumLevel() < Skills.getTotalLevel() && w.isNormal()));
            return ReactionGenerator.getQuick();
        }


        int boosted = Skills.getBoostedLevel(Skill.STRENGTH) - Skills.getRealLevel(Skill.STRENGTH);
        if (boosted < ScriptSettings.getSettingsData().minBoost) {
            Item boostPot = ItemVariants.COMBAT_POTION.getItem();
            if (boostPot != null) {
                Logger.info("Drinking boost");
                boostPot.interact("Drink");
                Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.STRENGTH) - Skills.getRealLevel(Skill.STRENGTH) > ScriptSettings.getSettingsData().minBoost,
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
