package org.dreambot.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.data.ItemID;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

public class MuleOff extends Fractal implements ChatListener {
    public static Timer timer;

    public MuleOff() {
        Client.getInstance().addEventListener(this);
    }

    public static final int[] itemsToMule = new int[]{
            ItemID.RAW_SHARK,
            ItemID.RAW_SHARK + 1,
            ItemID.RAW_SEA_TURTLE,
            ItemID.RAW_MANTA_RAY,
            ItemID.AMULET_OF_GLORY_UNCHARGED,
            ItemID.COMBAT_BRACELET
    };

    @Override
    public boolean isValid() {
        if (timer == null) timer = new Timer(ScriptSettings.getMuleOffTime());
        if (!OwnedItems.containsAll(
                ItemID.ANGLER_BOOTS,
                ItemID.ANGLER_HAT,
                ItemID.ANGLER_TOP,
                ItemID.ANGLER_WADERS)) {
            timer.reset();
            return false;
        }
        return timer.finished();
    }

    @Override
    public int onLoop() {
        NPC kylieMinnow = NPCs.closest("Kylie minnow");
        if (Inventory.count(ItemID.MINNOW) >= 40 && kylieMinnow != null) {
            if (Dialogues.canEnterInput()) {
                Keyboard.type("2m", true);
                return ReactionGenerator.getNormal();
            }
            if (Dialogues.inDialogue()) {
                Dialog.solve("Yes");
                return ReactionGenerator.getNormal();
            }

            kylieMinnow.interact("Talk-to");
            Sleep.sleepUntil(Dialogues::inDialogue, 4400);
            return ReactionGenerator.getNormal();
        }

        if (Minnows.MINNOWS_PLATFORM.contains(Players.getLocal())) {
            Magic.castSpell(Normal.HOME_TELEPORT);
            Sleep.sleepUntil(() -> !Minnows.MINNOWS_PLATFORM.contains(Players.getLocal()), 60_000);
            return ReactionGenerator.getNormal();
        }

        if (Bank.getLastBankHistoryCacheTime() < 1) {
            if (Bank.open()) Bank.close();
            return ReactionGenerator.getNormal();
        }

        if (OwnedItems.containsAnyUnworn(itemsToMule)) {
            new SellAllEvent(itemsToMule).execute();
            return ReactionGenerator.getNormal();
        }

        if (!Trade.isOpen() && OwnedItems.count(ItemID.COINS_995) <= ScriptSettings.getMuleRemainder()) {
            timer.reset();
            return ReactionGenerator.getNormal();
        }

        new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                .addOfferedItem(ItemID.COINS_995, OwnedItems.count(ItemID.COINS_995) - ScriptSettings.getMuleRemainder())
                .execute();
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        String str = message.getMessage();
//        Logger.info(message.getType() + " on msg " + str);
    }
}
