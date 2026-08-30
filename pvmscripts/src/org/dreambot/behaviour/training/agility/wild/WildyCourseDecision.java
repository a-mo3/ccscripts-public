package org.dreambot.behaviour.training.agility.wild;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.training.agility.AgilityStage;
import org.dreambot.comms.impl.agility.BoxingClient;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Decides what course obstacle to do
 */
public class WildyCourseDecision extends TickDecision {

    static final Area PIPE_AERA = new Area(
            new Tile(2992, 3935, 0),
            new Tile(2992, 3930, 0),
            new Tile(3004, 3931, 0),
            new Tile(3007, 3934, 0),
            new Tile(3007, 3940, 0),
            new Tile(3002, 3939, 0)
    );
    // Swing-on | Ropeswing
    static final Area ROPE_SWING_AREA = new Area(3003, 3953, 3008, 3949);
    // Cross | Steeping stone
    static final Area STEPPING_STONES = new Area(3001, 3965, 3008, 3958);
    // Walk-across | Log balance
    static final Area LOG_BALANCE = new Area( // possibly use a can reach on the first segment of the log to decide this rather than area
            new Tile(2990, 3963, 0),
            new Tile(2994, 3966, 0),
            new Tile(2997, 3963, 0),
            new Tile(2998, 3958, 0),
            new Tile(3001, 3959, 0),
            new Tile(3003, 3944, 0),
            new Tile(3000, 3944, 0),
            new Tile(2998, 3949, 0),
            new Tile(2994, 3949, 0),
            new Tile(2990, 3953, 0));
    // Climb | Rocks
    static final Area ROCK_SLIDE = new Area(
            new Tile(2999, 3934, 0),
            new Tile(2999, 3942, 0),
            new Tile(2995, 3943, 0),
            new Tile(2996, 3949, 0),
            new Tile(2987, 3946, 0),
            new Tile(2987, 3934, 0));
    public static final Area COURSE = new Area(2987, 3966, 3008, 3931);

    static List<AgilityStage> stages = Arrays.asList(
            new AgilityStage(
                    () -> PIPE_AERA.contains(Players.getLocal()),
                    () -> !Players.getLocal().isMoving(),
                    () -> GameObjects.closest("Obstacle pipe"),
                    "Squeeze-through"
            ),
            new AgilityStage(
                    () -> !Players.getLocal().isMoving() && ROPE_SWING_AREA.contains(Players.getLocal()),
                    () -> STEPPING_STONES.contains(Players.getLocal()),
                    () -> GameObjects.closest("Ropeswing"),
                    "Swing-on"
            ),
            new AgilityStage(
                    () -> STEPPING_STONES.contains(Players.getLocal()),
                    () -> LOG_BALANCE.contains(Players.getLocal()),
                    () -> GameObjects.closest("Stepping stone"),
                    "Cross"
            ),
            new AgilityStage(
                    () -> LOG_BALANCE.contains(Players.getLocal()),
                    () -> !LOG_BALANCE.contains(Players.getLocal()) && !Players.getLocal().isMoving(),
                    () -> GameObjects.closest("Log balance"),
                    "Walk-across"
            ),
            new AgilityStage(
                    () -> ROCK_SLIDE.contains(Players.getLocal()),
                    () -> !ROCK_SLIDE.contains(Players.getLocal()),
                    () -> GameObjects.closest("Rocks"),
                    "Climb"
            )
    );

    public static final List<Predicate<Player>> teammateConditions = Arrays.asList(
            PIPE_AERA::contains,
            p -> !p.isMoving() && ROPE_SWING_AREA.contains(p),
            STEPPING_STONES::contains,
            LOG_BALANCE::contains,
            ROCK_SLIDE::contains
    );

    // check where our teammate is and only proceed if they are at our stage or infront of us
    int teamMateIndex = 0;
    int ourIndex = 0;

    public WildyCourseDecision(WildernessAgilityMode mode) {
        this.mode = mode;
    }

    boolean hasFinishedLap() {
        return PlayerSettings.getBitValue(10676) == 1;
    }

    final WildernessAgilityMode mode;

    @Override
    public boolean evaluate() {
        if (Dialogues.inDialogue()) {
            log("Handle dialogue");
            Dialog.solve("ask again");
            return true;
        }

        if (Inventory.count(ItemID.COINS_995) >= 150_000) {
            log("Pay for rewards");
            ObjectUtil.interact("Agility dispenser", "Pay");
            return true;
        }


        Player teammate = null;
        if (mode == WildernessAgilityMode.BOXING) {
            String teamMateName = BoxingClient.getInstance().teamMate;
            teammate = Players.closest(teamMateName);
        }

        if (teammate != null) {
            log("Teammate present");
            for (int i = 0; i < teammateConditions.size(); i++) {
                if (teammateConditions.get(i).test(teammate)) {
                    log("Teammate at " + i);
                    teamMateIndex = i;
                }
                if (teammateConditions.get(i).test(Players.getLocal())) {
                    log("We're at index " + i);
                    ourIndex = i;
                }
            }
        } else {
            log("No teammate present");
        }

        if (hasFinishedLap()) {
            log("Can Tag for reward");
            if (Inventory.contains(ItemID.AGILITY_ARENA_TICKET)) {
                log("Hand in ticket");
                ObjectUtil.interact("Agility dispenser", "Redeem");
                return true;
            }

            ObjectUtil.interact("Agility dispenser", "Tag");
            return true;
        }

        if (mode == WildernessAgilityMode.BOXING && TickBoxingDecision.timeSinceBoxed.elapsed() > 3000 && teammate != null && teammate.distance() < 5) {
            log("Needs to box");
            return true;
        }

        if (!LootingBag.refreshLootBagCache()) return true;

        boolean canProceed = teammate == null || teamMateIndex >= ourIndex || (teamMateIndex == 0 && ourIndex == 4);
        if (canProceed) {
            for (AgilityStage stage : stages) {
                if (stage.isValid()) {
                    log("Do obstacle");
                    stage.onLoop();
                    return true;
                }
            }
        }
        return false;
    }
}
