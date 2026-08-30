package org.dreambot.behaviour.method.moonsofperil.eclipsemoon;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.function.Consumer;

/**
 * The phase where you hide behind the eclipsed moon
 */
public class EclipseMoonShieldPhase extends TickDecision {
    public EclipseMoonShieldPhase() {
        setSimpleName("Shield phase");
    }

    public static final int MOON_SHIELD_NPC_ID = 13020;

    // the direction the shield is moving and then therefore the offset from its tile you would need
    enum MoveMode {
        // moving south -> north
        NORTH(x -> x.translate(-1, 1)),
        // moving north -> south
        SOUTH(x -> x.translate(2, 0)),
        // moving west -> east
        EAST(x -> x.translate(1, 2)),
        // moving east -> west
        WEST(x -> x.translate(0, -1));

        // the offset you should be standing on given the moon shields server tile.
        final Consumer<Tile> tileOffset;

        MoveMode(Consumer<Tile> tileOffset) {
            this.tileOffset = tileOffset;
        }
        // todo add transforms
    }

    MoveMode moveMode = MoveMode.SOUTH;

    @Override
    public boolean evaluate() {
        NPC moonShield = NPCs.closest(MOON_SHIELD_NPC_ID);
        if (moonShield == null) {
            return false;
        }

        Prayers.toggleQuickPrayer(false);

        // we can eat to safe in this phase
        int healAmount = (int) (Math.min(Skill.COOKING.getLevel(), Skill.FISHING.getLevel()) * 0.3);
        int missingHealth = Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel();
        // you can walk and eat in the same tick so this should be fine
        if (healAmount <= missingHealth && Inventory.contains(ItemID.COOKED_BREAM)) {
            log("Shield phase safe to eat");
            Inventory.interact(ItemID.COOKED_BREAM);
        }

        int orient = moonShield.getOrientation();
        moveMode = MoveMode.SOUTH;
        if (orient > 400) moveMode = MoveMode.WEST;
        if (orient > 900) moveMode = MoveMode.NORTH;
        if (orient > 1400) moveMode = MoveMode.EAST;

        Tile shieldTile = moonShield.getServerTile();
        log("Shield on " + shieldTile);
        moveMode.tileOffset.accept(shieldTile);
        log("Stand on " + shieldTile);

        if (!shieldTile.equals(Players.getLocal().getServerTile()) && !shieldTile.equals(Walking.getDestination())) {
            log("Walk onto safe");
            Walking.walkExact(shieldTile);
        }
        return true;
    }
}
