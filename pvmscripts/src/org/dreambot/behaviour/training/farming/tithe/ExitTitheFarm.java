package org.dreambot.behaviour.training.farming.tithe;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

public class ExitTitheFarm extends Fractal {
    public static boolean canBuyHerbSack() {
        return PlayerSettings.getBitValue(4893) >= 250 && Skills.getRealLevel(Skill.HERBLORE) >= 58;
    }

    @Override
    public boolean isValid() {
        return canBuyHerbSack() || (Client.isDynamicRegion() && (ItemVariants.STAMINA_POTION.getItem() == null));
    }

    @Override
    public int onLoop() {
        if (!Client.isDynamicRegion()) {
            if (!EnterTitheFarm.TITHE_ENTRANCE.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(EnterTitheFarm.TITHE_ENTRANCE);
                return ReactionGenerator.getNormal();
            }

            if (Widgets.isOpen()) {
                Logger.info("Getting herb sack");
                WidgetChild herbSackChild = Widgets.get(x -> x.getName().contains("Herb sack") && x.hasAction("Buy"));
                if (herbSackChild != null) {
                    Logger.info("Buy");
                    herbSackChild.interact("Buy");
                }
                Sleep.sleepUntil(() -> OwnedItems.contains(ItemID.HERB_SACK), 4400);
                return ReactionGenerator.getNormal();
            }

            Logger.info("Getting herb sack open rewards");
            NPC farmer = NPCs.closest(x -> x.hasAction("Rewards"));
            if (farmer != null) {
                farmer.interact("Rewards");
                Sleep.sleepUntil(Widgets::isOpen, 4400);
            }
            return ReactionGenerator.getNormal();
        }

        // if you have fruit, deposit them
        if (Inventory.contains(x -> x.getName().contains("fruit"))) {
            ObjectUtil.interact("Sack");
            Sleep.sleepUntil(() -> !Inventory.contains(x -> x.getName().contains("fruit")), 4400);
            return ReactionGenerator.getNormal();
        }

        // exit
        ObjectUtil.interact("Farm door");
        ObjectUtil.interact("Portal"); // if you are in house
        Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 4400);
        return ReactionGenerator.getNormal();
    }
}
