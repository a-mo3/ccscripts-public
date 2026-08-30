package org.dreambot.comms.impl.mole;

import org.dreambot.comms.AbstractCommServer;
import org.dreambot.scriptdata.MoleSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public class MoleComms extends AbstractCommServer {
    private static MoleComms instance;

    private MoleComms() {
    }

    public static MoleComms getInstance() {
        if (instance== null)  {
            MoleSettings moleSettings =  SettingsRepository.findInstanceOf(new MoleSettings());
            instance = new MoleComms();
        }
        return instance;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onMessage(WebSocket socket, String msg) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }
}
