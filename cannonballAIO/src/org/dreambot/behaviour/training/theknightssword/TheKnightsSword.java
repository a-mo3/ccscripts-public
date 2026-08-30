package org.dreambot.behaviour.training.theknightssword;


import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;

import java.util.Arrays;
import java.util.List;

public class TheKnightsSword extends Fractal {
    private final Area SQUIRE_AREA = new Area(
            new Tile(2980, 3337, 0),
            new Tile(2980, 3346, 0),
            new Tile(2976, 3346, 0),
            new Tile(2974, 3348, 0),
            new Tile(2971, 3347, 0),
            new Tile(2971, 3337, 0)
    );

    public static final InventoryLoadout KNIGHTS_SWORD_LOADOUT = new InventoryLoadout()
            .addItem(ItemID.REDBERRY_PIE, 1)
            .addItem(ItemID.BRONZE_PICKAXE, 1)
            .addItem(ItemID.IRON_BAR, 2)
            .addItem(ItemID.LOBSTER, 8)
            .addItem(ItemID.VARROCK_TELEPORT, 1, 5)
            .addItem(ItemID.FALADOR_TELEPORT, 1, 6)
            .setStrict(true);

    private final Area LIBRARY = new Area(3214, 3497, 3207, 3490, 0);

    private final Area THURGO_HOUSE = new Area(
            new Tile(2995, 3148, 0),
            new Tile(3002, 3148, 0),
            new Tile(3002, 3142, 0),
            new Tile(3005, 3142, 0),
            new Tile(3003, 3138, 0),
            new Tile(2996, 3138, 0));

    public TheKnightsSword() {
        this.acceptCondition = () -> !FreeQuest.THE_KNIGHTS_SWORD.isFinished();
        this.paintArraySupplier = () -> new String[]{
                "The knights sword: " + FreeQuest.THE_KNIGHTS_SWORD.getState()
        };

        List<Integer> thurgoSteps = Arrays.asList(2, 3, 10);
        List<Integer> squireSteps = Arrays.asList(0);
        Tile PORT_TILE = new Tile(2984, 3335, 2);
        addChildren(
//                new DeathHandleFractal(),
                new TalkToFractal(() -> squireSteps.contains(FreeQuest.THE_KNIGHTS_SWORD.getConfigValue()), SQUIRE_AREA, () -> NPCs.closest("Squire"))

                        .setDialogueOptions(
                                "life as a squire", "I can make a new sword", "make another one", "give it a go", "Yes"
                        )
                        .setInventoryLoadout(KNIGHTS_SWORD_LOADOUT)
                        .setSimpleName("Start knights sword"),

                new TalkToFractal(() -> FreeQuest.THE_KNIGHTS_SWORD.getConfigValue() == 1, LIBRARY, () -> NPCs.closest("Reldo"))
                        .setDialogueOptions("Imcando dwarves")
                        .setSimpleName("Talking to Reldo"),

                new TalkToFractal(() -> thurgoSteps.contains(FreeQuest.THE_KNIGHTS_SWORD.getConfigValue()), THURGO_HOUSE, () -> NPCs.closest("Thurgo"))
                        .setDialogueOptions("redberry pie", "special sword")
                        .setSimpleName("Giving Thurgo pie"),

                new GetBlurite(() -> !Inventory.contains(ItemID.BLURITE_ORE) && FreeQuest.THE_KNIGHTS_SWORD.getConfigValue() == 4)
                        .setInventoryLoadout(new InventoryLoadout()
                                        .addItem(ItemID.BRONZE_PICKAXE)
                                        .addItem(ItemID.LOBSTER, 8).setEnabledCondition(() -> !Inventory.contains(ItemID.LOBSTER))
//                                .setStrict(true)
                        ).setSimpleName("Get Blurite"),

                new TalkToFractal(() -> FreeQuest.THE_KNIGHTS_SWORD.getConfigValue() == 4, SQUIRE_AREA, () -> NPCs.closest("Squire"))
                        .setAppendLogic(() -> {
                            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 7) {
                                Inventory.interact(ItemID.LOBSTER, "Eat");
                            }

                            if (Players.getLocal().isInCombat() && Walking.getRunEnergy() > 10 && !Walking.isRunEnabled()) {
                                Walking.toggleRun();
                            }
                            return false;
                        })
                        .setSimpleName("Got blurite talking to squire..."),

                new TalkToFractal(() -> FreeQuest.THE_KNIGHTS_SWORD.getConfigValue() == 5 && !Inventory.contains(ItemID.PORTRAIT),
                        PORT_TILE.getArea(2), () -> GameObjects.closest("Cupboard"))
                        .setInteraction(new String[]{"Open", "Search"})
                        .setSimpleName("Opening Cupboard, World hopping is expected")
                        .setAppendLogic(() -> {
                            if (Dialogues.inDialogue()) {
                                Dialog.solve();
                                Sleep.sleep(2000);
                                WorldHopper.quickHop(Worlds.getRandomWorld(w -> w.isMembers() == Client.isMembers()
                                        && w.isNormal()
                                        && w.getMinimumLevel() < Skills.getTotalLevel()).getWorld()
                                );
                                return true;
                            }
                            return false;
                        }),

                new TalkToFractal(() -> !Inventory.contains(ItemID.BLURITE_SWORD), THURGO_HOUSE, () -> NPCs.closest("Thurgo"))
                        .setDialogueOptions("About that sword...", "replacement sword")
                        .setInventoryLoadout(
                                new InventoryLoadout()
                                        .addItem(ItemID.IRON_BAR, 2)
                                        .addItem(ItemID.PORTRAIT).setEnabledCondition(() -> OwnedItems.contains(ItemID.PORTRAIT))
                                        .addItem(ItemID.BLURITE_ORE)
                        )
                        .setSimpleName("Getting sword"),

                new TalkToFractal(() -> FreeQuest.THE_KNIGHTS_SWORD.getConfigValue() == 6, SQUIRE_AREA, () -> NPCs.closest("Squire"))
                        .setSimpleName("Finish.")
        );
    }
}
