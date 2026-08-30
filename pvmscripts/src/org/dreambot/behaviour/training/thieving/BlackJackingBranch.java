package org.dreambot.behaviour.training.thieving;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

import java.util.function.Supplier;

public class BlackJackingBranch extends Fractal {
    public BlackJackingBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Blackjacking");
        Area pollnivneach = new Area(3331, 3010, 3379, 2937);

        addChildren(
                // todo get a shanty pass if above Y 3117
                new TalkToFractal(() -> Players.getLocal().getY() >= 3117 && !Inventory.contains(ItemID.SHANTAY_PASS),
                        new Area(3296, 3132, 3310, 3118),
                        () -> NPCs.closest("Shantay"))
                        .setInteraction("Buy-pass")
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.WEAPON, ItemID.MAPLE_BLACKJACK)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.JUG_OF_WINE + 1, 500)
                                .addItem(ItemID.SHANTAY_PASS)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.SHANTAY_PASS))
                                .addItem(ItemID.COINS_995, 2000))
                        .setSimpleName("Get a Shanty pass"),

                new UseOnFractal(() -> !Inventory.contains(ItemID.JUG_OF_WINE) && pollnivneach.contains(Players.getLocal()),
                        () -> Inventory.get(ItemID.JUG_OF_WINE + 1),
                        () -> NPCs.closest("Banknote Exchange Merchant"), true)
                        .setArea(new Tile(3360, 2989))
                        .setDialogueOptions("Yes", "All")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.JUG_OF_WINE + 1, 500)
                                .setEnabledCondition(() -> !Inventory.contains(ItemID.JUG_OF_WINE + 1))
                        )
                        .setPrependLogic(() -> {
                            Inventory.dropAll(ItemID.JUG);
                            return false;
                        })
                        .setSimpleName("Unnote wines"),

                // bandits @ 45
                new BlackJackBandit(() -> Skills.getRealLevel(Skill.THIEVING) < 65)
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.WEAPON, ItemID.MAPLE_BLACKJACK)
                        )
                        .setSimpleName("BJ Bandits ;)"),

                // menaphite thugs @ 65
                new BlackJackThug(() -> true)
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.WEAPON, ItemID.MAPLE_BLACKJACK)
                        )
                        .setSimpleName("BJ Thugs ;)")
        );

    }
}
