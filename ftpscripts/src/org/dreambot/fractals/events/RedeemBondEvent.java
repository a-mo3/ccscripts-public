package org.dreambot.fractals.events;

import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.bond.Bond;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.BondSettings;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.timing.ReactionGenerator;

public class RedeemBondEvent extends AbstractEvent implements ChatListener {
    ItemVariant BOND = new ItemVariant(ItemID.OLD_SCHOOL_BOND, ItemID.OLD_SCHOOL_BOND_UNTRADEABLE);
    boolean usedBond = false;
    final BondSettings bondSettings;

    Filter<World> membersWorldFilter = x -> x.isNormal() && x.getMinimumLevel() <= Skills.getTotalLevel() && x.isMembers();
    Filter<World> freeWorldFilter = x -> x.isNormal() && x.getMinimumLevel() <= 10 && !x.isMembers();

    final int BOND_MENU_PARENT = 861;
    boolean redeemed = false;

    public RedeemBondEvent() {
        Client.getInstance().addEventListener(this);
        SettingsLoader<BondSettings> bondLoader = new SettingsLoader<>(BondSettings.class);
        bondSettings = bondLoader.loadFile("bondSettings.json", new BondSettings());
    }

    @Override
    public int onLoop() {
        if (Worlds.getCurrent().isMembers()) {
            setComplete(true);
            return ReactionGenerator.getNormal();
        }

        if (Client.getMembershipLeft() > 0) {
            WorldHopper.hopWorld(Worlds.getRandomWorld(membersWorldFilter));
            return ReactionGenerator.getNormal();
        }

        Logger.info("Bond event");
        if (Client.getMembershipLeft() > 0 || PlayerSettings.getConfig(1780) > 0) {
            Logger.info("redeem bond event, you have membership");
            setComplete(true);
            return ReactionGenerator.getNormal();
        }

        // get chat message widget
        if (Widgets.get(x -> x.getText().contains("log out before attempting")) != null || redeemed) {
            WorldHopper.hopWorld(Worlds.getRandomWorld(freeWorldFilter));
            return ReactionGenerator.getNormal();
        }

        if (usedBond) {
            return ReactionGenerator.getNormal();
        }

        if (GrandExchange.contains(BOND.getIds()) || GrandExchange.isReadyToCollect()) {
            if (!GrandExchange.isOpen()) {
                if (Walking.shouldWalk()) GrandExchange.open();
                return ReactionGenerator.getNormal();
            }

            if (GrandExchange.isReadyToCollect()) {
                GrandExchange.collect();
                Sleep.sleepUntil(() -> BOND.getItem() != null, 2400);
                return ReactionGenerator.getNormal();
            }

            Sleep.sleepUntil(GrandExchange::isReadyToCollect, 2400);
            return ReactionGenerator.getNormal();
        }

        if (!OwnedItems.contains(BOND)) {
            if (OwnedItems.count(ItemID.COINS_995) < LivePrices.get(ItemID.OLD_SCHOOL_BOND)) {
                Logger.info("Cant afford bond so failing event");
                setFailed(true);
            }

            Logger.info("Bond loadout - " + new WithdrawLoadoutEvent(
                    new InventoryLoadout()
                            .addItem(BOND)
                            .setBuyPrice(bondSettings.bondBuyPrice)
                            .setPriceIncrease(1)
                    , null)
                    .executed());
            Sleep.sleepUntil(GrandExchange::isReadyToCollect, 1800);
            return ReactionGenerator.getNormal();
        }


        Logger.info("Open bond screen");
        if (!Bond.isRedeemBondScreenOpen()) {
            Bond.openRedeemBondScreen();
            return ReactionGenerator.getNormal();
        }


        Logger.info("Bond redeeming " + ClientSettings.getClientLayout() + " Width: " + Client.getViewportWidth() + " Height: " + Client.getViewportHeight());
        if (Bond.redeem(1)) {
            WorldHopper.hopWorld(Worlds.getRandomWorld(freeWorldFilter));
        }

        Sleep.sleepUntil(Client::isMembers, 30_000);
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().equals("log out before attempting")) redeemed = true;
    }
}
