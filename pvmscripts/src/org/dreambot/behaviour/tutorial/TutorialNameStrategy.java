package org.dreambot.behaviour.tutorial;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.dreambot.settings.fractalsettings.SettingsRepository;

import java.util.function.Supplier;

public enum TutorialNameStrategy {
    // eternal farms generator api, modified names from the hiscores
    // needs auth key now
//    ETERNAL_FARMS_API(),
    // faker js names literally.
    CCSCRIPTS_API(() -> {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://name./")
                .get()
                .addHeader("User-Agent", "insomnia/11.1.0")
                .build();

        String name = "";
        try (Response response = client.newCall(request).execute();) {
            return response.body().string();
        } catch (Exception e) {
            name = "Failed";
        }
        return name;
    }),
    // a user provided endpoint
    CUSTOM_API(() -> {
        String endpoint = SettingsRepository.getSetting("tutorialIsland", new TutorialBranchSettings()).customEndpoint;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(endpoint)
                .get()
                .addHeader("User-Agent", "insomnia/11.1.0")
                .build();

        String name = "";
        try (Response response = client.newCall(request).execute();) {
            return response.body().string();
        } catch (Exception e) {
            name = "Failed";
        }
        return name;
    }),
    // default runescape name generator or a random string of garbage
    RANDOM(null),

    ;

    public final Supplier<String> nameSupplier;

    TutorialNameStrategy(Supplier<String> nameSupplier) {
        this.nameSupplier = nameSupplier;
    }
}
