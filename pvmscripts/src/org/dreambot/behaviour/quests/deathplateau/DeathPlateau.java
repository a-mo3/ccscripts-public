package org.dreambot.behaviour.quests.deathplateau;

import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GoDoFractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

public class DeathPlateau extends Fractal {
    boolean talkedToSaba;
    boolean talkedToTenzing1;
    boolean talkToDunstan1;
    boolean talkToDenulthForDunstan;
    boolean talkToDunstan2;
    boolean talkedToTenzing2;
    boolean hasExplored;

    Tile EXPLORE = new Tile(2866, 3609);

    public DeathPlateau() {
        super(() -> !PaidQuest.DEATH_PLATEAU.isFinished());

        BankLocation.blacklist(BankLocation.WARRIORS_GUILD);
        setSimpleName("Death Plateau");
        this.paintArraySupplier = () -> new String[]{
                "State: " + questState(),
                "Saba " + talkedToSaba,
                "Tenzing 1 " + talkedToTenzing1 + " 2 " + talkedToTenzing2,
                "Dunstan 1 " + talkToDunstan1,
                "Denu for Dun " + talkToDenulthForDunstan,
                "explored " + hasExplored,
                ""
        };

        addChildren(
                new TalkToFractal(() -> questState() == 0,
                        new Tile(2896, 3529),
                        () -> NPCs.closest("Denulth"))
                        .setDialogueOptions("quests for me?", "Yes.")
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 1500)
                                .addItem(ItemID.TROUT, 10)
                                .addItem(ItemID.BREAD, 10)
                                .addItem(ItemID.IRON_BAR)
                                .addItem(ItemID.ASGARNIAN_ALE)
                                .addItem(ItemID.BLURBERRY_SPECIAL)
                                .setStrict(true)
                        )
                        .setSimpleName("Denulth start"),

                new TalkToFractal(() -> questState() == 10 || questState() == 30,
                        new Tile(2900, 3566, 1),
                        () -> NPCs.closest("Eohric"))
                        .setDialogueOptions("guard that was")
                        .setSimpleName("Eohric"),

