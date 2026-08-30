package org.dreambot.behaviour.dragons;


import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.data.NpcID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Dragons extends Fractal {
    public static final int QUANTITY_MULTIPLIER = 5;

    public Dragons(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
//        this.inventoryLoadout = new InventoryLoadout()
//                .addItem(ItemID.AIR_RUNE, 5, 1500).setRefill(1500 * QUANTITY_MULTIPLIER)
//                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 75 || ScriptSettings.getSettingsData().staffMode == StaffMode.FIRE_STAFF)
//                .addItem(ItemID.CHAOS_RUNE, 1, 500).setRefill(500 * QUANTITY_MULTIPLIER)
//                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 75 || ScriptSettings.getSettingsData().staffMode == StaffMode.FIRE_STAFF)
//                .addItem(ItemID.JUG_OF_WINE, 1, 12).setRefill(1000)
//                .addItem(ItemID.KNIFE, 1).setRefill(5)
////                .setStrict(() -> !Combat.isInWild())
//        ;

//        this.equipmentLoadout = new EquipmentLoadout()
//                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
//                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 70 || !ScriptSettings.getSettingsData().useOccult)
//                .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
//                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70 && ScriptSettings.getSettingsData().useOccult)
//                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
//                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 75 || ScriptSettings.getSettingsData().staffMode == StaffMode.FIRE_STAFF)
//                .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT)
//                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 75 && ScriptSettings.getSettingsData().staffMode == StaffMode.TRIDENT)
//        ;

    }


    private List<GroundItem> getLootList() {
        NPC dragon = NPCs.closest(NpcID.LAVA_DRAGON);

        List<GroundItem> loot = GroundItems.all().stream()
                .filter(x -> x.distance() < 8)
                .filter(x -> dragon == null || x.distance(dragon) > 8)
                .filter(x -> x.getID() != ItemID.BURNT_BONES)
                .filter(x -> LivePrices.getHigh(x.getID()) * x.getAmount() > ScriptSettings.getMinLootValue() || !x.getItem().isTradable())
                .filter(Entity::canReach)
                .filter(x -> !ItemVariants.LOOTING_BAG.contains(x.getID()) || ScriptSettings.getSettingsData().useLootingBag)
                .collect(Collectors.toList());

        return loot;
    }

    private GroundItem getLoot() {
        return getLootList().stream().findFirst().orElse(null);
    }

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            Dialog.solve();
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.STAFF_OF_FIRE) && Equipment.isSlotEmpty(EquipmentSlot.WEAPON)) {
            Logger.info("Equipping fire staff");
            Inventory.interact(ItemID.STAFF_OF_FIRE);
            return ReactionGenerator.getQuick();
        }

        // custom gear seemed to cause issues equipping trident in loadouts, equip if we got here and send me an annoying ass webhook about it
        Item trident = ItemVariants.TRIDENT.getItem();
        if (trident != null && Equipment.isSlotEmpty(EquipmentSlot.WEAPON)) {
            Logger.info("Equipping trident");
            trident.interact();
            return ReactionGenerator.getQuick();
        }

        if (Inventory.contains(ItemID.OCCULT_NECKLACE)) {
            Inventory.interact(ItemID.OCCULT_NECKLACE);
            return ReactionGenerator.getQuick();
        }

        if (Inventory.contains(ItemID.JUG)) {
            Inventory.dropAll(ItemID.JUG);
            return ReactionGenerator.getQuick();
        }

        if (Inventory.contains(ItemID.LOOTING_BAG_CLOSED)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Opening looting bag");
            Inventory.interact(ItemID.LOOTING_BAG_CLOSED, "Open");
            Sleep.sleepUntil(() -> AntiPk.getThreat() != null || !Inventory.contains(ItemID.LOOTING_BAG_OPENED), 1800);
            return ReactionGenerator.getNormal();
        }

