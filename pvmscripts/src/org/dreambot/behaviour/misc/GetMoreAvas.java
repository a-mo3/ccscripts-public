package org.dreambot.behaviour.misc;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetMoreAvas extends Fractal {
    public GetMoreAvas(Supplier<Boolean> acceptCondition) {
        super(() -> acceptCondition.get() && GetMoreAvas.shouldGetMore());

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.COINS_995, 200_000)
                .addItem(ItemID.STEEL_ARROW, 75 * 20)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 50)
        ;
    }

    public GetMoreAvas() {
        super(GetMoreAvas::shouldGetMore);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.COINS_995, 200_000)
                .addItem(ItemID.STEEL_ARROW, 75 * 20)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 50)

        ;
    }

    public static boolean shouldGetMore() {
        if (!Bank.isCached()) return false;
        if (!PaidQuest.ANIMAL_MAGNETISM.isFinished()) return false;
        return !OwnedItems.containsAny(ItemID.AVAS_ASSEMBLER, ItemID.AVAS_ATTRACTOR, ItemID.AVAS_ACCUMULATOR) && PaidQuest.ANIMAL_MAGNETISM.isFinished();
    }

    @Override
    public int onLoop() {
        if (!SpecialWalker.enterAvasRoom()) {
            return ReactionGenerator.getNormal();
        }

        if (!Widgets.isOpen()) {
            NPC ava = NPCs.closest("Ava");
            if (ava != null && ava.interact("Devices")) {
                Antiban.sleepUntil(Widgets::isOpen, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        for (int i = 0; i < 20; i++) {
            if (!Inventory.contains(ItemID.COINS_995)) {
                return ReactionGenerator.getNormal();
            }

            WidgetChild wc = Widgets.get(67, Skills.getRealLevel(Skill.RANGED) < 50 ? 2 : 7);
            if (wc != null && wc.interact()) {
                Sleep.sleep(200, 800);
            }
        }
        return ReactionGenerator.getNormal();
    }
}
