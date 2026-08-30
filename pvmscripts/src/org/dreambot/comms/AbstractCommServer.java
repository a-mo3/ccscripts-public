package org.dreambot.comms;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public abstract class AbstractCommServer {

    public abstract void onOpen(WebSocket webSocket, ClientHandshake clientHandshake);
    public abstract void onMessage(WebSocket socket, String msg);
    public abstract void onClose(WebSocket webSocket, int i, String s, boolean b);
    public abstract void onError(WebSocket webSocket, Exception e);
}
