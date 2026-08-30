package org.dreambot.behaviour.training.thieving;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Accessors(chain = true)
@Getter
public class RoguesDenObstacle {
    final Tile t;
    @Setter
    boolean shouldRun; // for tiles that you walk to

    Supplier<GameObject> objectSupplier; // for interaction obstacles
    @Setter
    String interaction;

    public RoguesDenObstacle(int x, int y, boolean run, BooleanSupplier completeCondition) {
        this.t = new Tile(x, y, 1); // rogues den is all in Z = 1
        this.completeCondition = completeCondition;
        this.shouldRun = run;
    }

    public RoguesDenObstacle(int x, int y, boolean run) {
        this.t = new Tile(x, y, 1); // rogues den is all in Z = 1
        this.completeCondition = () -> t.equals(Players.getLocal().getTile());
        this.shouldRun = run;
    }

    public RoguesDenObstacle(Tile t, boolean run, BooleanSupplier completeCondition) {
        this.t = t;
        this.completeCondition = completeCondition;
        this.shouldRun = run;
    }

    public RoguesDenObstacle(int x, int y, String objName, BooleanSupplier completeCondition) {
        this.t = new Tile(x, y, 1);
        this.completeCondition = completeCondition;
        this.objectSupplier = () -> GameObjects.closest(obj -> objName.equals(obj.getName()) && t.equals(obj.getTile()));
    }

    public RoguesDenObstacle(int x, int y, Supplier<GameObject> objectSupplier, BooleanSupplier completeCondition) {
        this.t = new Tile(x, y, 1);
        this.completeCondition = completeCondition;
        this.objectSupplier = objectSupplier;
    }

    public RoguesDenObstacle(Tile t, Supplier<GameObject> objectSupplier, BooleanSupplier completeCondition) {
        this.t = t;
        this.completeCondition = completeCondition;
        this.objectSupplier = objectSupplier;
    }

    @Setter
    BooleanSupplier completeCondition;
}
