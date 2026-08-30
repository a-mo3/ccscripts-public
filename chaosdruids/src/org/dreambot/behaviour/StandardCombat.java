package org.dreambot.behaviour;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.script.ScriptSettings;
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

    public StandardCombat(Supplier<Boolean> acceptCondition, Area area, Supplier<NPC> npcSupplier, Integer... foodID) {
        this.acceptCondition = acceptCondition;
        this.area = area;
        this.npcSupplier = npcSupplier;
        this.foodID = Arrays.asList(foodID);
    }


    public StandardCombat(Supplier<Boolean> acceptCondition, Area area, String npcName, Integer... foodID) {
        this.acceptCondition = acceptCondition;
        this.area = area;
        this.npcSupplier = () -> NPCs.closest(x -> x.getName().contains(npcName)
                && x.hasAction("Attack")
                && x.canReach() && area.contains(x));
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
        int limit = ScriptSettings.getSettingsData().combatTargets;
//        Logger.info("atk: " + atk + " str: " + str + " def: " + def);
        if (def + 9 < atk || (atk >= limit && str >= limit)) {
            return CombatStyle.DEFENCE;
        }
        if (atk + 9 <= str || def >= atk || (str >= limit)) {
            return CombatStyle.ATTACK;
        }
        if (atk >= str) return CombatStyle.STRENGTH;
        return Combat.getCombatStyle();
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

        if (!area.contains(Players.getLocal())) {
            if (Walking.shouldWalk(6)) Walking.walk(area);
            return ReactionGenerator.getQuick();
        }

        if (Combat.getHealthPercent() < 70) {
            Inventory.interact(x -> foodID.contains(x.getID()), "Eat");
            return ReactionGenerator.getNormal();
        }

        if (lootFilter != null) {
            GroundItem loot = GroundItems.closest(lootFilter);
            if (loot != null) {
                if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 10) Walking.toggleRun();
                SmartLootEvent.Response r = new SmartLootEvent(() -> GroundItems.all(lootFilter), dropIds)
                        .executed();
                Logger.info("Smart looting: " + r);
                return ReactionGenerator.getNormal();
            }
        }


        if (Players.getLocal().isInCombat()) {
            return ReactionGenerator.getQuick();
        }

        // switch combat style
        CombatStyle style = styleSupplier.get();
        if (Combat.getCombatStyle() != style) {
            Logger.info("Set attack style: " + style);
            Combat.setCombatStyle(style);
            return ReactionGenerator.getNormal();
        }

        Player localPlayer = Players.getLocal();
        List<Player> otherPlayer = Players.all(p -> area.contains(p) && p != localPlayer && p.distance() < 12);
        if (otherPlayer.size() > 5) {
            WorldHopper.hopWorld(Worlds.getRandomWorld(w -> (Client.isMembers() == w.isMembers())
                    && w.isNormal()
                    && w.getMinimumLevel() < Skills.getTotalLevel())
            );
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
                mob = NPCs.closest(x -> x.getName().equals(npcName) && !x.isInCombat() && area.contains(x) && x.getHealthPercent() > 0);
            }
            if (npcSupplier != null) {
                mob = npcSupplier.get();
            }
            Logger.info("Standard combat - attacking: " + mob);
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
}
