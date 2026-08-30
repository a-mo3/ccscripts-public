package org.dreambot.muling.impl;

import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.utilities.Logger;
import org.dreambot.muling.messages.OwnedItem;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MuleState {
    @Setter
    private static boolean isMule;
    public static String MULE_SERVER_ADDRESS = "0.0.0.0";
    public static List<TimeoutMuleRequest> queuedRequest = new ArrayList<>();

    @Setter
    private static MuleConnection muleConnection = null;

    public static boolean isMuleConnectionNull() {
        return muleConnection == null;
    }

    public static void shutDown() {
        Logger.info("Closing websocket");
        if (muleConnection != null) muleConnection.close();
    }

    public static MuleConnection getMuleConnection() {
        if (muleConnection == null) {
            Logger.info("Making mule connection");
            if (Client.isLoggedIn()) {
                try {
                    muleConnection = new MuleConnection(new URI("ws://" + MULE_SERVER_ADDRESS), isMule);
                } catch (URISyntaxException e) {
                    Logger.info(e);
                    throw new RuntimeException(e);
                }
            }
        }

        if (!muleConnection.isHasOpened()) {
            return null;
        }

        return muleConnection;
    }

    public static boolean updateOwnedItems(List<OwnedItem> ownedItems) {
        MuleConnection mc = getMuleConnection();
        if (mc == null) {
            Logger.info("Could not get mule connection - probably logged off ?");
            return false;
        }

        if (!mc.isHasOpened()) {
            return false;
        }

        mc.updateOwnedItems(ownedItems);
        return true;
    }

    public static boolean tradeComplete(boolean success, String reason, String reqId) {
        MuleConnection mc = getMuleConnection();
        if (mc == null) {
            Logger.info("Could not get mule connection - probably logged off ?");
            return false;
        }

        if (!mc.isHasOpened() || !mc.isOpen()) {
            return false;
        }

        mc.sendComplete(success, reason, reqId);
        return true;
    }

    /**
     * @param message request message
     * @return returns mule request for that user
     */
    public static TimeoutMuleRequest findRequestForMessage(String message) {
        return queuedRequest.stream()
                .filter(x -> {
                    Logger.info(x.getRequestMessage().playerName);
                    return message.toLowerCase().contains(x.getRequestMessage().playerName.toLowerCase());
                })
                .findFirst()
                .orElse(null);
    }

    public static void clearOldReqs() {
        queuedRequest.removeIf(TimeoutMuleRequest::isExpired);
    }
}