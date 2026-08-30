package org.dreambot.behaviour.combat;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.data.consumables.Food;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebPathResponse;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.IronFractal;
import org.dreambot.fractals.IronmanType;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Setter
@Getter
@Accessors(chain = true)
public class GenericCombat extends IronFractal {
    // an area one runs to when the hp threshold is reached
    Area restLocation;
    // i think this makes sense as int because most monsters have fixed max hit
    private int runAwayThreshold;
    private boolean healing;

    // area where mobs are located
    final Area mobLocation;
    final Supplier<NPC> enemySupplier;

    StyleSelector styleSelector = new MeleeStyleSelector();

    Supplier<GroundItem> lootSupplier;

    // plants you can pick for food in the rest area
    final List<String> plants = Arrays.asList(
            "Potato",
            "Cabbage"
    );

    // toggles picking up bones (While not in combat) & praying
    boolean trainPrayer = Calculations.chance(25);

    public GenericCombat setLootFilter(Filter<GroundItem> filter) {
        if (IronmanType.getCurrent() != IronmanType.NORMAL) {
            Filter<GroundItem> finalFilter = filter;
            filter = x -> finalFilter.match(x) && x.getOwnership() != 2;
        }
        Filter<GroundItem> finalFilter1 = filter;
        lootSupplier = () -> GroundItems.closest(i -> mobLocation.contains(i) && finalFilter1.match(i));
        return this;
    }

    public GenericCombat(BooleanSupplier acceptCondition, Area mobLocation, Filter<NPC> enemyFilter) {
        super(acceptCondition);
        this.mobLocation = mobLocation;
        this.enemySupplier = () -> NPCs.closest(x -> mobLocation.contains(x) && enemyFilter.match(x));
        setSimpleName("Combat");
    }

    @Override
    protected int onLoop() {
        int currentHp = Skill.HITPOINTS.getBoostedLevel();
        int maxHp = Skill.HITPOINTS.getLevel();

        if (currentHp == maxHp) healing = false;
        if (currentHp <= runAwayThreshold || healing) {
            healing = currentHp != maxHp;
            Food f = Food.getBestOnHand(false);
            log("Eat food " + f);
            if (f != null) {
                f.eat();

                return sleep();
            }

            if (restLocation == null) {
                warn("Rest location unspecified");
                return sleep();
            }

            if (restLocation.contains(Players.getLocal())) {
                log("In rest location");
                Item pickedFood = Inventory.get(x -> plants.contains(x.getName()));
                if (pickedFood != null) {
                    log("Eat " + pickedFood);
                    pickedFood.interact();
                    return sleep();
                }
                // pick plants
                GameObject plant = GameObjects.closest(x -> restLocation.contains(x) && plants.contains(x.getName()));
                if (plant != null) {
                    log("Pickable plant " + plant);
                    plant.interact();
                }
                return sleep();
            }

            log("Go to rest location");
            if (Walking.shouldWalk()) Walking.walk(restLocation);
            return sleep();
        }

        Player localPlayer = Players.getLocal();
        if (styleSelector.setStyle()) return sleep();

        if (!mobLocation.contains(localPlayer)) {
            log("Walk to mob location");
            if (Walking.shouldWalk()) Walking.walk(mobLocation);
            return sleep();
        }

        GroundItem loot = lootSupplier.get();
        if (loot != null && (!Inventory.isFull() || (loot.getItem().isStackable() && Inventory.contains(loot.getId())))) {
            log("Looting " + loot.getName());
            loot.interact();
            return sleep();
        }

        if (localPlayer.isInCombat()) {
            log("We're in combat");
            return sleep();
        }

        if (trainPrayer) {
            Item invBones = Inventory.get(x -> x.getName().toLowerCase().contains("bones"));
            if (invBones != null) {
                log("Bury bones");
                invBones.interact("Bury");
                return sleep();
            }

            boolean isNormal = IronmanType.getCurrent() == IronmanType.NORMAL;
            GroundItem bones;
            if (isNormal) {
                bones = GroundItems.closest(x -> x.canReach() && mobLocation.contains(x)
                        && x.getName().toLowerCase().contains("bones"));
            } else {
                bones = GroundItems.closest(x -> x.canReach() && mobLocation.contains(x)
                        && x.getOwnership() != 2 && x.getName().toLowerCase().contains("bones"));
            }

            if (bones != null && !Inventory.isFull()) {
                log("Taking bones");
                bones.interact();
                return sleep();
            }
        }

        NPC npc = enemySupplier.get();
        if (npc == null) {
            log("Unable to find npc");
            return sleep();
        }

        log("Found npc " + npc);
        npc.interact("Attack");
        return sleep();
    }
}
