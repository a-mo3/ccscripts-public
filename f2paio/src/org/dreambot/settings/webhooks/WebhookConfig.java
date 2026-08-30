package org.dreambot.settings.webhooks;

import lombok.Setter;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.utilities.Logger;
import org.dreambot.discordwebhook.WebHookUtil;
import org.dreambot.discordwebhook.pojo.EmbedPojo;
import org.dreambot.discordwebhook.pojo.WebHookPojo;

import java.io.IOException;

public class WebhookConfig {
    @Setter
    private static WebhookSettings settings;

    public static void sendLevelUpWebhook(Skill skill, int level) {
        if (settings == null || settings.levelUpWebhook.isEmpty()) {
            Logger.info("You can set a discord webhook url in /scripts/cCAIO/webhooks.json to be updated on level up");
            return;
        }

        try {
            WebHookUtil.execute(settings.getLevelUpWebhook(),
                    new WebHookPojo()
                            .setEmbeds(
                                    new EmbedPojo()
                                            .setTitle("Level up - " + Players.getLocal().getName()) // this webhook can only be sent to a user set endpoint
                                            .setDescription(String.format("Level up %s - %d -> %d", skill.name(), level, level + 1))
                            )
            );
        } catch (IOException e) {
            Logger.warn("Failed to send webhook " + e);
        }
    }
}
