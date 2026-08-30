package org.dreambot.behaviour.training.thieving;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/*
uses black jacks on thugs, black thugs jack, jacking black thuuugs
 */
public class BlackJackBandit extends Fractal implements ChatListener {
    Area BANDIT_HOUSE = new Area(3365, 3000, 3363, 3003);
    boolean escapeCombat = false;
    int forceHop = -1;

    public BlackJackBandit(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        Client.getInstance().addEventListener(this);

        this.paintArraySupplier = () -> {
            NPC beardedBandit = NPCs.closest(x -> BANDIT_HOUSE.contains(x) && x.getName().equals("Bandit") && x.getLevel() == 41);
            if (beardedBandit == null) return new String[0];
            return new String[]{
                    "Ani " + beardedBandit.getAnimation(),
                    "H " + beardedBandit.getRenderableHeight(),
                    "hidden " + hidden.elapsed()
            };
        };
    }

    @Override
    public int onLoop() {
        if (Players.getLocal().getZ() != 0 && Players.getLocal().getY() < 3005) {
            // probably on top roof when resetting combat
            log("Climb down");
            escapeCombat = false;
            GameObject ladder = GameObjects.closest(x -> x.hasAction("Climb-down"));
            if (ladder != null) ladder.interact();
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.contains(ItemID.JUG_OF_WINE)) {
            if (!Inventory.contains(ItemID.JUG_OF_WINE + 1)) {
                log("No noted jugs left");

                new WithdrawLoadoutEvent(new InventoryLoadout().addItem(ItemID.JUG_OF_WINE + 1, 500), null).executed();
                return ReactionGenerator.getNormal();
            }

        }

        if (forceHop > 0 && forceHop != Worlds.getCurrentWorld()) forceHop = -1;
        if (!BANDIT_HOUSE.contains(Players.getLocal())) {
            log("Walk to bank house");
            if (Walking.shouldWalk()) Walking.walk(BANDIT_HOUSE);
            return ReactionGenerator.getNormal();
        }

        if (escapeCombat) {
            log("Needs to escape combat");
            GameObject ladder = GameObjects.closest(x -> x.hasAction("Climb-up"));
            if (ladder != null) ladder.interact();
            return ReactionGenerator.getNormal();
        }

        if (Combat.getHealthPercent() < 50 || Skills.getBoostedLevel(Skill.HITPOINTS) <= 7) {
            log("Drink wine");
            Inventory.interact(ItemID.JUG_OF_WINE);
        }

        NPC beardedBandit = NPCs.closest(x -> BANDIT_HOUSE.contains(x) && x.getName().equals("Bandit") && x.getLevel() == 41
                && (x.hasAction("Knockout") || x.hasAction("Pickpocket"))
        );
        if (beardedBandit == null || forceHop == Worlds.getCurrentWorld()) {
            if (Players.getLocal().isInCombat()) {
                log("Needs to escape combat to hop");
                escapeCombat = true;
                return ReactionGenerator.getNormal();
            }

            log("No bandit found, hopping to another world to find one");
            WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isMembers()
                    && x.getMinimumLevel() < Skills.getTotalLevel()
                    && x.isNormal() && x.getWorld() != 401));
            return 12_000;
        }

        if (Inventory.isItemSelected()) Inventory.deselect();
        if (beardedBandit.getRenderableHeight() < 100 || !hidden.finished()) {
            // knocked out
            log("Pickpocket");
            beardedBandit.interact("Pickpocket");
            return 1200;
        } else {
            log("knockout");
            beardedBandit.interact("Knock-out");
            return 120;
        }
    }

    Timer hidden = new Timer(1000);

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message == null || message.getMessage() == null) return;
        if (message.getMessage().toLowerCase().contains("during combat")) {
            escapeCombat = true;
        }

        if (message.getMessage().toLowerCase().contains("will see me")) {
            log("2 thugs in a room");
            forceHop = Worlds.getCurrentWorld();
        }
        if (message.getMessage().contains("can't do that")) hidden.reset();
    }
}
