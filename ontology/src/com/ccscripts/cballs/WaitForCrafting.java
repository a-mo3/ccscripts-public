package com.ccscripts.cballs;

import com.ccscripts.cballs.framework.ItemID;
import com.ccscripts.cballs.framework.ScriptNode;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.script.event.impl.ExperienceEvent;
import org.dreambot.api.script.listener.ExperienceListener;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;

import java.awt.*;
import java.util.List;

/**
 * accepts if have gained experience recently, cant really infer sleep untils which is why im doing this
 */
public class WaitForCrafting extends ScriptNode implements ExperienceListener {
    Timer waitCooldown = new Timer(0);

    public WaitForCrafting() {
        Client.getInstance().addEventListener(this);
    }

    @Override
    public boolean isValid() {
        // when you bank its possible to transition back to waiting instead of furnace if ur not afk enough
        if (Bank.isOpen() || !Inventory.contains(ItemID.STEEL_BAR)) {
            waitCooldown.setRunTime(-10);
        }
        if (ItemProcessing.isOpen()) {
            waitCooldown.reset();
            waitCooldown.setRunTime(8_000);
        }
        return !ItemProcessing.isOpen() && !waitCooldown.finished();
    }

    @Override
    public int fallBack() {
        return 0;
    }

    @Override
    public String getIdentifier() {
        return "Waiting";
    }

    @Override
    public String getExpectedNextState() {
        return "OpenBank";
    }

    @Override
    public void onGained(ExperienceEvent event) {
        if (event.getSkill() == Skill.SMITHING) waitCooldown.reset();
        waitCooldown.setRunTime(8_000);
    }

    protected void sleepAfterReplay() {
        Sleep.sleepUntil(() -> !isValid(), 80_000);
    }

    @Override
    public List<Rectangle> trainingHighlights() {
        return List.of();
    }

    @Override
    protected boolean shouldConfigureReproducers() {
        return false;
    }
}
