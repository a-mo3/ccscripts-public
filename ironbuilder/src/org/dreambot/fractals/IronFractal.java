package org.dreambot.fractals;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.analytics.impl.AnalyticsReporter;
import org.dreambot.analytics.impl.IdleMouseFeatureFlag;
import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.gui.factory.JPaneFractal;
import org.dreambot.gui.settings.SettingFractal;
import org.dreambot.loadouts.InventoryLoadout;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.loadouts.behavior.BankingFractal;
import org.dreambot.utility.Entities;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 *
 * Same as fractals, but the inventory system is going to require big changes, restock methods
 */
@Accessors(chain = true)
@Setter
@Getter
public class IronFractal {
    public static List<String> decisionPath = new LinkedList<>();
    boolean isSafeForHCIM = true;
    int sleepLow = 600;
    int sleepHigh = 2000;
    private String simpleName = "Unnamed";
    protected BooleanSupplier acceptCondition = () -> true;
    /**
     * used for the idea of locking a fractal
     * locked means locked true, once a locked condition is met, fractal is always true until unlocked is met
     * helpful for things like selling multiple items to a store when inventory is full, or like wintertodt
     * without having to use a priority system
     */
    private BooleanSupplier storedAcceptCondition = null;
    protected BooleanSupplier lockedWhen = () -> false;
    protected BooleanSupplier unlockedWhen = () -> false;

    @Getter
    protected List<IronFractal> children = new ArrayList<>();
    /**
     *  true = return false = continue to onloop
     */
    protected BooleanSupplier prependLogic = null;

    @Setter
    protected InventoryLoadout inventoryLoadout;

    public IronFractal addInventoryItem(InventoryLoadoutItem item) {
        if (inventoryLoadout == null) inventoryLoadout = new InventoryLoadout();
        inventoryLoadout.addItem(item);
        return this;
    }

    public IronFractal(BooleanSupplier acceptCondition) {
        this.acceptCondition = acceptCondition;
    }

    public boolean isValid() {
        if (!isSafeForHCIM && IronmanType.getCurrent() == IronmanType.HCIM) {
            return false;
        }

        if (acceptCondition != null) return acceptCondition.getAsBoolean();
        return true;
    }

    protected int onLoop() {
        Logger.log("Hitting the fractal onLoop " + simpleName);
        return sleep();
    }

    public IronFractal addChildren(IronFractal... children) {
        Collections.addAll(this.children, children);
        return this;
    }

    public IronFractal shuffle() {
        if (this.children.isEmpty()) return this;
        Collections.shuffle(this.children);
        log("Shuffle children " + this.children.size() + " " + this.children.get(0).simpleName);
        return this;
    }

    static List<String> tempHierarchy = new ArrayList<>();

    private boolean enforceFractalSettings() {
        if (inventoryLoadout != null) {
            if (!inventoryLoadout.isFulfilled()) {
                log("Inventory loadout not fulfilled.");
                BankingFractal.setPubInventoryLoadout(inventoryLoadout);
                return false;
            }
        }
        return true;
    }

    public int run() {
        tempHierarchy.add(this.simpleName);
//        if (paintArraySupplier != null) FractalAPI.paintArrSupplier = paintArraySupplier;
        if (children.isEmpty()) {
            // this is a child node.
            decisionPath.clear();
            decisionPath.addAll(tempHierarchy);
            tempHierarchy.clear();
            if (prependLogic != null && prependLogic.getAsBoolean()) return 100;
            if (lockedWhen.getAsBoolean()) {
                log("Locked " + getSimpleName());
                if (storedAcceptCondition == null) {
                    storedAcceptCondition = acceptCondition;
                    simpleName += " locked";
                    acceptCondition = () -> true;
                }
            }
            if (unlockedWhen.getAsBoolean() && storedAcceptCondition != null) {
                log("Unlocked " + getSimpleName());
                acceptCondition = storedAcceptCondition;
                storedAcceptCondition = null;
                simpleName = simpleName.replace(" locked", "");
            }

            if (!enforceFractalSettings()) return sleep();
//            if (afterLoadouts != null && afterLoadouts.get()) return ReactionGenerator.getNormal();
            AnalyticsReporter.reportFractalExecute(decisionPath.toString());
            return onLoop();
        }


//        children.removeIf(x -> {
//            if (x.cleanAfterAccomplished && !cleanedSoSkip && !x.isValid()) {
//                log("Clean " + x.getSimpleName());
//                return true;
//            }
//            return false;
//        });

        IronFractal firstValid = children.stream()
//                .filter(x -> !x.cleanedSoSkip)
                .filter(IronFractal::isValid)
                .findFirst()
                .orElse(null);
        if (firstValid == null) {
            log("No valid children " + simpleName);
            tempHierarchy.clear();
            return 200;
        }
        return firstValid.run();
    }

