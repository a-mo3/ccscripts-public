package org.dreambot.behaviour.method.lavadragons;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.*;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.method.antipk.AntiPkBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.scriptdata.LavaDragonSettings;
import org.dreambot.scripts.LavaDragonScript;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Comparator;
import java.util.function.Supplier;

public class KillLavaDragons extends Fractal implements SpawnListener {

    final Area WILDY_LEVER_AREA = new Area(3089, 3478, 3094, 3474);

    final LavaDragonAntiPKStrategy antiPKStrategy;
    final boolean useLootingBag;
    final int minLootValue;
    final int defenseTarget;
    final boolean avoidCompetition;

    public KillLavaDragons(Supplier<Boolean> acceptCondition, LavaDragonSettings settings) {
        super(acceptCondition);
        LavaDragonNodes.init();
        Client.getInstance().addEventListener(this);

        antiPKStrategy = settings.antiPKStrategy;
        useLootingBag = settings.useLootingBag;
        minLootValue = settings.minLootValue;
        defenseTarget = settings.defenseTarget;
        avoidCompetition = settings.avoidCompetition;

        this.equipmentLoadout = settings.lavaDragonLoadout.equipmentLoadout;
        this.inventoryLoadout = settings.lavaDragonLoadout.inventoryLoadout
                .setStrict(true);
        this.loadoutCondition = () -> !Combat.isInWild() && (worldHopDisableLoadout.finished() || !initial.finished());
    }


    public KillLavaDragons(Supplier<Boolean> acceptCondition,
                           EquipmentLoadout loadout,
                           InventoryLoadout inventoryLoadout,
                           LavaDragonAntiPKStrategy pkStrategy,
                           boolean useLootingBag,
                           int minLootValue,
                           int defenseTarget,
                           boolean avoidCompetition
    ) {
        super(acceptCondition);
        LavaDragonNodes.init();
        Client.getInstance().addEventListener(this);

        this.antiPKStrategy = pkStrategy;
        this.useLootingBag = useLootingBag;
        this.minLootValue = minLootValue;
        this.defenseTarget = defenseTarget;
        this.avoidCompetition = avoidCompetition;

        this.equipmentLoadout = loadout;
        this.inventoryLoadout = inventoryLoadout
                .setStrict(true);
        this.loadoutCondition = () -> !Combat.isInWild() && (worldHopDisableLoadout.finished() || !initial.finished());
    }

