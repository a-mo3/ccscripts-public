package org.dreambot.discordwebhook.pojo;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.*;
import org.dreambot.api.utilities.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Getter
@Setter
@Accessors(chain = true)
public class DiscordWebHook {
    @SerializedName("username")
    private String username;
    @SerializedName("content")
    private String content;
    @SerializedName("avatar_url")
    private String avatar_url;
    @SerializedName("embeds")
    private DiscordEmbed[] embeds;
    public DiscordWebHook setEmbeds(DiscordEmbed... embeds) {
        this.embeds = embeds;
        return this;
    }


    public void send(String url, BufferedImage img) throws IOException {
        // search embeds for an image
        OkHttpClient okHttpClient = new OkHttpClient();


        RequestBody imageBody = RequestBody.create(
                MediaType.parse("image/png"),
                bufferedImageToPngBytes(img)
        );

        for (DiscordEmbed embed : embeds) {
            embed.image = new DiscordImage("attachment://image.png");
        }
        Logger.info("Penis");
        RequestBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", new Gson().toJson(this))
                .addFormDataPart("file", "image.png", imageBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Logger.info("Unexpected code " + response.code() + ": " + response.body().string());
            }
        } catch (Exception ignored) {

        }
    }

    private static byte[] bufferedImageToPngBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }


}
