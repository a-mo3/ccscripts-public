package org.dreambot.behaviour.trawler;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.data.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.text.DecimalFormat;

public class Trawler extends Fractal implements ChatListener {
    final Area PORT_KHAZARD = new Area(2646, 3174, 2690, 3141);
    final Area TRAWLER = new Area(1790, 4926, 2044, 4732);
    final int MAX_CONTRIBUTION = ScriptSettings.getSettingsData().trawlerContributionLimit;
    final Timer bankCheckTime = new Timer(60L * 1000 *
            ScriptSettings.getSettingsData().getTrawlerBankCheckTimeMinutes());
    final int LOOT_PARENT = 367;
    final String PET_WARNING = "You can't take that follower on the trawler. It might get wet.";
    final String LOOTLESS_DIALOGUE = "I'd better not steal other people's fish!";
    // sometimes loot varbit gets stuck at 3 after looting, usually caused by a disconnect or something
    boolean bustedLoot;
    final int LOOT_VARBIT = 12117;

    public Trawler() {
        Client.getInstance().addEventListener(this);
        this.setSimpleName("Fishing trawler");

        this.paintArraySupplier = () -> new String[]{
                String.format("contribution: %d/%d", getContribution(), MAX_CONTRIBUTION),
                "Bank check: " + formatTime(bankCheckTime.elapsed()),
                "Loot varbit: " + PlayerSettings.getBitValue(12117),
                "hasHat: " + OwnedItems.contains(ItemID.ANGLER_HAT),
                "hasChest: " + OwnedItems.contains(ItemID.ANGLER_TOP),
                "hasLegs: " + OwnedItems.contains(ItemID.ANGLER_WADERS),
                "hasBoots: " + OwnedItems.contains(ItemID.ANGLER_BOOTS),
        };

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SWAMP_PASTE, 100, 2000).setBuyPrice(16)
                .addItem(ItemID.BUCKET).setBuyPrice(500)
                .addItem(ItemVariants.COMBAT_BRACLET)
                .setStrict(true)
        ;
    }

    @Override
    public boolean isValid() {
        return !OwnedItems.containsAll(
                ItemID.ANGLER_BOOTS,
                ItemID.ANGLER_HAT,
                ItemID.ANGLER_TOP,
                ItemID.ANGLER_WADERS
        );
    }

    private int getContribution() {
        return PlayerSettings.getBitValue(3377);
    }

    @Override
    public int onLoop() {
        if (Worlds.getCurrentWorld() != 370) {
            WorldHopper.hopWorld(Worlds.getWorld(370));
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue() && Dialogues.getNPCDialogue().equalsIgnoreCase(LOOTLESS_DIALOGUE)) {
            bustedLoot = true;
        }

        if (bustedLoot && PlayerSettings.getBitValue(LOOT_VARBIT) < 3) {
            bustedLoot = false;
        }

        if (Client.isInCutscene()) return ReactionGenerator.getNormal();
        if (Client.isDynamicRegion()) return ReactionGenerator.getNormal();

        WidgetChild bankAll = Widgets.get(LOOT_PARENT, 19);
        if (bankAll != null && bankAll.isVisible()) {
            bankAll.interact();
            return ReactionGenerator.getLong();
        }

        Player lp = Players.getLocal();
        if (!TRAWLER.contains(lp) && lp.getZ() == 0) {
            if (!PORT_KHAZARD.contains(lp)) {
                if (Walking.shouldWalk()) Walking.walk(PORT_KHAZARD.getCenter());
                return ReactionGenerator.getNormal();
            }

            if (lp.getZ() != 1) {
                GameObject lootNet = GameObjects.closest("Trawler net");
                if (lootNet != null && hasLoot()) {
                    lootNet.interact("Inspect");
                    Sleep.sleepUntil(() -> {
                        Widget w = Widgets.getWidget(LOOT_PARENT);
                        return w != null && w.isVisible();
                    }, 4400);
                    return ReactionGenerator.getNormal();
                }
                // check bank every once in a while for what equipment u have
                if (bankCheckTime.finished()) {
                    if (Bank.isOpen()) {
                        bankCheckTime.reset();
                        Bank.close();
                        return ReactionGenerator.getNormal();
                    }

                    if (!Bank.isOpen() && Walking.shouldWalk()) {
                        Bank.open();
                    }
                    return ReactionGenerator.getNormal();
                }

                GameObject gp = GameObjects.closest("Gangplank");
                if (gp != null) {
                    if (gp.distance() > 5) {
                        if (Walking.shouldWalk()) Walking.walk(gp.getTile());
                        return ReactionGenerator.getNormal();
                    }
                    gp.interact("Cross");
                    Sleep.sleepUntil(() -> Players.getLocal().getZ() == 1, 4400);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }
        // do trawler

        if (getContribution() >= MAX_CONTRIBUTION) {
            return ReactionGenerator.getLong();
        }
        // walk to edge
        if (lp.getY() == 4825) {
            if (Walking.shouldWalk(6)) Walking.walk(lp.getX(), lp.getY() + 1);
            return ReactionGenerator.getNormal();
        }

        GameObject leak = GameObjects.closest(x -> x.getName().equals("Leak") && x.distance() < 3 && x.hasAction());
        if (leak != null) {
            leak.interact("Fill");
        }
        return ReactionGenerator.getQuick();
    }

    private boolean hasLoot() {
        return !bustedLoot && PlayerSettings.getBitValue(12117) > 1;
    }

    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        DecimalFormat format = new DecimalFormat("00");
        return String.format("%s:%s:%s",
                format.format(hours),
                format.format(minutes),
                format.format(seconds));
    }

    @Override
    public void onMessage(Message message) {
        String msg = message.getMessage();
        if (msg.toLowerCase().contains("smelly net is")) bustedLoot = true;
    }
}
