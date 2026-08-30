package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.event.impl.ExperienceEvent;
import org.dreambot.api.script.listener.ExperienceListener;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettings;
import org.dreambot.settings.webhooks.WebhookConfig;
import org.dreambot.settings.webhooks.WebhookSettings;
import org.dreambot.ui.AIOMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;

@ScriptManifest(category = Category.MISC, name = "cCAIO", author = "camalCase", version = 0.0)
public class AIOMain extends AbstractScript implements PaintInfo, HumanMouseListener, ExperienceListener {
    Timer t = new Timer();
    public static Fractal cCAIO = new Fractal().setSimpleName("cCAIO");
    private final FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_RIGHT_PLAY_SCREEN, this);

    @Override
    public void onStart() {
        loadReactionTimes();
        loadWebhookConfig();

        SwingUtilities.invokeLater(AIOMenu::getInstance);
        super.onStart();
    }


    private void loadReactionTimes() {
        SettingsLoader<ReactionSettings> reactionTimes = new SettingsLoader<>(ReactionSettings.class);
        ReactionGenerator.setReactionSettings(reactionTimes.loadFile("reactionTime.json", new ReactionSettings()));
    }


    private void loadWebhookConfig() {
        SettingsLoader<WebhookSettings> webhookLoader = new SettingsLoader<>(WebhookSettings.class);
        WebhookConfig.setSettings(webhookLoader.loadFile("webhooks.json", new WebhookSettings()));
    }

    @Override
    public int onLoop() {
        return cCAIO.run();
    }

    public static void addChildren(Fractal... child) {
        cCAIO.addChildren(child);
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
    }

    @Override
    public void onPaint(Graphics graphics) {
        Alerts.renderList(graphics);
        scriptPaint.paint(graphics);
        // todo fractal paint
    }

    @Override
    public void onLevelUp(ExperienceEvent event) {
        Logger.log(String.format("Skill: %s type: %d change: %d", event.getSkill().name(), event.getType(), event.getChange()));
        WebhookConfig.sendLevelUpWebhook(event.getSkill(), Skills.getRealLevel(event.getSkill()));
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "cCAIO - BETA 0.0 - " + t.formatTime(),
                Arrays.toString(FractalAPI.hierarchy)

        };
    }
}
