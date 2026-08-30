package org.dreambot.behaviour.method.emirs;

import org.dreambot.api.Client;
import org.dreambot.api.methods.clan.chat.ClanChat;
import org.dreambot.api.methods.clan.chat.ClanChatTab;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class FindMatch extends Fractal implements ChatListener {
    public FindMatch() {
        Client.getInstance().addEventListener(this);
    }

    @Override
    public int onLoop() {
        if (!Tabs.isOpen(Tab.CLAN)) {
            log("Open friends tab");
            Tabs.open(Tab.CLAN);
            return ReactionGenerator.getNormal();
        }

        if (!isOnGroupingTab()) {
            log("Open grouping tab");
            ClanChat.openChatTab(ClanChatTab.GROUPING);
            return ReactionGenerator.getNormal();
        }

        if (PlayerSettings.getBitValue(13143) == 0) {
            log("Open pvp arena menu");
            WidgetChild wc = Widgets.get(76, 4);
            if (wc != null && wc.interact()) {
                Sleep.sleepUntil(() -> PlayerSettings.getBitValue(13143) == 1, 4000);
            }
            return ReactionGenerator.getNormal();
        }

        WidgetChild leaveButton = Widgets.get(762, 13);
        if (leaveButton != null && leaveButton.hasAction("Leave")) {
            log("Waiting for start fight button");
            if (forceLeave) {
                leaveButton.interact();
                forceLeave = false;
                return ReactionGenerator.getNormal();
            }

            WidgetChild joinFightButton = Widgets.get(762, 9);
            if (joinFightButton != null && joinFightButton.isVisible()
                    && joinFightButton.hasAction("Join fight")) {
                log("Joining fight");
                joinFightButton.interact();
                Sleep.sleepUntil(() -> Players.getLocal().getZ() == 1, 5500);
            }
            return ReactionGenerator.getNormal();
        }

        WidgetChild signUpButton = Widgets.get(762, 12);
        if (signUpButton != null && signUpButton.hasAction("Sign up")) {
            log("Sign up");
            signUpButton.interact("Sign up");
            return ReactionGenerator.getNormal() + 2000;
        }

        if (Dialogues.inDialogue()) {
            log("Join duels group");
            Dialog.solve("Duels group");
            return ReactionGenerator.getNormal() + 2000;
        }

        WidgetChild findButton = Widgets.get(762, 13);
        if (findButton != null && findButton.isVisible()) {
            log("Finding a team");
            findButton.interact("Find");
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }

    private boolean isOnGroupingTab() {
        return PlayerSettings.getBitValue(13071) == 3;
    }

    private boolean isMatchMenuOpen() {
        return PlayerSettings.getBitValue(13143) == 1;
    }

    boolean forceLeave;

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().contains("do not currently appear to have a fight")) forceLeave = true;
    }
}
