package org.dreambot.behaviour.training.slayer.behaviour;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.misc.SmartLootEvent;
import org.dreambot.behaviour.training.slayer.CannonHelper;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Accessors(chain = true)
@Setter
public class StandardCombat extends Fractal {
    Filter<GroundItem> lootFilter;
    Integer[] dropIds = new Integer[0];
    Area area;
    //    WalkCondition walkCondition = () -> false;
    Supplier<NPC> npcSupplier;
    boolean switchCombats = true;
    List<Integer> foodID;
    Supplier<Boolean> hopCondition;
    Supplier<Integer> sleepSupplier;
    Tile cannonTile = null;
    Prayer overhead = null;
    int eatPercentThreshold = 70;
    List<Integer> alchableIds = new ArrayList<>();

    public StandardCombat setAlchableIds(Integer... alchableIds) {
        this.alchableIds = Arrays.asList(alchableIds);
        return this;
    }

    public StandardCombat(Supplier<Boolean> acceptCondition, Area area, Supplier<NPC> npcSupplier, Integer... foodID) {
        this.acceptCondition = acceptCondition;
        this.area = area;
        this.npcSupplier = npcSupplier;
        this.foodID = Arrays.asList(foodID);
    }

    public StandardCombat(Area area, Supplier<NPC> npcSupplier, Integer... foodID) {
        this.area = area;
        this.npcSupplier = npcSupplier;
        this.foodID = Arrays.asList(foodID);
    }

    public StandardCombat(Area area, String npcName, Integer... foodID) {
        this.area = area;
//        this.npcName = npcName;
        this.npcSupplier = () -> NPCs.closest(x -> x.getName().equals(npcName)
                && !x.isInCombat() && (area == null || area.contains(x))
                && x.getHealthPercent() > 0);
        this.foodID = Arrays.asList(foodID);
    }

    public StandardCombat(Area area, List<String> npcNames, Integer... foodID) {
        this.area = area;
//        this.npcName = npcName;
        this.npcSupplier = () -> NPCs.closest(x -> npcNames.contains(x.getName())
                && !x.isInCombat() && (area == null || area.contains(x))
                && x.getHealthPercent() > 0);
        this.foodID = Arrays.asList(foodID);
    }

    public StandardCombat(Area area, String npcName, boolean switchCombats) {
        this.area = area;
        this.npcSupplier = () -> NPCs.closest(x -> x.getName().equals(npcName)
                && !x.isInCombat()
                && (area == null || area.contains(x)) && x.getHealthPercent() > 0);
        this.switchCombats = switchCombats;
    }

    @Getter
    @Setter
    private Supplier<CombatStyle> styleSupplier = () -> {
        int atk = Skills.getRealLevel(Skill.ATTACK);
        int str = Skills.getRealLevel(Skill.STRENGTH);
        int def = Skills.getRealLevel(Skill.DEFENCE);
        if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
        if (atk <= def) return CombatStyle.ATTACK;
        return CombatStyle.DEFENCE;
    };

