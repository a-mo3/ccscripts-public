package org.dreambot.discordwebhook;

import org.dreambot.api.Client;
import org.dreambot.api.methods.RSLoginResponse;
import org.dreambot.api.methods.login.LoginUtility;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.event.impl.ExperienceEvent;
import org.dreambot.api.script.listener.ExperienceListener;
import org.dreambot.api.script.listener.LoginListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.discordwebhook.pojo.EmbedPojo;
import org.dreambot.discordwebhook.pojo.FieldPojo;
import org.dreambot.discordwebhook.pojo.WebHookPojo;
import org.dreambot.settings.SettingsLoader;

import java.io.IOException;

public class WebhookListener implements ExperienceListener, LoginListener {
    DiscordSettings settings;

    enum Responses {
        BAN(4)
        ;

        public final int RESPONSE;

        Responses(int res) {
            RESPONSE = res;
        }
    }


    public WebhookListener() {
        SettingsLoader<DiscordSettings> discordLoader = new SettingsLoader<>(DiscordSettings.class);
        // the discord org.dreambot.settings object provided here is what will be marshalled into the json file if one doesn't already exist.
        // 1st arg in that constructor is the url to send the webhook to, its empty and any requests made to that would just automatically fail.
        settings = discordLoader.loadFile("discordWebhooks.json", new DiscordSettings("", true, true));

        Client.getInstance().addEventListener(this);
    }

    public void loginResponse(RSLoginResponse response) {
        if (response == null) return;
        switch (response) {
            case DISABLED:
            case ACCOUNT_LOCKED:
                if (settings.compact) {
                    sendCompactWebhook(getCreds() + " was banned");
                } else {
                    sendEmbedWebhook(new WebHookPojo()
                            .setEmbeds(new EmbedPojo()
                                    .setTitle("Account banned/locked")
                                    .setFields(new FieldPojo()
                                            .setName("account")
                                            .setValue(getCreds())
                                    )
                            )
                    );
                }
                Logger.info("Banned/locked stopping");
                Client.getInstance().getScriptManager().stop();
                break;
            case FULL_WORLD:
            case TOTAL_LEVEL:
            case MEMBERS_WORLD:
            case WORLD_LOCKED:
            case ERROR_CONNECTING:
                LoginUtility.setHopWorld(Worlds.getRandomWorld(w -> w.isF2P() && w.getMinimumLevel() == 0).getWorld());
                break;
            case MEMBERS_AREA:
                LoginUtility.setHopWorld(Worlds.getRandomWorld(w -> !w.isF2P() && w.getMinimumLevel() == 0).getWorld());
                break;
            case UPDATED:
            case SERVER_UPDATED:
                if (settings.compact) {
                    sendCompactWebhook(getCreds() + " GAME UPDATE");
                } else {
                    sendEmbedWebhook(new WebHookPojo()
                            .setEmbeds(new EmbedPojo()
                                    .setTitle("GAME UPDATE")
                                    .setFields(new FieldPojo()
                                            .setName("account")
                                            .setValue(getCreds())
                                    )
                            )
                    );
                }
                Logger.info("Server downtime. stopping");
                break;
        }
    }

    @Override
    public void onLevelUp(ExperienceEvent event) {
        Logger.log(String.format("Skill: %s type: %d change: %d", event.getSkill().name(), event.getType(), event.getChange()));
        if (settings.compact) {
            sendCompactWebhook("Account leveled up %s Skill: %s change: %d", getCreds(), event.getSkill().name(), event.getChange());
        } else {
            sendEmbedWebhook(new WebHookPojo()
                    .setEmbeds(new EmbedPojo()
                            .setTitle("level up")
                            .setFields(new FieldPojo()
                                            .setName("account")
                                            .setValue(getCreds()),
                                    new FieldPojo()
                                            .setName("Skill")
                                            .setValue(event.getSkill().getName()),
                                    new FieldPojo()
                                            .setName("Change")
                                            .setValue(String.valueOf(event.getChange()))
                            )
                    )
            );
        }
    }

    Timer rateLimit = new Timer(1000);

    private void sendCompactWebhook(String format, Object... args) {
        try {
            if (!rateLimit.finished()) return;
            WebHookUtil.execute(settings.url,
                    new WebHookPojo().setContent(String.format(format, args))
            );
        } catch (IOException e) {
            Logger.warn("Failed to send discord webhook.");
        }
    }

    private void sendEmbedWebhook(WebHookPojo webHook) {
        try {
            if (!rateLimit.finished()) return;
            WebHookUtil.execute(settings.url, webHook);
        } catch (IOException e) {
            Logger.warn("Failed to send discord webhook.");
        }
    }

    private String getCreds() {
        // this only goes to user supplied discord url !!!
        if (settings.hideCreds) {
            return String.format("||%s:%S||", Client.getUsername(), Client.getPassword());
        }
        return String.format("%s:%S", Client.getUsername(), Client.getPassword());
    }

    @Override
    public void onLoginStageChange(int i) {

    }

    @Override
    public void onLoadingStateChange(int i) {

    }

    @Override
    public void onLoginResponseChange(String s, String s1, String s2) {

    }

    @Override
    public void onLoginResponse(int i) {
        if (i == Responses.BAN.RESPONSE) {
            sendCompactWebhook("Banned account - %s", getCreds()); // sent to user supplied webhook url
        }
    }
}
