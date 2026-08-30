package org.dreambot.behaviour.quests.quizbranch;


import org.dreambot.api.methods.map.Tile;

public enum Plaque {
    // NORTH
    LIZARD(24605, 3675, new Tile(1743, 4978)),
    TORTOISE(24606, 3680, new Tile(1753, 4978)),
    DRAGON(24607, 3672, new Tile(1768, 4978)),
    WYVERN(24608, 3681, new Tile(1778, 4978)),
    // EAST
    SNAIL(24613, 3674, new Tile(1776, 4963)),
    MONKEY(24615, 3676, new Tile(1774, 4957)),
    SEASLUG(24616, 3682, new Tile(1781, 4957)),
    SNAKE(24614, 3677, new Tile(1783, 4963)),
    // SOUTH
    TERRORBIRD(24617, 3683, new Tile(1755, 4940)),
    KALPHITE_QUEEN(24618, 3684, new Tile(1762, 4938)),
    // WEST
    LEECH(24610, 3685, new Tile(1744, 4963)),
    PENGUIN(24612, 3673, new Tile(1742, 4957)),
    CAMEL(24609, 3679, new Tile(1737, 4963)),
    MOLE(24611, 3678, new Tile(1735, 4957));


    public final int ID;
    public final int VARBIT;
    public final Tile TILE;

    Plaque(int id, int varbit, Tile tile) {
        this.ID = id;
        this.VARBIT = varbit;
        this.TILE = tile;
    }
}
