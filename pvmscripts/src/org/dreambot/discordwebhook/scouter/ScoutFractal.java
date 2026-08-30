package org.dreambot.discordwebhook.scouter;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.discordwebhook.pojo.DiscordEmbed;
import org.dreambot.discordwebhook.pojo.DiscordEmbedField;
import org.dreambot.discordwebhook.pojo.DiscordWebHook;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ScoutFractal extends Fractal implements ConfigurableFractal<ScoutSettings>, SpawnListener {
    /**
     * this is opt in, by default all the webhooks are null / empty, users have to enter their for this to subscribe
     */
    public ScoutFractal() {
        super(() -> false);
        ScoutSettings settings = getSettings();
        setSimpleName("Scouter");
        if (hookExists(settings.generalWebhook) || hookExists(settings.plusOneWebhook) || hookExists(settings.skulledWebhook)) {
            log("Register scouter");
            Client.getInstance().addEventListener(this);
        }
    }

    ExecutorService scoutExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onPlayerSpawn(Player entity) {
        if (!Combat.isInWild()) return;

        List<Item> equip = entity.getEquipment()
                .stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(Item::getLivePrice))
                .collect(Collectors.toList());

        int totalValue = 0;
        int risk = 0;
        for (int i = 0; i < equip.size(); i++) {
            if (i == 0) {
                totalValue += equip.get(i).getLivePrice();
                continue;
            }
            totalValue += equip.get(i).getLivePrice();
            risk += equip.get(i).getLivePrice();
        }

        ScoutSettings settings = getSettings();
        String script = Client.getInstance().getScriptManager().getCurrentScript().getSDNName();
        log("Player spawned ");
        int world = Worlds.getCurrentWorld();
        if (hookExists(settings.generalWebhook)) {
            log(entity.getName() + " General");
            sendWebhook(settings.screenshotOnGeneral, settings.generalWebhook, entity, totalValue, risk, world, script);
        }

        if (hookExists(settings.skulledWebhook) && entity.isSkulled()) {
            log(entity.getName() + " Skulled");
            sendWebhook(settings.screenshotOnSkulled, settings.skulledWebhook, entity, totalValue, risk, world, script);
        }

        if (hookExists(settings.plusOneWebhook) && totalValue >= settings.plusOneValue) {
            log(entity.getName() + " plus one");
            sendWebhook(settings.screenshotOnPlusOne, settings.plusOneWebhook, entity, totalValue, risk, world, script);
        }
    }

    private void sendWebhook(boolean incluedImage, String webhookUrl, Player player, int value, int risk, int world, String script) {
        scoutExecutor.execute(() -> {
            try {
                new DiscordWebHook()
                        .setEmbeds(
                                new DiscordEmbed()
                                        .setDescription(
                                                new StringBuilder()
                                                        .append("Lvl " + player.getLevel())
                                                        .append(" | Name " + player.getName())
                                                        .append(" | " + (player.isSkulled() ? "Skulled" : "Unskulled"))
                                                        .append(" | Value " + numberFormat(value))
                                                        .append(" | Risk " + numberFormat(risk))
                                                        .append(" | World " + world)
                                                        .append(" | Tile " + player.getX() + " " + player.getY() + " " + player.getZ())
                                                        .toString()
                                        )
                                        .setTitle(script)
                        )
                        .send(webhookUrl, incluedImage ? Client.getCanvasImage() : null);
            } catch (IOException e) {
                Logger.info("Failed to send webhook " + e);
            }
        });
    }


    private String numberFormat(int num) {
        if (num >= 1_000_000) return String.format("%.2f", (float) num / 1_000_000) + "M";
        if (num >= 1_000) return String.format("%.2f", (float) num / 1_000) + "K";
        return num + "";
    }

    private boolean hookExists(String hook) {
        return hook != null && !hook.replace(" ", "").isEmpty();
    }

    @Override
    public int onLoop() {
        log("Shouldn't be seeing this");
        return ReactionGenerator.getNormal();
    }

    @Override
    public ScoutSettings getSettings() {
        return SettingsRepository.getSetting("scout", new ScoutSettings());
    }

    @Override
    public String settingName() {
        return "scout";
    }
}
