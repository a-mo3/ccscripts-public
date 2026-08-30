package org.dreambot.behaviour.training.magic;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;

import java.util.function.Supplier;

public class MagicBranch extends Fractal {
    public MagicBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        addChildren(
                new ImpCatcher().setSimpleName("Impcatcher")
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                            }
                            return false;
                        }),
                new EnchantRecoils().setSimpleName("Enchant Recoils "),
                new EnchantDueling().setSimpleName("Enchant Duelings "),
                new AlchSomething(() -> true)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.RUNE_ARROW, 1, 2000)
                                .addItem(ItemID.NATURE_RUNE, 1, 2000)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                        )
                        .setSimpleName("Alch rune arrows")
        );
    }
}
