package org.dreambot.muling;

import java.security.SecureRandom;

public class Random {
    private static final SecureRandom random = new SecureRandom();

    public static int asInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
