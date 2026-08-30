package org.dreambot.behaviour.method.clues;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.method.clues.data.*;
import org.dreambot.behaviour.training.slayer.Text;
import org.dreambot.fractals.Fractal;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

public class CheckClueScroll extends Fractal implements ItemContainerListener {
    //    public static ClueScrollBranch.ClueScrollType currentScrollType;
//    public static EmoteClue emoteClue;
//    public static CoordinateClue cordClue;
//    public static AnagramClue anagramClue;
//    public static CrypticClue crypticClue;
    public static ClueScroll lastClue = null;

    public CheckClueScroll() {
        setAcceptCondition(() -> lastClue == null);
        Client.getInstance().addEventListener(this);
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        log("Inv change");
        log(incoming.toString());
        log(existing.toString());
        if (existing.getName().equals("Clue scroll (medium)")) {
            log("Medium clue changed");
            log(incoming.toString());
            log(existing.toString());
            lastClue = null;

        }
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        log("Inv swap");
        log(incoming.toString());
        log(outgoing.toString());
        if (outgoing.getName().equals("Clue scroll (medium)")) {
            log("Medium clue changed");
            log(incoming.toString());
            log(outgoing.toString());
            lastClue = null;

        }
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        if (item.getName().equals("Clue scroll (medium)")) {
            log("Medium clue added");
            log(item.toString());
            lastClue = null;
        }
    }

    @Override
    public void onInventoryItemRemoved(Item item) {
        if (item.getName().equals("Clue scroll (medium)")) {
            log("Medium clue added");
            log(item.toString());
            lastClue = null;
        }
    }

    // assumed we own a clue scroll, medium clue scroll
    @Override
    public int onLoop() {
        WidgetChild clue = Widgets.get(203, 2);
        if (clue == null) {
            log("Needs to open clue scroll");
            // open clue scroll.
            if (Inventory.contains(x -> x.getName().contains("medium"))) {
                if (Bank.isOpen() || GrandExchange.isOpen()) {
                    log("Close bank");
                    Widgets.closeAll();
                    return ReactionGenerator.getNormal();
                }

                log("Interact with med clue");
                Inventory.interact(x -> x.getName().contains("medium"));
                return ReactionGenerator.getNormal();
            }

            // get clue from bank
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) {
                    log("Bank open");
                    Bank.open();
                }
                return ReactionGenerator.getNormal();
            }

            // todo consider other scrolls here
            log("Withdraw clue");
            Bank.withdraw("Clue scroll (medium)");

            return ReactionGenerator.getNormal();
        }

        String clueText = clue.getText();
        log(clueText);
        String cleaned = Text.sanitizeMultilineText(clueText).toLowerCase();
        log("Cleaned: " + cleaned);

        AnagramClue anagramClue = AnagramClue.forText(cleaned);
        log("Anagram " + anagramClue);
        if (anagramClue != null) {
            log("Setting anagram state");
//            currentScrollType = ClueScrollBranch.ClueScrollType.ANAGRAM;
//            CheckClueScroll.anagramClue = anagramClue;
            lastClue = anagramClue;
            return ReactionGenerator.getNormal();
        }

        CrypticClue crypticClue = CrypticClue.forText(cleaned);
        if (crypticClue != null) {
            log("Cryptic clue");
//            currentScrollType = ClueScrollBranch.ClueScrollType.CRYPTIC;
//            CheckClueScroll.crypticClue = crypticClue;
            lastClue = crypticClue;
            return ReactionGenerator.getNormal();
        }

        if (cleaned.startsWith("i'd like to hear some music.")) {
            lastClue = MusicClue.forText(clueText);
            log("Music clue " + lastClue);
            return ReactionGenerator.getNormal();
        }

        // check for emote
        EmoteClue emoteClue = EmoteClue.forText(cleaned);
        log("Emote Clue " + emoteClue);
        if (emoteClue != null) {
            log("Setting emote state");
//            currentScrollType = ClueScrollBranch.ClueScrollType.EMOTE;
//            CheckClueScroll.emoteClue = emoteClue;
            lastClue = emoteClue;
            return ReactionGenerator.getNormal();
        }

        CoordinateClue cordClue = coordinatesToWorldPoint(cleaned);
        if (cordClue != null) {
            log("cord clue " + cordClue);
//            currentScrollType = ClueScrollBranch.ClueScrollType.COORDINATE;
//            CheckClueScroll.cordClue = cordClue;
            lastClue = cordClue;
            return ReactionGenerator.getNormal();
        }


        return ReactionGenerator.getNormal();
    }

    private CoordinateClue coordinatesToWorldPoint(String text) {
        String[] splitText = text.split(" ");

        if (splitText.length != 10) {
            Log.info("Splitting \"" + text + "\" did not result in an array of 10 cells");
            return null;
        }

        if (!splitText[1].startsWith("degree") || !splitText[3].startsWith("minute")) {
            Log.info("\"" + text + "\" is not a well formed coordinate string");
            return null;
        }

        int degY = Integer.parseInt(splitText[0]);
        int minY = Integer.parseInt(splitText[2]);

        if (splitText[4].equals("south")) {
            degY *= -1;
            minY *= -1;
        }

        int degX = Integer.parseInt(splitText[5]);
        int minX = Integer.parseInt(splitText[7]);

        if (splitText[9].equals("west")) {
            degX *= -1;
            minX *= -1;
        }

        Tile coordinate = coordinatesToWorldPoint(degX, minX, degY, minY);
        return CoordinateClue.forLocation(coordinate);
    }

    private Tile coordinatesToWorldPoint(int degX, int minX, int degY, int minY) {
        // Center of the Observatory
        int x2 = 2440;
        int y2 = 3161;

        x2 += degX * 32 + Math.round(minX / 1.875);
        y2 += degY * 32 + Math.round(minY / 1.875);

        return new Tile(x2, y2, 0);
    }
}
