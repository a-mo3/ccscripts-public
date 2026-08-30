package org.dreambot.behaviour.training.quests.animalmagnetism.behaviour.undeadchickens;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.training.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

public class GetEctoTokens extends Fractal {
    private final int ECTO_TOKENS_BIT = 4769;
    InventoryLoadout reqs = new InventoryLoadout()
            .addItem(ItemID.BUCKET, 4)
            .setEnabledCondition(() -> !Inventory.contains(ItemID.BUCKET_OF_SLIME))
            .addItem(ItemID.BONES, 4)
            .setEnabledCondition(() -> !Inventory.contains(ItemID.BONEMEAL))
            .addItem(ItemID.POT, 4)
            .setEnabledCondition(() -> !Inventory.contains(ItemID.BONEMEAL))
            .addItem(ItemID.GHOSTSPEAK_AMULET)
            .addItem(ItemVariants.STAMINA_POTION, 1, 4);

    EquipmentLoadout teleports = new EquipmentLoadout()
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET);


    @Override
    public boolean isValid() {
        return PaidQuest.ANIMAL_MAGNETISM.getConfigValue() == 10 && Inventory.count(ItemID.ECTOTOKEN) < 20;
    }

    @Override
    public int onLoop() {
        if (Inventory.count(ItemID.ECTOTOKEN) >= 20) {
            return ReactionGenerator.getNormal();
        }

        if (PlayerSettings.getBitValue(ECTO_TOKENS_BIT) >= 20) {
            if (Inventory.contains(ItemID.GHOSTSPEAK_AMULET)) {
                Inventory.interact(ItemID.GHOSTSPEAK_AMULET, "Wear");
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                Dialog.solve();
                //Dialogues.solve();
                return ReactionGenerator.getNormal();
            }

            NPC ghost = NPCs.closest("Ghost disciple");
            if (ghost != null && ghost.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        if (!reqs.isFulfilled() || !teleports.isFulfilled()) {
            Log.info("walking to withdraw loadout");
            new WithdrawLoadoutEvent(reqs, teleports)
                    .setBuyRemainder(true)
                    .executed();
            return ReactionGenerator.getNormal();
        }

        if (Inventory.count(ItemID.BONEMEAL) == Inventory.count(ItemID.BUCKET_OF_SLIME) && Inventory.contains(ItemID.BONEMEAL)) {
            Log.info("exit funtus");
            // go pray at funtus
            if (SpecialWalker.exitFuntus()) {
                GameObject ectofuntus = GameObjects.closest("Ectofuntus");
                if (ectofuntus != null && ectofuntus.interact("Worship")) {
                    Sleep.sleep(550, 700); // you can spam click to go fast
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        // get ecto tokens
        if (Inventory.count(ItemID.BONEMEAL) >= 4) {
            Log.info("Has bonemeal");
            if (Inventory.count(ItemID.BUCKET_OF_SLIME) < 4) {
                if (SpecialWalker.enterSlimeRoom()) {
                    GameObject slime = GameObjects.closest(x -> x.getID() == 17119 && x.canReach());
                    Item bucket = Inventory.get(ItemID.BUCKET);
                    if (bucket.useOn(slime)) {
                        Sleep.sleepUntil(() -> Inventory.count(ItemID.BUCKET_OF_SLIME) >= 4, 5000);
                        return ReactionGenerator.getNormal();
                    }
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        if (SpecialWalker.enterMorytania()) {
            if (!SpecialWalker.ECTOFUNTUS.contains(Players.getLocal())) {
                Log.info("Walking to ectofuntus");
                if (Walking.shouldWalk(8)) Walking.walk(SpecialWalker.ECTOFUNTUS.getCenter());
                return ReactionGenerator.getNormal();
            }

            GameObject boneGrinder = GameObjects.closest("Loader");
            Item bone = Inventory.get(ItemID.BONES);
            if (bone.useOn(boneGrinder)) {
                Sleep.sleepUntil(() -> Inventory.count(ItemID.BONEMEAL) >= 4,
                        () -> Players.getLocal().isAnimating(),
                        10_000,
                        100);
                return ReactionGenerator.getNormal();

            }
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