                new TalkToFractal(() -> questState() == 20 || questState() == 40,
                        new Tile(2906, 3540, 1),
                        () -> NPCs.closest("Harold"))
                        .setDialogueOptions("guard that was", "can I buy you a drink")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 1500)
                                .addItem(ItemID.TROUT, 10)
                                .addItem(ItemID.BREAD, 10)
                                .addItem(ItemID.IRON_BAR)
                                .addItem(ItemID.ASGARNIAN_ALE)
                                .addItem(ItemID.BLURBERRY_SPECIAL)
                                .setStrict(true)
                        )
                        .setSimpleName("Harold"),

                new TalkToFractal(() -> questState() == 50,
                        new Tile(2906, 3540, 1),
                        () -> NPCs.closest("Harold"))
                        .setDialogueOptionsSupplier(() -> Inventory.contains(ItemID.BLURBERRY_SPECIAL) ? new String[]{"drink"} : new String[]{"gamble?"})
                        .setAfterChat(() -> Dialogues.canEnterInput() || Widgets.isOpen())
                        .setSleepTimeout(2400)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 101, 1500)
                        )
                        .setPrependLogic(() -> {
                            if (Dialogues.canEnterInput()) {
                                Keyboard.type("101", true);
                                Sleep.sleepUntil(Widgets::isOpen, 4400);
                                return true;
                            }

                            if (Widgets.isOpen()) {
                                WidgetChild rollDice = Widgets.get(x -> x.getText().contains("Roll Dice!") && x.getParentID() == 99);
                                if (rollDice != null) {
                                    rollDice.interact();
                                    Sleep.sleep(3500);
                                }
                                return true;
                            }

                            return false;
                        })
                        .setSimpleName("Gamble"),

                new TalkToFractal(() -> questState() == 55,
                        new Tile(2900, 3566, 1),
                        () -> NPCs.closest("Eohric"))
                        .setPrependLogic(() -> {
                            if (!Dialogues.inDialogue()) {

                                Inventory.interact("Iou", "Read");
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("ReadIou"),

                new PlaceStones().setSimpleName("Place stones")
                        .setAcceptCondition(() -> questState() == 60),

                new Fractal(() -> questState() == 70)
                        .setSimpleName("Final steps")
                        .addChildren(
                                new TalkToFractal(() -> !Inventory.contains("Combination"),
                                        new Tile(2906, 3540, 1),
                                        () -> NPCs.closest("Harold"))
                                        .setDialogueOptions("guard that was", "can I buy you a drink")
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.COINS_995, 1500)
                                                .addItem(ItemID.TROUT, 10)
                                                .addItem(ItemID.BREAD, 10)
                                                .addItem(ItemID.IRON_BAR)
                                                .addItem(ItemID.CLIMBING_BOOTS)
                                                .addItem(ItemID.COMBINATION)
                                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.COMBINATION))
                                        )
                                        .setSimpleName("Harold (get new combination)"),
                                // once you get the secret map you do this
                                new Fractal().setAcceptCondition(() -> Inventory.contains("Secret way map"))
                                        .setSimpleName("Explore path")
                                        .addChildren(
                                                new TalkToFractal(() -> hasExplored,
                                                        new Tile(2896, 3529, 0),
                                                        () -> NPCs.closest("Denulth"))
                                                        .setSimpleName("Denulth")
                                                        .setPrependLogic(() -> {
                                                            String dialogue = Dialogues.getNPCDialogue();
                                                            if (dialogue != null && dialogue.contains("found the secret way up Death"))
                                                                talkToDenulthForDunstan = true;
                                                            return false;
                                                        }),
                                                new GoDoFractal(() -> true, EXPLORE, ReactionGenerator::getNormal)
                                                        .setPrependLogic(() -> {
                                                            if (EXPLORE.equals(Players.getLocal().getTile())) {
                                                                hasExplored = true;
                                                            }
                                                            return false;
                                                        })
                                                        .setSimpleName("Explore")

                                        ),

                                new Fractal(() -> !Inventory.contains(ItemID.SPIKED_BOOTS))
                                        .addChildren(
                                                new TalkToFractal(() -> !talkedToSaba,
                                                        new Tile(2270, 4757, 0),
                                                        () -> NPCs.closest("Saba"))
                                                        .setDialogueOptions("another way")
                                                        .setPrependLogic(() -> {
                                                            String dialogue = Dialogues.getNPCDialogue();
                                                            if (dialogue != null && dialogue.contains("pesky trolls yet?"))
                                                                talkedToSaba = true;
                                                            return false;
                                                        })
                                                        .setSimpleName("Saba"),

                                                new TalkToFractal(() -> !talkedToTenzing1,
                                                        new Tile(2820, 3555, 0),
                                                        () -> NPCs.closest("Tenzing"))
                                                        .setDialogueOptions("Ok, I'll get those for you.", "I'm alright thanks", "lost!")
                                                        .setSimpleName("Tenzing 1")
                                                        .setInventoryLoadout(new InventoryLoadout()
                                                                .addItem(ItemID.COINS_995, 1500)
                                                                .addItem(ItemID.TROUT, 10)
                                                                .addItem(ItemID.BREAD, 10)
                                                                .addItem(ItemID.IRON_BAR)
                                                                .addItem(ItemID.CLIMBING_BOOTS)
                                                                .addItem(ItemID.COMBINATION)
                                                        )
                                                        .setPrependLogic(() -> {
                                                            String dialogue = Dialogues.getNPCDialogue();
                                                            if (dialogue != null && (dialogue.contains("Has Dunstan added spikes") || dialogue.contains("Have you brought me the items")))
                                                                talkedToTenzing1 = true;
                                                            return false;
                                                        }),

                                                new TalkToFractal(() -> !talkToDunstan1,
                                                        new Tile(2920, 3573, 0),
                                                        () -> NPCs.closest("Dunstan"))
                                                        .setDialogueOptions("Ok, I'll get those for you.")
                                                        .setSimpleName("Dunstan 1")
                                                        .setPrependLogic(() -> {
                                                            String dialogue = Dialogues.getNPCDialogue();
                                                            if (dialogue != null && (dialogue.contains("Have you managed to get my son signed up") || dialogue.contains("you brought me the items")))
                                                                talkToDunstan1 = true;
                                                            return false;
                                                        }),

                                                new TalkToFractal(() -> !talkToDenulthForDunstan,
                                                        new Tile(2896, 3529, 0),
                                                        () -> NPCs.closest("Denulth"))
                                                        .setDialogueOptions("quest I am on")
                                                        .setSimpleName("Denulth cert")
                                                        .setPrependLogic(() -> {
                                                            String dialogue = Dialogues.getNPCDialogue();
                                                            log(dialogue);
                                                            if (dialogue != null && dialogue.contains("way up Death"))
                                                                talkToDenulthForDunstan = true;
                                                            return false;
                                                        }),

                                                new TalkToFractal(() -> !Inventory.contains(ItemID.SPIKED_BOOTS),
                                                        new Tile(2920, 3573, 0),
                                                        () -> NPCs.closest("Dunstan"))
                                                        .setDialogueOptions("spikes")
                                                        .setInventoryLoadout(new InventoryLoadout()
                                                                .addItem(ItemID.COINS_995, 1500)
                                                                .addItem(ItemID.TROUT, 10)
                                                                .addItem(ItemID.BREAD, 10)
                                                                .addItem(ItemID.IRON_BAR)
                                                                .addItem(ItemID.CLIMBING_BOOTS)
                                                                .addItem(ItemID.SPIKED_BOOTS)
                                                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.SPIKED_BOOTS))
                                                                .addItem(ItemID.COMBINATION)
                                                                .addItem(ItemID.CERTIFICATE_21775)
                                                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.CERTIFICATE_21775))
                                                        )
                                                        .setSimpleName("Dunstan 2")
                                        ).setSimpleName("Boots"),

                                new TalkToFractal(() -> !talkedToTenzing2,
                                        new Tile(2820, 3555, 0),
                                        () -> NPCs.closest("Tenzing"))
                                        .setDialogueOptions("alright thanks", "Ok, I'll get those for you.", "lost!")
                                        .setSimpleName("Tenzing 2")
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.COINS_995, 1500)
                                                .addItem(ItemID.TROUT, 10)
                                                .addItem(ItemID.BREAD, 10)
                                                .addItem(ItemID.SPIKED_BOOTS)
                                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.SPIKED_BOOTS))
                                                .addItem(ItemID.COMBINATION)
                                        )
                                        .setPrependLogic(() -> {
                                            String dialogue = Dialogues.getNPCDialogue();
                                            if (dialogue != null && dialogue.contains("123"))
                                                talkedToTenzing2 = true;
                                            return false;
                                        })
                        )
        );
    }

    private int questState() {
        return PaidQuest.DEATH_PLATEAU.getConfigValue();
    }
}
