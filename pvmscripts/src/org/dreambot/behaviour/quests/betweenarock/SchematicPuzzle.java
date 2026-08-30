package org.dreambot.behaviour.quests.betweenarock;

import org.dreambot.api.ClientSettings;
import org.dreambot.api.data.ClientLayout;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scripts.Gargoyle;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.HashMap;
import java.util.function.Supplier;

public class SchematicPuzzle extends Fractal {
    public SchematicPuzzle(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SCHEMATIC_4578).enabledIfOwned()
                .addItem(ItemID.SCHEMATICS_4577).enabledIfOwned()
                .addItem(ItemID.SCHEMATICS).enabledIfOwned()
                .addItem(ItemID.SCHEMATIC).enabledIfOwned()
                .addItem(ItemID.DWARVEN_LORE).enabledIfOwned()
//                .setEnabledCondition(() -> OwnedItems.contains(ItemID.DWARVEN_LORE))
        ;
        setSimpleName("Schematic puzzle");


        pieces[0] = new HashMap<>();
        pieces[0].put(PIECE_ID, 11);
        pieces[0].put(SELECTED, 261);
        pieces[0].put(SELECT_BUTTON, 24);

        solvedPieces[0] = new HashMap<>();
        solvedPieces[0].put(HORIZONTAL, 240);
        solvedPieces[0].put(VERTICAL, 170);
        solvedPieces[0].put(ROTATE, 1856);

        pieces[1] = new HashMap<>();
        pieces[1].put(PIECE_ID, 6);
        pieces[1].put(SELECTED, 262);
        pieces[1].put(SELECT_BUTTON, 25);

        solvedPieces[1] = new HashMap<>();
        solvedPieces[1].put(HORIZONTAL, 235);
        solvedPieces[1].put(VERTICAL, 170);
        solvedPieces[1].put(ROTATE, 1860);

        pieces[2] = new HashMap<>();
        pieces[2].put(PIECE_ID, 8);
        pieces[2].put(SELECTED, 263);
        pieces[2].put(SELECT_BUTTON, 26);

        solvedPieces[2] = new HashMap<>();
        solvedPieces[2].put(HORIZONTAL, 235);
        solvedPieces[2].put(VERTICAL, 175);
        solvedPieces[2].put(ROTATE, 1864);

