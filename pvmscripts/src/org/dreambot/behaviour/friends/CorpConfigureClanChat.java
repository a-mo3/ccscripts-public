package org.dreambot.behaviour.friends;

import lombok.Setter;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.clan.chat.ClanChat;
import org.dreambot.api.methods.clan.chat.ClanChatTab;
import org.dreambot.api.methods.clan.guild.Clan;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.method.corp.CorpClient;
import org.dreambot.behaviour.method.corp.messages.CorpRole;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * Host a clan chat friends only
 */
@Setter
public class CorpConfigureClanChat extends Fractal {
    public CorpConfigureClanChat(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Start clan chat");
    }

    // there is seemingly no varbit or varcstr i could find for the channel name
    // so we will check widgets and time out the state after a while
    // ID: 94, 10

    boolean setChatPublic;
    Timer chatNameCacheReset = new Timer(60 * 2 * 1000);

    @Override
    public int onLoop() {
        String host = CorpClient.getLeader();
        if (host == null) {
            log("Host is null");
            return ReactionGenerator.getNormal();
        }
        log("Configure cc " + CorpClient.getRole());
        log(Players.getLocal().getName().equalsIgnoreCase(ClanChat.getOwner()) + " " + ClanChat.inChat());

        if (chatNameCacheReset.finished()) {
            setChatPublic = false;
        }

        String ownerName = ClanChat.getName();
        // if owner name is null but we are in a chat that should just be owners offline, and therefore not us, so we leave
        if (CorpClient.getRole() == CorpRole.HOST && !Players.getLocal().getName().equals(ownerName) && Clan.inChat()) {
            if (Dialogues.canEnterInput()) {
                Keyboard.type(Players.getLocal().getName(), true);
                return ReactionGenerator.getLong() + 5000;
            }
            log("We are not the leader, leave clan chat");
            openTabAndJoinOrLeave();
            return ReactionGenerator.getNormal();
        }

        if (CorpClient.getRole() != CorpRole.HOST && ClanChat.getOwner() != null && !ClanChat.getOwner().equalsIgnoreCase(CorpClient.getLeader()) ) {
            log("We are not host but have our own gc");
            openTabAndJoinOrLeave();
            return ReactionGenerator.getNormal();
        }

        if (!ClanChat.inChat()) {
            log("Not in chat");
            // if we havent checked our set chat name we now need to
            if (!setChatPublic) {
                // open tab so we can click setup button
                if (openTab()) return ReactionGenerator.getNormal();
                // if chat channel is open we to check 94, 10 text for not being disabled, anything other than disabled is OKAY
                // before we do this, who can enter is  anything other than "Any friends", we need to change that
                // any friends is safe because in PVMMain L 267 we set private to friends &
                // in configure friends list we unfriend anyone no assigned by the corp co ord server
                // what am i a some kind of blogger?
                // 94,13 is who can enter chat and the action is "Any friends"
                // this should be default anyway
                WidgetChild whoCanEnter = Widgets.get(94, 13);
                if (whoCanEnter != null && !"Any friends".equals(whoCanEnter.getText())) {
                    log("Set who can enter to any friends");
                    whoCanEnter.interact("Any friends");
                    return ReactionGenerator.getNormal();
                }

                if (Dialogues.canEnterInput()) {
                    Keyboard.type("butt", true);
                    setChatPublic = true;
                    chatNameCacheReset.reset();
                    return ReactionGenerator.getNormal() + 5_000;
                }

                // ID: 94, 10
                WidgetChild channelNameButton = Widgets.get(94, 10);
                if (channelNameButton != null) {
                    log("Set channel name");
                    channelNameButton.interact();
                    return ReactionGenerator.getNormal();
                }

                // setup button, 7, 20
                WidgetChild setupButton = Widgets.get(7, 20);
                if (setupButton != null) {
                    log("Setup button interact");
                    setupButton.interact();
                }

                return ReactionGenerator.getNormal();
            }

            log("Not in chat, join leader or create one if we are the host");
            boolean areWeHosting = host.equals(Players.getLocal().getName());
            log("Are we host?: " + areWeHosting);
            if (openTabAndJoinOrLeave()) return ReactionGenerator.getNormal();
            // type owner username to join chat channel
            if (Dialogues.canEnterInput()) {
                log("Enter own chat");
                Keyboard.type(host, true);
                return ReactionGenerator.getNormal() + 5000;
            }
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }

    private boolean openTab() {
        if (!Tabs.open(Tab.CLAN)) {
            log("Open clan tab");
            return true;
        }

        if (!ClanChat.openChatTab(ClanChatTab.CHAT_CHANNEL)) {
            log("Open chat channel tab");
            return true;
        }
        return false;
    }

    private boolean openTabAndJoinOrLeave() {
        if (openTab()) return true;
        // press leave button, 7, 18, cant be join or else leader name would be null
        WidgetChild leaveOrJoinButton = Widgets.get(7, 18);
        if (leaveOrJoinButton != null) {
            log("Leave button interact");
            leaveOrJoinButton.interact();
        }
        return false;
    }
}
