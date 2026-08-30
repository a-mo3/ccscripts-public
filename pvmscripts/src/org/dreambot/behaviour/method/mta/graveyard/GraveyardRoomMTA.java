package org.dreambot.behaviour.method.mta.graveyard;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.method.mta.MTANodes;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Objects;
import java.util.function.Supplier;

public class GraveyardRoomMTA extends Fractal {
    private final int FALLING_BONE_GRAPHIC = 522;
    public static final Area GRAVE_ROOM = new Area(3339, 9663, 3387, 9617, 1);

    public GraveyardRoomMTA(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Graveyard room");
        MTANodes.init();

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                .addItem(EquipmentSlot.WEAPON, ItemID.MUD_BATTLESTAFF);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.NATURE_RUNE, 1, 1600);

        this.paintArraySupplier = () -> new String[]{
                "B count " + getInvPoints(),
                ""
        };
    }

    // if false we go to east to of pile, if true we go below, switches when we see a falling bone
    boolean moveSwitch;
    Tile boneSpot = new Tile(3352, 9637, 1);
    Tile chuteTile = new Tile(3354, 9639, 1);
    /*
    ID: 10735 Real ID: 10735 Tile: (3354, 9639, 1)
     */

    @Override
    public int onLoop() {
        if (!GRAVE_ROOM.contains(Players.getLocal())) {
            log("Go to grave room");
            if (Walking.shouldWalk()) Walking.walk(GRAVE_ROOM);
            return ReactionGenerator.getNormal();
        }

        // todo something to leave or eat if low hp
        if (Inventory.contains("Banana")) {
            if (Skill.HITPOINTS.getBoostedLevel() < 8) {
                log("Low HP eat a banana");
                Inventory.interact("Banana");
                return ReactionGenerator.getQuick();
            }

            GameObject chute = GameObjects.closest(x -> chuteTile.equals(x.getTile()) && x.getName().equals("Food chute"));
            if (chute == null) {
                log("Failed to find food chute");
            } else {
                chute.interact("Deposit");
                Sleep.sleepUntil(() -> !Inventory.contains("Banana"), 2400);
            }
            return ReactionGenerator.getNormal();
        }

        // dodge logic
        log(GraphicsObjects.all(522).size() + " ");
        GraphicsObject falling = GraphicsObjects.closest(x -> x.getTile().equals(Players.getLocal().getTile()));
        if (Walking.getDestination() == null && falling != null) {
            log("Move ");
            moveSwitch = !moveSwitch;
        }

        if (moveSwitch) {
            if (Players.getLocal().getX() - 1 != boneSpot.getX()) {
                log("Moving to east of bone");
                Walking.walkExact(boneSpot.clone().translate(1, 0));
                return ReactionGenerator.getNormal();
            }
        } else {
            if (Players.getLocal().getY() + 1 != boneSpot.getY()) {
                log("Moving to south of bone");
                Walking.walkExact(boneSpot.clone().translate(0, -1));
                return ReactionGenerator.getNormal();
            }
        }

        if (getInvPoints() >= 24) {
            log("enough bones cast BTB");
            // todo once bones to peaches unlocked do that instead
            Magic.castSpell(Normal.BONES_TO_BANANAS);
            return ReactionGenerator.getNormal();
        }


        GameObject bonePile = GameObjects.closest(x -> boneSpot.equals(x.getTile()));
        if (bonePile == null) {
            log("Failed to find bone pile");
        } else {
            bonePile.interact();
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }

    private int getInvPoints() {
        return Inventory.all().stream().filter(Objects::nonNull).mapToInt(x -> getPoints(x.getId())).sum();
    }

    private int getPoints(int id) {
        switch (id) {
            // 18/05/26
            // these are animal bones no item id because it would break shit if someone replaced them
            case 6904:
                return 5;
            case 6905:
                return 2;
            case 6906:
                return 3;
            case 6907:
                return 4;
            default:
                return 0;
        }
    }
}
