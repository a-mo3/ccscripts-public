/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dreambot.behaviour.method.clues.data;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.behaviour.method.clues.ClueScroll;
import org.dreambot.behaviour.method.clues.ClueScrollType;

import java.util.List;

import static org.dreambot.fractals.data.ItemID.*;

@Getter
public class MapClue extends ClueScroll {
    public static final String CHAMPIONS_GUILD = "West of the Champions' Guild";
    public static final String VARROCK_EAST_MINE = "Outside Varrock East Mine";
    public static final String STANDING_STONES = "At the standing stones north of Falador";
    public static final String WIZARDS_TOWER_DIS = "On the south side of the Wizards' Tower (DIS)";
    public static final String SOUTH_OF_DRAYNOR_BANK = "South of Draynor Village Bank";

    static final List<MapClue> CLUES = ImmutableList.of(
//		new MapClue(CLUE_SCROLL_EASY_12179, new Tile(3300, 3291, 0), "Al Kharid mine"),
            new MapClue(CLUE_SCROLL_EASY_2713, new Tile(3166, 3361, 0), CHAMPIONS_GUILD),
            new MapClue(CLUE_SCROLL_EASY_2716, new Tile(3290, 3374, 0), VARROCK_EAST_MINE),
            new MapClue(CLUE_SCROLL_EASY_2719, new Tile(3043, 3398, 0), STANDING_STONES),
//		new MapClue(CLUE_SCROLL_EASY_3516, new Tile(2612, 3482, 0), "Brother Galahad's house, West of McGrubor's Wood."),
//		new MapClue(CLUE_SCROLL_EASY_3518, new Tile(3110, 3152, 0), WIZARDS_TOWER_DIS),
//		new MapClue(CLUE_SCROLL_EASY_7236, new Tile(2970, 3415, 0), "North of Falador."),
            new MapClue(CLUE_SCROLL_MEDIUM_2827, new Tile(3091, 3227, 0), SOUTH_OF_DRAYNOR_BANK),
            new MapClue(CLUE_SCROLL_MEDIUM_3596, new Tile(2907, 3295, 0), "West of the Crafting Guild"),
            new MapClue(CLUE_SCROLL_MEDIUM_3598, new Tile(2658, 3488, 0), 357, "The crate in McGrubor's Wood. Fairy ring ALS"),
            new MapClue(CLUE_SCROLL_MEDIUM_3599, new Tile(2651, 3231, 0), "North of the Tower of Life. Fairy ring DJP"),
            new MapClue(CLUE_SCROLL_MEDIUM_3601, new Tile(2565, 3248, 0), 354, "The crate west of the Clocktower."),
            new MapClue(CLUE_SCROLL_MEDIUM_3602, new Tile(2924, 3210, 0), "Behind the Chemist's house in Rimmington."),
            new MapClue(CLUE_SCROLL_MEDIUM_7286, new Tile(2536, 3865, 0), "Miscellania. Fairy ring CIP"),
            new MapClue(CLUE_SCROLL_MEDIUM_7288, new Tile(3434, 3265, 0), "Mort Myre Swamp, west of Mort'ton. Fairy ring BIP"),
            new MapClue(CLUE_SCROLL_MEDIUM_7290, new Tile(2454, 3230, 0), "At the entrance to the Ourania Cave."),
            new MapClue(CLUE_SCROLL_MEDIUM_7292, new Tile(2578, 3597, 0), "South-east of the Lighthouse. Fairy ring ALP"),
            new MapClue(CLUE_SCROLL_MEDIUM_7294, new Tile(2666, 3562, 0), "Between Seers' Village and Rellekka. South-west of Fairy ring CJR"),
            new MapClue(CLUE_SCROLL_HARD, new Tile(3309, 3503, 0), 2620, "A crate in the Lumber Yard, north-east of Varrock.")
//		new MapClue(CLUE_SCROLL_HARD_3520, new Tile(2615, 3078, 0), "Yanille anvils, south of the bank. You can dig from inside the building."),
//		new MapClue(CLUE_SCROLL_HARD_3522, new Tile(2488, 3308, 0), "In the western section of West Ardougne."),
//		new MapClue(CLUE_SCROLL_HARD_3524, new Tile(2457, 3182, 0), CRATE_18506, "In a crate by the stairs to the Observatory Dungeon."),
//		new MapClue(CLUE_SCROLL_HARD_3525, new Tile(3026, 3628, 0), CRATE_354, "In a crate at the Dark Warriors' Fortress in level 14 Wilderness."),
//		new MapClue(CLUE_SCROLL_HARD_7239, new Tile(3021, 3912, 0), "South-east of the Wilderness Agility Course in level 50 Wilderness."),
//		new MapClue(CLUE_SCROLL_HARD_7241, new Tile(2722, 3338, 0), "South of the Legends' Guild. Fairy ring BLR"),
//		new MapClue(CLUE_SCROLL_ELITE_12130, new Tile(2449, 3130, 0), "South-west of Tree Gnome Village."),
//		new MapClue(CLUE_SCROLL_ELITE_19782, new Tile(2953, 9523, 1), "In the Mogre Camp, near Port Khazard. You require a Diving Apparatus and a Fishbowl Helmet"),
//		new MapClue(CLUE_SCROLL_ELITE_19783, new Tile(2202, 3062, 0), "Zul-Andra. Fairy ring BJS"),
//		new MapClue(CLUE_SCROLL_ELITE_19784, new Tile(1815, 3852, 0), "At the Soul Altar, north-east of the Arceuus essence mine."),
//		new MapClue(CLUE_SCROLL_ELITE_19785, new Tile(3538, 3208, 0), "East of Burgh de Rott."),
//		new MapClue(CLUE_SCROLL_ELITE_19786, new Tile(2703, 2716, 0), CRATE_6616, "The crate in south-western Ape Atoll"),
//		new MapClue(TREASURE_SCROLL_23068, new Tile(3203, 3213, 0), "Behind Lumbridge Castle, just outside the kitchen door"),
//		new MapClue(MYSTERIOUS_ORB_23069, new Tile(3108, 3262, 0), "South-west of the wheat field east of Draynor Village.")
    );

    private final int itemId;
    private final Tile location;
    private final int objectId;
    private final String description;

    private MapClue(int itemId, Tile location, int objectId) {
        this(itemId, location, objectId, null);
    }

    MapClue(int itemId, Tile location, String description) {
        this(itemId, location, -1, description);
    }

    private MapClue(int itemId, Tile location, int objectId, String description) {
        this.itemId = itemId;
        this.location = location;
        this.objectId = objectId;
        this.description = description;
//        setRequiresSpade(objectId == -1);
    }

    @Override
    public int solve() {
        return 0;
    }

    @Override
    public ClueScrollType getType() {
        return ClueScrollType.MAP;
    }
}
