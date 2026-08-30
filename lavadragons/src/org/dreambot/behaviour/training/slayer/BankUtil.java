package org.dreambot.behaviour.training.slayer;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.utilities.Logger;

import java.util.HashSet;
import java.util.Set;

public class BankUtil {
    static Set<BankLocation> blacklist = new HashSet<>();

    {
        // black list of dungeons that close enough to be the Euclidean closest but are actually super far away
        blacklist.add(BankLocation.BLAST_FURNACE); // vault of war
        blacklist.add(BankLocation.WOODCUTTING_GUILD_DUNGEON); // kourend dags
    }

    public static boolean openClosest() {
        BankLocation loc = Bank.getClosestBankLocation(true);
        if (blacklist.contains(loc)) loc = BankLocation.GRAND_EXCHANGE;
        if (loc == null) {
            Logger.warn("No closest bank");
            loc = BankLocation.GRAND_EXCHANGE;
        }
        return Bank.open(loc);
    }

}
