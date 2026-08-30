package org.dreambot.behaviour.method.corp;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.dreambot.behaviour.method.corp.messages.CorpRole;

import java.util.HashMap;
import java.util.Map;

/**
 * State that a bot holds to know its role, world, team etc
 */
@Builder
@ToString
@Getter
public class CorpTeam {
    @SerializedName("teamNumber")
    int teamId;
    // maybe start time to get the teams kph
    @SerializedName("kc")
    int killCount = 0;
    // 3 specs per kill to lower def to 100
    @SerializedName("dhwSpecCount")
    int dwhSpecCount = 0;
    // need to deal 400 BGS damage
    @SerializedName("bgsDamage")
    int bgsDamage = 0;

    //    @SerializedName("clanChat")
//    String clanChat;
    @SerializedName("teamRoles")
    Map<String, CorpRole> memberRoles = new HashMap<>();
    @SerializedName("world")
    int world = 379;

    public int activeSpecialForces() {
        return memberRoles.values().stream()
                .mapToInt(corpRole -> corpRole == CorpRole.SPECIAL_FORCES ? 1 : 0)
                .sum();
    }

    public String getLeader() {
        return memberRoles.entrySet().stream()
                .filter(x -> x.getValue() == CorpRole.HOST)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public CorpTeam setMember(String username, CorpRole role) {
        memberRoles.put(username, role);
        return this;
    }
}
