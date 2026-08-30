package org.dreambot;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import okhttp3.*;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.Varcs;
import org.dreambot.api.script.listener.ActionListener;
import org.dreambot.api.script.listener.MenuRowListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.MenuRow;
import org.dreambot.behaviour.quests.pip.PipNodes;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.scout.PlayerSighting;
import org.dreambot.scriptdata.MoonsOfPerilsSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.webnodes.GWDNodes;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class PvmTesting extends PseudoScript implements MenuRowListener, SpawnListener, ActionListener {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PvmTesting.class);
    FractalRoot<MoonsOfPerilsSettings> tree = new FractalRoot<>(new MoonsOfPerilsSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    Area area = new Area(3082, 3505, 3090, 3490);
    Timer runtime = new Timer();

    @Override
    public void init() {
        PipNodes.init();
        tree.setSimpleName("Testing");
        Client.getInstance().addEventListener(this);
        GWDNodes.init();
        tree.addChildren(
                new SlayerBranch(() -> true)
        );
//        new LoggingMouseAlgo(clickHistory);
    }

    final static int PLAY_TIME_VARCINT = 526;


    @SneakyThrows
    @Override
    public int onLoop() {
        Logger.info("Pvm onloop");
        return 5000;
    }

    @SneakyThrows
    @Override
    public void onMousePressed(MouseEvent e) {
        Logger.info("Mouse press");
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
    }

    @Override
    public void onScriptPaint(Graphics g) {
    }

    Tile[] cPath = new Tile[]{};

    private Stack<Tile> findAPath(Tile start, Tile dest, Set<Tile> obstacles) {
        Stack<Tile> path = new Stack<>();
        move(start, dest, obstacles, path);
        cPath = path.toArray(new Tile[]{});
        return path;
    }

    private void move(Tile start, Tile dest, Set<Tile> obstacles, Stack<Tile> path) {
        if (start == null) return;

        int dy = dest.getY() - start.getY();
        int dx = dest.getX() - start.getX();
        if (dy < 0) dy *= -1;
        if (dx < 0) dx *= -1;
        // 0 = diagonal, 1 = vertical, 2 = horizontal
        int moveMode = Integer.compare(dy, dx);
        Tile visit;
        int verticalMove = Integer.compare(dest.getY(), start.getY());
        int horizontalMove = Integer.compare(dest.getX(), start.getX());
        if (moveMode == 0) {
            visit = start.clone().translate(horizontalMove, verticalMove);
        } else if (moveMode == 1) {
            visit = start.clone().translate(0, verticalMove);
        } else {
            visit = start.clone().translate(horizontalMove, 0);
        }

        if (visit.equals(dest)) return;

        if (obstacles.contains(visit)) {
            // scan out, lets grab the 1 tile radius around the start and find a tile that isn't an obstacle
            // this is not robust at all but should be okay for zalcano portals, maybe.
            visit = Arrays.stream(start.getArea(1)
                            .getTiles())
                    .filter(tile -> !obstacles.contains(tile))
                    .min(Comparator.comparingDouble(x -> x.distance(dest)))
                    .orElse(null);
        }

        path.push(visit);
        move(visit, dest, obstacles, path);
    }

    private Stack<Tile> clickableTiles(Tile[] wholePath) {
        Stack<Tile> clickPoints = new Stack<>();
        Tile previous = null;
        boolean movingDiag = false;
        for (Tile t : wholePath) {
            if (previous == null) {
                previous = t;
                continue;
            }
            boolean md = t.getX() != previous.getX() && t.getY() != previous.getY();
            if (!md & movingDiag) {
                clickPoints.push(previous);
                previous = t;
                movingDiag = false;
                continue;
            }
            movingDiag = md;
            previous = t;
        }
        // always add the last tile in the path
        clickPoints.push(wholePath[wholePath.length - 1]);
        return clickPoints;
    }

    @Override
    public String[] getPaintInfo() {

        int playedMins = Varcs.getInt(PLAY_TIME_VARCINT);
        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Index: " + Combat.getCombatModeIndex(),
                "Play time " + playedMins,
                "General screen " + GrandExchange.isGeneralOpen()
        };
    }

    @Override
    public String getScriptName() {
        return "cCSailing";
    }

    @Override
    public int getMoneyMade() {
        return 100;
    }

    @Override
    public Timer getRuntime() {
        return runtime;
    }

    @Override
    public long getMuleOffTime() {
        return 12031203;
    }

    @Override
    public Fractal getFractal() {
        return tree;
    }

    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        DecimalFormat format = new DecimalFormat("00");
        return String.format("%s:%s:%s",
                format.format(hours),
                format.format(minutes),
                format.format(seconds));
    }

    HashSet<PlayerSighting> usernames = new HashSet<>();
    OkHttpClient okHttpClient = new OkHttpClient();
    Gson gson = new Gson();

    @SneakyThrows
    @Override
    public void onPlayerSpawn(Player entity) {
        PlayerSighting sighting = new PlayerSighting(entity);
        usernames.add(sighting);
        // send to local pano api
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        String body = gson.toJson(new PlayerSighting[]{sighting});
        Logger.info(body);
//        Request request = new Request.Builder()
//                .url("http://localhost:8080/api/player-sightings")
//                .post(RequestBody.create(mediaType, body))
//                .addHeader("Content-Type", "application/json")
//                .addHeader("User-Agent", "insomnia/11.1.0")
//                .build();

//        Response response = client.newCall(request).execute();

    }

    @SneakyThrows
    @Override
    public void onExit() {
        Files.write(Path.of(System.getProperty("scripts.path") + "/names.txt"),
                usernames.stream().map(PlayerSighting::toString).collect(Collectors.toList()),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    @Override
    public void onAction(MenuRow eventRow, int mouseX, int mouseY) {
        Logger.info("Menu row " + eventRow);

    }
}