    // we are overriding this so its only enforced when we want it
    // we also always check that we are in the relevant area before hopping
    @Override
    public Fractal setHopCondition(Supplier<Boolean> hopCondition) {
        this.hopCondition = hopCondition;
        return this;
    }

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) {
            log("Dialogue solve");
            Dialog.solve("Okay", "");
            return ReactionGenerator.getNormal();
        }

        if (Magic.isSpellSelected()) {
            Logger.info("std combat deselect spell");
            Magic.deselect();
        }

        if (Combat.getHealthPercent() < eatPercentThreshold) {
            log("Std eat");
            Inventory.interact(x -> foodID.contains(x.getId()), "Eat");
        }

        if (area != null && !area.contains(Players.getLocal())) {
            slowLog("Std walk");
            if (Walking.shouldWalk(6)) Walking.walk(area);
            return ReactionGenerator.getQuick();
        }

        if (overhead != null && Skills.getBoostedLevel(Skill.PRAYER) > 1 && !Prayers.isActive(overhead)) {
            log("pray overhead");
            Prayers.toggle(true, overhead);
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) < 5) {
            Item pot = ItemVariants.PRAYER_POTION.getItem();
            if (pot != null && pot.interact("Drink")) {
                log("Drank prayer");
                Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) > 5, 1400);
            }
        }


        if (lootFilter != null && (!Inventory.isFull() || Inventory.contains(dropIds))) {
            List<GroundItem> loot = GroundItems.all(x -> lootFilter.match(x) && (area == null || area.contains(x)));
            if (!loot.isEmpty()) {
                log("Taking loot");
                if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 10) Walking.toggleRun();
                new SmartLootEvent(() -> GroundItems.all(x -> lootFilter.match(x) && (area == null || area.contains(x))), dropIds)
                        .executed();
                return ReactionGenerator.getNormal();
            }
        }

        if (Players.getLocal().isInCombat()) {
            if (Players.getLocal().getInteractingCharacter() == null) {
                Entity attackingMe = Players.getLocal().getCharacterInteractingWithMe();
                if (attackingMe != null) {
                    log("Being attacked");
                    if (Inventory.isItemSelected()) Inventory.deselect();
                    attackingMe.interact("Attack");
                    Sleep.sleepUntil(() -> Players.getLocal().getInteractingCharacter() != null, 2400);
                }
            }
            return ReactionGenerator.getQuick();
        }

        Item alchable = Inventory.get(x -> alchableIds.contains(x.getId()));
        if (Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY) && alchable != null) {
            log("Alching item");
            Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY, alchable);
            return ReactionGenerator.getNormal();
        }


        // switch combat style
        CombatStyle style = styleSupplier.get();
        if (Combat.getCombatStyle() != style && !Equipment.contains(ItemID.ABYSSAL_WHIP)) {
            Logger.info("Set attack style: " + style);
            Combat.setCombatStyle(style);
            return ReactionGenerator.getNormal();
        }

        Player localPlayer = Players.getLocal();
        List<Player> otherPlayer = Players.all(p -> (area == null || area.contains(p)) && p != localPlayer && p.distance() < 12);
        if (otherPlayer.size() > 5) {
            log("World hop");
            WorldHopper.hopWorld(Worlds.getRandomWorld(w -> (Client.isMembers() != w.isF2P())
                    && w.isNormal() && w.getMinimumLevel() < Skills.getTotalLevel()));
            return ReactionGenerator.getNormal();
        }

        if (!Players.getLocal().isInCombat()) {
            if (hopCondition != null && hopCondition.get() && this.worldSupplier != null) {
                Calculations.setRandomSeed(System.currentTimeMillis());
                WorldHopper.hopWorld(this.worldSupplier.get());
                return ReactionGenerator.getNormal();
            }
            Logger.info("Attack mob");
            NPC mob = null;
//            if (npcName != null) {
//                mob = NPCs.closest(x -> x.getName().equals(npcName) && !x.isInCombat() && (area == null || area.contains(x)) && x.getHealthPercent() > 0);
//            }
            if (npcSupplier != null) {
                mob = npcSupplier.get();
            }
            Logger.info("Standard combat - attacking: " + mob);
            if (Walking.getRunEnergy() > 30 && !Walking.isRunEnabled()) Walking.toggleRun();
            if (Inventory.isItemSelected()) Inventory.deselect();
            if (mob != null && mob.interact("Attack")) {
                Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 5000);
                return ReactionGenerator.getNormal();
            }
        }
        return ReactionGenerator.getNormal();
    }

    public StandardCombat setLootStrategy(Filter<GroundItem> lootFilter, Integer... dropIds) {
        this.lootFilter = lootFilter;
        this.dropIds = dropIds;
        return this;
    }

    private int sleep() {
        return sleepSupplier == null ? ReactionGenerator.getNormal() : sleepSupplier.get();
    }

    public StandardCombat setCannonTile(int x, int y) {
        this.cannonTile = new Tile(x, y);
        return this;
    }

    public StandardCombat setCannonTile(int x, int y, int z) {
        this.cannonTile = new Tile(x, y, z);
        return this;
    }
}
