package org.dreambot.generics;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.fractals.IronFractal;
import org.dreambot.loadouts.data.ItemSpawn;
import org.dreambot.utility.Dialog;
import org.dreambot.utility.Entities;
import org.dreambot.utility.OwnedItems;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Accessors(chain = true)
/**
 * Entity interaction is for basic, go somewhere (optional), interact with something, handle a dialogue (optional)
 * One of the most common things to do in the game, used in all sorts of quests and activities
 *
 * Any construction with string name *should* prefer npc than a gameobject
 */
@Setter
public class GenericEntityInteraction extends IronFractal {
    Area entityLocation;
    final Supplier<Entity> entitySupplier;
    String[] dialogueChoices;
    String action;
    boolean doReachCheck = true;

    Condition sleepCondition = Dialogues::inDialogue;
    Condition resetCondition = () -> false;
    int sleepTime = 2400;
    int polling = 200;

    // handle item processing
    int processingItem = 0;

    public GenericEntityInteraction setDialogueChoices(String... dialogueChoices) {
        this.dialogueChoices = dialogueChoices;
        return this;
    }

    public GenericEntityInteraction(BooleanSupplier acceptCond, String entityName, Area npcLocation) {
        super(acceptCond);
        this.entityLocation = npcLocation;
        this.entitySupplier = () -> Entities.closest(entityName);
        setSimpleName(entityName);
    }

    public GenericEntityInteraction(ItemSpawn itemSpawn) {
        super(() -> !OwnedItems.contains(itemSpawn.getItemId()));
        this.entityLocation = itemSpawn.getSpawnLocation();
        this.entitySupplier = () -> GroundItems.closest(itemSpawn.getItemId());
        setSimpleName("Take 1 " + itemSpawn.getSimpleName());
        setAction("Take");
    }

    public GenericEntityInteraction(ItemSpawn itemSpawn, int quantity) {
        super(() -> OwnedItems.count(itemSpawn.getItemId()) < quantity);
        this.entityLocation = itemSpawn.getSpawnLocation();
        this.entitySupplier = () -> GroundItems.closest(itemSpawn.getItemId());
        setSimpleName("Take " + quantity + " " + itemSpawn.getSimpleName());
        setAction("Take");
    }

    public GenericEntityInteraction(BooleanSupplier acceptCond, Supplier<Entity> entitySupplier) {
        super(acceptCond);
        this.entityLocation = null;
        this.entitySupplier = entitySupplier;
    }

    public GenericEntityInteraction(BooleanSupplier acceptCond, Area area, Supplier<Entity> entitySupplier) {
        super(acceptCond);
        this.entityLocation = area;
        this.entitySupplier = entitySupplier;
    }

    public GenericEntityInteraction(String entityName) {
        super(() -> true);
        this.entitySupplier = () -> Entities.closest(entityName);
        this.entityLocation = null;
        setSimpleName(entityName);
    }

    public GenericEntityInteraction(BooleanSupplier acceptCond, String entityName, Tile npcLoc, int radius) {
        super(acceptCond);
        this.entityLocation = npcLoc.getArea(radius);
        this.entitySupplier = () -> Entities.closest(entityName);
        setSimpleName(entityName);
    }

    public GenericEntityInteraction(BooleanSupplier acceptCond, String entityName, Tile npcLoc) {
        super(acceptCond);
        this.entityLocation = npcLoc.getArea(5);
        this.entitySupplier = () -> Entities.closest(entityName);
        setSimpleName(entityName);
    }

    @Override
    protected int onLoop() {
        // tutorial island special dialogue
        if (TutorialTree.tutState() >= 1000 ? Dialogues.inDialogue() : (Dialogues.isProcessing() || Dialogues.areOptionsAvailable() || Dialogues.canContinue())) {
            if (ItemProcessing.isOpen() && processingItem != 0) {
                log("Processing item " + processingItem);
                ItemProcessing.makeAll(processingItem);
                Sleep.sleepUntil(() -> false, () -> Players.getLocal().isAnimating(), 1400, 500);
                return sleep();
            }

            if (dialogueChoices != null) {
                Dialog.solve(dialogueChoices);
            } else {
                Dialog.solve("");
            }
            return sleep();
        }

        // location can be null if we just assume they're near us, or its a random spawn
        if (entityLocation != null && !entityLocation.contains(Players.getLocal())) {
            log("Go to Entity location Distance " + entityLocation.distance(Players.getLocal().getTile()) + " "
                    + entityLocation.getTile() + " " + Players.getLocal().getTile());
            if (Walking.shouldWalk()) Walking.walk(entityLocation);
            return sleep();
        }

        Entity n = entitySupplier.get();
        if (n != null) {
            if (doReachCheck && !n.canReach()) {
                log("Cant reach walking closer");
                if (Walking.shouldWalk()) Walking.walkExact(n.getTile());
                return sleep();
            }

            if (action != null) {
                log("Interacting with entity " + action + " " + n.distance());
                n.interact(action);
            } else {
                log("Interacting with entity");
                n.interact();
            }
            Sleep.sleepUntil(sleepCondition, resetCondition, sleepTime, polling);
        } else {
            log("Failed to find entity ");
        }
        return sleep();
    }
}
