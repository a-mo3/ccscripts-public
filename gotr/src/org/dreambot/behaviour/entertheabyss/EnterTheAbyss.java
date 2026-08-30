package org.dreambot.behaviour.entertheabyss;


import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.MiniQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

public class EnterTheAbyss extends Fractal {
    public static final Area SEDRIDOR_ROOM = new Area(3096, 9574, 3107, 9566, 0);
    public static final Area WILD_ZAMORAK_MAGE = new Area(3094, 3574, 3116, 3552);
    public static final Area VARROCK_ZAM_MAGE = new Area(
            new Tile(3256, 3390, 0),
            new Tile(3263, 3390, 0),
            new Tile(3263, 3380, 0),
            new Tile(3253, 3380, 0),
            new Tile(3253, 3385, 0),
            new Tile(3256, 3386, 0));
    public static final Area AUBURY = new Area(3252, 3404, 3254, 3399, 0);
    public static final Area ARDY_AND_FISHING_GUILD = new Area(2549, 3412, 2701, 3306);
    public static final Area CROMPERTY = new Area(2682, 3327, 2685, 3319);
    /*
        teleportedFromWizardsTower = new VarbitRequirement(2314, 1);
		teleportedFromVarrock = new VarbitRequirement(2315, 1);
		teleportedFromArdougne = new VarbitRequirement(2316, 1);

		we will go aubury -> wizard tower, -> ardounge guy
     */
    private final int VARROCK_VABIT = 2315;
    private final int SEDRIDOR_VARBIT = 2314;
    private final int CROMPERTY_VARBIT = 2316;

    public EnterTheAbyss() {
        this.acceptCondition = () -> !MiniQuest.ENTER_THE_ABYSS.isFinished();
        addChildren(
                new TalkToFractal(() -> !MiniQuest.ENTER_THE_ABYSS.isStarted(), WILD_ZAMORAK_MAGE, () -> NPCs.closest("Mage of Zamorak"))
                        .setDialogueOptions("runes from", "Yes", "alright")
                        .setSimpleName("Start @ Mage of Zamorak"),
                new TalkToFractal(() -> MiniQuest.ENTER_THE_ABYSS.getConfigValue() == 1, VARROCK_ZAM_MAGE, () -> NPCs.closest("Mage of Zamorak"))
                        .setDialogueOptions("runes from", "worth your while", "still help you", "Yes", "alright", "Deal.")
                        .setSimpleName("Talk to mage in Varrock")
                        .setInventoryLoadout(
                                new InventoryLoadout()
                                        .addItem(ItemVariants.COMBAT_BRACLET)
                                        .addItem(ItemVariants.AMULET_OF_GLORY)
                                        .addItem(ItemID.VARROCK_TELEPORT, 1, 5)
                                        .setStrict(true)
                        ),
                new TalkToFractal(() -> PlayerSettings.getBitValue(VARROCK_VABIT) != 1, AUBURY, () -> NPCs.closest("Aubury"))
                        .setInteraction("Teleport")
                        .setSimpleName("Teleport w/ Aubury"),
                new TalkToFractal(() -> PlayerSettings.getBitValue(SEDRIDOR_VARBIT) != 1, SEDRIDOR_ROOM, () -> NPCs.closest("Archmage Sedridor"))
                        .setInteraction("Teleport")
                        .setSimpleName("Teleport w/ Sedridor"),
                new TalkToFractal(() -> PlayerSettings.getBitValue(CROMPERTY_VARBIT) != 1, CROMPERTY, () -> NPCs.closest("Wizard Cromperty"))
                        .setInteraction("Teleport")
                        .setSimpleName("Teleport w/ Cromperty"),
                new TalkToFractal(() -> true, VARROCK_ZAM_MAGE, () -> NPCs.closest("Mage of Zamorak"))
                        .setDialogueOptions("runes from", "worth your while", "still help you", "Yes", "alright", "Deal.")
                        .setSimpleName("Finish w/ Mage in Varrock")
        );

        this.paintArraySupplier = () -> new String[]{
                "Enter the abyss state: " + MiniQuest.ENTER_THE_ABYSS.getConfigValue()
        };
    }
}
