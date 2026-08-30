package org.dreambot.behaviour.method.toa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RaidRoom {

    NEXUS(new int[]{14160}, "Nexus", RaidRoomType.LOBBY),
    CRONDIS(new int[]{15698}, "Crondis", RaidRoomType.PUZZLE),
    ZEBAK(new int[]{15700}, "Zebak", RaidRoomType.BOSS),
    SCABARAS(new int[]{14162}, "Scabaras", RaidRoomType.PUZZLE),
    KEPHRI(new int[]{14164}, "Kephri", RaidRoomType.BOSS),
    APMEKEN(new int[]{15186}, "Apmeken", RaidRoomType.PUZZLE),
    BABA(new int[]{15188}, "Ba-Ba", RaidRoomType.BOSS),
    HET(new int[]{14674}, "Het", RaidRoomType.PUZZLE),
    AKKHA(new int[]{14676}, "Akkha", RaidRoomType.BOSS),
    WARDENS(new int[]{15184, 15696}, "Wardens", RaidRoomType.BOSS),
    TOMB(new int[]{14672}, "Tomb", RaidRoomType.LOBBY),
    ;

    public enum RaidRoomType {
        LOBBY,
        PUZZLE,
        BOSS,
        ;
    }

    private final int[] regionIds;

    @Getter
    private final String displayName;

    @Getter
    private final RaidRoomType roomType;

    public static RaidRoom forRegionId(int region) {
        for (RaidRoom r : RaidRoom.values()) {
            for (int regionId : r.regionIds) {
                if (regionId == region) {
                    return r;
                }
            }
        }

        return null;
    }

    public static RaidRoom forString(String roomName) {
        for (RaidRoom r : RaidRoom.values()) {
            if (r.getDisplayName().equals(roomName)) {
                return r;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}