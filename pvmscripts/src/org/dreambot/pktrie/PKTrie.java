package org.dreambot.pktrie;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PKTrie {
    private static final Timer refreshTimer = new Timer(60_000 * 60);
    private static boolean hasLoaded = false;
    /*
        use a trie to store all the players that have ever attack any of our bots
     */
    public static PKTrieNode root = new PKTrieNode();

    public static void addString(String string) {
        PKTrieNode current = root;
        char[] charArr = string.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            Character c = charArr[i];
            boolean isEnd = i == charArr.length - 1;

            if (!current.map.containsKey(c)) current.map.put(c, new PKTrieNode());
            if (isEnd) current.map.get(c).isEnd = true;

            current = current.map.get(c);
        }
    }

    public static boolean checkString(String string) {
        if (string == null) return false;
        if (!hasLoaded || refreshTimer.finished()) {
            hasLoaded = true;
            refreshTimer.reset();
            Logger.info("Mandatory 60 minute PK list refresh");
            root = new PKTrieNode();
            refreshPkerList();
        }

        PKTrieNode current = root;
        char[] charArr = string.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            Character c = charArr[i];

            if (current == null) return false;

            boolean isEnd = i == charArr.length - 1;

            current = current.map.get(c);

            if (current == null || isEnd && !current.isEnd) {
                return false;
            }
        }
        return true;
    }

    /**
     * request the pker tree from ccscripts and clone it
     */
    public static void refreshPkerList() {
        Thread t = new Thread(() -> {
            String smeeeep = makeRequest();
            JsonObject pkers;
            try {
                JsonObject j = JsonParser.parseString(smeeeep).getAsJsonObject();
                pkers = j.get("pkers").getAsJsonObject().get("root").getAsJsonObject();
            } catch (Exception e) {
                Logger.info("Failure to load pkers");
                return;
            }
            copyTree(pkers); // this one modifies all teh state
        });
        t.start();
    }

    /**
     * make the get request to /pk/penis and return the response body
     *
     * @return
     */
    private static String makeRequest() {
        try {
            URL webHookURL = new URL("/");
            HttpURLConnection http = (HttpURLConnection) webHookURL.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(true);
            http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)");
            http.setRequestProperty("Content-Type", "application/json");
//            byte[] out = "".getBytes(StandardCharsets.UTF_8);
//            OutputStream stream = http.getOutputStream();
//            stream.write(out);
//            stream.flush();
//            stream.close();
            String res = http.getResponseMessage(); // this is needed.

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(http.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            http.disconnect();
            return content.toString();
        } catch (Exception e) {
            Logger.error("PK Remote Error: ", e);
        }
        return "";
    }

    /**
     * post request to /pk/{pkerName}
     */
    public static void reportPker(String username) {
        try {
            URL webHookURL = new URL("/" + username);
            HttpURLConnection http = (HttpURLConnection) webHookURL.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)");
            http.setRequestProperty("Content-Type", "application/json");
//            byte[] out = jsonBody.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = http.getOutputStream();
            stream.write(new byte[0]);
            stream.flush();
            stream.close();
            String res = http.getResponseMessage(); // this is needed.
            http.disconnect();
        } catch (Exception e) {
        }
    }

    /**
     * run through the ccscripts site response and copy the tree
     *
     * @param obj
     */
    private static void copyTree(JsonObject obj) {
        obj.entrySet().forEach((entry) -> {
            // if its a char
            if (entry.getKey().contains("children")) {
                JsonObject children = obj.get("children").getAsJsonObject();
                boolean isEnd = obj.get("isEndOfWord").getAsBoolean();
                children.entrySet().forEach(x -> {
                    if (x.getKey().length() < 2) {
                        PKTrieNode node = new PKTrieNode();
                        PKTrie.root.map.put(x.getKey().charAt(0), node);
                        PKTrie.root.isEnd = isEnd;

                        // traverse again
                        copyTree(children.get(x.getKey()).getAsJsonObject(), node);
                    }
                });
            }
        });
    }


    private static void copyTree(JsonObject obj, PKTrieNode node) {
        boolean isEnd = obj.get("isEndOfWord").getAsBoolean();
        node.isEnd = isEnd;

        obj.entrySet().forEach((entry) -> {
            // if its a char
            if (entry.getKey().contains("children")) {
                JsonObject children = obj.get("children").getAsJsonObject();
                children.entrySet().forEach(x -> {
                    if (x.getKey().length() < 2) {
                        PKTrieNode newNode = new PKTrieNode();
                        node.map.put(x.getKey().charAt(0), newNode);
                        copyTree(children.get(x.getKey()).getAsJsonObject(), newNode);
                    }
                });
            }
        });
    }
}
