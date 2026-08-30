package org.dreambot.behaviour.quests.merlinscrystal;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

import java.util.concurrent.atomic.AtomicBoolean;

public class MerlinsCrystal extends Fractal {
    public MerlinsCrystal() {
        super(() -> !PaidQuest.MERLINS_CRYSTAL.isFinished());
        setSimpleName("Merlins crystal");

        AtomicBoolean hasSpokenToLadyOfTheLake = new AtomicBoolean(false);
        AtomicBoolean hasReadSpell = new AtomicBoolean(true);
        paintArraySupplier = () -> new String[]{
                "State " + PaidQuest.MERLINS_CRYSTAL.getConfigValue(),
                "Lake " + hasSpokenToLadyOfTheLake.get(),
                "Spell " + hasReadSpell
        };

        Tile ghostStar = new Tile(2780, 3515, 0);

        addChildren(
                new TalkToFractal(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 0,
                        new Tile(2763, 3513, 0),
                        () -> NPCs.closest("King Arthur"))
                        .setDialogueOptions("Yes.", "I want to become a Knight of the Round Table!")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.BUCKET_OF_WAX)
                                .addItem(ItemID.BAT_BONES)
                                .addItem(ItemID.TINDERBOX)
                                .addItem(ItemID.BREAD)
                                .addItem(ItemID.SHARK, 6)
                                .addItem(ItemID.FALADOR_TELEPORT, 3, 5)
                                .addItem(ItemID.CAMELOT_TELEPORT, 3, 5)
                                .addItem(ItemID.VARROCK_TELEPORT, 3, 5)
                        )
                        // todo equip a sword
                        .setSimpleName("Start at king arthur"),

                new TalkToFractal(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 1,
                        new Tile(2758, 3504, 0),
                        () -> NPCs.closest("Sir Gawain"))
                        .setDialogueOptions("Do you know how")
                        .setSimpleName("Talk to Gawain"),

                new TalkToFractal(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 2,
                        new Tile(2760, 3511, 1),
                        () -> NPCs.closest("Sir Lancelot"))
                        .setDialogueOptions("Any ideas", "Thank you")
                        .setSimpleName("Talk to Lancelot"),

//                new TalkToFractal(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 3,
//                        new Tile(2801, 3442, 0),
//                        () -> GameObjects.closest(63))
//                        .setInteraction("Hide-in")
//                        .setDialogueOptions("Yes")
//                        // todo equip a sword theres combat after this, its weak and we do this on high lvl account though
//                        .setSimpleName("Hide in crate")
                // todo this
                new KillSirMordred(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 3),


                new TalkToFractal(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 4
                        && !OwnedItems.contains(ItemVariants.BLACK_CANDLE),
                        new Tile(2797, 3440, 0),
                        () -> NPCs.closest("Candle maker"))
                        .setDialogueOptions("black")
                        .setSimpleName("Get black candle"),

                new TalkToFractal(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 4
                        && !hasSpokenToLadyOfTheLake.get()
                        && !OwnedItems.contains(ItemID.EXCALIBUR),
                        new Tile(2924, 3404, 0),
                        () -> NPCs.closest("The Lady of the Lake"))
                        .setDialogueOptions("seek the sword")
                        .setPrependLogic(() -> {
                            String chat = Dialogues.getNPCDialogue();
                            if (chat != null && chat.contains("I shall set a test for you."))
                                hasSpokenToLadyOfTheLake.set(true);
                            return false;
                        })
                        .setSimpleName("Talk to lady of the lake"),

                new TalkToFractal(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 4
                        && !OwnedItems.contains(ItemID.EXCALIBUR),
                        new Tile(3016, 3246, 0),
                        () -> GameObjects.closest(59))
                        .setDialogueOptions("Yes")
                        .setInteraction("Open")
                        .setSimpleName("Give beggar bread"),

                new TalkToFractal(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 4
                        && !hasReadSpell.get(),
                        new Tile(3260, 3381, 0),
                        () -> GameObjects.closest(61))
                        .setDialogueOptions("Yes")
                        .setInteraction("Check")
                        .setPrependLogic(() -> {
                            if (Widgets.get(229, x -> x.getText().contains("Snarthon Candtrick Termanto")) != null)
                                hasReadSpell.set(true);
                            return false;
                        })
                        .setSimpleName("Read spell"),

                new TalkToFractal(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 4,
                        ghostStar.getArea(1),
                        () -> null)
                        .setDialogueOptions("Snarthon Candtrick Termanto")
                        .setPrependLogic(() -> {
                            if (Inventory.containsAll(ItemID.TINDERBOX, ItemID.BLACK_CANDLE)) {
                                log("Light black candle");
                                Inventory.combine(ItemID.TINDERBOX, ItemID.BLACK_CANDLE);
                                return true;
                            }

                            if (ghostStar.equals(Players.getLocal().getTile())) {
                                Inventory.drop(ItemID.BAT_BONES);
                                Sleep.sleepUntil(Dialogues::inDialogue, 3600);
                            }
                            return false;
                        })
                        .setSimpleName("Go minge ghost"),

                new UseOnFractal(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 5,
                        () -> Inventory.get(ItemID.EXCALIBUR), () -> GameObjects.closest(62), true)
                        .setArea(new Tile(2768, 3494, 2))
                        .setSimpleName("Free the real"),

                new TalkToFractal(() -> PaidQuest.MERLINS_CRYSTAL.getConfigValue() == 6,
                        new Tile(2763, 3513, 0),
                        () -> NPCs.closest("King Arthur"))
                        .setDialogueOptions("Yes.", "I want to become a Knight of the Round Table!")
                        .setSimpleName("finish at king arthur")

        );

    }
}
