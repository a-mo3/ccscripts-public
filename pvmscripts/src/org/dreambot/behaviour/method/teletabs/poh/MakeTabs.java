package org.dreambot.behaviour.method.teletabs.poh;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.scriptdata.PohTeleTabSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public class MakeTabs extends Fractal {
    public MakeTabs(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        // should be in there
        if (!Client.isDynamicRegion()) {
            Logger.info("Go into POH");
            // todo enter house from adverts
            return ReactionGenerator.getNormal();
        }

        // todo check for troll houses

        GameObject lectern = GameObjects.closest("Lectern");
        if (lectern == null) {
            EnterHouse.blacklistedOwners.add(EnterHouse.lastHouseOwner);
            Logger.info("Couldn't find a lectern blacklisted " + EnterHouse.lastHouseOwner);
            GameObject portal = GameObjects.closest("Portal");
            if (portal != null && portal.interact()) {
                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 4000);
            }
            return ReactionGenerator.getNormal();
        }

        if (!Widgets.isOpen()) {
            lectern.interact();
            Sleep.sleepUntil(Widgets::isOpen, 4400);
            return ReactionGenerator.getNormal();
        }

        WidgetChild createButton = Widgets.get(x -> x.getParentID() == 403
                && x.getActions() != null
                && Arrays.stream(x.getActions())
                .filter(Objects::nonNull)
                .anyMatch(a -> a.contains("Create"))
        );
        if (createButton != null) {
            Logger.info("Creating tabs");
            createButton.interact();
            Sleep.sleepUntil(() -> !Inventory.contains(ItemID.SOFT_CLAY), // (unnoted)
                    () -> Players.getLocal().isAnimating(),
                    4400,
                    100
            );
            return ReactionGenerator.getNormal();
        }

        if (Client.isDynamicRegion() && EnterHouse.blacklistedOwners.contains(EnterHouse.lastHouseOwner)) {
            log("Gotta leave blacklisted house");
            if (Widgets.isOpen()) Widgets.closeAll();
            GameObject portal = GameObjects.closest("Portal");
            if (portal != null && portal.interact()) {
                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 4000);
            }
            return ReactionGenerator.getNormal();
        }

        PohTeleTabOption option = SettingsRepository.findInstanceOf(new PohTeleTabSettings()).option;
        WidgetChild selectTeleport = Widgets.get(x -> x.getText().equalsIgnoreCase(option.title));
        if (selectTeleport == null) {
            Logger.info("Couldnt find widget, blacklisting house " + EnterHouse.blacklistedOwners);
            EnterHouse.blacklistedOwners.add(EnterHouse.lastHouseOwner);
            return ReactionGenerator.getNormal();
        }
        selectTeleport.interact();

        return ReactionGenerator.getNormal();
    }
}
