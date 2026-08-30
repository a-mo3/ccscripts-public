package org.dreambot.behaviour.quests.templeoftheeye;

import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
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
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TempleOfTheEye extends Fractal {
    public static final Area ABYSS = new Area(3006, 4861, 3074, 4804);
    public static final Area NULL_AREA = null;
    public static final Tile PERSTREN_TWO = new Tile(3285, 3232, 0);
    public static final Tile ARCHMAGE_SED = new Tile(3104, 9571, 0);
    public static final Tile TRAIBORN_TILE = new Tile(3112, 3162, 1);

    Supplier<Boolean> felixPuzzleNotSeen = () -> PlayerSettings.getBitValue(13743) == 0;
    Supplier<Boolean> tamaraPuzzleNotSeen = () -> PlayerSettings.getBitValue(13742) == 0;
    Supplier<Boolean> cordeliaPuzzleNotSeen = () -> PlayerSettings.getBitValue(13744) == 0;

    List<Integer> perstenStates = new ArrayList<Integer>() {{
        add(0);
        add(5);
    }};

    List<Integer> perstenTwoStates = new ArrayList<Integer>() {{
        add(45);
        add(50);
        add(55);
    }};

    List<Integer> zammyMageState = new ArrayList<Integer>() {{
        add(10);
        add(15);
        add(20);
//        add(25); doesnt change varbit after u tp
    }};

    List<Integer> darkMageStates = new ArrayList<Integer>() {{
        add(25);
        add(30);
        add(40);
    }};

    List<Integer> traibornState = new ArrayList<Integer>() {{
        add(70);
        add(75);
        add(80);
    }};

    public TempleOfTheEye() {
        this.acceptCondition = () -> !PaidQuest.TEMPLE_OF_THE_EYE.isFinished();
        this.paintArraySupplier = () -> new String[]{
                "Stage: " + questState()
        };
        addChildren(
                new TalkToFractal(() -> perstenStates.contains(questState()),
                        new Tile(3285, 3232, 0), () -> NPCs.closest("Wizard Persten"))
                        .setDialogueOptions("Yes.", "What's a wizard doing in Al Kharid?")
                        .setSimpleName("Start @ Wizard Persten in Al Kharid")
                        .setInventoryLoadout(
                                new InventoryLoadout()
                                        .addItem(ItemID.VARROCK_TELEPORT, 1, 5)
                                        .addItem(ItemVariants.AMULET_OF_GLORY)
                        )
                        .setAppendLogic(() -> {
                            if (EarthRunes.MUD_ALTAR.contains(Players.getLocal())) {
                                GameObject portal = GameObjects.closest("Portal");
                                if (portal != null) {
                                    portal.interact("Use");
                                    Sleep.sleepUntil(() -> !EarthRunes.MUD_ALTAR.contains(Players.getLocal()), 4400);
                                }
                                return true;
                            }
                            return false;
                        }),

                new TalkToFractal(() -> questState() == 15 && !Inventory.contains(ItemID.STRONG_CUP_OF_TEA),
                        new Tile(3271, 3411, 0), () -> NPCs.closest("Tea seller"))
                        .setDialogueOptions("strong cup of")
                        .setSimpleName("Get strong tea for zammy mage")
                        .setInventoryLoadout(
                                new InventoryLoadout()
                                        .addItem(ItemID.BUCKET_OF_WATER)
                                        .addItem(ItemID.BRONZE_PICKAXE)
                                        .addItem(ItemID.CHISEL)
                                        .addItem(ItemVariants.AMULET_OF_GLORY)
                                        .addItem(ItemID.VARROCK_TELEPORT, 1, 5)
                                        .addItem(ItemID.EYE_AMULET)
                                        .strictIgnore(ItemID.STRONG_CUP_OF_TEA)
                                        .setStrict(true)
                        ),
                new TalkToFractal(() -> zammyMageState.contains(questState()),
                        new Tile(3258, 3383, 0), () -> NPCs.closest("Mage of zamorak"))
                        .setDialogueOptions("amulet", "Yes.")
                        .setSimpleName("Talk to zammy mage in varrock"),

                new TalkToFractal(() -> questState() == 25 && !ABYSS.contains(Players.getLocal()),
                        new Tile(3258, 3383, 0), () -> NPCs.closest("Mage of zamorak"))
                        .setDialogueOptions("amulet", "Yes.")
                        .setSimpleName("Talk to zammy mage in varrock"),

                new TalkToFractal(() -> darkMageStates.contains(questState()),
                        NULL_AREA, () -> NPCs.closest("Dark mage"))
                        .setDialogueOptions("help with an amulet.")
                        .setDoReachCheck(false)
                        .setSimpleName("Talk to dark mage"),
                new ColoredCirclePuzzle(() -> questState() == 35).setSimpleName("Solve puzzle"),

                new TalkToFractal(() -> perstenTwoStates.contains(questState()), PERSTREN_TWO, () -> NPCs.closest("Wizard Persten"))
                        .setDialogueOptions("About that incantation")
                        .setSimpleName("Talk to Persten"),

                new TalkToFractal(() -> questState() == 60 || questState() == 65, ARCHMAGE_SED, () -> NPCs.closest("Archmage sedridor"))
                        .setDialogueOptions("Yes.", "I need your help with an incantation")
                        .setSimpleName("Talk to Archmage Sedridor"),

                new Fractal(() -> felixPuzzleNotSeen.get() || cordeliaPuzzleNotSeen.get() || tamaraPuzzleNotSeen.get()).setSimpleName("Solve Traiborn puzzle")
                        .addChildren(
                                new TalkToFractal(() -> questState() == 75 && felixPuzzleNotSeen.get(), TRAIBORN_TILE, () -> NPCs.closest("Apprentice Felix"))
                                        .setDialogueOptions("I'd better go.")
                                        .setSimpleName("Felix"),
                                new TalkToFractal(() -> questState() == 75 && tamaraPuzzleNotSeen.get(), TRAIBORN_TILE, () -> NPCs.closest("Apprentice tamara"))
                                        .setDialogueOptions("I'd better go.")
                                        .setSimpleName("tamara"),

                                new TalkToFractal(() -> questState() == 75 && cordeliaPuzzleNotSeen.get(), TRAIBORN_TILE, () -> NPCs.closest("Apprentice cordelia"))
                                        .setDialogueOptions("I'd better go.")
                                        .setSimpleName("cordelia")
                        ),

                new TalkToFractal(() -> traibornState.contains(questState()), TRAIBORN_TILE, () -> NPCs.closest("Wizard Traiborn"))
                        .setDialogueOptions("I need your apprentices to help with an incantation.", "Okay, thanks", "I think I know what a")
                        .setAppendLogic(() -> {
                            if (Dialogues.canEnterInput()) {
                                Keyboard.type("11", true);
                                Sleep.sleep(4000);
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Get help from Traiborn"),

                new TalkToFractal(() -> questState() == 85 || questState() == 90 || questState() == 95, ARCHMAGE_SED, () -> NPCs.closest("Archmage sedridor"))
                        .setDialogueOptions("Yes.", "Let's do it")
                        .setAppendLogic(Client::isInCutscene)
                        .setSimpleName("Talk to Archmage Sedridor"),

                new TalkToFractal(() -> questState() == 100, NULL_AREA, () -> NPCs.closest("Apprentice cordelia"))
                        .setDialogueOptions("I'd better go.")
                        .setAppendLogic(() -> {
                            NPC cord = NPCs.closest("Apprentice cordelia");
                            if (cord == null || !cord.isOnScreen()) {
                                if (Walking.shouldWalk()) Walking.walk(Players.getLocal().getTile().translate(0, 10));
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("cordelia")
        );
    }

    private int questState() {
        return PaidQuest.TEMPLE_OF_THE_EYE.getConfigValue();
    }
}