    // so this doesnt prevent loadouts on first run
    public static Timer initial = new Timer(24_000);
    public static Timer worldHopDisableLoadout = new Timer(24_000);
    int lastAteTick = 0;
    Timer grindTimer = new Timer(600);
    Timer lootingBagTimer = new Timer(1600);

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            Dialog.solve("Yes,", "");
            return ReactionGenerator.getNormal();
        }

        // anti pk logic
        Player threat = Players.closest(x -> antiPKStrategy.pkClassifier.test(x));
        if (Combat.isInWild() && threat != null) {
            log("Threat exists " + threat.getName());
            AntiPkBranch.setAttackerName(threat.getName());
            LavaDragonScript.logout();
            return 75;
        }

        if (Inventory.contains(ItemID.JUG)) Inventory.dropAll(ItemID.JUG);

        if (Inventory.contains(ItemID.LOOTING_BAG_CLOSED) && useLootingBag) {
            log("Open looting bag");
            Inventory.interact(ItemID.LOOTING_BAG_CLOSED, "Open");

        }

        if (grindTimer.finished() && Inventory.containsAll(ItemID.PESTLE_AND_MORTAR, ItemID.LAVA_SCALE)) {
            grindTimer.reset();
            log("Grind jaunt");
            Inventory.combine(ItemID.PESTLE_AND_MORTAR, ItemID.LAVA_SCALE);
        }

        NPC dragon = LavaDragonConst.location.dragonSupplier.get();
        // loot your loot
        GroundItem loot = GroundItems.all().stream()
                .filter(x -> x.distance(LavaDragonConst.location.safeTile) < 8)
                .filter(x -> x.distance() < 8)
                .filter(x -> dragon == null || x.distance(dragon) > 8)
                .filter(x -> x.getId() != ItemID.BURNT_BONES)
                .filter(x -> LivePrices.getHigh(x.getId()) * x.getAmount() > minLootValue || !x.getItem().isTradable() || x.getId() == ItemID.LAVA_SCALE)
                .filter(Entity::canReach)
                .filter(x -> !ItemVariants.LOOTING_BAG.contains(x.getId()) || useLootingBag)
                .min(Comparator.comparingInt(x -> x.getItem().getLivePrice()))
                .orElse(null);
        if (dragon == null && loot != null) {
            if (Inventory.isFull()) Inventory.drop(ItemID.JUG_OF_WINE);
            log("Looting time " + loot.getName());
            loot.interact("Take");
            return 75;
        }

        // eat (drink wines)
        int missingHP = Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel();
        if (Combat.isInWild() && missingHP >= 8 && lastAteTick + 3 < Client.getGameTick()) { // todo an eat tick delay that doesnt return
            log("Drink");
            lastAteTick = Client.getGameTick();
            if (Widgets.isOpen()) Widgets.closeAll();
            Inventory.interact(ItemID.JUG_OF_WINE);
            return ReactionGenerator.getQuick();
        }

        // get to the spot
        if (!LavaDragonConst.location.safeTile.equals(Players.getLocal().getTile())) {
            if (!Combat.isInWild()) {
                slowLog("Pull wildy lever");
                if (!WILDY_LEVER_AREA.contains(Players.getLocal())) {
                    if (Walking.shouldWalk()) Walking.walk(WILDY_LEVER_AREA);
                    return ReactionGenerator.getNormal();
                }

                GameObject lever = GameObjects.closest("Lever");
                if (lever != null) lever.interact();
                Sleep.sleepUntil(Combat::isInWild, 2400);
                return ReactionGenerator.getNormal();
            }

            if (Walking.shouldWalk()) Walking.walk(LavaDragonConst.location.safeTile);
            return 75;
        }

        // handle avoid competition
        Player competition = Players.closest(x -> !x.getName().equals(Players.getLocal().getName()) && x.distance() < 4);
        if (avoidCompetition && competition != null) {
            log("Avoid competition " + competition.getName());
            LavaDragonScript.logout();
            return 75;
        }

        // handle gravestone
        NPC grave = NPCs.closest("Grave");
        if (grave != null && !Inventory.isFull() && grave.interact("Loot")) {
            Logger.info("Looted grave");
        }

        if (Equipment.contains(ItemID.STAFF_OF_WATER)) {
            Spell spell = LavaDragonLoadout.getSpell();
            if (Magic.getAutocastSpell() != spell) {
                log("Switch to " + spell);
                // this would continue training def after the max setting level is reached.
                if (Skills.getRealLevel(Skill.DEFENCE) < defenseTarget) {
                    log("def cast");
                    Magic.setDefensiveAutocastSpell(spell);
                } else {
                    Magic.setAutocastSpell(spell);
                }
                return 75;
            }
        } else {
            if (Equipment.contains(ItemID.TRIDENT_OF_THE_SEAS)) {
                boolean shouldDefCast = Skills.getRealLevel(Skill.DEFENCE) < defenseTarget;
                log("Trident " + shouldDefCast + " " + Combat.getCombatModeIndex());
                Combat.setCombatModeIndex(shouldDefCast ? 3 : 1);
            }

        }

        // kill a dragon
        Character target = Players.getLocal().getInteractingCharacter();
        if (target == null || !target.equals(dragon)) {
            log("Needs to attack dragon");
            if (dragon != null) {
                log("Attacking dragon");
                dragon.interact("Attack");
            } else {
                log("No dragon found");
            }
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onPlayerSpawn(Player player) {
        if (!Combat.isInWild()) return;
        if (player.distance() > 26) return;
        if (!CombatUtil.canAttackMe(player)) return;
        if (antiPKStrategy.pkClassifier.test(player)) {
            log("PKer spawn on tick " + Client.getGameTick());
            LavaDragonScript.logout();
        }
    }
}
