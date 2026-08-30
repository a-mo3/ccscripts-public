package org.dreambot.behaviour.method.revs.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.revs.data.RevenantLocations;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.scriptdata.RevenantSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

public class FightRevs extends Fractal {
    final Timer antiCrash = new Timer(Math.max(15_000, SettingsRepository.findInstanceOf(new RevenantSettings()).anticrashTime));

    public FightRevs() {
        this.paintArraySupplier = () -> new String[]{
                "AntiCrash timer: " + antiCrash.remaining()
        };
    }

    @Override
    public int onLoop() {
        RevenantLocations target = SettingsRepository.findInstanceOf(new RevenantSettings()).targetRevenant;
        // drink prayer
        boolean canPray = Skills.getRealLevel(Skill.PRAYER) > 10;
        Item restore = ItemVariants.BLIGHTED_SUPER_RESTORE.getItem();
        if (canPray && Skills.getBoostedLevel(Skill.PRAYER) < 10) {
            if (restore == null) {
                ExitRevs.setForceLeave(true);
                return ReactionGenerator.getQuick();
            }

            restore.interact("Drink");
        }

        // pray mage
        if (canPray && !Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);

        // eat food
        if (Combat.getHealthPercent() < 75) {
            Item food = Inventory.get(ItemID.BLIGHTED_MANTA_RAY);
            if (food == null) {
                log("Force leave");
                ExitRevs.setForceLeave(true);
                return ReactionGenerator.getQuick();
            }

            food.interact("Eat");
            return ReactionGenerator.getQuick();
        }

        // if using fire staff use best spell
        if (Equipment.contains(ItemID.STAFF_OF_FIRE)) {
            Logger.info("Set autocast");
            if (!Magic.isAutocasting()) {
                Magic.setAutocastSpell(getSpell());
                return ReactionGenerator.getQuick();
            }
        }

        Area loc = SettingsRepository.findInstanceOf(new RevenantSettings()).targetRevenant.getArea();
        if (!loc.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(loc);
            return ReactionGenerator.getQuick();
        }

        if (!LootingBag.refreshLootBagCache()) {
            return ReactionGenerator.getQuick();
        }

        if (Players.getLocal().isInCombat()) {
            antiCrash.reset();
            if (Players.getLocal().getInteractingCharacter() == null) {
                Entity attackingMe = Players.getLocal().getCharacterInteractingWithMe();
                if (attackingMe != null) {
                    attackingMe.interact("Attack");
                    Sleep.sleepUntil(() -> Players.getLocal().getInteractingCharacter() != null, 2400);
                }
            }
            return ReactionGenerator.getQuick();
        }

        // hit a rev
        NPC tgt = NPCs.closest(npc -> npc.getInteractingCharacter() == null
                && npc.getName().toLowerCase().contains(target.getMobName())
                && target.getArea().contains(npc)
        );
        if (tgt == null) {
            Logger.info("No rev target found waiting for respawn " + antiCrash.remaining());
            if (antiCrash.finished() && SettingsRepository.findInstanceOf(new RevenantSettings()).antiCrash) {
                int world = Worlds.getCurrentWorld();
                Logger.info("Anti crash world hop");
                antiCrash.reset();
                WorldHopper.hopWorld(
                        Worlds.getRandomWorld(w -> w.isNormal() && w.isMembers() && w.getMinimumLevel() < Skills.getTotalLevel())
                );
                Sleep.sleep(1500);
                Sleep.sleepUntil(() -> Worlds.getCurrentWorld() != world
                        && Client.isLoggedIn() && Client.getGameStateID() != 45, 6400);
                Logger.info("World hopped");
            }

            if (SettingsRepository.findInstanceOf(new RevenantSettings()).centerWhenNoRevs) {
                if (target.getArea().getCenter().distance() > 5) {
                    Logger.info("Walk to revs");
                    if (Walking.shouldWalk()) Walking.walk(target.getArea().getCenter());
                }
            }
            return ReactionGenerator.getQuick();
        }

        if (tgt.equals(Players.getLocal().getInteractingCharacter())) {
            Logger.info("Already attacking found rev.");
            return ReactionGenerator.getQuick();
        }

        tgt.interact("Attack");
        Sleep.sleepUntil(() -> tgt.equals(Players.getLocal().getInteractingCharacter()), 800);
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

}
