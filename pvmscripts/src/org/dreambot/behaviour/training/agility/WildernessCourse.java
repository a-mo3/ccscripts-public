package org.dreambot.behaviour.training.agility;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.comms.impl.agility.BoxingClient;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class WildernessCourse extends Fractal implements ChatListener {
    // todo turn off auto retaliate
    // todo handle dungeon 3005, 10362, 0

    // Squeeze-through | Obstacle pipe
//            = new Area(3005, 3938, 3002, 3935);
    final Area PIPE_AERA = new Area(
            new Tile(2992, 3935, 0),
            new Tile(2992, 3930, 0),
            new Tile(3004, 3931, 0),
            new Tile(3007, 3934, 0),
            new Tile(3007, 3940, 0),
            new Tile(3002, 3939, 0)
    );
    // Swing-on | Ropeswing
    final Area ROPE_SWING_AREA = new Area(3003, 3953, 3008, 3949);
    // Cross | Steeping stone
    final Area STEPPING_STONES = new Area(3001, 3965, 3008, 3958);
    // Walk-across | Log balance
    final Area LOG_BALANCE = new Area( // possibly use a can reach on the first segment of the log to decide this rather than area
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
    final Area ROCK_SLIDE = new Area(
            new Tile(2999, 3934, 0),
            new Tile(2999, 3942, 0),
            new Tile(2995, 3943, 0),
            new Tile(2996, 3949, 0),
            new Tile(2987, 3946, 0),
            new Tile(2987, 3934, 0));
    public static final Area COURSE = new Area(2987, 3966, 3008, 3931);

    List<AgilityStage> stages = Arrays.asList(
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

    public WildernessCourse(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);
    }

    Area SPIKE_AREA = new Area(2987, 10368, 3010, 10337);
    Timer timeSinceBoxed = new Timer(10 * 1000);
    List<Integer> foods = Arrays.asList(
            ItemID.JUG_OF_WINE,
            ItemID.BLIGHTED_KARAMBWAN,
            ItemID.BLIGHTED_MANTA_RAY,
            ItemID.BLIGHTED_KARAMBWAN
    );

    boolean hasFinishedLap() {
        return PlayerSettings.getBitValue(10676) == 1;
    }


    @Override
    public int onLoop() {
        if (SPIKE_AREA.contains(Players.getLocal())) {
            log("Get out of spikes");
            ObjectUtil.interact("Ladder");
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.JUG)) Inventory.dropAll(ItemID.JUG);

        if (Inventory.contains(ItemID.LOOTING_BAG_CLOSED)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            log("Open loot bag");
            Inventory.interact(ItemID.LOOTING_BAG_CLOSED);
            return ReactionGenerator.getNormal();
        }

        if (!AreaUtils.containsIgnorePlane(COURSE, Players.getLocal().getTile())) {
            if (Combat.isAutoRetaliateOn()) {
                if (Bank.isOpen()) Bank.close();
                Logger.info("Turn off auto retaliate");
                Combat.toggleAutoRetaliate(false);
                return ReactionGenerator.getNormal();
            }
            int world =  BoxingClient.getInstance().world;
            if (!Players.getLocal().isInCombat() && world > 0 && Worlds.getCurrentWorld() != world) {
                log("Hop to team world");
                WorldHopper.hopWorld(world);
                return ReactionGenerator.getNormal();
            }
            if (Walking.shouldWalk(8)) Walking.walk(PIPE_AERA.getCenter());
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            log("Handle dialogue");
            Dialog.solve("ask again");
            return ReactionGenerator.getNormal();
        }

        if (Inventory.count(ItemID.COINS_995) >= 150_000) {
            ObjectUtil.interact("Agility dispenser", "Pay");
            return ReactionGenerator.getNormal();
        }

        if (hasFinishedLap()) {
            log("Can Tag for reward");
            if (Inventory.contains(ItemID.AGILITY_ARENA_TICKET)) {
                log("Hand in ticket");
                ObjectUtil.interact("Agility dispenser", "Redeem");
                return ReactionGenerator.getNormal();
            }

            ObjectUtil.interact("Agility dispenser", "Tag");
            return ReactionGenerator.getNormal();
        }


        if (!LootingBag.refreshLootBagCache()) return ReactionGenerator.getNormal();

        if (timeSinceBoxed.finished()) {
            String teamMateName = BoxingClient.getInstance().teamMate;
            Player p = Players.closest(teamMateName);
            if (p != null) {
                log("Teammate is X far: " + p.distance());
                log("Combat diff " + (p.getLevel() - Combat.getCombatLevel()));
                if (p.equals(Players.getLocal().getInteractingCharacter()) || p.equals(Players.getLocal().getCharacterInteractingWithMe())) {
                    log("In combat with teammate continue");
                    timeSinceBoxed.reset();
                }
                if (p.canReach() && p.canAttack() && p.distance() < 5) {
                    log("Attacking teammate");
                    p.interact("Attack");
                    return ReactionGenerator.getQuick();
                }
            }
        }

        if (Inventory.contains(x -> foods.contains(x.getId())) && Combat.getHealthPercent() < 40) {
            log("eat");
            Inventory.interact(x -> foods.contains(x.getId()));
        }

        if (redoLog) {
            // redo log obstacle
            log("Redo log");
            stages.get(3).onLoop();
            return ReactionGenerator.getNormal();
        }

//        if (redoRope) {
//            log("Redo rope");
//            stages.get(1).onLoop();
//            return ReactionGenerator.getNormal();
//        }

        for (AgilityStage stage : stages) {
            if (stage.isValid()) return stage.onLoop();
        }
        return ReactionGenerator.getNormal();
    }


    boolean redoRope;
    boolean redoLog;

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        String msg = message.getMessage();

        if (msg.contains("You slip and fall onto the spikes")) {
            redoLog = true;
            return;
        }

        if (msg.contains("You slip and fall to the pit")) {
            redoRope = true;
            return;
        }
        if (msg.contains("You skillfully edge across the gap")) redoLog = false;
        if (msg.contains("You skillfully swing across")) redoRope = false;
    }
}
