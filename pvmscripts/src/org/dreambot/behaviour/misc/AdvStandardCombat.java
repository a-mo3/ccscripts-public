package org.dreambot.behaviour.misc;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.data.ActionMode;
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
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.HitSplatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
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
public class AdvStandardCombat extends Fractal implements HitSplatListener {
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
    Timer lastTimeAttacking = new Timer(400);
    boolean flickPrayer = false;
    boolean flickBoosts = false;
    Timer overheadPrayerFlickTimer = new Timer(0);
    Timer boostPrayerFlickTimer = new Timer(0);
    @Setter
    Filter<Entity> reAggroCheck = null;

    Prayer boostPrayer;

    public AdvStandardCombat setFlickBoostTiming(int attackSpeed, Prayer boostPrayer) {
        boostPrayerFlickTimer = new Timer(attackSpeed);
        flickBoosts = true;
        this.boostPrayer = boostPrayer;
        Client.getInstance().addEventListener(this); // i hope dreambot only lets you add one or i never use this method twice
        return this;
    }

    public AdvStandardCombat setFlickTiming(int attackSpeed) {
        overheadPrayerFlickTimer = new Timer(attackSpeed);
        flickPrayer = true;
        Client.getInstance().addEventListener(this); // i hope dreambot only lets you add one or i never use this method twice
        return this;
    }

    public AdvStandardCombat(Supplier<Boolean> acceptCondition, Area area, Supplier<NPC> npcSupplier, Integer... foodID) {
        this.acceptCondition = acceptCondition;
        this.area = area;
        this.npcSupplier = npcSupplier;
        this.foodID = Arrays.asList(foodID);
    }

    public AdvStandardCombat(Area area, Supplier<NPC> npcSupplier, Integer... foodID) {
        this.area = area;
        this.npcSupplier = npcSupplier;
        this.foodID = Arrays.asList(foodID);
    }

    public AdvStandardCombat(Area area, String npcName, Integer... foodID) {
        this.area = area;
        this.npcName = npcName;
        this.foodID = Arrays.asList(foodID);
    }

    public AdvStandardCombat(Area area, String npcName, boolean switchCombats) {
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
        if (Players.getLocal().getInteractingCharacter() != null) lastTimeAttacking.reset();
        if (Dialogues.inDialogue()) {
            log("dialog solve");
            Dialog.solve("Okay", "");
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.getNPCAttackOptionsMode() == ActionMode.HIDDEN) {
            Logger.info("unhide npc attack option");
            ClientSettings.setNPCAttackOptionsMode(ActionMode.DEPENDS_ON_COMBAT_LEVELS);
        }

        if (Combat.getHealthPercent() < 70 && Inventory.contains(x -> foodID.contains(x.getId()))) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Inventory.interact(x -> foodID.contains(x.getId()), "Eat");
            return ReactionGenerator.getNormal();
        }

        if (area != null && !area.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(area);
            return ReactionGenerator.getQuick();
        }

        if (flickPrayer) Prayers.toggle(overheadPrayerFlickTimer.finished(), overhead);

        if (overhead != null && !Prayers.isActive(overhead)) {
            Prayers.toggle(overheadPrayerFlickTimer.finished(), overhead);
        }

//        if (flickBoosts && Skills.getBoostedLevel(Skill.PRAYER) > 0 && Players.getLocal().getInteractingCharacter() != null)
//            Prayers.toggle(boostPrayerFlickTimer.finished(), boostPrayer);

        if (Skills.getBoostedLevel(Skill.PRAYER) < 5) {
            Item pot = ItemVariants.PRAYER_POTION.getItem();
            if (pot != null && pot.interact("Drink")) {
                Antiban.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) > 5, 1400);
            }
        }

        if (Skills.getBoostedLevel(Skill.STRENGTH) - Skills.getRealLevel(Skill.STRENGTH) < 1) {
            Item pot = ItemVariants.SUPER_COMBAT_POTION.getItem();
            if (pot != null && pot.interact("Drink")) {
                Antiban.sleepUntil(() -> Skills.getBoostedLevel(Skill.STRENGTH) - Skills.getRealLevel(Skill.STRENGTH) > 5, 1400);
            }
        }


        if (lootFilter != null && (!Inventory.isFull() || Inventory.contains(dropIds))) {
            List<GroundItem> loot = GroundItems.all(x -> lootFilter.match(x) && inArea(x));
            if (loot != null && !loot.isEmpty()) {
                if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 10) Walking.toggleRun();
                SmartLootEvent.Response r = new SmartLootEvent(() -> GroundItems.all(x -> lootFilter.match(x) && inArea(x)), dropIds)
                        .executed();
                Logger.info("Smart looting: " + r);
                return ReactionGenerator.getNormal();
            }
        }

        if (shouldReagro()) {
            Entity enemy = Players.getLocal().getCharactersInteractingWithMe()
                    .stream()
                    .filter(x -> x.hasAction("Attack") && x instanceof NPC && !x.getName().contains("Spirit")) // ignore spiritual creatures and pets
                    .findFirst().orElse(null);
            Logger.info("reAgroo " + enemy);
            if (enemy != null && enemy.hasAction("Attack") && (area == null || area.contains(enemy))) {
                log("Enemy");
                if (reAggroCheck == null || reAggroCheck.match(enemy)) {
                    enemy.interact("Attack");
                    Sleep.sleepUntil(() -> Players.getLocal().getInteractingCharacter() != null, 1600);
                    return ReactionGenerator.getQuick();
                }
            }
        }

        if (Players.getLocal().isInCombat() && Players.getLocal().getInteractingCharacter() != null) {
            log("in combat");
            return ReactionGenerator.getQuick();
        }

        // switch combat style
        CombatStyle style = styleSupplier.get();
        if (Combat.getCombatStyle() != style && !Equipment.contains(ItemID.ABYSSAL_WHIP)) {
            log("Set attack style: " + style);
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
        return ReactionGenerator.getNormal();
    }

    public AdvStandardCombat setLootStrategy(Filter<GroundItem> lootFilter, Integer... lootIds) {
        this.lootFilter = lootFilter;
        this.dropIds = lootIds;
        return this;
    }

    private int sleep() {
        return sleepSupplier == null ? ReactionGenerator.getNormal() : sleepSupplier.get();
    }

    public AdvStandardCombat setCannonTile(int x, int y) {
        this.cannonTile = new Tile(x, y);
        return this;
    }

    public AdvStandardCombat setCannonTile(int x, int y, int z) {
        this.cannonTile = new Tile(x, y, z);
        return this;
    }

    private boolean inArea(Entity e) {
        return area == null || area.contains(e);
    }

    // methods to attack faster after looting / eating and not just rely on isInCombat
    private boolean shouldReagro() {
        Player lp = Players.getLocal();
        return lp.getInteractingCharacter() == null && lp.getCharacterInteractingWithMe() != null;
    }


    @Override
    public void onHitSplatAdded(Entity entity, int type, int damage, int id, int special, int gameCycle) {
        if (entity.equals(Players.getLocal())) {
            overheadPrayerFlickTimer.reset();
        }

        Entity target = Players.getLocal().getInteractingCharacter();
        if (entity.equals(target)) boostPrayerFlickTimer.reset();
    }
}
