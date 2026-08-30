package org.dreambot.scout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.Unobfuscated;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Accessors(chain = true)
@Setter
@Getter
@Unobfuscated
public class PlayerScout {
    @Unobfuscated
    String username;
    @Unobfuscated
    int combatLevel;
    @Unobfuscated
    int world;
    @Unobfuscated
    boolean isMembers;
    @Unobfuscated
    String interactingWith;
    @Unobfuscated
    int animation;
    @Unobfuscated
    int tileX;
    @Unobfuscated
    int tileY;
    @Unobfuscated
    int skullStatus;
    @Unobfuscated
    long equipmentValue;
    @Unobfuscated
    EquipmentModel[] equipment = new EquipmentModel[12];
    @Unobfuscated
    int[] equipmentVector = new int[11];

    public PlayerScout(Player player) {
        username = player.getName();
        combatLevel = player.getLevel();
        world = Worlds.getCurrentWorld();
        isMembers = Worlds.getCurrent().isMembers();
        Character target = player.getInteractingCharacter();
        interactingWith = target == null ? "none" : target.getName();
        animation = player.getAnimation();
        tileX = player.getX();
        tileY = player.getY();
        skullStatus = player.getSkullIcon();
        player.getEquipment().forEach(x -> {
            equipment[x.getSlot()] = new EquipmentModel(x);
            if (x.getSlot() < equipmentVector.length) equipmentVector[x.getSlot()] = x.getID();
            equipmentValue += x.getLivePrice();
        });
        equipment = Arrays.stream(equipment).map(x -> {
            if (x == null) x = new EquipmentModel();
            return x;
        }).toArray(EquipmentModel[]::new);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonBody = gson.toJson(this);
//        Logger.info(jsonBody);
        sendAPIReq("/scout", jsonBody);
    }

    private static Executor service = Executors.newFixedThreadPool(100);

    private void sendAPIReq(String endpoint, String jsonBody) {
        service.execute(() -> {
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
                String res = http.getResponseMessage(); // this is needed.
//                Logger.info(res);
                http.disconnect();
            } catch (Exception e) {
//                Logger.info("Pano Error (non critical): ");
            }
        });
    }
}
