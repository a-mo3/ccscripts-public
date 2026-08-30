package org.dreambot.behaviour.method.mixology;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.NPCUtil;
import org.dreambot.settings.timing.ReactionGenerator;

public class ClaimMixologyReward extends Fractal {
    final MixologyRewardItem rewardItem;

    public ClaimMixologyReward(MixologyRewardItem rewardTarget, int multiple) {
        super(() -> rewardTarget.claimableQuantity() >= multiple);
        this.rewardItem = rewardTarget;
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH);
    }

    public static final Area MIXOLOGY_AREA = new Area(1390, 9317, 1400, 9308);

    @Override
    public int onLoop() {
        if (!MIXOLOGY_AREA.contains(Players.getLocal())) {
            log("Get to mixology area");
            if (Inventory.count(ItemID.COINS_995) < 5000 && Players.getLocal().getY() < 5000) {
                log("Getting coins for boat - " + new WithdrawLoadoutEvent(new InventoryLoadout()
                        .addItem(ItemID.COINS_995, 10_000), null)
                        .executed());
            }
            if (Walking.shouldWalk()) Walking.walk(MIXOLOGY_AREA);
            return ReactionGenerator.getNormal();
        }

        // open rewards
        Widget rewardWindow = Widgets.getWidget(819);
        if (rewardWindow == null || !rewardWindow.isVisible()) {
            log("Open reward window");
            NPCUtil.interact("Supervisor Lalo", "Rewards");
            Sleep.sleepUntil(Widgets::isOpen, 2400);
            return ReactionGenerator.getNormal();
        }

        WidgetChild buyReward = Widgets.get(x -> (x.getName().contains(rewardItem.itemName())
                || x.getText().contains(rewardItem.itemName()))
                && (rewardItem == MixologyRewardItem.CHUGGING_BARREL ? x.hasAction("Buy") :  x.hasAction("Buy-50") ) );
        if (buyReward == null) {
            log("Failed to find widget to buy " + rewardItem.itemName());
            return ReactionGenerator.getNormal();
        }

        // mouse on scroll check
        if (!Menu.isMenuManipulationActive()) {
            WidgetChild scrollPane = Widgets.get(819, 33);
            if (scrollPane == null) {
                log("Failed to find scrollpane when doing mouse scroll check");
                return ReactionGenerator.getNormal();
            }

            Mouse.move(scrollPane.getRectangle());
            Mouse.scrollDown(1000, () -> false);
        }
        buyReward.interact("Buy-50");
        return ReactionGenerator.getNormal();
    }
}
