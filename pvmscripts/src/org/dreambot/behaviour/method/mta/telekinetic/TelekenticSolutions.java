package org.dreambot.behaviour.method.mta.telekinetic;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.interactive.util.Direction;
import org.dreambot.api.wrappers.map.Region;

import java.util.Arrays;
import java.util.Stack;

public enum TelekenticSolutions {
    // this is the order given by the wiki
    ONE(new Area(3368, 9702, 3389, 9671, 1), makeStack(
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH
    )),
    TWO(new Area(3368, 9726, 3391, 9704, 1), makeStack(
            Direction.SOUTH,
            Direction.EAST,
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.EAST,
            Direction.NORTH
    )),
    THREE(new Area(3329, 9698, 3358, 9670), makeStack(
            Direction.EAST,
            Direction.NORTH,
            Direction.WEST,
            Direction.NORTH,
            Direction.WEST,
            Direction.SOUTH,
            Direction.EAST
    )),
    FOUR(new Area(3334, 9698, 3366, 9671, 1), makeStack(
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.NORTH
    )),
    FIVE(new Area(3332, 9725, 3368, 9703, 1), makeStack(
            Direction.WEST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.NORTH,
            Direction.WEST,
            Direction.SOUTH
    )),
    SIX(new Area(3359, 9725, 3386, 9705), makeStack(
            Direction.WEST,
            Direction.NORTH,
            Direction.EAST,
            Direction.NORTH,
            Direction.WEST,
            Direction.NORTH,
            Direction.EAST,
            Direction.NORTH
    )),
    SEVEN(new Area(3335, 9724, 3371, 9696, 2), makeStack(
            Direction.NORTH,
            Direction.WEST,
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.SOUTH
    )),
    EIGHT(new Area(3331, 9694, 3374, 9668, 2), makeStack(
            Direction.NORTH,
            Direction.WEST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.NORTH,
            Direction.WEST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.NORTH
    )),
    NINE(new Area(3361, 9703, 3388, 9665), makeStack(
            Direction.WEST,
            Direction.NORTH,
            Direction.EAST,
            Direction.NORTH,
            Direction.WEST,
            Direction.SOUTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.EAST,
            Direction.NORTH
    )),
    TEN(new Area(3328, 9724, 3356, 9698), makeStack(
            Direction.EAST,
            Direction.NORTH,
            Direction.WEST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.NORTH,
            Direction.EAST,
            Direction.NORTH,
            Direction.WEST,
            Direction.NORTH
    ));


    // the area the instance is based off, we will know the puzzle type by what area we can in when we convert instanced Direction
    public final Area baseArea;
    // the solve for the maze, SOUTH would means move SOUTH and thus be on the southern side on the maze
    // in reverse order from finish to start moves, for obvious reasons.
    public final Stack<Direction> solve;

    private static Stack<Direction> makeStack(Direction... directions) {
        Stack<Direction> s = new Stack<>();
        for (Direction direction : directions) {
            s.push(direction);
        }
        return s;
    }

    public static TelekenticSolutions findCurrentMaze() {
        return Arrays.stream(TelekenticSolutions.values())
                .filter(x -> x.baseArea.contains(Region.fromInstance(Players.getLocal().getTile())))
                .findAny()
                .orElse(null);
    }

    TelekenticSolutions(Area baseArea, Stack<Direction> solve) {
        this.baseArea = baseArea;
        this.solve = solve;
    }
}
