package org.dreambot.behaviour.dragons;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.script.StaffMode;
import org.dreambot.settings.timing.ReactionGenerator;

public class RechargeSceptre extends Fractal implements ChatListener {

    public static boolean shouldRecharge;
    Timer t = new Timer(3 * 1000 * 60);

    public RechargeSceptre() {
        this.eventBreakCondition = () -> t.finished() || !Worlds.getCurrent().isMembers() || Skills.getRealLevel(Skill.MAGIC) < 70
                || (Inventory.emptySlotCount() > 0 && !Equipment.isSlotEmpty(EquipmentSlot.WEAPON));
        Client.getInstance().addEventListener(this);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemVariants.SCEPTRE)
                .addItem(ItemID.REVENANT_ETHER, 1300).setEnabledCondition(() -> !OwnedItems.contains(ItemID.ACCURSED_SCEPTRE) || Inventory.contains(ItemID.ACCURSED_SCEPTRE_U))
                .addItem(ItemID.REVENANT_ETHER, 300).setEnabledCondition(() -> OwnedItems.contains(ItemID.ACCURSED_SCEPTRE))
                .setMuleRequestAmount(LivePrices.get(ItemID.REVENANT_ETHER) * 1500)
        ;

        this.appendLogic = () -> {
            if (Inventory.emptySlotCount() > 0 && !Equipment.isSlotEmpty(EquipmentSlot.WEAPON)) {
                if (Widgets.isOpen()) {
                    Widgets.closeAll();
                    return true;
                }

                Equipment.unequip(EquipmentSlot.WEAPON);
                return true;
            }

            return false;
        };
    }

    @Override
    public boolean isValid() {
        return !Combat.isInWild() && Bank.isCached() && Skills.getRealLevel(Skill.MAGIC) >= 70
                && ScriptSettings.getSettingsData().staffMode == StaffMode.SCEPTRE
                && (!OwnedItems.contains(ItemID.ACCURSED_SCEPTRE) || shouldRecharge);
    }

    @Override
    public int onLoop() {
        // if not recharging sceptre_u you only need like 300
        if (Inventory.contains(ItemID.ACCURSED_SCEPTRE) && Inventory.count(ItemID.REVENANT_ETHER) > 350) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getQuick();
            }

            Bank.deposit(ItemID.REVENANT_ETHER, 1000);
            Sleep.sleepUntil(() -> Inventory.count(ItemID.REVENANT_ETHER) < 350, 2400);
            return ReactionGenerator.getNormal();
        }

        if (GrandExchange.isOpen() || Bank.isOpen()) {
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }

        Item s = ItemVariants.SCEPTRE.getItem();
        Item e = Inventory.get(ItemID.REVENANT_ETHER);
        if (e == null || s == null) {
            t.reset();
            return ReactionGenerator.getLong();
        }
        if (s.useOn(e)) {
            shouldRecharge = false;
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("sceptre is out of charges!")) {
            shouldRecharge = true;
        }
    }
}
