package org.dreambot;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AnalyticMessage {
    @SerializedName("muleID")
    String muleID; // hash of mule script manager nickname
    @SerializedName("script")
    String script; // script / identifier, the name of the script that made the mule request, not cCMule
    @SerializedName("amount")
    int amount; // the amount of gold changed, negative or positive from the mules pov
    @SerializedName("owner")
    String owner; // the user that owns this mule, used to show each user their
    @SerializedName("accToken")
    String accToken; // hash of the account so we can find which exact accounts have profited/lost the most

    public AnalyticMessage(String script, int amount, String owner, String accToken) {
        this.muleID = hashStringSHA256(ScriptManager.getScriptManager().getAccountNickname());
        this.script = script;
        this.amount = amount;
        this.owner = owner;
        this.accToken = accToken;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

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
}
