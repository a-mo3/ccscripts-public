package org.dreambot.fractals;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.LoadoutExecutor;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Collections;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@Accessors(chain = true)
public class Fractal implements EventListener {
    protected Supplier<Boolean> acceptCondition;
    @Getter
    protected List<Fractal> children = new LinkedList<>();
    @Setter
    protected InventoryLoadout inventoryLoadout = null;
    @Setter
    protected EquipmentLoadout equipmentLoadout = null;
    // can use this to add logic to generic org.dreambot.fractals or skilling org.dreambot.fractals that require walking out of an area prior
    // true = return normal false = continue with loadouts & onloop
    @Setter
    /**
     * if true return before exec fractal
     */
    protected Supplier<Boolean> appendLogic = null;
    // to give org.dreambot.fractals their own paintinfo
    protected Supplier<String[]> paintArraySupplier;
    @Setter
    @Getter
    private String simpleName = "Fractal";
    @Setter
    protected Supplier<Boolean> hopCondition;
    @Setter
    protected Supplier<World> worldSupplier = () -> Worlds.getRandomWorld(w -> w.isNormal() && w.isF2P());

    public Fractal(Supplier<Boolean> acceptCondition) {
        this.acceptCondition = acceptCondition;
    }

    public Fractal() {
    }

    public Fractal addChildren(Fractal... childFractals) {
        Collections.addAll(this.children, childFractals);
        return this;
    }

    public boolean isValid() {
        return true;
    }

    public int onLoop() {
        Logger.log("Hitting the fractal onLoop " + simpleName);
        return 100;
    }


    public int run() {
        LinkedList<String> memo = new LinkedList<>();
        if (acceptCondition == null ? isValid() : acceptCondition.get()) {
            if (!children.isEmpty()) {
                for (Fractal child : children) {
                    if (child.acceptCondition == null ? child.isValid() : child.acceptCondition.get()) {
                        memo.add(toString());
                        return child.run(memo);
                    }
                }
            } else {
                memo.add(getSimpleName());
                FractalAPI.hierarchy = memo.toArray(new String[]{});
                if (appendLogic != null && appendLogic.get()) return ReactionGenerator.getNormal();
                if (this.equipmentLoadout != null && !this.equipmentLoadout.isFulfilled()) {
                    return LoadoutExecutor.execEquipmentLoadout(this.equipmentLoadout);
                }
                if (this.inventoryLoadout != null && !this.inventoryLoadout.isFulfilled()) {
                    return LoadoutExecutor.execInvLoadout(this.inventoryLoadout);
                }
                FractalAPI.hierarchy = memo.stream().toArray(String[]::new);
                if (hopCondition != null && hopCondition.get()) {
                    WorldHopper.hopWorld(worldSupplier.get());
                    return ReactionGenerator.getNormal();
                }
                return onLoop();
            }
        }
        return 100; // 🧙
    }

    private int run(LinkedList<String> memo) {
        if (acceptCondition == null ? isValid() : acceptCondition.get()) {
            if (!children.isEmpty()) {
                for (Fractal child : children) {
                    if (child.acceptCondition == null ? child.isValid() : child.acceptCondition.get()) {
                        memo.add(getSimpleName());
                        return child.run(memo);
                    }
                }
                if (hopCondition != null && hopCondition.get()) {
                    WorldHopper.hopWorld(worldSupplier.get());
                    return ReactionGenerator.getNormal();
                }
                if (paintArraySupplier != null) FractalAPI.paintArrSupplier = paintArraySupplier;
                if (appendLogic != null && appendLogic.get()) return ReactionGenerator.getNormal();
                return this.onLoop(); // if no children/leafs are valid it will run the onLoop of the parent/branch
            } else {
                memo.add(getSimpleName());

                FractalAPI.hierarchy = memo.toArray(new String[]{});
                if (appendLogic != null && appendLogic.get()) return ReactionGenerator.getNormal();

                if (this.equipmentLoadout != null && !this.equipmentLoadout.isFulfilled()) {
                    return LoadoutExecutor.execEquipmentLoadout(this.equipmentLoadout);
                }
                if (this.inventoryLoadout != null && !this.inventoryLoadout.isFulfilled()) {
                    return LoadoutExecutor.execInvLoadout(this.inventoryLoadout);
                }
                if (hopCondition != null && hopCondition.get()) {
                    WorldHopper.hopWorld(worldSupplier.get());
                    return ReactionGenerator.getNormal();
                }
                FractalAPI.hierarchy = memo.toArray(new String[]{});
                if (paintArraySupplier != null) FractalAPI.paintArrSupplier = paintArraySupplier;
                return onLoop();
            }
        }
        Logger.info("return 100");
        return 100; // 🧙
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

    public void log(String message) {
        Logger.log(String.format("[%s] %s", this, message));
    }

    /**
     * generally you give an accept condition supplier to check if below a certain level, once above that level you can prune them
     */
    public void prune() {
        children.removeIf(child -> child.acceptCondition != null && !child.acceptCondition.get());
    }

    protected void setStatus(String status) {
        FractalAPI.status = status;
    }

    @Override
    public String toString() {
        return simpleName;
    }
}