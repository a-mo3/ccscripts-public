package org.dreambot.behaviour.quests;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.hint.HintArrow;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.training.thieving.GenericPickpocket;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.BuyFromShopFractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.VarbitRequirement;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;

public class TheFeud extends Fractal {
    public TheFeud() {
        super(() -> getState() < 9);

        // 315 -> 2 Talked to thug -> 3 Talked to bandit
        VarbitRequirement talkedToThug = new VarbitRequirement(315, 2);
        VarbitRequirement talkedToBandit = new VarbitRequirement(315, 1);

//        VarbitRequirement talkedToBanditReturn = new VarbitRequirement(316, 0); // Might have missed?
        // 340 -> 1 when pickpocket villager
        VarbitRequirement doorOpen = new VarbitRequirement(320, 1);


        paintArraySupplier = () -> new String[]{
                "State " + getState(),
                "Beer " + PlayerSettings.getBitValue(318),
                ""
        };

        setSimpleName("The Feud");
        addChildren(
                new TalkToFractal(
                        () -> getState() == 0,
                        new Tile(3304, 3211, 0),
                        () -> NPCs.closest("Ali Morrisane"))
                        .setDialogueOptions(
                                "really the",
                                "selling goods",
                                "like to help",
                                "find you your help",
                                "Yes.",
                                "Okay"
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 2000)
                        )
                        .setSimpleName("Start quest @ Ali & get disguise"),

                new Fractal(() -> !OwnedItems.contains(ItemID.DESERT_DISGUISE))
                        .setSimpleName("Get Disguise")
                        .addChildren(
                                new Fractal(() -> Inventory.containsAll(ItemID.FAKE_BEARD, ItemID.KHARIDIAN_HEADPIECE))
                                        .setPrependLogic(() -> {
                                            if (Widgets.isOpen()) {
                                                Widgets.closeAll();
                                            }
                                            Inventory.combine(ItemID.FAKE_BEARD, ItemID.KHARIDIAN_HEADPIECE);
                                            Sleep.sleepUntil(() -> OwnedItems.contains(ItemID.DESERT_DISGUISE), 4000);
                                            return true;
                                        })
                                        .setSimpleName("Make disguise"),

                                new BuyFromShopFractal(() -> !Inventory.contains(ItemID.FAKE_BEARD),
                                        "Ali Morrisane",
                                        new Tile(3304, 3211, 0).getArea(1),
                                        ItemID.FAKE_BEARD)
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.COINS_995, 500, 2000)
                                        )
                                        .setSimpleName("Buy Beard"),

                                new BuyFromShopFractal(() -> !Inventory.contains(ItemID.KHARIDIAN_HEADPIECE),
                                        "Ali Morrisane",
                                        new Tile(3304, 3211, 0).getArea(1),
                                        ItemID.KHARIDIAN_HEADPIECE)
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.COINS_995, 500, 2000)
                                        )
                                        .setSimpleName("Buy Headpiece")
                        ),

                new TalkToFractal(() -> Players.getLocal().getY() > 3117 && !Inventory.contains(ItemID.SHANTAY_PASS),
                        new Area(3296, 3132, 3310, 3118),
                        () -> NPCs.closest("Shantay"))
                        .setInteraction("Buy-pass")
                        // assumed you have correct items
                        .setSimpleName("Get a Shanty pass"),
                new BuyFromShopFractal(() -> getState() == 1 && PlayerSettings.getBitValue(318) < 3 && !Inventory.contains(ItemID.BEER) && !Dialogues.inDialogue(),
                        "Ali the Barman",
                        new Tile(3360, 2957).getArea(5),
                        ItemID.BEER)
                        .setPrependLogic(() -> {
                            if (Dialogues.inDialogue()) {
                                log("Talk to the drunk");
                                Dialog.solve("");
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Buy a bear"),

                new UseOnFractal(() -> getState() == 1,
                        () -> Inventory.get(ItemID.BEER),
                        () -> NPCs.closest("Drunken Ali"), true)
                        .setDialogueOptions("")
                        .setArea(new Tile(3360, 2957))
                        .setSleepCondition(Dialogues::inDialogue)
                        .setReturnAfterDialogues(true)
                        .setPrependLogic(() -> {
                            if (Inventory.contains(ItemID.BEER) && Shop.isOpen()) {
                                log("Close shop");
                                Widgets.closeAll();
                            }
                            return false;
                        })
                        .setSimpleName("Visit drunkard"),

                new Fractal(() -> getState() == 2)
                        .addChildren(
                                new TalkToFractal(talkedToBandit::isNotComplete,
                                        new Tile(3362, 2993),
                                        () -> NPCs.closest("Bandit"))
                                        .setDialogueOptions("thanks")
                                        .setSimpleName("Talk to Bandit"),

                                new TalkToFractal(talkedToThug::isNotComplete,
                                        new Tile(3347, 2955),
                                        () -> NPCs.closest("Menaphite thug"))
                                        .setDialogueOptions("thanks")
                                        .setSimpleName("Talk to thug")
                        )
                        .setSimpleName("Find Beef"),

                new TalkToFractal(() -> getState() == 3,
                        new Tile(3350, 2966),
                        () -> NPCs.closest("Ali the Camel Man"))
                        // todo consider inv loadout for 500+ gp
                        .setDialogueOptions("camels around", "price do you want", "500 gold")
                        .setSimpleName("Camelman"),

                new Fractal(() -> getState() == 4)
                        .addChildren(
                                new TalkToFractal(() -> Inventory.count("Receipt") == 2,
                                        new Tile(3362, 2993),
                                        () -> NPCs.closest("Bandit"))
                                        .setDialogueOptions("thanks")
                                        .setSimpleName("Talk to Bandit"),

                                new TalkToFractal(() -> true,
                                        new Tile(3347, 2955),
                                        () -> NPCs.closest("Menaphite thug"))
                                        .setDialogueOptions("thanks")
                                        .setSimpleName("Talk to thug")
                        )
                        .setSimpleName("Find Beef"),

                new TalkToFractal(() -> getState() == 5,
                        new Tile(3332, 2948, 0),
                        () -> NPCs.closest("Ali the operator"))
                        .setDialogueOptions("bandits should be taught")
                        .setSimpleName("Smoove operator"),

                new GenericPickpocket(() -> getState() == 6,
                        () -> NPCs.closest("Villager"),
                        new Tile(3356, 2962).getArea(5))
                        .setSimpleName("Pickpocket villager"),

                new TalkToFractal(() -> getState() == 7 || (getState() == 8 && HintArrow.getPointed() == null),
                        new Tile(3356, 2962).getArea(8),
                        () -> NPCs.closest("Street urchin"))
                        .setDialogueOptions("")
                        .setSimpleName("street urchin"),

                new GenericPickpocket(() -> getState() == 8,
                        () -> (NPC) HintArrow.getPointed(),
                        new Tile(3356, 2962).getArea(8))
                        .setSleepTime(5_000)
                        .setSimpleName("Pickpocket villager"),

                new TalkToFractal(() -> getState() == 9,
                        new Tile(3332, 2948, 0),
                        () -> NPCs.closest("Ali the operator"))
                        .setDialogueOptions("bit of advice")
                        .setSimpleName("Get blackjack")

        );
    }

    private static int getState() {
        return PaidQuest.THE_FEUD.getConfigValue();
    }
}
