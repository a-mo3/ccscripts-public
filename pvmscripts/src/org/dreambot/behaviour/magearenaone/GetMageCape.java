package org.dreambot.behaviour.magearenaone;


import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class GetMageCape extends Fractal {
    public static final Area CAPES = new Area(2496, 4727, 2518, 4716);

    @Override
    public boolean isValid() {
        // 267 is updated every time you kill a kolodion form, 7 once u pray
        return PlayerSettings.getConfig(267) >= 6;
    }

    @Override
    public int onLoop() {
        if (Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) {
            Prayers.toggle(false, Prayer.PROTECT_FROM_MAGIC);
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.contains(ItemID.KNIFE)) {
            if (!Bank.isOpen()) {
                Bank.open(BankLocation.GRAND_EXCHANGE);
                return ReactionGenerator.getNormal();
            }
            if (Bank.contains(ItemID.KNIFE)) {
                Bank.withdraw(ItemID.KNIFE, 1);
                Sleep.sleepUntil(() -> Inventory.contains(ItemID.KNIFE), 3000);
                return ReactionGenerator.getNormal();
            }
            log("No knife?");
            return ReactionGenerator.getNormal();
        }

        if (MageArenaOneBranch.CAPE_ROOM.contains(Players.getLocal())) {
            // get guthix cape
            if (!CAPES.contains(Players.getLocal())) {
                log("walking to capes");
                if (Walking.shouldWalk()) Walking.walk(CAPES.getCenter());
                return ReactionGenerator.getNormal();
            }

            WidgetChild guthixCapeTakeWidget = Widgets.get(x -> x.hasAction("Take"));

            if (guthixCapeTakeWidget != null) {
                guthixCapeTakeWidget.interact("Take");
                return ReactionGenerator.getNormal() + 900;
            }

            GroundItem guthixCape = GroundItems.closest(ItemID.GUTHIX_CAPE);
            if (guthixCape != null && guthixCape.interact("Take")) {
                Sleep.sleepUntil(() -> Inventory.contains(ItemID.GUTHIX_CAPE), 2400);
                return ReactionGenerator.getNormal();
            }

            GameObject guthixStatue = GameObjects.closest("Statue of Guthix");
            if (guthixStatue != null && guthixStatue.interact("Pray-at")) {
                Sleep.sleep(1800);
            }
            return ReactionGenerator.getNormal();
        }

        if (MageArenaOneBranch.MAGE_ARENA_BANK.contains(Players.getLocal())) {
            // enter water
            if (Dialogues.inDialogue()) {
                Dialog.solve();
                return ReactionGenerator.getNormal();
            }

            GameObject sparklingPool = GameObjects.closest("Sparkling pool");
            if (sparklingPool != null && sparklingPool.interact("Step-into")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }

            return ReactionGenerator.getNormal();
        }

        // walk to jaunt
        if (Combat.getWildernessLevel() < 1 && !MageArenaOneBranch.MAGE_ARENA_BANK.contains(Players.getLocal())) {
            if (!MageArenaOneBranch.EDGEVILLE_SWITCH.contains(Players.getLocal())) {
                log("walking to egdeville switch");
                Walking.walk(MageArenaOneBranch.EDGEVILLE_SWITCH.getCenter());
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                Dialog.solve("brave");
                return ReactionGenerator.getNormal();
            }

            GameObject lever = GameObjects.closest("Lever");
            if (lever != null && lever.interact("Pull")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        // enter mage bank
        if (!MageArenaOneBranch.MAGE_ARENA_BANK.contains(Players.getLocal())) {
            if (!MageArenaOneBranch.MAGE_BANK_SWITCH.contains(Players.getLocal())) {
                log("walking to bank switch");
                Walking.walk(MageArenaOneBranch.MAGE_BANK_SWITCH.getCenter());
                return ReactionGenerator.getNormal();
            }

            GameObject lever = GameObjects.closest("Lever");
            if (lever != null && lever.interact("Pull")) {
                Sleep.sleepUntil(() -> MageArenaOneBranch.MAGE_ARENA_BANK.contains(Players.getLocal()), 2400);
            }
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }
}
