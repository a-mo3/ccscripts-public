package org.dreambot.comms;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.api.utilities.Logger;
import org.dreambot.comms.impl.agility.BoxingComms;
import org.dreambot.comms.impl.callisto.CallistoComms;
import org.dreambot.comms.impl.gwd.GodWarsComms;
import org.dreambot.comms.impl.mole.MoleComms;
import org.dreambot.comms.impl.venenatis.VenenatisComms;
import org.dreambot.comms.impl.vetion.VetionComms;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.awt.*;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * previously every script that uses comms (mule, huey, corp) have had their own
 * this has to use a different port for everything, its annoying to script, gross
 * <p>
 * everything from now on will use this, and a routingCode will forward it to the appropriate script logic
 * <p>
 * previously servers would have constructors for script related logic like team size, composition, etc
 * now servers should be singletons that grab the relevant settings object from SettingsRepository,
 * and assume thats going to be in memory, or default to something
 * <p>
 * only onMessage and onOpen will be able to have a route code
 * for onClose onError events we will broadcast to all the instantiated services
 * <p>
 * centralising everything here in future allows for maintaining logs and providing introspection w/ a local webapp
 */
public class EverythingCommServer extends WebSocketServer {
    public static final int PORT = 6335;
    Map<String, Supplier<AbstractCommServer>> routes = new HashMap<>();
    Set<AbstractCommServer> instantiatedRoutes = new HashSet<>();
    private static EverythingCommServer instance;
    private static AsyncBufferedLogger logger;

    public static EverythingCommServer getInstance() {
        if (instance == null) instance = new EverythingCommServer();
        return instance;
    }

    private EverythingCommServer() {
        super(new InetSocketAddress(PORT), 1, null);

        routes.put("mole", MoleComms::getInstance);
        routes.put("boxing", BoxingComms::getInstance);
        routes.put("vetion", VetionComms::getInstance);
        routes.put("callisto", CallistoComms::getInstance);
        routes.put("venenatis", VenenatisComms::getInstance);
        routes.put("gwd", GodWarsComms::getInstance);

        Logger.info("Starting everything comms");
        start();
    }


    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        log("On open " + clientHandshake.toString());
        String routeCode = clientHandshake.getFieldValue("routeCode");
        if (routeCode != null) {
            AbstractCommServer route = getServerFromRouteCode(routeCode);
            if (route == null) return;
            log("Instantiated route size " + instantiatedRoutes.size());

            log("Forward to route " + routeCode);
            route.onOpen(webSocket, clientHandshake);
        } else {
            log("No route code on handshake");
        }
    }

    private AbstractCommServer getServerFromRouteCode(String routeCode) {
        AbstractCommServer route = routes.getOrDefault(routeCode, () -> null).get();
        if (route == null) {
            log("No route found for code " + routeCode);
            return null;
        }

        // add so we can broadcast later
        instantiatedRoutes.add(route);
        return route;
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        log("Broadcasting close message to all routes: " + instantiatedRoutes + " " + s);
        instantiatedRoutes.forEach(x -> x.onClose(webSocket, i, s, b));
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        if (logger == null)
            logger = new AsyncBufferedLogger(System.getProperty("scripts.path") + "/cCCommunications/" + System.currentTimeMillis() + ".log");
        logger.log(s);
        JsonObject obj = JsonParser.parseString(s).getAsJsonObject();
        if (obj == null || !obj.has("routeCode")) {
            log("message was null or did not have route code " + obj);
            return;
        }

        String routeCode = obj.get("routeCode").getAsString();
        if (routeCode != null) {
            AbstractCommServer route = getServerFromRouteCode(routeCode);
            if (route == null) return;
            log("Instantiated route size " + instantiatedRoutes.size());

            log("Forward to route " + routeCode);
            route.onMessage(webSocket, s);
        } else {
            log("No route code on handshake");
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        if (e instanceof BindException) return;
        log("Broadcasting error message to all routes: " + instantiatedRoutes + " " + e);
        instantiatedRoutes.forEach(x -> x.onError(webSocket, e));
    }

    @Override
    public void onStart() {
        log("Router start");
    }

    public static void log(String log) {
        if (logger == null)
            logger = new AsyncBufferedLogger(System.getProperty("scripts.path") + "/cCCommunications/" + System.currentTimeMillis() + ".log");
        logger.log(("[Comms] - " + log));
        Logger.log(Color.PINK, ("[Comms] - " + log));
    }

    @Override
    public void start() {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> Logger.info("Uncaught exception (server already running probably, can ignore) " + e));
        super.start();
    }

    @Override
    public void run() {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> Logger.info("Uncaught exception (server already running probably, can ignore) " + e));
        super.run();
    }

    @Override
    public void stop() throws InterruptedException {
        if (logger != null) logger.shutdown();
        super.stop();
    }
}
