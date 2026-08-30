package org.dreambot.behaviour.training.quests.earnestthechicken;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetOilCan extends Fractal {
    Supplier<Boolean> isLeverADown = () -> PlayerSettings.getBitValue(1788) == 1;
    Supplier<Boolean> isLeverBDown = () -> PlayerSettings.getBitValue(1789) == 1;
    Supplier<Boolean> isLeverCDown = () -> PlayerSettings.getBitValue(1790) == 1;
    Supplier<Boolean> isLeverDDown = () -> PlayerSettings.getBitValue(1791) == 1;
    Supplier<Boolean> isLeverEDown = () -> PlayerSettings.getBitValue(1792) == 1;
    Supplier<Boolean> isLeverFDown = () -> PlayerSettings.getBitValue(1793) == 1;

    final int DOOR_7_ID = 137;
    final int DOOR_6_ID = 138;
    final int DOOR_4_ID = 140;
    final int DOOR_8_ID = 142;
    final int DOOR_5_ID = 143;
    final int D_ROOM_BOTTOM_DOOR_ID = 144;
    final int DOOR_3_ID = 145;
    final int OIL_CAN_DOOR = 141;

    final Area PUZZLE_ROOM = new Area(3089, 9769, 3118, 9745);
    public static final Area INSIDE_AVAS_ROOM = new Area(3091, 3363, 3096, 3354);
    final Area OUTSIDE_AVAS_ROOM = new Area(3097, 3363, 3104, 3354);

    // in the top left theres 4 rooms, these are those rooms
    final Area SOUTH_EAST = new Area(3100, 9762, 3104, 9758, 0);
    final Area SOUTH_WEST = new Area(3096, 9762, 3099, 9758, 0);
    final Area NORTH_EAST = new Area(3100, 9767, 3104, 9763, 0);
    final Area NORTH_WEST = new Area(3096, 9767, 3099, 9763, 0);

    @Override
    public boolean isValid() {
        return !Inventory.contains(ItemID.OIL_CAN);
    }

    @Override
    public int onLoop() {
        if (!PUZZLE_ROOM.contains(Players.getLocal())) {
            Logger.info("Walk into puzzle room");
            if (INSIDE_AVAS_ROOM.contains(Players.getLocal())) {
                Logger.info("Puzzle center");
                GameObject ladder = GameObjects.closest("Ladder");
                if (ladder != null && ladder.interact("Climb-down")) {
                    Sleep.sleepUntil(() -> PUZZLE_ROOM.contains(Players.getLocal()), 4000);
                }
                return ReactionGenerator.getNormal();
            }

            if (!OUTSIDE_AVAS_ROOM.contains(Players.getLocal())) {
                Walking.walk(OUTSIDE_AVAS_ROOM.getCenter());
                return ReactionGenerator.getNormal();
            }

            GameObject bookcase = GameObjects.closest("Bookcase");
            if (bookcase != null && bookcase.interact("Search")) {
                Sleep.sleepUntil(() -> INSIDE_AVAS_ROOM.contains(Players.getLocal()), 2400);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        // pull down levers a & b, then D
        if (!isLeverDDown.get()) {
            if (!isLeverBDown.get()) {
                Log.info("Pull lever B");
                GameObject leverB = GameObjects.closest("Lever B");
                if (leverB != null && leverB.interact("Pull")) {
                    Sleep.sleepUntil(() -> isLeverBDown.get(), 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            if (!isLeverADown.get()) {
                GameObject leverA = GameObjects.closest("Lever A");
                if (leverA != null && leverA.interact("Pull")) {
                    Sleep.sleepUntil(() -> isLeverADown.get(), 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            GameObject leverD = GameObjects.closest("Lever D");
            if (leverD == null) return ReactionGenerator.getNormal();
            if (!leverD.canReach()) {
                GameObject dRoomBottomDoor = GameObjects.closest(D_ROOM_BOTTOM_DOOR_ID);
                if (dRoomBottomDoor != null && dRoomBottomDoor.interact("Open")) {
                    Sleep.sleepUntil(leverD::canReach, 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            if (leverD.interact("Pull")) {
                Sleep.sleepUntil(() -> isLeverDDown.get(), 2400);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        // d is now down
        // pull a & b up
        if (isLeverBDown.get() || isLeverADown.get()) {
            GameObject leverA = GameObjects.closest("Lever A");
            GameObject dRoomBottomDoor = GameObjects.closest(D_ROOM_BOTTOM_DOOR_ID);
            if (leverA == null) return ReactionGenerator.getNormal();
            if (!leverA.canReach() && dRoomBottomDoor != null && dRoomBottomDoor.interact("Open")) {
                Sleep.sleepUntil(leverA::canReach, 2400);
                return ReactionGenerator.getNormal();
            }

            if (isLeverADown.get() && leverA.interact("Pull")) {
                Sleep.sleepUntil(() -> !isLeverADown.get(), 2400);
                return ReactionGenerator.getNormal();
            }

            GameObject leverB = GameObjects.closest("Lever B");
            if (isLeverBDown.get() && leverB.interact("Pull")) {
                Sleep.sleepUntil(() -> !isLeverBDown.get(), 2400);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        // now a and b are up, enter doors 3, 4, 5
        // pull e & f down, e will be pulled up later, so only check f here
        if (!isLeverFDown.get()) {
            GameObject leverA = GameObjects.closest("Lever A");
            // if you can reach this you are in the main room, and need to enter door 3
            if (leverA != null && leverA.canReach()) {
                GameObject door3 = GameObjects.closest(DOOR_3_ID);
                if (door3 != null && door3.interact("Open")) {
                    Sleep.sleepUntil(() -> !leverA.canReach(), 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            // enter door 4
            if (SOUTH_EAST.contains(Players.getLocal())) {
                GameObject door4 = GameObjects.closest(DOOR_4_ID);
                if (door4 != null && door4.interact("Open")) {
                    Sleep.sleepUntil(() -> !SOUTH_EAST.contains(Players.getLocal()), 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            // enter door 5
            if (SOUTH_WEST.contains(Players.getLocal())) {
                GameObject door5 = GameObjects.closest(DOOR_5_ID);
                if (door5 != null && door5.interact("Open")) {
                    Sleep.sleepUntil(() -> SOUTH_WEST.contains(Players.getLocal()), 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            if (!isLeverEDown.get()) {
                GameObject leverE = GameObjects.closest("Lever E");
                if (leverE != null && leverE.interact("Pull")) {
                    Sleep.sleepUntil(() -> isLeverEDown.get(), 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            if (!isLeverFDown.get()) {
                GameObject leverF = GameObjects.closest("Lever F");
                if (leverF != null && leverF.interact("Pull")) {
                    Sleep.sleepUntil(() -> isLeverFDown.get(), 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }
        }

        // go through doors 6 & 7 to get to D & C room, pull down lever C
        if (!isLeverCDown.get()) {
            GameObject leverF = GameObjects.closest("Lever F");
            if (leverF != null && leverF.canReach()) {
                GameObject door6 = GameObjects.closest(DOOR_6_ID);
                if (door6 != null && door6.interact("Open")) {
                    Sleep.sleepUntil(() -> !leverF.canReach(), 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            GameObject leverC = GameObjects.closest("Lever C");
            if (leverC == null) return ReactionGenerator.getNormal();
            if (!leverC.canReach()) {
                GameObject door7 = GameObjects.closest(DOOR_7_ID);
                if (door7 != null && door7.interact("Open")) {
                    Sleep.sleepUntil(leverC::canReach, 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            if (leverC.interact("Pull")) {
                Sleep.sleepUntil(() -> isLeverCDown.get(), 2400);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        // pull lever e back up
        if (isLeverEDown.get()) {
            GameObject leverC = GameObjects.closest("Lever C");
            if (leverC == null) return ReactionGenerator.getNormal();
            if (leverC.canReach()) {
                GameObject door7 = GameObjects.closest(DOOR_7_ID);
                if (door7 != null && door7.interact("Open")) {
                    Sleep.sleepUntil(() -> !leverC.canReach(), 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            GameObject leverE = GameObjects.closest("Lever E");
            if (leverE == null) return ReactionGenerator.getNormal();
            if (!leverE.canReach()) {
                GameObject door6 = GameObjects.closest(DOOR_6_ID);
                if (door6 != null && door6.interact("Open")) {
                    Sleep.sleepUntil(leverE::canReach, 2400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }

            if (leverE.interact("Pull")) {
                Sleep.sleepUntil(() -> !isLeverEDown.get(), 2400);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }


        // get oil can.
        if (NORTH_WEST.contains(Players.getLocal())) {
            GameObject door6 = GameObjects.closest(DOOR_6_ID);
            if (door6 != null && door6.interact("Open")) {
                Sleep.sleepUntil(() -> !NORTH_WEST.contains(Players.getLocal()), 2400);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        if (NORTH_EAST.contains(Players.getLocal())) {
            GameObject door8 = GameObjects.closest(DOOR_8_ID);
            if (door8 != null && door8.interact("Open")) {
                Sleep.sleepUntil(() -> !NORTH_EAST.contains(Players.getLocal()), 2400);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        if (SOUTH_EAST.contains(Players.getLocal())) {
            GameObject door3 = GameObjects.closest(DOOR_3_ID);
            if (door3 != null && door3.interact("Open")) {
                Sleep.sleepUntil(() -> !SOUTH_EAST.contains(Players.getLocal()), 2400);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        GroundItem oilCan = GroundItems.closest(ItemID.OIL_CAN);
        if (oilCan == null) return ReactionGenerator.getNormal();
        if (!oilCan.canReach()) {
            GameObject oilCanDoor = GameObjects.closest(OIL_CAN_DOOR);
            if (oilCanDoor != null && oilCanDoor.interact("Open")) {
                Sleep.sleepUntil(oilCan::canReach, 2400);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        if (oilCan.interact("Take")) {
            Sleep.sleepUntil(() -> Inventory.contains(ItemID.OIL_CAN), 2400);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