//        Character c = Players.getLocal().getInteractingCharacter();
//        if (ScriptSettings.getSettingsData().attackCompetition && c instanceof Player) {
//            Logger.info("Killing Player");
//            return ReactionGenerator.getNormal();
//        }

        NPC dragon = LocationConfig.getDragon();

        GroundItem loot = getLoot();
        if (dragon == null && loot != null) {
            if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 10) Walking.toggleRun();
            SmartLootEvent.Response r = new SmartLootEvent(this::getLootList, ItemID.JUG_OF_WINE, ItemID.JUG)
                    .setBreakCondition(() -> AntiPk.getThreat() != null)
                    .executed();
            Logger.info("Smart looting: " + r);
            return ReactionGenerator.getQuick();
        }

        if (!LocationConfig.getSafeTile().equals(Players.getLocal().getTile())) {
//            Logger.info("Walking to safe tile - if this is breaking use no click walk");
            if (!Walking.isRunEnabled() && Players.getLocal().isInCombat() && Walking.getRunEnergy() > 3) {
                Logger.info("Being attacked, booking it");
                Walking.toggleRun();
            }

            if (LocationConfig.getSafeTile().distance() < 8) {
                Walking.clickTileOnMinimap(LocationConfig.getSafeTile());
                return ReactionGenerator.getQuick();
            }

            if (Walking.shouldWalk(6)) Walking.walkExact(LocationConfig.getSafeTile());
            return ReactionGenerator.getQuick();
        }


        if (ScriptSettings.getSettingsData().avoidCompetition && dragon != null && !Players.getLocal().isInCombat()) {
            Player opps = Players.closest(x -> !x.getName().equals(Players.getLocal().getName()) && x.isInteracting(dragon));
            if (opps != null) {
                if (ScriptSettings.getSettingsData().attackCompetition && AntiPk.canAttackMe(opps)) {
                    opps.interact("Attack");
                    Sleep.sleepUntil(() -> AntiPk.getThreat() != null || Players.getLocal().isAnimating(), 1600);
                    return ReactionGenerator.getQuick();
                }

                WorldHopper.hopWorld(Worlds.getRandomWorld(x -> !x.isF2P()
                        && x.getMinimumLevel() < Skills.getTotalLevel()
                        && x.isNormal()));
                return ReactionGenerator.getQuick();
            }
        }


        boolean shouldBeDefensive = Skills.getRealLevel(Skill.DEFENCE) < ScriptSettings.getSettingsData().defenceTarget;
        boolean isDefensive = Magic.isAutocastDefensive();
        if (Equipment.contains(ItemVariants.TRIDENT.getIds()) || Equipment.contains(ItemVariants.SCEPTRE.getIds())) {
            Combat.setCombatStyle(shouldBeDefensive ? CombatStyle.DEFENCE : CombatStyle.ATTACK);
        }
        if ((Magic.getAutocastSpell() != getSpell() || shouldBeDefensive != isDefensive)
                && Equipment.contains(ItemID.STAFF_OF_WATER)) {
            if (shouldBeDefensive) {
                Magic.setDefensiveAutocastSpell(getSpell());
            } else {

                Magic.setAutocastSpell(getSpell());
            }
            return ReactionGenerator.getNormal();
        }

        NPC grave = NPCs.closest("Grave");
        if (grave != null && !Inventory.isFull() && grave.interact("Loot")) {
            Logger.info("Looted grave");
        }

        if (!Players.getLocal().isInCombat()) {
            if (dragon != null && dragon.interact("Attack")) {
                Sleep.sleepUntil(() -> AntiPk.getThreat() != null || Players.getLocal().isInCombat(), 1200);
                return ReactionGenerator.getQuick();
            }
        }

        if (!WorldHopper.isWorldHopperOpen()) WorldHopper.openWorldHopper();
        return ReactionGenerator.getQuick();
    }


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

    private Spell getSpell() {
        return bestWaterSpell.get();
    }
}
