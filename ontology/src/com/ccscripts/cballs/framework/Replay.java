package com.ccscripts.cballs.framework;

import com.ccscripts.reproducer.AbstractActionReproducer;
import lombok.Getter;

import java.util.List;

public class Replay {
    @Getter
    private final List<AbstractActionReproducer> reproducers;
    @Getter
    final int continuityNumber;

    public Replay(List<AbstractActionReproducer> reproducers, int continuityNumber) {
        this.reproducers = reproducers;
        this.continuityNumber = continuityNumber;
    }


    public AbstractActionReproducer get(int index) {
        return reproducers.get(index);
    }
}
