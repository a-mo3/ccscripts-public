package org.dreambot.behaviour.method.gwd.zilyana;

import lombok.AllArgsConstructor;
import org.dreambot.api.methods.map.Tile;

import java.util.LinkedList;
import java.util.List;

/**
 * model for a zilyana tile
 * this is for the energy efficient run cycle where you attack, walk to a tile then run to the next attack tile
 */
@AllArgsConstructor
public class ZilyanaTile {
    public static List<ZilyanaTile> tileGraph = new LinkedList<>();
    public static final ZilyanaTile AWAIT_TILE = new ZilyanaTile(new Tile(2897, 5259), false, false); // the tile you wait on after zilyana has been killed
    public static final Tile FIRST_TILE = new Tile(2895, 5258); // the tile you start on after await tile, part of the graph

    static {
        tileGraph.add(new ZilyanaTile(new Tile(2895, 5258), false, true));
        tileGraph.add(new ZilyanaTile(new Tile(2894, 5258), true, false));
        tileGraph.add(new ZilyanaTile(new Tile(2889, 5258), false, true));
        tileGraph.add(new ZilyanaTile(new Tile(2889, 5266), false, true));
        tileGraph.add(new ZilyanaTile(new Tile(2889, 5268), true, false));
        tileGraph.add(new ZilyanaTile(new Tile(2889, 5271), false, true));
        tileGraph.add(new ZilyanaTile(new Tile(2891, 5273), true, false));
        tileGraph.add(new ZilyanaTile(new Tile(2897, 5273), false, true));
        tileGraph.add(new ZilyanaTile(new Tile(2899, 5273), true, false));
        tileGraph.add(new ZilyanaTile(new Tile(2902, 5273), false, true));
        tileGraph.add(new ZilyanaTile(new Tile(2904, 5273), true, false));
        tileGraph.add(new ZilyanaTile(new Tile(2907, 5273), false, true));
        tileGraph.add(new ZilyanaTile(new Tile(2906, 5266), false, true));
        tileGraph.add(new ZilyanaTile(new Tile(2906, 5264), true, false));
        tileGraph.add(new ZilyanaTile(new Tile(2906, 5261), false, true));
        tileGraph.add(new ZilyanaTile(new Tile(2906, 5260), true, false));
        tileGraph.add(new ZilyanaTile(new Tile(2900, 5258), false, true));
        tileGraph.add(new ZilyanaTile(new Tile(2898, 5258), true, false));
    }

    public Tile location;
    public boolean walkTile; // if true you walk to it
    public boolean attackTile; // if true you attack on this tile
    // walk tiles are never attack tiles so its redundant but im going to leave it.
}
