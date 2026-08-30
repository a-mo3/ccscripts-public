package org.dreambot.behaviour.method.teletabs.poh;

import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.scriptdata.PohTeleTabSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EnterHouse extends Fractal {
    final Area RIMMINGTON_PORTAL = new Area(2951, 3228, 2956, 3220);
    public static final Set<String> blacklistedOwners = new HashSet<>();
    // the other blacklist will be cleared, the manual blacklist will not be cleared
    // todo i think the fractal is used in multiple scripts this should be injected
    public static final Set<String> settingsBlackList = Arrays.stream(SettingsRepository.findInstanceOf(new PohTeleTabSettings())
            .blacklistedHouses.split(",")).collect(Collectors.toSet());
    public static String lastHouseOwner = "";

    public EnterHouse() {
        blacklistedOwners.add("V 3");
        blacklistedOwners.add("ZZX_0ZZ");
        blacklistedOwners.add("Pyramid");
        blacklistedOwners.add("XVLKLEOPATRA");
    }

    @Override
    public int onLoop() {
        if (Worlds.getCurrentWorld() != 330) {
            WorldHopper.hopWorld(330);
            return ReactionGenerator.getLong();
        }

        if (!RIMMINGTON_PORTAL.contains(Players.getLocal())) {
            if (Bank.isOpen()) {
                Bank.close();
                return ReactionGenerator.getNormal();
            }
            if (Walking.shouldWalk()) Walking.walk(RIMMINGTON_PORTAL);
            return ReactionGenerator.getNormal();
        }

        Widget houseAds = Widgets.getWidget(52);
        if (houseAds == null || !houseAds.isVisible()) {
            GameObject house = GameObjects.closest("House Advertisement");
            if (house != null && house.interact("View")) {
                Sleep.sleep(800, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        // check if sorted high to low
        WidgetChild sortArrow = Widgets.get(52, 5, 8);
        Logger.info("sort " + sortArrow);
        // texture id was sprite id
        if (sortArrow != null && sortArrow.getTextureId() != 1051) {
            Mouse.click(sortArrow.getRectangle().getLocation());
            return ReactionGenerator.getNormal();
        }

        // get topmost (lowest Y) enter button
        WidgetChild enterHouse = getBestEnter();
        Logger.info("Best house " + getBestEnter());
        if (enterHouse == null) blacklistedOwners.clear();
        Logger.info("Enter house " + enterHouse);
        if (enterHouse != null && enterHouse.interact("Enter House")) {
            Sleep.sleepUntil(Client::isDynamicRegion, 3500);
        }

        return ReactionGenerator.getNormal();
    }

    final int ADVERT_PARENT = 52;
    final int NAME_PARENT = 9; // 52, 9, x is the player name for x button
    Timer refreshTimer = new Timer(20_000);

    private WidgetChild getBestEnter() {
        WidgetChild refreshButton = Widgets.get(x -> x.hasAction("Refresh Data"));
        if (refreshButton != null && refreshTimer.finished()) {
            refreshTimer.reset();
            log("Refresh advert list");
            refreshButton.interact("Refresh Data");
        }

        List<WidgetChild> enterButtons = Widgets.getAll(x -> x.hasAction("Enter House"));
        WidgetChild lowestYWidget = null;
        for (WidgetChild button : enterButtons) {
            int houseIndex = button.getIndex();
//            Log.info("House index - " + houseIndex);
            WidgetChild nameWidget = Widgets.get(ADVERT_PARENT, NAME_PARENT, houseIndex);
            if (nameWidget == null) {
                log("couldnt find name widget so button was skipped");
                continue;
            }

            String houseOwner = nameWidget.getText();
            Logger.info("last home owner " + houseOwner + " " + houseIndex);
            if (settingsBlackList.contains(houseOwner) || blacklistedOwners.contains(houseOwner)) {
                log("this owner was blacklisted");
                continue;
            }


            if (lowestYWidget == null) {
                lowestYWidget = button;
                lastHouseOwner = houseOwner;
                log("Default");
            }
            log("Lowest " + lowestYWidget.getY() + " this " + button.getY());
            if (button.getY() < lowestYWidget.getY()) {
                log("House owned " + houseOwner);
                lastHouseOwner = houseOwner;
                lowestYWidget = button;
            }
        }
        return lowestYWidget;
    }
}
