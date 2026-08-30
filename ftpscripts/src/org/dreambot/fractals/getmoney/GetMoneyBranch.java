package org.dreambot.fractals.getmoney;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.OwnedItems;

import java.util.function.Supplier;

/**
 * Optionally instead of muling we can set a target on getMoneyBranch & use this for making money!
 * the methods in this branch are f2p method, with little reqs, that are not scalable enough to be standalone methods
 * their products must be sellable by trade restricted accounts, eg no oak logs
 */
public class GetMoneyBranch extends Fractal {
    public static int gpTarget = -1;

    public GetMoneyBranch(Supplier<Boolean> acceptCond) {
        super(() -> {
            if (OwnedItems.count(ItemID.COINS_995) >= GetMoneyBranch.gpTarget) GetMoneyBranch.gpTarget = -1;
            return acceptCond.get() && gpTarget > -1;
        });
        setSimpleName("Get Money");
        addChildren(
                new PickPotatoes().setSimpleName("taters")
        );
    }
}
