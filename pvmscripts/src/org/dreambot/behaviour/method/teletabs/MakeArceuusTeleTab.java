package org.dreambot.behaviour.method.teletabs;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.scriptdata.ArceuusTeleTabSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public class MakeArceuusTeleTab extends Fractal {
    Area PODIUM = new Area(1675, 3769, 1681, 3764);

    public MakeArceuusTeleTab(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Make Teleport");
    }

    @Override
    public int onLoop() {
        if (!PODIUM.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(PODIUM);
            return ReactionGenerator.getNormal();
        }

        if (!Widgets.isOpen()) {
            Logger.info("Open lectern");
            GameObject lectern = GameObjects.closest("Lectern");
            if (lectern != null) {
                lectern.interact("Study");
            }
            return ReactionGenerator.getNormal();
        }

        WidgetChild createButton = Widgets.get(x -> x.getParentID() == 403
                && x.getActions() != null && Arrays.stream(x.getActions())
                .filter(Objects::nonNull)
                .anyMatch(a -> a.contains("Create"))
        );
        if (createButton != null) {
            Logger.info("Creating tabs");
            createButton.interact();
            Sleep.sleepUntil(() -> !Inventory.contains("Dark essence block"),
                    () -> Players.getLocal().isAnimating(),
                    4400,
                    100
            );
            return ReactionGenerator.getNormal();
        }

        ArceuusTeleTabOption option = SettingsRepository.findInstanceOf(new ArceuusTeleTabSettings()).option;
        WidgetChild selectTeleport = Widgets.get(x ->
                x.getText().equalsIgnoreCase(option.title)
        );

        if (selectTeleport == null) {
            Logger.info("Couldnt find widget");
            Logger.info(SettingsRepository.findInstanceOf(new ArceuusTeleTabSettings()).option);
            return ReactionGenerator.getNormal();
        }
        selectTeleport.interact();
        return ReactionGenerator.getNormal();
    }
}
