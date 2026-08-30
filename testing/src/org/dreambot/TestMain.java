package org.dreambot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dreambot.api.Client;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.emirs.DoFight;
import org.dreambot.emirs.FindMatch;
import org.dreambot.emirs.Tutorial;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;

@ScriptManifest(category = Category.MISC, name = "testing", author = "", version = 0.0)
public class TestMain extends AbstractScript implements SpawnListener, ItemContainerListener, PaintInfo {
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    Fractal root = new Fractal().setSimpleName("Raymund mefod");
    Timer runtime = new Timer();


    @Override
    public void onStart() {
        root.addChildren(
                new Tutorial().setSimpleName("PVP Tutorial"),
                new DoFight().setSimpleName("ready & fight"),
                new FindMatch().setSimpleName("Find Match")
        );
    }

    @Override
    public int onLoop() {
        if (Client.isMembers() && Worlds.getCurrentWorld() != 578) {
            WorldHopper.hopWorld(578);
        }

        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 25) {
            Walking.toggleRun();
        }
        return root.run();
    }

    @Override
    public void onPaint(Graphics graphics) {
        scriptPaint.paint(graphics);
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("Incoming " + gson.toJson(incoming));
        Logger.info("existing " + gson.toJson(existing));
    }

    // todo cache on first time logged in
    int startingPoints = getPoints();
    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "Oak logs " + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy),
                "Is Instance " + Client.isDynamicRegion(),
                String.format("Points %d %d/hr", getPoints(), runtime.getHourlyRate(getPoints() - startingPoints)),
                String.format("gp value %s %s/hr", df.format(getPoints() * 750L), df.format(runtime.getHourlyRate((getPoints() - startingPoints) * 750)))
        };
    }

    private int getPoints() {
        return PlayerSettings.getBitValue(13991);
    }
}
