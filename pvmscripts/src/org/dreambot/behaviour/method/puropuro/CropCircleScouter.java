package org.dreambot.behaviour.method.puropuro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import okio.BufferedSink;
import org.dreambot.api.Client;
import org.dreambot.api.methods.ForumUser;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * class for interacting with puro. api, which keeps track of where the overworld portals are
 * this removes quest requirements, which removes most skill requirements, from puro puro, letting it be ran on a lvl 27 hunter account.
 */
public class CropCircleScouter {

    static Map<String, CropCirclePojo> data = new HashMap<>();
    static OkHttpClient httpClient = new OkHttpClient();
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static String url = "https://purocom/";

    public static int getWorld() {
        return Integer.parseInt(data.keySet().stream().findFirst().orElse("-1"));
    }

    public static void getCropCircles() {
        Request req = new Request.Builder()
                .url(url)
                .get()
                .build();
        sendAndUpdate(req);
    }

    public static void reportCropCircle() {
        ForumUser forumUser = Client.getForumUser();
        if (forumUser == null) {
            Logger.info("No scouting cant find forum user");
            return;
        }
        Request req = new Request.Builder()
                .url(url + "?world=" + Worlds.getCurrentWorld())
                .addHeader("TOKEN", Client.getForumUser().getAuthenticationCode())
                .post(new RequestBody() {
                    @Nullable
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(BufferedSink bufferedSink) throws IOException {

                    }
                })
                .build();
        sendAndUpdate(req);
    }

    public static void invalidateCropCircle() {
        Request req = new Request.Builder()
                .url(url + "?world=" + Worlds.getCurrentWorld())
                .delete()
                .build();
        sendAndUpdate(req);
    }

    private static void sendAndUpdate(Request req) {
        try (Response res = httpClient.newCall(req).execute()) {
            if (res.body() == null) {
                Logger.info("No res body");
                return;
            }
            String body = res.body().string();
            Logger.info("Crop circle response " + body);

            // set data
            data = gson.fromJson(body, new TypeToken<Map<String, CropCirclePojo>>() {
            }.getType());
            Logger.info("Setting crop circles");
            Logger.info(data);
        } catch (IOException e) {
            Logger.info("Failed to get crop circle scouting info");
        }
    }
}
