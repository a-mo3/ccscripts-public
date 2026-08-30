package org.dreambot.fractals;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.antiban.AntibanSettings;
import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.fractals.events.EventExitCondition;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.*;
import java.util.function.Supplier;

@Accessors(chain = true)
@Setter
@Getter
public class Fractal implements EventListener {
    /**
     * Once the accept condition is false,
     * because GUI settings are generated off the script tree we dont actually want to remove them
     * or once you pass a training stage the settings are removed,
     * cleanedSoSkip = true means isValid wont be checked.
     */
    boolean cleanAfterAccomplished = false;
    private boolean cleanedSoSkip = false;
    protected Supplier<Boolean> acceptCondition = () -> true;
    protected List<Fractal> children = new LinkedList<>();
    protected InventoryLoadout inventoryLoadout = null;
    protected EquipmentLoadout equipmentLoadout = null;
    // can use this to add logic to generic org.dreambot.fractals or skilling org.dreambot.fractals that require walking out of an area prior
    // true = return normal false = continue with loadouts & onloop
    /**
     * if true return before exec fractal
     */
    protected Supplier<Boolean> prependLogic = null;
    protected Supplier<Boolean> afterLoadouts = null;
    // to give org.dreambot.fractals their own paintinfo
    protected Supplier<String[]> paintArraySupplier;
    private String simpleName = "Fractal";
    protected Supplier<Boolean> hopCondition;
    protected Supplier<World> worldSupplier = () -> Worlds.getRandomWorld(w -> w.isNormal() && w.isF2P());
    protected Supplier<Boolean> eventBreakCondition = () -> false;
    protected Supplier<Boolean> loadoutCondition = () -> true; // do the loadout only if this is true
    protected float muleRequestMultiplier = 1.25f; // price of loadout * this is how much gold u mule
    private Timer slowLogTimer = new Timer(1500);

    public Fractal(Supplier<Boolean> acceptCondition) {
        this.eventBreakCondition = () -> !isValid() || NPCs.closest("Death") != null;
        this.acceptCondition = acceptCondition;
    }

    public Fractal() {
    }

    public Fractal addChildren(Fractal... childFractals) {
        Collections.addAll(this.children, childFractals);
        return this;
    }

    public boolean isValid() {
        return acceptCondition.get();
    }

    public int onLoop() {
        Logger.log("Hitting the fractal onLoop " + simpleName);
        return 100;
    }

    public static List<String> tempHierarchy = new ArrayList<>();

    // set by the antiban fractal constructor
    public static AntibanSettings antibanSettings = null;

    public int run() {
        tempHierarchy.add(this.simpleName);
        if (paintArraySupplier != null) FractalAPI.paintArrSupplier = paintArraySupplier;
        if (children.isEmpty()) {
            // this is a child node.
            FractalAPI.hierarchy.clear();
            FractalAPI.hierarchy.addAll(tempHierarchy);
            tempHierarchy.clear();
            if (prependLogic != null && prependLogic.get()) return ReactionGenerator.getNormal();
            if (!enforceFractalSettings()) return ReactionGenerator.getNormal();
            if (afterLoadouts != null && afterLoadouts.get()) return ReactionGenerator.getNormal();
            if (hopCondition != null && hopCondition.get() && worldSupplier != null)
                WorldHopper.hopWorld(worldSupplier.get());
            int i = onLoop();

            // antiban actions, proven effective in the may IM beta tests
            if (antibanSettings != null) {
                // off screen
                if (Calculations.chance(Math.min(antibanSettings.mouseOffChance, 100))) {
                    Mouse.moveOutsideScreen(Calculations.chance(50));
                    return i;
                } else if (Calculations.chance(Math.min(antibanSettings.hoverEntity, 100))) {
                    log("Antiban test - hover - b flag");
                    List<Entity> a = new ArrayList<>(NPCs.all(x -> x.distance() < 7 && x.isOnScreen()));
                    a.addAll(Players.all(x -> x.distance() < 7 && x.isOnScreen()));
                    a.addAll(GameObjects.all(o -> o.distance() > 4 && o.distance() < 9));
                    // we check is on screen because i dont want to rotate and therefore cause mouse gaussian
                    if (a.size() > 3) {
                        Entity e = a.get(Calculations.random(a.size()));
                        Mouse.move(e);
                        Sleep.sleep(i / 4);
                    }
                    return i;
                }
            }

            return i;
        }


        children.removeIf(x -> {
            if (x.cleanAfterAccomplished && !cleanedSoSkip && !x.isValid()) {
                log("Clean " + x.getSimpleName());
                return true;
            }
            return false;
        });

        Fractal firstValid = children.stream()
                .filter(x -> !x.cleanedSoSkip)
                .filter(Fractal::isValid)
                .findFirst()
                .orElse(null);
        if (firstValid == null) {
            log("No valid children " + simpleName);
            tempHierarchy.clear();
            return ReactionGenerator.getNormal();
        }
        return firstValid.run();
    }


    public void deregisterListeners() {
        Client.getInstance().removeEventListener(this);
        for (Fractal child : children) {
//            Logger.info("Deregistering: " + child.getClass().getSimpleName());
            child.deregisterListeners();
        }
    }

    public void registerListeners() {
        Client.getInstance().addEventListener(this);

        for (Fractal child : children) {
//            Logger.info("Registering: " + child.getClass().getSimpleName());
            child.registerListeners();
            return;
        }
    }


    // enabled if users used the swap ids flag so ill know if they're causing themselves problems
    public static boolean swappedSomething;

    public void log(String message) {
        if (swappedSomething)
            Logger.log(String.format("[%s] %s", this, message) + " (swapped ids btw)");
        else
            Logger.log(String.format("[%s] %s", this, message));
    }

    /**
     * log for something that otherwise would be spammed to shit
     *
     * @param message message to be logged
     */
    public void slowLog(String message) {
        if (slowLogTimer.finished()) {
            Logger.log(String.format("[%s] %s", this, message));
            slowLogTimer.reset();
        }
    }

    /**
     * generally you give an accept condition supplier to check if below a certain level, once above that level you can prune them
     */
    public void prune() {
        children.removeIf(child -> child.acceptCondition != null && !child.acceptCondition.get());
    }

    @Override
    public String toString() {
        return simpleName;
    }

    /**
     * equips all loadouts, make mule request if it cannot afford them
     *
     * @return true if everything is enforced
     */
    private boolean enforceFractalSettings() {
        if (loadoutCondition.get()) {
            if (inventoryLoadout != null && !inventoryLoadout.isFulfilled()) {
                Logger.info("Fractal inventory loadout " + new WithdrawLoadoutEvent(inventoryLoadout, equipmentLoadout)
                        .addExitCondition(new EventExitCondition(eventBreakCondition, "Fractal break"))
                        .executed());
                // loadouts request their own gp from mule on dreambot
                return false;
            }

            if (equipmentLoadout != null && !equipmentLoadout.isFulfilled()) {
                Logger.info("Fractal equipment loadout " + new WithdrawLoadoutEvent(inventoryLoadout, equipmentLoadout)
                        .addExitCondition(new EventExitCondition(eventBreakCondition, "Fractal break"))
                        .executed());
                return false;
            }
        }

        return true;
    }
}