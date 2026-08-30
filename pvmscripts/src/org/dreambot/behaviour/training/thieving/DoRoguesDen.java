package org.dreambot.behaviour.training.thieving;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ProjectileListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.Projectile;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllEquipmentEvent;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class DoRoguesDen extends Fractal implements ProjectileListener {
    public DoRoguesDen(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        Client.getInstance().addEventListener(this);
        paintArraySupplier = () -> new String[]{
                "State " + (currentRunState == null ? "-" : currentRunState.size())
        };

    }

    Queue<RoguesDenObstacle> currentRunState;
    RoguesDenObstacle currentStep;
    final Area WHOLE_MAZE = new Area(2937, 5121, 3077, 4954, 1);
    boolean hasToTalkToBrain; // the guy that lets you in

    @Override
    public int onLoop() {
        // talk to the guy 1st time
        String npcDialogue = Dialogues.getNPCDialogue();
        if (npcDialogue != null) {
            log("Brain: " + npcDialogue);
            if (npcDialogue.contains("Come and talk to me before you go wandering")) hasToTalkToBrain = true;
            if (npcDialogue.contains("When you enter the maze, I'll give you")) hasToTalkToBrain = false;
        }

        if (hasToTalkToBrain) {
            if (Dialogues.inDialogue()) {
                log("Solve brian dialogue");
                Dialog.solve("Yes actually", "Ok that sounds", "I want to try");
                return ReactionGenerator.getNormal();
            }

            NPC brian = NPCs.closest("Brian O'Richard");
            if (brian != null) {
                log("Talk to brian");
                brian.interact();
                Sleep.sleepUntil(Dialogues::inDialogue, 6000);
            } else {
                log("Cant find brian");
            }
            return ReactionGenerator.getNormal();
        }

        // go to rogues den before deposits
        if (!WHOLE_MAZE.contains(Players.getLocal())) {
            slowLog("Go to rogues den");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.ROGUES_DEN);
            return ReactionGenerator.getNormal();
        }

        // open crates
        if (Inventory.contains(ItemID.ROGUES_EQUIPMENT_CRATE)) {
            if (Dialogues.inDialogue()) {
                log("Redeeming crate");
                Dialog.solve("Rogue equipment", getPiece());
            } else {
                Inventory.interact(ItemID.ROGUES_EQUIPMENT_CRATE);
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        // drink stamina so you have enough to complete a course
        if (!Inventory.contains("Mystic jewel") && Walking.getRunEnergy() < (Walking.isStaminaActive() ? 60 : 75)) {
            log("Need to drink a stamina.");
            Item stamina = ItemVariants.STAMINA_POTION.getItem();
            if (stamina == null) {
                log("Get staminas");
                new WithdrawLoadoutEvent(new InventoryLoadout()
                        .addItem(ItemVariants.STAMINA_POTION, 1, 1)
                        .setRefill(25),
                        null)
                        .executed();
            } else {
                log("Drink stamina");
                if (Widgets.isOpen()) Widgets.closeAll();
                stamina.interact();
                Sleep.sleepTicks(2);
            }
            return ReactionGenerator.getNormal();
        }


        // deposit all items if you arent in maze
        if (!Inventory.contains("Mystic jewel") && !Inventory.isEmpty() || !Equipment.isEmpty()) {
            log("Deposit items");
            new BankAllInventoryEvent().execute();
            new BankAllEquipmentEvent().execute();
        }

        // todo redeem crates if they are in inv or bank

        // if you dont have gem you are not in the jaunt and you can reset the queue
        if (currentRunState == null) {
            log("Init new queue");
            currentRunState = makeQueue();
        }

        // handle looting
        if (currentRunState.isEmpty() && Inventory.contains("Mystic jewel")) {
            log("Loot den");
            GameObject safe = GameObjects.closest("Wall safe");
            if (safe != null) {
                log("crack wall safe");
                safe.interact();
                Sleep.sleepUntil(() -> !Inventory.contains("Mystic jewel"), 6000);
            }
            return ReactionGenerator.getNormal();
        }


        if (currentStep == null || currentStep.completeCondition.getAsBoolean()) {
            log("Poll");
            currentStep = currentRunState.poll();
            return ReactionGenerator.getNormal();
        }

        // reset state  once you complete a lap
        if (currentRunState.size() < 49 && !Inventory.contains("Mystic jewel")) {
            log("Reset run state");
            currentRunState = makeQueue();
            currentStep = null;
            flashed = false;
            return ReactionGenerator.getNormal();
        }

        GroundItem tile = GroundItems.closest(5568);
        if (!Inventory.contains(5568) && tile != null && tile.distance() < 3) {
            log("Picking up a tile");
            tile.interact();
            Sleep.sleepUntil(() -> Inventory.contains(5568), 4400);
            return ReactionGenerator.getNormal();
        }

        WidgetChild wc = Widgets.get(688, 5);
        if (wc != null && wc.isVisible()) {
            log("Tile widget");
            if (!Inventory.contains(5568)) Widgets.closeAll();
            wc.interact();
            return ReactionGenerator.getNormal();
        }

        // logic for throwing flash powder at the guard
        if (!flashed) {
            GroundItem flash = GroundItems.closest(5559);
            if (!Inventory.contains(5559) && flash != null && flash.distance() < 3) {
                log("Picking up flash powder");
                flash.interact();
                Sleep.sleepUntil(() -> Inventory.contains(5559), 4400);
                return ReactionGenerator.getNormal();
            }

            if (Inventory.contains(5559)) {
                NPC guard = NPCs.closest(3191);
                if (guard != null && guard.getX() > Players.getLocal().getX()) {
                    log("Flash guard");
                    Inventory.get(5559).useOn(guard);
                    return ReactionGenerator.getNormal();
                } else {
                    log("Failed to find guard");
                    Inventory.dropAll(5559);
                    return ReactionGenerator.getNormal();
                }
            }
        }

        // logic for executing the obstacle, walk and game object interactions are handled
        if (currentStep.getObjectSupplier() == null) {
            // this is a walk step
            if (Walking.shouldWalk()) Walking.walkExact(currentStep.t);

            Sleep.sleepUntil(() -> currentStep.completeCondition.getAsBoolean() && Walking.getDestination() != null,
                    () -> Players.getLocal().isMoving() || Players.getLocal().isAnimating(),
                    800,
                    100);
        } else {
            // this is a game object step
            GameObject gObj = currentStep.objectSupplier.get();
            if (gObj != null) {
                log("Interacting with obstacle");
                gObj.interact(); // hopefully i just never need the action
            }

            Sleep.sleepUntil(currentStep.completeCondition::getAsBoolean, () -> Players.getLocal().isMoving() || Players.getLocal().isAnimating(),
                    800,
                    100);
        }
        return ReactionGenerator.getNormal();
    }

    boolean flashed = false; // set to true after you flash a guy, reset every run

    @Override
    public void onTargeted(Projectile projectile, Tile target) {
        // this might not work well if multiple people are running the jaunt at the same time
        if (projectile.getId() == 408) flashed = true;
    }

    private java.util.Queue<RoguesDenObstacle> makeQueue() {
        java.util.Queue<RoguesDenObstacle> queue = new LinkedList<>();

        // init all the steps
        queue.add(new RoguesDenObstacle(3056, 4991, "Doorway", () -> Inventory.contains("Mystic jewel")));
        queue.add(new RoguesDenObstacle(3049, 4997, "Contortion Bars", () -> Players.getLocal().getX() < 3049));
        queue.add(new RoguesDenObstacle(3035, 5003, true));
        queue.add(new RoguesDenObstacle(3028, 5003, true));
        queue.add(new RoguesDenObstacle(3024, 5001, "Grill", () -> Players.getLocal().getX() < 3024));
        queue.add(new RoguesDenObstacle(3011, 5005, true));
        queue.add(new RoguesDenObstacle(3004, 5003, true));
        queue.add(new RoguesDenObstacle(2994, 5004, true));
        queue.add(new RoguesDenObstacle(2993, 5004, "Ledge", () -> Players.getLocal().getX() < 2992));
        queue.add(new RoguesDenObstacle(2969, 5018, true, () -> Players.getLocal().getX() < 2969)); // this is a blade obstacle need to be exact here
        queue.add(new RoguesDenObstacle(2958, 5031, "Ledge", () -> Players.getLocal().getY() > 5034).setInteraction("Climb"));
        queue.add(new RoguesDenObstacle(2962, 5050, true));
        queue.add(new RoguesDenObstacle(2963, 5056, true));
        // 80 thieving shortcut here but im not going to support that
        queue.add(new RoguesDenObstacle(2957, 5069, "Passageway", () -> Players.getLocal().getY() > 5070).setInteraction("Enter"));
        queue.add(new RoguesDenObstacle(2955, 5084, true)); // this skips over blade but probably doesnt matter if its exact or not
        queue.add(new RoguesDenObstacle(2955, 5095, "Passageway", () -> Players.getLocal().getY() > 5096).setInteraction("Enter"));
        queue.add(new RoguesDenObstacle(2972, 5097, "Passageway", () -> Players.getLocal().getY() < 5095).setInteraction("Enter"));
        queue.add(new RoguesDenObstacle(2972, 5094, "Grill", () -> Players.getLocal().getY() < 5094).setInteraction("Open"));

        queue.add(new RoguesDenObstacle(2975, 5087, true)); // avoids rotating blades
        queue.add(new RoguesDenObstacle(2983, 5087, "Ledge", () -> Players.getLocal().getX() > 2990)); // maybe need precise obj
        queue.add(new RoguesDenObstacle(2993, 5087, "Wall", () -> Players.getLocal().isAnimating()).setInteraction("Search")); // maybe need precise obj

        queue.add(new RoguesDenObstacle(2997, 5088, true));
        queue.add(new RoguesDenObstacle(3006, 5088, true));

        queue.add(new RoguesDenObstacle(3018, 5081, true)); // this is next to the tile spawn
        // i dont think we need obstacle object to handle tile, just pick it up if its there and you dont have one
        // ID: 5568
        queue.add(new RoguesDenObstacle(3023, 5082, "Door", () -> Players.getLocal().getX() > 3023).setInteraction("Open")); // maybe need precise obj
        // tile widget is opened here, 688, 5 is the child we want to click, has "Select" action

        // maze
        queue.add(new RoguesDenObstacle(3030, 5079, "Grill", () -> Players.getLocal().getX() > 3030));
        queue.add(new RoguesDenObstacle(3032, 5078, "Grill", () -> Players.getLocal().getY() < 5078));
        queue.add(new RoguesDenObstacle(3036, 5076, "Grill", () -> Players.getLocal().getX() > 3036));
        queue.add(new RoguesDenObstacle(3039, 5079, "Grill", () -> Players.getLocal().getX() > 3039));
        queue.add(new RoguesDenObstacle(3042, 5076, "Grill", () -> Players.getLocal().getX() > 3042));
        queue.add(new RoguesDenObstacle(3044, 5069, "Grill", () -> Players.getLocal().getY() < 5069));
        queue.add(new RoguesDenObstacle(3041, 5068, "Grill", () -> Players.getLocal().getY() > 5068));
        queue.add(new RoguesDenObstacle(3040, 5070, "Grill", () -> Players.getLocal().getX() < 3040));
        queue.add(new RoguesDenObstacle(3038, 5069, "Grill", () -> Players.getLocal().getY() < 5069));

        // we might just be able to walk here but theres no nodes so i wont test it

        queue.add(new RoguesDenObstacle(3037, 5055, true));
        queue.add(new RoguesDenObstacle(3039, 5041, true));
        queue.add(new RoguesDenObstacle(3028, 5033, true)); // this is the tile you HAVE to stand on
        queue.add(new RoguesDenObstacle(3024, 5033, true)); // you are now past the hidden trap
        queue.add(new RoguesDenObstacle(3015, 5033, "Grill", () -> Players.getLocal().getX() < 3015));
        queue.add(new RoguesDenObstacle(3010, 5033, "Grill", () -> Players.getLocal().getX() < 3010)); // need to be running here

        queue.add(new RoguesDenObstacle(3000, 5034, true)); // think this really needs to be running

        queue.add(new RoguesDenObstacle(2992, 5045, true));
        queue.add(new RoguesDenObstacle(2992, 5055, true));
        // if i was to add the 80 thieving shortcut it starts here
        queue.add(new RoguesDenObstacle(2992, 5067, true));
        queue.add(new RoguesDenObstacle(2992, 5075, true));

        queue.add(new RoguesDenObstacle(3001, 5067, true));

        // flash powder tile
        queue.add(new RoguesDenObstacle(3009, 5064, true));
        /*
        [id=3191, index=21704] Rogue Guard
        flash powder id 5559
         */
        // in front of swinging maces
        queue.add(new RoguesDenObstacle(3028, 5056, true));
        queue.add(new RoguesDenObstacle(3028, 5047, true));
        queue.add(new RoguesDenObstacle(3018, 5047, true)); // next to value

        return queue;
    }

    private String getPiece() {
        if (!OwnedItems.contains(ItemID.ROGUE_BOOTS)) return "Boots";
        if (!OwnedItems.contains(ItemID.ROGUE_TOP)) return "Top";
        if (!OwnedItems.contains(ItemID.ROGUE_MASK)) return "Mask";
        if (!OwnedItems.contains(ItemID.ROGUE_TROUSERS)) return "Trousers";
        return "Gloves";
    }
}
