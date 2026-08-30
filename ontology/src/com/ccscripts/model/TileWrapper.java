package com.ccscripts.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.dreambot.api.methods.map.Tile;

@ToString
@Getter
public class TileWrapper {
    @SerializedName("8")
    private final int x;
    @SerializedName("3")
    private final int y;
    @SerializedName("1")
    private final int z;

    public TileWrapper(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public TileWrapper(Tile t) {
        this.x = t.getX();
        this.y = t.getY();
        this.z = t.getZ();
    }

    public Tile unwrap() {
        return new Tile(x, y, z);
    }
}
