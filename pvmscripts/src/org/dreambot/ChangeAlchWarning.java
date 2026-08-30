package org.dreambot;

import org.dreambot.api.ClientSettings;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.awt.*;
import java.util.function.Supplier;

public class ChangeAlchWarning {

    public static final int BUY_WARNING_VARBIT = 14700;
    public static final int SELL_WARNING_VARBIT = 14701;

    public static final int LEVEL_UP_INTERFACES_VARBIT = 9452;


    public static final int SETTINGS_MENU_PARENT = 134;
    public static final int SETTINGS_TAB_VARBIT = 9656;

    public static final int HIGH_ALCH_WARNING_THRESHOLD = 6091; // varbit

    // parent for the tabs menu not the tab selector
    public static final int SETTINGS_TAB_PARENT = 116;
    // the widgets with actions have no text & name is Ok (on all of them) so no clear way to do it dynamically
    static final Supplier<WidgetChild> toggleBuyWarningWC = () -> Widgets.get(SETTINGS_MENU_PARENT, 19, 39);
    static final Supplier<WidgetChild> toggleSellWarningWC = () -> Widgets.get(SETTINGS_MENU_PARENT, 19, 40);

    static final Supplier<WidgetChild> toggleLevelUpInterfaceWC = () -> Widgets.get(SETTINGS_MENU_PARENT, 19, 21);

    public static boolean openWarningsTab() {
        if (!ClientSettings.isOpen()) {
            // just use this to open settings
            Logger.info("Open client settings");
//            if (ClientSettings.getClientLayout() == ClientLayout.FIXED_CLASSIC) ClientSettings.setClientLayout(ClientLayout.RESIZABLE_CLASSIC);
            ClientSettings.toggleCollectionLogNotifications(!ClientSettings.areCollectionLogNotificationsEnabled());
            return false;
        }

        if (PlayerSettings.getBitValue(SETTINGS_TAB_VARBIT) != 7) { // 🧙
            WidgetChild warningsTab = Widgets.get(134, 25, 79); // 🧙
            Logger.info("Opening warnings tab " + warningsTab);
            if (warningsTab != null) {
                // no actions on this one
                Mouse.click(warningsTab.getRectangle().getLocation());
                Antiban.sleepUntil(() -> PlayerSettings.getBitValue(SETTINGS_TAB_VARBIT) == 7, 2400);
                return false;
            }
        }
        return true;
    }

    public static boolean setHighAlchWarning(int target) {
        if (PlayerSettings.getBitValue(HIGH_ALCH_WARNING_THRESHOLD) >= target) return true;
        if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
        return ClientSettings.setMinimumAlchWarningValue(target);
    }
}
