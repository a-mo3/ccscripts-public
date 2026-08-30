package org.dreambot.behaviour.method.amethyst;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class Amethyst extends Fractal {
    Area PUBLIC_AMETHYST_AREA = new Area(3016, 9713, 3030, 9698);

    public Amethyst(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Amethyst mining");

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(() -> {
                    int mineLvl = Skills.getRealLevel(Skill.MINING);
                    if (mineLvl >= 61) return ItemID.DRAGON_PICKAXE;
                    if (mineLvl >= 41) return ItemID.RUNE_PICKAXE;
                    if (mineLvl >= 21) return ItemID.MITHRIL_PICKAXE;
                    return ItemID.BRONZE_PICKAXE;
                }, 1)
                .strictIgnore(ItemID.AMETHYST)
                .setStrict(true);
    }

    @Override
    public int onLoop() {
        if (Inventory.isFull()) new BankAllInventoryEvent().execute();

        if (!PUBLIC_AMETHYST_AREA.contains(Players.getLocal())) {
            Walking.walk(PUBLIC_AMETHYST_AREA);
            return ReactionGenerator.getNormal();
        }

        // todo consider fletching here

        GameObject amethyst = GameObjects.closest("Amethyst crystals");
        if (amethyst != null) {
            log("Mine amethyst");
            amethyst.interact();
            Sleep.sleepUntil(Inventory::isFull,
                    () -> Players.getLocal().isAnimating() || Players.getLocal().isMoving(),
                    2400,
                    100);
        }

        return ReactionGenerator.getNormal();
    }
}
