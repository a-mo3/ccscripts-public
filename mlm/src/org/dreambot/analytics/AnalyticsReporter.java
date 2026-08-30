package org.dreambot.analytics;

import com.google.gson.Gson;
import org.dreambot.analytics.models.BanReport;
import org.dreambot.api.Client;
import org.dreambot.api.randoms.RandomManager;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.LoginListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.BooleanSupplier;

public class AnalyticsReporter implements Runnable, LoginListener {
    private final int minutes;
    private final Timer timer;
    private final BooleanSupplier isBreaking;
    private final Thread thread = new Thread(this);
    private static AnalyticsReporter reporter = null;

    public static void start(int minutes, BooleanSupplier isBreaking) {
        if (reporter == null) reporter = new AnalyticsReporter(minutes, isBreaking);
    }

    public static void stop() {
        if (reporter == null) return;
        reporter.thread.interrupt();
    }

    private AnalyticsReporter(int minutes, BooleanSupplier isBreaking) {
        this.isBreaking = isBreaking;
        this.minutes = minutes;
        timer = new Timer(60L * 1000 * minutes);
        Client.getInstance().addEventListener(this);
        thread.start();
    }

    @Override
    public void run() {
        ScriptManager sm = Client.getInstance().getScriptManager();
        RandomManager rm = Client.getInstance().getRandomManager();
    }

    private void sendAPIReq(String endpoint, String jsonBody) {
        try {
            URL webHookURL = new URL("" + endpoint);
            HttpURLConnection http = (HttpURLConnection) webHookURL.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)");
            http.setRequestProperty("Content-Type", "application/json");
            byte[] out = jsonBody.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = http.getOutputStream();
            stream.write(out);
            stream.flush();
            stream.close();
            http.getResponseMessage(); // this is needed.
            http.disconnect();
        } catch (Exception e) {
            Logger.error("Analytics Error (non critical): ", e);
        }
    }
//
//    @Override
//    public void notify(LoginResponseEvent loginResponseEvent) {
//        if (loginResponseEvent.getResponse() == LoginResponse.BANNED) {
//            Gson gson = new Gson();
//            String bod = gson.toJson(new BanReport().setAccountToken(WebhookUtils.getCreds()));
//            sendAPIReq(String.format("/%s/heartbeat/%s", AbstractScript.getScriptName(),
//                            hashStringSHA256(Client.getLoginUsername() + ":" + Client.getLoginPassword())),
//                    bod);
//        }
//    }

    public static String hashStringSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes());

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Logger.error("Error hashing: ", e);
            return null;
        }
    }

    @Override
    public void onLoginStageChange(int i) {

    }


    @Override
    public void onLoginResponse(int i) {
        Logger.info("Analytics login response " + i);
        if (i == 4) {
            ScriptManager sm = Client.getInstance().getScriptManager();
            BanReport br = new BanReport()
                    .setScriptName(sm.getCurrentScript().getSDNName().replaceAll(" ", ""))
                    .setAccountHash(hashStringSHA256(Client.getUsername() + ":" + Client.getPassword()))
                    .setDreambotUser(Client.getForumUser().getUsername());
            Gson g = new Gson();
            String jsonBody = g.toJson(br);
            String scriptName = sm.getCurrentScript().getSDNName().replaceAll(" ", "");
            sendAPIReq(String.format("/%s/heartbeat/%s",
                            scriptName,
                            br.getDreambotUser() + br.getAccountHash()),
                    jsonBody);
        }
    }

    @Override
    public void onLoadingStateChange(int i) {

    }

    @Override
    public void onLoginResponseChange(String s, String s1, String s2) {

    }
}