    protected void log(String log) {
        Logger.log(simpleName + " : " + log);
    }

    protected void warn(String log) {
        Logger.warn(simpleName + " : " + log);
    }

    public IronFractal setSleepAround(int mid, int var) {
        setSleepLow(Math.max(mid - var, 1));
        setSleepLow(Math.max(mid + var, 1));
        return this;
    }


    // decides what idle actions can happen, all false unless you have analytics on.
    public static IdleMouseFeatureFlag mouseFeatureFlag = new IdleMouseFeatureFlag();

    protected int sleep() {
        int s;
        if (sleepHigh > sleepLow) s = Calculations.random(sleepLow, sleepHigh);
        else s = Calculations.random(sleepHigh, sleepLow);
        return sleep(s);
    }

    protected int sleep(int sleep) {
        if (Client.isInCutscene()) return sleep;
        if (mouseFeatureFlag.isFlagD()) {
            log("Antiban test flag d, this ones tarded.");
            Mouse.move(new Point(
                    Calculations.random(0, Client.getViewportWidth()),
                    Calculations.random(0, Client.getViewportHeight())
            ));
            return sleep;
        }

        if (mouseFeatureFlag.isFlagC() && Calculations.random(5) == 1) {
            // camera movement
            log("Antiban test - camera - c flag");
            Camera.rotateTo(
                    (Camera.getYaw() + Calculations.random(-600, 600)) % 2001,
                    Camera.getPitch() + Calculations.random(-50, 50)
            );
            Camera.setZoom(Calculations.random(200, 520));

        } else if (mouseFeatureFlag.isFlagB() && Calculations.random(7) == 1) {
            // hover a random entity
            log("Antiban test - hover - b flag");
            List<Entity> a = new ArrayList<>(NPCs.all(x -> x.distance() < 7 && x.isOnScreen()));
            a.addAll(Players.all(x -> x.distance() < 7 && x.isOnScreen()));
//            a.addAll(GameObjects.all(Entity::isOnScreen));
            // we check is on screen because i dont want to rotate and therefore cause mouse gaussian
            if (a.size() > 3) {
                Entity e = a.get(Calculations.random(a.size()));
                Mouse.move(e);
                Sleep.sleep(sleep / 4);
            }
        } else if (Mouse.isMouseInScreen() && mouseFeatureFlag.isFlagA() && Calculations.random(5) == 1) {
            // flag a = mouse off screen
            // we do mouse off screen check last because it makes the most sense for you to mouse off screen after some other shit
            log("Antiban test - off screen - a flag");
            Mouse.moveOutsideScreen(Calculations.random(3) == 2);
        }
        return sleep;
    }

    /**
     * searches the entire tree for anything that
     */
    public void makeUI() {
        JFrame baseFrame = new JFrame("cCSettings");
        baseFrame.setSize(600, 500);

        JTabbedPane tabs = new JTabbedPane();

        log("Generating UI");
        //
        // when >1 of the same setting fractal exist in a tree use this to prevent tab duplication
        Set<String> alreadyMade = new HashSet<>();
        // add other tabs for every fractal that is configurable
        // todo fully traverse tree not just branches
        searchTree(this, tabs, alreadyMade);
        baseFrame.add(tabs);
        baseFrame.setVisible(true);
        log("Open UI");
    }

    private void searchTree(IronFractal f, JTabbedPane tabs, Set<String> alreadyMade) {
        if (!f.getChildren().isEmpty()) {
            f.getChildren().forEach(x -> searchTree(x, tabs, alreadyMade));
        }

        if (!(f instanceof JPaneFractal)) return;
        String name;
        if (f instanceof SettingFractal) {
            SettingFractal cf = (SettingFractal) f;
            name = cf.settingName();
            log("Make " + name);
            if (alreadyMade.contains(name)) {
                log("Duplicate tab name when generating ui");
                return;
            }
            tabs.add(name, ((SettingFractal<?>) f).makePane());
        } else {
            // simple name sometimes includes setting values, this is not an optimal solution
            name = f.getSimpleName();
            if (alreadyMade.contains(name)) {
                log("Duplicate tab name when generating ui");
                return;
            }
            tabs.add(name, ((JPaneFractal) f).makePane());
        }

        alreadyMade.add(name);
    }

    @Override
    public String toString() {
        return this.simpleName;
    }
}
