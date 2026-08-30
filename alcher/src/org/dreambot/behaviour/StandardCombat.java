package org.dreambot.behaviour;

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
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
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
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

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
    String npcName;
    Supplier<NPC> npcSupplier;
    boolean switchCombats = true;
    List<Integer> foodID;
    Supplier<Boolean> hopCondition;
    Supplier<Integer> sleepSupplier;
    Tile cannonTile = null;
    Prayer overhead = null;

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
        this.npcName = npcName;
        this.foodID = Arrays.asList(foodID);
    }

    public StandardCombat(Area area, String npcName, boolean switchCombats) {
        this.area = area;
        this.npcName = npcName;
        this.switchCombats = switchCombats;
    }

    @Getter
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
            Dialog.solve();
            return ReactionGenerator.getNormal();
        }

        if (Combat.getHealthPercent() < 70) {
            Inventory.interact(x -> foodID.contains(x.getID()), "Eat");
            return ReactionGenerator.getNormal();
        }

        if (area != null && !area.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(area);
            return ReactionGenerator.getQuick();
        }

        if (overhead != null && !Prayers.isActive(overhead)) {
            Prayers.toggle(true, overhead);
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) < 5) {
            Item pot = ItemVariants.PRAYER_POTION.getItem();
            if (pot != null && pot.interact("Drink")) {
                Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) > 5, 1400);
            }
        }


        if (lootFilter != null && (!Inventory.isFull() || Inventory.contains(dropIds))) {
            List<GroundItem> loot = GroundItems.all(x -> lootFilter.match(x) && inArea(x));
            if (loot != null && !loot.isEmpty()) {
                if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 10) Walking.toggleRun();
                return ReactionGenerator.getNormal();
            }
        }

        if (Players.getLocal().isInCombat()) {
            return ReactionGenerator.getQuick();
        }

        // switch combat style
        CombatStyle style = styleSupplier.get();
        if (Combat.getCombatStyle() != style && !Equipment.contains(ItemID.ABYSSAL_WHIP)) {
            Logger.info("Set attack style: " + style);
            Combat.setCombatStyle(style);
            return ReactionGenerator.getNormal();
        }

        Player localPlayer = Players.getLocal();
        List<Player> otherPlayer = Players.all(p -> inArea(p) && p != localPlayer && p.distance() < 12);
        if (otherPlayer.size() > 5) {
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
            if (npcName != null) {
                mob = NPCs.closest(x -> x.getName().equals(npcName) && !x.isInCombat() && inArea(x) && x.getHealthPercent() > 0);
            }
            if (npcSupplier != null) {
                mob = npcSupplier.get();
            }
            Logger.info("Standard combat - attacking: " + mob);
            if (Walking.getRunEnergy() > 30 && !Walking.isRunEnabled()) Walking.toggleRun();
            if (mob != null && mob.interact("Attack")) {
                Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 5000);
                return ReactionGenerator.getNormal();
            }
        }
        return ReactionGenerator.getNormal();
    }

    public StandardCombat setLootStrategy(Filter<GroundItem> lootFilter, Integer... lootIds) {
        this.lootFilter = lootFilter;
        this.dropIds = lootIds;
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

    private boolean inArea(Entity e) {
        return area == null || area.contains(e);
    }
}
