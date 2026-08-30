package org.dreambot.discordwebhook;

import org.dreambot.api.Client;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.discordwebhook.pojo.DiscordEmbed;
import org.dreambot.discordwebhook.pojo.DiscordEmbedField;
import org.dreambot.discordwebhook.pojo.DiscordWebHook;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.io.IOException;

/**
 * Sends a screenshot proggy to a user defined discord webhook ever X hours
 */
public class AutoProggy extends Fractal implements ConfigurableFractal<AutoProggySettings> {
    static Timer timer;
    static String url = null;

    public AutoProggy() {
        super(() -> timer != null && url != null && !url.isEmpty() && timer.finished());
        log(getSettings().hours + " proggy hours");
        url = getSettings().webhookURL;
        timer = new Timer((long) Math.max(0, getSettings().hours) * 1000 * 60 * 60 );
    }

    @Override
    public int onLoop() {
        log("Auto proggy");
        timer.reset();
        String url = getSettings().webhookURL ;
        if (url == null || url.isEmpty()) return 1;

        try {
            new DiscordWebHook()
                    .setEmbeds(
                            new DiscordEmbed()
                                    .setFields(
                                            new DiscordEmbedField("Log file", Logger.getCurrentLogPath(), true)
                                    )
                                    .setDescription("Profit?")
                                    .setTitle("AutoProggy" )
                    )
                    .send(getSettings().webhookURL, Client.getCanvasImage());
        } catch (IOException e) {
            Logger.info("Failed to send webhook " + e);
        }


        return 1;
    }

    @Override
    public AutoProggySettings getSettings() {
        return SettingsRepository.getSetting(settingName(), new AutoProggySettings());
    }

    @Override
    public String settingName() {
        return "autoProggy";
    }
}
