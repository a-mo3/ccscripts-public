package org.dreambot.behaviour.misc;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import javax.print.DocFlavor;

public class GetBankCache extends Fractal {
    public GetBankCache() {
        super(() -> !Bank.isCached());
        setSimpleName("Get bank cache");
    }

    @Override
    public int onLoop() {
        log("Get bank cache");
        if (Walking.shouldWalk()) Bank.open();
        return ReactionGenerator.getNormal();
    }
}