        highlightButtons.put(DOWN_BUTTON, 0);
        highlightButtons.put(LEFT_BUTTON, 0);
        highlightButtons.put(RIGHT_BUTTON, 0);
        highlightButtons.put(UP_BUTTON, 0);
        highlightButtons.put(ROTATE_BUTTON, 0);
    }

    public static final int SELECT_BUTTON_PARENT = 114;
    public static final int PIECE_PARENT = 113;

    private final int DOWN_BUTTON = 34;
    private final int LEFT_BUTTON = 33;
    private final int RIGHT_BUTTON = 32;
    private final int UP_BUTTON = 31;
    private final int ROTATE_BUTTON = 30;

    private final int HORIZONTAL = 0;
    private final int VERTICAL = 1;
    private final int FLIP = 2;
    private final int ROTATE = 3;
    private final int SELECTED = 4;
    private final int SELECT_BUTTON = 5;
    private final int PIECE_ID = 6;

    private HashMap<Integer, Integer> highlightButtons = new HashMap<>();

    // array of hashmaps containing widget and varbit information for pieces
    private final HashMap<Integer, Integer>[] pieces = new HashMap[3];

    // array of hashmaps containing the correct positions & rotations for the widgets to be in
    private final HashMap<Integer, Integer>[] solvedPieces = new HashMap[3];

    @Override
    public int onLoop() {
        if (Bank.isOpen() || GrandExchange.isOpen()) {
            Widgets.closeAll();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("Yes");
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.getClientLayout() != ClientLayout.FIXED_CLASSIC) {
            Logger.info("This puzzle requires fixed classic");
            if (Bank.isOpen()) Bank.close();
            ClientSettings.setClientLayout(ClientLayout.FIXED_CLASSIC);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.DWARVEN_LORE)) {
            // open book if it isnt already
            if (!Widgets.isOpen()) {
                Inventory.interact(ItemID.DWARVEN_LORE, "Read");
                Sleep.sleepUntil(Widgets::isOpen, 2400);
                return ReactionGenerator.getNormal();
            }

            Widgets.closeAll();
            Sleep.sleepUntil(Dialogues::inDialogue, 4400);
            return ReactionGenerator.getNormal();
        }

        // open the schematics puzzle
        if (Widgets.getWidget(113) == null) {
            Inventory.interact(ItemID.SCHEMATICS);
            Sleep.sleepUntil(Widgets::isOpen, 2400);
            return ReactionGenerator.getNormal();
        }

        // loop through the pieces, solve if unsolved,
        for (int i = 0; i < 3; i++) {
            // if piece is solved continue
            WidgetChild piece = Widgets.get(PIECE_PARENT, pieces[i].get(PIECE_ID));
            if (piece == null || !piece.isVisible()) {
                Logger.info("Piece is null or invis " + i);
                continue;
            }

            int verticalMargin = piece.getY() - solvedPieces[i].get(VERTICAL);
            int horizontalMargin = piece.getX() - solvedPieces[i].get(HORIZONTAL);
            Logger.info(String.format("Margins piece %d: y %d x %d", i, verticalMargin, horizontalMargin));
            boolean isRotated = piece.getSequenceId() == solvedPieces[i].get(ROTATE);
            Logger.info(String.format("is %d rotated: %b rotation: %d goal: %d", i, isRotated, piece.getSequenceId(), solvedPieces[i].get(ROTATE)));
            if (!isRotated) {
                Logger.info("Not rotated, selecting and rotating");
                if (!selectPiece(i)) return ReactionGenerator.getLong();

                clickButton(ROTATE_BUTTON);
                return ReactionGenerator.getLong();
            }

            // move to correct y
            // if y is < 4 move it down
            if (verticalMargin < 0) {
                if (!selectPiece(i)) return ReactionGenerator.getLong();
                Logger.info("Move down");
                clickButton(DOWN_BUTTON);
                return ReactionGenerator.getLong();
            }

            if (verticalMargin > 4) {
                if (!selectPiece(i)) return ReactionGenerator.getLong();
                Logger.info("Move up");
                clickButton(UP_BUTTON);
                return ReactionGenerator.getLong();
            }

            // move to correct x
            // if x is < 4 move it left
            if (horizontalMargin < 0) {
                if (!selectPiece(i)) return ReactionGenerator.getLong();
                Logger.info("Move right");
                clickButton(RIGHT_BUTTON);
                return ReactionGenerator.getLong();
            }

            if (horizontalMargin > 4) {
                if (!selectPiece(i)) return ReactionGenerator.getLong();
                Logger.info("Move left");
                clickButton(LEFT_BUTTON);
                return ReactionGenerator.getLong();
            }
        }

        return ReactionGenerator.getNormal();
    }

    /**
     * @param pieceId index in pieces hashmap
     * @return true if is selected
     */
    private boolean selectPiece(int pieceId) {
        // select button
        int isSelectedBit = PlayerSettings.getConfig(pieces[pieceId].get(SELECTED));
        Logger.info("Selected: " + pieceId + " " + isSelectedBit);
        boolean isSelected = isSelectedBit == 1;
        if (!isSelected) {
            Logger.info("Selecting " + pieceId);
            WidgetChild selectButton = Widgets.get(SELECT_BUTTON_PARENT, pieces[pieceId].get(SELECT_BUTTON));
            if (selectButton == null || !selectButton.isVisible()) {
                Logger.info("Null or invis select button " + pieceId);
                return false;
            }

            selectButton.interact();
            // maybe sleep until selected here
            return false;
        }

        // unselect other buttons, probably the above code should be deleted
        for (int i = 0; i < 3; i++) {
            if (i == pieceId) continue;
            isSelectedBit = PlayerSettings.getConfig(pieces[i].get(SELECTED));
            Logger.info("Deselecting " + i + " " + isSelectedBit);
            isSelected = isSelectedBit == 1;
            if (isSelected) {
                Logger.info("Deelecting " + i);
                WidgetChild selectButton = Widgets.get(SELECT_BUTTON_PARENT, pieces[i].get(SELECT_BUTTON));
                if (selectButton == null || !selectButton.isVisible()) {
                    Logger.info("Null or invis select button " + pieceId);
                    return false;
                }

                selectButton.interact();
                // maybe sleep until selected here
                return false;
            }
        }

        return true;
    }

    private void clickButton(int child) {
        WidgetChild button = Widgets.get(SELECT_BUTTON_PARENT, child);
        if (button == null || !button.isVisible()) {
            Logger.info("Couldnt move piece button null " + child);
            return;
        }

        button.interact();
    }

}
