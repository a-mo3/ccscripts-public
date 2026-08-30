package org.dreambot.behaviour.training.farming.tithe;

import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * get watering cans, spade, dipper
 * get seeds
 * enter tithe farm instance
 */
public class EnterTitheFarm extends Fractal {
    public static final Area TITHE_ENTRANCE = new Tile(1802, 3501, 0).getArea(5);

    public EnterTitheFarm(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemVariants.WATERING_CAN, 8, 8)
                .addItem(ItemID.SPADE)
                .addItem(ItemID.SEED_DIBBER)
                .addItem(ItemVariants.STAMINA_POTION, 12, 12)
                .setRefill(35)
                .strictIgnore(ItemID.BOLOGANO_SEED, ItemID.GOLOVANOVA_SEED, ItemID.LOGAVANO_SEED)
                .setStrict(true)
        ;

        setSimpleName("Enter Tithe");
    }


    @Override
    public int onLoop() {
        if (!TITHE_ENTRANCE.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(TITHE_ENTRANCE);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Logger.info("Dialigue");
            Dialog.solve("expert");
        }

        if (Inventory.count(getSeedID()) < 1000) {
            if (Dialogues.areOptionsAvailable()) {
                if (Skills.getRealLevel(Skill.FARMING) >= 54) {
                    Logger.info(Skills.getRealLevel(Skill.FARMING) + " Bologano");
                    Dialog.solve("expert ", "Bologano");
                    return ReactionGenerator.getNormal();
                }

                Dialog.solve("Golovanova");
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.canEnterInput()) {
                Keyboard.type("10k", true);
                Sleep.sleepUntil(() -> Inventory.contains(getSeedID()), 4440);
                return ReactionGenerator.getNormal();
            }

            Logger.info("get more seeds");
            ObjectUtil.interact("Seed table", "Search");
            Sleep.sleepUntil(Dialogues::inDialogue, 4400);
            return ReactionGenerator.getNormal();
        }

        // Enter tithe
        if (Dialogues.inDialogue()) return ReactionGenerator.getNormal();
        if (ObjectUtil.interact("Farm door", "Open")) {
            Sleep.sleepUntil(Client::isDynamicRegion, 4400);
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }

    private int getSeedID() {
        return Skills.getRealLevel(Skill.FARMING) < 54 ? ItemID.GOLOVANOVA_SEED : ItemID.BOLOGANO_SEED;
    }
}
