package org.dreambot.behaviour.method.scurrius;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.NPCUtil;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * give spine + 50k + weapon to Historian Aldo
 */
public class GetSpineLamp extends Fractal {
    final Skill skill;

    public GetSpineLamp(Supplier<Boolean> acceptCondition, Skill skill) {
        super(() -> acceptCondition.get() && (skill.getLevel() > 50 || skill == Skill.PRAYER ));
        this.skill = skill;
        setSimpleName("Get bone weapon");
        this.eventBreakCondition = () -> Inventory.contains(ItemID.SCURRIUS_LAMP);
        this.setInventoryLoadout(new InventoryLoadout()
                .addItem(ItemID.SCURRIUS_SPINE, () -> Math.min(28, OwnedItems.count(ItemID.SCURRIUS_SPINE)))
                .setEnabledCondition(() -> OwnedItems.contains(ItemID.SCURRIUS_SPINE)));
    }

    @Override
    public int onLoop() {
        // walk to guy then talk to him about getting a bone weapon
        if (Inventory.contains(ItemID.SCURRIUS_LAMP) && !Dialogues.inDialogue()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            log("Interact with scurrius lamp");
            Inventory.interact(ItemID.SCURRIUS_LAMP);
            return ReactionGenerator.getNormal();
        }

        WidgetChild proceedWarning = Widgets.get(x -> x.hasAction("Proceed"));
        if (proceedWarning != null) {
            log("Proceeding...");
            proceedWarning.interact();
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue() && Inventory.contains(ItemID.SCURRIUS_LAMP)) {
            log("Dialogue handle for the lamp");
            log("lamp redeem");
            Dialog.solve(skill.getName(), "option");
            return ReactionGenerator.getNormal();
        }

        if (!GoToScurrius.SCURRIUS_GRATE.contains(Players.getLocal())) {
            log("Go to historian");
            if (Walking.shouldWalk()) Walking.walk(GoToScurrius.SCURRIUS_GRATE);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            log("Get lamp dialogue");
            Dialog.solve("Lamp", "Yes");
            return ReactionGenerator.getNormal();
        }

        // trade requires having handled a certain dialogue when making a weapon, talk to doesnt have a dialogue path to make a lamp only weaposn
        // combining should cover all types of accounts
        NPC aldo = NPCs.closest("Historian Aldo");
        Item spine = Inventory.get(ItemID.SCURRIUS_SPINE);
        if (aldo == null || spine == null) {
            log("Cant find spine or aldo");
            log(aldo + " ");
            log(spine + " ");
            return ReactionGenerator.getNormal();
        }
        log("Combine spine and aldo");
        spine.useOn(aldo);

//        NPCUtil.interact("Historian Aldo", "Trade");
        return ReactionGenerator.getNormal();
    }
}
