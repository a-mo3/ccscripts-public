package org.dreambot.comms.impl.gwd;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.wrappers.interactive.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GodWarsTeam {
    @SerializedName("world")
    public int world = Worlds.getRandomWorld(x -> x.isNormal() && x.getMinimumLevel() < 50 && x.isMembers()).getWorld();
    @SerializedName("members")
    public Set<String> members = new HashSet<>();

    public GodWarsTeam addMember(String member) {
        members.add(member);
        return this;
    }

    public boolean isMember(Player p) {
        if (p == null || p.getName() == null) return false;
        return members.contains(p.getName());
    }
}
