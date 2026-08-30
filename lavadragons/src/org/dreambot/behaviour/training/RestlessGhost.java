package org.dreambot.behaviour.training;

import org.dreambot.api.methods.container.impl.Inventory;
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
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;

public class RestlessGhost extends Fractal {
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
                        () -> FreeQuest.THE_RESTLESS_GHOST.getConfigValue() == 2,
                        coffinArea,
                        () -> NPCs.closest("Restless ghost"))
                        .setDialogueOptions("Yes", "Yep")
                        .setAppendLogic(() -> {
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
                        () -> FreeQuest.THE_RESTLESS_GHOST.getConfigValue() == 3,
                        new Tile(3120, 9567, 0).getArea(5),
                        () -> GameObjects.closest("Altar"))
                        .setInteraction("Search")
                        .setSimpleName("Get ghosts skull"),

                new UseOnFractal(
                        () -> FreeQuest.THE_RESTLESS_GHOST.getConfigValue() == 4,
                        () -> Inventory.get(ItemID.GHOSTS_SKULL),
                        () -> GameObjects.closest("Coffin"), true)
                        .setArea(coffinArea)
                        .setAppendLogic(() -> {
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
