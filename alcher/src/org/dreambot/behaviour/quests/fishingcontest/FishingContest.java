package org.dreambot.behaviour.quests.fishingcontest;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;

import java.util.function.Supplier;

public class FishingContest extends Fractal {
    final Area VESTRI_HOUSE = new Area(2823, 3485, 2818, 3487);
    final Supplier<Entity> VESTRI_SUP = () -> NPCs.closest("Vestri");

    final Area WORM_VINE = new Area(2630, 3498, 2634, 3495);
    final Supplier<Entity> VINE_SUP = () -> GameObjects.closest(x -> x.hasAction("Check")
            && x.getName().equalsIgnoreCase("Vine"));

    final Tile JACK_TILE = new Tile(2649, 3451, 0);
    final Supplier<Entity> JACK_SUP = () -> NPCs.closest("Grandpa jack");

    final Area PIPE_AREA = new Area(2638, 3445, 2639, 3444);
    final Supplier<Entity> PIPE_SUP = () -> GameObjects.closest(x ->
            x.getTile().equals(new Tile(2638, 3446, 0)) && x.getName().equalsIgnoreCase("Wall pipe"));

    final Area BONZO_AREA = new Area(2637, 3445, 2642, 3434);
    final Supplier<Entity> BONZO_SUP = () -> NPCs.closest("Bonzo");

    final Area MCGRUBORS_GATE = new Area(2641, 3476, 2654, 3470);

    public FishingContest() {
        super(() -> !PaidQuest.FISHING_CONTEST.isFinished());

        this.setSimpleName("Fishing Contest");
        this.paintArraySupplier = () -> new String[]{
                "State: " + PaidQuest.FISHING_CONTEST.getConfigValue()
        };

        Timer eatTimer = new Timer(1500);
        addChildren(
                new Fractal(Client::isInCutscene).setSimpleName("cutscene"),
                new TalkToFractal(() -> !PaidQuest.FISHING_CONTEST.isStarted(), VESTRI_HOUSE, VESTRI_SUP)
                        .setDialogueOptions(
                                "I was wondering what was down those stairs?",
                                "Why not?",
                                "If you were my friend I wouldn't mind it.",
                                "Well, let's be friends!",
                                "And how am I meant to do that?",
                                "Yes."
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.FISHING_ROD).setBuyPrice(1000)
                                .addItem(ItemID.GARLIC).setBuyPrice(1000)
                                .addItem(ItemID.SPADE).setBuyPrice(4000)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.SALMON, 6)
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5)
                                .addItem(ItemID.COINS_995, 5, 25)
                        )
                        .setSimpleName("Start fishing contest"),
                new TalkToFractal(() -> questState() == 1 && Inventory.count(ItemID.RED_VINE_WORM) < 3, WORM_VINE, VINE_SUP)
                        .setInteraction("Check")
                        .setSimpleName("Get worms")
                        .setInventoryLoadout(
//                                ScriptSettings.getSettingsData().rebuyLoadout ?
                                new InventoryLoadout()
                                .addItem(ItemID.FISHING_ROD).setBuyPrice(1000)
                                .addItem(ItemID.GARLIC).setBuyPrice(1000)
                                .addItem(ItemID.SPADE).setBuyPrice(4000)
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.SALMON, 6)
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5)
                                .addItem(ItemID.COINS_995, 5, 25)
                        )
                        .setAppendLogic(() -> {
                            if (Players.getLocal().isInCombat() && Walking.getRunEnergy() > 1) {
                                if (!Walking.isRunEnabled()) Walking.toggleRun();
                            }

                            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 6 && Inventory.contains(ItemID.SALMON)) {
                                if (eatTimer.finished()) {
                                    Inventory.interact(ItemID.SALMON, "Eat");
                                    eatTimer.reset();
                                }
                            }
                            return false;
                        }),

                new UseOnFractal(() -> questState() == 1 && Inventory.contains(ItemID.GARLIC),
                        () -> Inventory.get(ItemID.GARLIC), PIPE_SUP, true)
                        .setArea(PIPE_AREA)
                        .setDialogueOptions(
                                "Can I buy one of your fishing rods?",
                                "Very fair, I'll buy that rod!"
                        )
                        .setAppendLogic(() -> {
                            if (MCGRUBORS_GATE.contains(Players.getLocal())) {
                                if (Dialogues.inDialogue()) {
                                    Dialog.solve("Ranging Guild");
                                    Sleep.sleepUntil(() -> !MCGRUBORS_GATE.contains(Players.getLocal()), 6400);
                                }

                                if (ItemVariants.COMBAT_BRACLET.interact("Rub")) {
                                    Sleep.sleepUntil(Dialogues::inDialogue, 4400);
                                    return true;
                                }
                                return true;
                            }

                            if (Dialogues.inDialogue()) {
                                Logger.info("pre check dialogue");
                                Dialog.solve(
                                        "Can I buy one of your fishing rods?",
                                        "Very fair, I'll buy that rod!",
                                        "Nowhere"
                                );
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Garlic up the pipe"),
                new TalkToFractal(() -> questState() == 1, BONZO_AREA, BONZO_SUP)
                        .setInteraction("Pay")
                        .setAppendLogic(() -> {
                            if (Inventory.isItemSelected()) Inventory.deselect();
                            return false;
                        })
                        .setSimpleName("Pay bonzo to start quest"),

                new FishingFractal(() -> questState() == 3 && Inventory.contains(ItemID.RED_VINE_WORM),
                        BONZO_AREA, () -> NPCs.closest(x -> BONZO_AREA.contains(x) && x.hasAction("Bait")))
                        .setSimpleName("Fish"),

                new TalkToFractal(() -> questState() == 3, BONZO_AREA, BONZO_SUP)
                        .setDialogueOptions("big fish.")
                        .setInteraction("Talk-to")
                        .setSimpleName("Finish contest"),

                new TalkToFractal(() -> questState() == 4, VESTRI_HOUSE, VESTRI_SUP)
                        .setSimpleName("Finish quest")

        );
    }

    private int questState() {
        return PaidQuest.FISHING_CONTEST.getConfigValue();
    }
}
