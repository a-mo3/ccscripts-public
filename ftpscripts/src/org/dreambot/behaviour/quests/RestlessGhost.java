package org.dreambot.behaviour.quests;

import org.apache.log4j.Logger;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;

public class RestlessGhost extends Fractal {
    private static final Logger log = Logger.getLogger(RestlessGhost.class);

    public RestlessGhost() {
        this.acceptCondition = () -> !FreeQuest.THE_RESTLESS_GHOST.isFinished();
        this.paintArraySupplier = () -> new String[]{
                "Restless Ghost: " + FreeQuest.THE_RESTLESS_GHOST.getConfigValue()
        };

        Area coffinArea = new Tile(3250, 3193, 0).getArea(2);
        addChildren(
                new TalkToFractal(
                        () -> !FreeQuest.THE_RESTLESS_GHOST.isStarted(),
                        new Tile(3243, 3206, 0).getArea(5),
                        () -> NPCs.closest("Father Aereck"))
                        .setDialogueOptions("quest!", "Yes.")
                        .setInventoryLoadout(new InventoryLoadout().setStrictSupplier(() -> Inventory.emptySlotCount() < 6))
                        .setSimpleName("Start @ Aereck"),


                new TalkToFractal(
                        () -> FreeQuest.THE_RESTLESS_GHOST.getConfigValue() == 1,
                        new Tile(3147, 3175, 0).getArea(5),
                        () -> NPCs.closest("Father Urhney"))
                        .setDialogueOptions(
                                "Father Aereck",
                                "ghost haunting his"
                        )
                        .setSimpleName("Father Urhney"),

                new TalkToFractal(
                        () -> !OwnedItems.contains(ItemID.GHOSTSPEAK_AMULET),
                        new Tile(3147, 3175, 0).getArea(5),
                        () -> NPCs.closest("Father Urhney"))
                        .setDialogueOptions("lost the")
                        .setInventoryLoadout(new InventoryLoadout().setStrictSupplier(() -> Inventory.emptySlotCount() < 6))
                        .setPrependLogic(() -> {
                            if (!Bank.isCached()) {
                                log("get new bank cache");
                                if (Bank.open()) Bank.updateCache();
                                return true;
                            }

                            String npcChat = Dialogues.getNPCDialogue();
                            if (Bank.isCached() && npcChat != null && npcChat.contains("stored somewhere")) {
                                log("You probably have ghostspeak in coffer, forcing bank cache and death check");
                                if (!Bank.isCached()) new BankAllInventoryEvent().execute();
//                                EmptyDeathsCoffer.forceEmpty = true;
                            }
                            return false;
                        })
                        .setSimpleName("Get new Ghostspeak"),

                new TalkToFractal(
                        () -> Bank.contains(ItemID.GHOSTSPEAK_AMULET),
                        new Tile(3147, 3175, 0).getArea(5),
                        () -> NPCs.closest("Father Urhney"))
                        .setDialogueOptions("lost the")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.GHOSTSPEAK_AMULET)
                                .setStrictSupplier(() -> Inventory.emptySlotCount() < 6))
                        .setSimpleName("Withdraw Ghostspeak"),

                new TalkToFractal(
                        () -> FreeQuest.THE_RESTLESS_GHOST.getConfigValue() == 2,
                        coffinArea,
                        () -> NPCs.closest("Restless ghost"))
                        .setDialogueOptions("Yes", "Yep")
                        .setPrependLogic(() -> {
                            if (Inventory.contains(ItemID.GHOSTSPEAK_AMULET)) {
                                Inventory.interact(ItemID.GHOSTSPEAK_AMULET);
                                return true;
                            }

                            if (coffinArea.contains(Players.getLocal())) {
                                NPC ghost = NPCs.closest("Restless ghost");
                                GameObject coffin = GameObjects.closest("Coffin");
                                if (ghost == null) {
                                    coffin.interact();
                                    return true;
                                }
                            }

                            return false;
                        })
                        .setSimpleName("Ghost"),

                new TalkToFractal(
                        () -> FreeQuest.THE_RESTLESS_GHOST.getConfigValue() == 3, // todo maybe replace with just if you dont own the skull, ID: 553
//                        () -> !Inventory.contains(ItemID.GHOSTS_SKULL) && !coffinArea.contains(Players.getLocal()), // todo maybe replace with just if you dont own the skull, ID: 553
                        new Tile(3120, 9567, 0).getArea(5),
                        () -> GameObjects.closest("Altar"))
                        .setInteraction("Search")
                        .setSimpleName("Get ghosts skull"),

                new UseOnFractal(
                        () -> FreeQuest.THE_RESTLESS_GHOST.getConfigValue() == 4,
                        () -> Inventory.get(ItemID.GHOSTS_SKULL),
                        () -> GameObjects.closest("Coffin"), true)
                        .setArea(coffinArea)
                        .setPrependLogic(() -> {
                            GameObject coffin = GameObjects.closest("Coffin");
                            if (coffin != null && coffin.canReach() && coffin.distance() < 2.5 && coffin.hasAction("Open")) {
                                coffin.interact("Open");
                                Sleep.sleep(3000);
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Place skull")
        );
    }
}
