package org.dreambot.fractals.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dreambot.api.Client;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.GrandExchangeItem;
import org.dreambot.api.methods.grandexchange.Status;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BuyLimitManager implements Runnable {
    private static BuyLimitManager manager;

    private BuyLimitManager() {
        Logger.info("Start ge watcher thread");
        data = this.loadFile().data;
        new Thread(this).start();
    }

    public static BuyLimitManager get() {
        if (manager == null) manager = new BuyLimitManager();
        return manager;
    }

    GrandExchangeItem[] lastCheck;

    List<BuyLimitData> data = new ArrayList<>();
    Timer lastCleaned = new Timer(120_000);

    public int getBrought(int id) {
        if (data == null) return 0;
        if (lastCleaned.finished()) {
            data.removeIf(x -> System.currentTimeMillis() - x.firstBuyTimestamp >= fourHours());
            lastCleaned.reset();
            ;
        }
        BuyLimitData brought = data.stream().filter(x -> x.getItemId() == id).findFirst().orElse(null);
        return brought == null ? 0 : brought.quantityBrought;
    }

    public BuyLimitData getData(int id) {
        if (lastCleaned.finished()) {
            data.removeIf(x -> System.currentTimeMillis() - x.firstBuyTimestamp >= fourHours());
            lastCleaned.reset();
            ;
        }
        return data.stream().filter(x -> x.getItemId() == id).findFirst().orElse(null);
    }

    @Override
    public void run() {
        while (ScriptManager.getScriptManager().isRunning() || ScriptManager.getScriptManager().isPaused()) {
            GrandExchangeItem[] items = GrandExchange.getItems();
            if (items == null) continue;
            if (lastCheck == null) lastCheck = items;
            if (!GrandExchange.isOpen() || !Client.isLoggedIn()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Logger.info("Buy limit manager stopped");
                    throw new RuntimeException(e);
                }
                continue;
            }

            for (int i = 0; i < lastCheck.length; i++) {
                GrandExchangeItem newItem = items[i];
                if (newItem == null || lastCheck[i] == null) continue;
                // check for diffs
                if (!newItem.getName().equals("null")) {
//                    Logger.info(lastCheck[i].getItem() + " " + lastCheck[i].getTransferredAmount() + " " + lastCheck[i].getStatus());
//                    Logger.info("->");
//                    Logger.info(newItem.getItem() + " " + newItem.getTransferredAmount() + " " + newItem.getStatus());
//                    Logger.info("---");

//                    if (newItem.getStatus() != null) statusCache.put(newItem.getSlot(), newItem.getStatus());
                }

                if (lastCheck[i].getTransferredAmount() < newItem.getTransferredAmount()) {
//                    Logger.info(String.format("difference " +
//                            "%b %b %s", lastCheck[i].isBuyOffer(), newItem.isBuyOffer(), newItem.getStatus()));
                    if (newItem.getStatus() == Status.BUY || newItem.getStatus() == Status.BUY_COLLECT) {
//                        Logger.info(String.format("Item brought Item: %s iD: %d quantity: %d",
//                                newItem.getName(),
//                                newItem.getId(),
//                                newItem.getTransferredAmount() - lastCheck[i].getTransferredAmount()
//                        ));
//
                        BuyLimitData inList = data.stream().filter(x -> x.getItemId() == newItem.getId())
                                .findFirst().orElse(null);
                        if (inList != null) {
                            // check timestamp, if over 4 hours completely reset it
                            if ((inList.firstBuyTimestamp - System.currentTimeMillis()) > fourHours()) {
                                // delete old save new
                                inList.quantityBrought += newItem.getTransferredAmount() - lastCheck[i].getTransferredAmount();
                                inList.firstBuyTimestamp = System.currentTimeMillis();
                                ;
                            } else {
                                inList.quantityBrought += newItem.getTransferredAmount() - lastCheck[i].getTransferredAmount();
                            }
                        } else {
                            // item isn't in list, just add it with a new timestamp
                            data.add(new BuyLimitData(newItem.getId(),
                                    newItem.getTransferredAmount(),
                                    System.currentTimeMillis())
                            );
                        }

                        saveFile(new BuyLimitStore(data));
                        lastCheck[i] = items[i];
                    }
                }
                if (lastCheck[i].getStatus() == Status.EMPTY || newItem.getStatus() != null) {
                    lastCheck[i] = items[i];
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Logger.info("Buy limit manager stopped");
                throw new RuntimeException(e);
            }
        }

        Logger.info("Buy limit manager stopped");
    }

    private long fourHours() {
        return 4 * 60 * 60 * 1000;
    }

    private String getPath() {
        // fix for EF / qs users
        String nickname = ScriptManager.getScriptManager().getAccountNickname();
        if (nickname == null) nickname = Client.getUsername();
        if (nickname == null) nickname = "NoAccInfo";
        return System.getProperty("scripts.path") + "/limits/BuyLimit-" + nickname.replaceAll("\\.", "");

    }

    public BuyLimitStore loadFile() {
        File f = Paths.get(getPath()).toFile();
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            saveFile(new BuyLimitStore(data));
            Logger.info(String.format("Failed to load file %s, made default and trying again.", getPath()));
            return loadFile();
        }

        try {
            String settings = new String(Files.readAllBytes(Paths.get(getPath())));
            Logger.info(String.format("Loaded file: %s", getPath()));
            return new Gson().fromJson(settings, BuyLimitStore.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveFile(BuyLimitStore data) {
        Gson pretty = new GsonBuilder().setPrettyPrinting().create();
        String json = pretty.toJson(data);

        try {
            Logger.info(String.format("Saved file: %s", getPath()));
            Files.write(Paths.get(getPath()), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
