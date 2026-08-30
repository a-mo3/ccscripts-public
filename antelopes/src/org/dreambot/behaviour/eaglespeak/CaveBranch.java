package org.dreambot.behaviour.eaglespeak;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;

import java.util.function.Supplier;

public class CaveBranch extends Fractal {
    Supplier<Boolean> hasntSpokenToNickolaus = () -> PlayerSettings.getBitValue(3110) < 3;
    static final Area EAGLES_PEAK_CAVE = new Area(1981, 4990, 2032, 4943, 3);
    static final Tile EAGLES_PEAK_ENTRANCE = new Tile(2328, 3496, 0);
    static final int EAGLES_PEAK_ENTRANCE_ID = 19790;
    final Area SHOUT_AT_NICOLAUS = new Area(2003, 4969, 2005, 4967, 3);
    final Area ASYFF_AREA = new Area(3277, 3400, 3283, 3394);
    final Area IN_CAVE = new Area(1982, 4990, 2031, 4944, 3);
    final Area FEATHER_DOOR = new Area(2000, 4949, 2002, 4947, 3);
    /*
            hasInsertedBronzeFeather = new VarbitRequirement(3108, 1);
        hasInsertedSilverFeather = new VarbitRequirement(3099, 6);
        hasInsertedGoldFeather = new VarbitRequirement(3107, 1);

     */
    Supplier<Boolean> hasInsertedBronzeFeather = () -> PlayerSettings.getBitValue(3108) == 1;
    Supplier<Boolean> hasInsertedSilverFeather = () -> PlayerSettings.getBitValue(3099) == 6;
    Supplier<Boolean> hasInsertedGoldFeather = () -> PlayerSettings.getBitValue(3107) == 1;

    @Override
    public boolean isValid() {
        return PaidQuest.EAGLES_PEAK.getConfigValue() == 15;
    }

    public CaveBranch() {
        this.paintArraySupplier = () -> new String[]{
                hasntSpokenToNickolaus.get() ? "Hasnt spoken to Nickolaus" : "Has spoken to Nickolaus"
        };

        addChildren(
                new Fractal(() -> !OwnedItems.contains(ItemID.EAGLE_CAPE))
                        .addChildren(

                                new TalkToFractal(hasntSpokenToNickolaus, SHOUT_AT_NICOLAUS, () -> NPCs.closest("Nickolaus"))
                                        .setInteraction("Shout-to")
                                        .setDoReachCheck(false)
                                        .setDialogueOptions("zookeeper sent me", "gave me a ferret", "Could I help")
                                        .setAppendLogic(enterCave)
                                        .setSimpleName("Talk to Nickolaus"),

                                new TalkToFractal(() -> Inventory.count(ItemID.EAGLE_FEATHER) < 10,
                                        SHOUT_AT_NICOLAUS,
                                        () -> GameObjects.closest("Giant feathers"))
                                        .setInteraction("Take")
                                        .setSleepTimeout(600)
                                        .setAppendLogic(enterCave)
                                        .setSimpleName("Pick up feathers"),

                                new TalkToFractal(() -> true, ASYFF_AREA, () -> NPCs.closest("Asyff"))
                                        .setDialogueOptions("bird costume", "feathers and materials", "Eagle me up.")
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.YELLOW_DYE)
                                                .addItem(ItemID.SWAMP_TAR)
                                                .addItem(ItemID.EAGLE_FEATHER, 10)
                                                .addItem(ItemID.VARROCK_TELEPORT, 1, 5)
                                                .addItem(ItemID.PISCATORIS_TELEPORT, 1, 5)
                                                .addItem(ItemID.COINS_995, 50))
                                        .setAppendLogic(() -> {
                                            if (IN_CAVE.contains(Players.getLocal())) {
                                                if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
                                                return true;
                                            }
                                            return false;
                                        })
                                        .setSimpleName("Buy outfit from Asyff")
                        ).setSimpleName("Get Eagle Outfit"),

                new GoldPuzzleSolver(() -> !Inventory.contains("Golden feather") && !hasInsertedGoldFeather.get()),
                new SilverPuzzleSolver(() -> !Inventory.contains("Silver feather") && !hasInsertedSilverFeather.get()),
                new BronzePuzzleSolver(() -> !Inventory.contains("Bronze feather") && !hasInsertedBronzeFeather.get()),

                new UseOnFractal(() -> Inventory.contains("Bronze feather"),
                        () -> Inventory.get("Bronze feather"),
                        () -> GameObjects.closest("Stone door"), true)
                        .setArea(FEATHER_DOOR)
                        .setSleepCondition(Dialogues::inDialogue)
                        .setAppendLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                GameObject tunnel = GameObjects.closest("Tunnel");
                                if (tunnel != null && tunnel.interact("Enter")) {
                                    Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 2400);
                                }
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Use bronze feather"),

                new UseOnFractal(() -> Inventory.contains("Silver feather"),
                        () -> Inventory.get("Silver feather"),
                        () -> GameObjects.closest("Stone door"), true)
                        .setArea(FEATHER_DOOR)
                        .setSleepCondition(Dialogues::inDialogue)
                        .setSimpleName("Use silver"),

                new UseOnFractal(() -> Inventory.contains("Golden feather"),
                        () -> Inventory.get("Golden feather"),
                        () -> GameObjects.closest("Stone door"), true)
                        .setArea(FEATHER_DOOR)
                        .setSleepCondition(Dialogues::inDialogue)
                        .setSimpleName("Use golden")
        );
    }

    // to make sure you are in the cave when you should be
    // false if in cave
    public static final Supplier<Boolean> enterCave = () -> {
        if (!EAGLES_PEAK_CAVE.contains(Players.getLocal())) {
            GameObject entrance = GameObjects.closest(EAGLES_PEAK_ENTRANCE_ID);
            if (entrance == null || !EAGLES_PEAK_ENTRANCE.getArea(2).contains(Players.getLocal())) {
                if (!EaglesPeak.ON_MOUNTAIN.contains(Players.getLocal())) {
                    if (Walking.shouldWalk()) Walking.walk(EaglesPeak.MOUNTAIN_START);
                    return true;
                }
                if (Walking.shouldWalk()) Walking.walk(EAGLES_PEAK_ENTRANCE);
                return true;
            }

            if (Inventory.isItemSelected()) {
                Inventory.deselect();
                return true;
            }

            entrance.interact("Enter");
            return true;
        }
        return false;
    };
}
