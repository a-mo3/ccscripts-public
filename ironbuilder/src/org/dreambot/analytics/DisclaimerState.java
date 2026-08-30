package org.dreambot.analytics;

import java.io.*;

public final class DisclaimerState {
    private static final String FILE =
            System.getProperty("scripts.path") + "/dataDisclaimer";

    private DisclaimerState() {}

    public static void saveAccepted(boolean accepted) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(FILE))) {
            out.writeBoolean(accepted);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean loadAccepted() {
        File file = new File(FILE);
        if (!file.exists()) {
            return false;
        }

        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            return in.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}