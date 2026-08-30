package org.dreambot.behaviour.friends;

import org.dreambot.api.methods.clan.chat.ClanChat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class JoinClanChat extends Fractal {
    final String ccname;
    public JoinClanChat(Supplier<Boolean> acceptCondition, String ccname) {
        super(acceptCondition);
        setSimpleName("Join CC");
        this.ccname = ccname;
    }

    @Override
    public int onLoop() {
        if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
//        log("In cc " + ClanChat.getName() + " " + ccname);
        if (ccname.equalsIgnoreCase(ClanChat.getOwner())) {
            log("In the right cc");
            return ReactionGenerator.getNormal();
        }
        log("Joining cc");
        ClanChat.join(ccname);
        return ReactionGenerator.getNormal();
    }
}
