package org.dreambot.behaviour.training.thieving;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
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
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/*
uses black jacks on thugs, black thugs jack, jacking black thuuugs
 */
public class BlackJackThug extends Fractal implements ChatListener {
    Area BANDIT_HOUSE = new Area(3365, 3000, 3363, 3003);
    Area THUG_HOUSE = new Area(3340, 2956, 3344, 2953);
    boolean escapeCombat = false;
    int forceHop = -1;

    public BlackJackThug(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        Client.getInstance().addEventListener(this);

        this.paintArraySupplier = () -> {
            NPC beardedBandit = NPCs.closest(x -> THUG_HOUSE.contains(x) && x.getName().equals("Menaphite Thug"));
            if (beardedBandit == null) return new String[0];
            return new String[]{
                    "Ani " + beardedBandit.getAnimation(),
                    "H " + beardedBandit.getRenderableHeight(),
                    "hidden " + hidden.elapsed()
            };
        };
    }

//    Timer noThievingExpTimer = new Timer(180_000);
    int lastThievingXp = 0;

    @Override
    public int onLoop() {
        if (forceHop > 0 && forceHop != Worlds.getCurrentWorld()) forceHop = -1;

        if (!Inventory.contains(ItemID.JUG_OF_WINE)) {
            if (!Inventory.contains(ItemID.JUG_OF_WINE + 1)) {
                log("No noted jugs left");

                new WithdrawLoadoutEvent(new InventoryLoadout().addItem(ItemID.JUG_OF_WINE + 1, 500), null).executed();
                return ReactionGenerator.getNormal();
            }
        }

        if (!THUG_HOUSE.contains(Players.getLocal())) {
            log("Walking to thug house");
            if (Walking.shouldWalk()) Walking.walk(THUG_HOUSE);
            return ReactionGenerator.getNormal();
        }

        if (escapeCombat) {
            log("Needs to escape combat - run north");
            if (!Players.getLocal().isInCombat()) escapeCombat = false;
            if (Walking.shouldWalk()) Walking.walk(BANDIT_HOUSE);
            return ReactionGenerator.getNormal();
        }

        if (Combat.getHealthPercent() < 50 || Skills.getBoostedLevel(Skill.HITPOINTS) <= 7) {
            Inventory.interact(ItemID.JUG_OF_WINE);
        }

//        if (!THUG_HOUSE.contains(Players.getLocal())) noThievingExpTimer.reset();
//        if (lastThievingXp > Skill.THIEVING.getExperience()) {
//            log("Xp drop");
//            noThievingExpTimer.reset();
//        }
//        if (noThievingExpTimer.finished()) {
//            log("No theiving xp timer went off failsafe hopping");
//            noThievingExpTimer.reset();
//            forceHop = Worlds.getCurrentWorld();
//        }

        NPC thug = NPCs.closest(x -> THUG_HOUSE.contains(x)
                && x.getName().equals("Menaphite Thug")
                && (x.hasAction("Knockout") || x.hasAction("Pickpocket"))
        );
        if (thug == null || forceHop == Worlds.getCurrentWorld()) {
            if (Players.getLocal().isInCombat()) {
                log("Needs to escape combat to hop world");
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
        if (thug.getRenderableHeight() < 100 || !hidden.finished()) {
            // knocked out
            thug.interact("Pickpocket");
            return 1200;
        } else {
            thug.interact("Knock-out");
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

        if (message.getMessage().toLowerCase().contains("another menaphite will see")) {
            log("2 thugs in a room");
            forceHop = Worlds.getCurrentWorld();
        }
        if (message.getMessage().contains("can't do that")) hidden.reset();
    }
}
