package org.dreambot.behaviour.training.agility.wild;

import com.google.gson.Gson;
import okhttp3.*;
import okio.BufferedSink;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.util.CombatUtil;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RagList {
    public static List<String> names = new ArrayList<>();
    static ExecutorService ragExecutor = Executors.newSingleThreadExecutor();
    static long timestamp;

    public static void updateList() {

        if (names.isEmpty()) {
            System.out.println("Refresh name list");
            timestamp = System.currentTimeMillis();

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://puro./raglist?=")
                    .get()
                    .addHeader("User-Agent", "insomnia/11.1.0")
                    .build();

            ragExecutor.execute(() -> {
                try {
                    System.out.println("Send req");
                    Response response = client.newCall(request).execute();
                    String penis = response.body().string();
                    Gson gson = new Gson();
                    System.out.println(penis);
                    names = gson.fromJson(penis, names.getClass());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // non empty just request update
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://puro./ragsince?since=" + timestamp)
                .get()
                .addHeader("User-Agent", "insomnia/11.1.0")
                .build();

        timestamp = System.currentTimeMillis();
        ragExecutor.execute(() -> {
            try {
                Response response = client.newCall(request).execute();
                String penis = response.body().string();
                Gson gson = new Gson();
                List<String> updatedNames = gson.fromJson(penis, names.getClass());
                names.addAll(updatedNames);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * @param user this is someone who attacked our bots, not one of our bots, so we dont need to hash it
     */
    public static void report(String user) {
        OkHttpClient client = new OkHttpClient();
        Logger.info("Report " + user);
        Request request = new Request.Builder()
                .url("https://puro./raglist?user=" + user)
                .put(new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(BufferedSink bufferedSink) throws IOException {
                    }
                })
                .addHeader("User-Agent", "insomnia/11.1.0")
                .build();

        ragExecutor.execute(() -> {
            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Player getRagTarget() {
        return Players.closest(x -> names.contains(x.getName())
                && !x.getName().equals(Players.getLocal().getName())
                && CombatUtil.canAttackMe(x)
        );
    }
}
