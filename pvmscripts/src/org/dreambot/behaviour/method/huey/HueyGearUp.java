package org.dreambot.behaviour.method.huey;

import org.dreambot.alerts.Alerts;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.friend.Friends;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.method.huey.comms.HueyCommsClient;
import org.dreambot.behaviour.method.huey.comms.HueyTeam;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.scriptdata.HueycoatlSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.awt.*;
import java.util.function.Supplier;

public class HueyGearUp extends Fractal {
    public static final Area BANK_LOCATION = new Area(1522, 3297, 1534, 3286);
    final boolean instanceSolo;
    final HueycoatlSettings settings;

    public static final int firstTimeVarbit = 11413; // 1 once you completed

    public HueyGearUp(Supplier<Boolean> acceptCondition, HueyLoadout loadout, HueycoatlSettings settings) {
        super(acceptCondition);
        this.settings = settings;
        this.instanceSolo = settings.instanceSolo;
        setSimpleName("Start huey");

        this.inventoryLoadout = loadout.loadout;

        this.equipmentLoadout = loadout.equipmentLoadout;

        // huey bank cant be added as a bank location because db is FUCKED and wont support objs so we do this
        prependLogic = () -> {
            PrayerUtils.disableAll();
            GameObject bankBuff = GameObjects.closest(x -> x.getName().contains("Bank buffalo"));
            log("Bank buffalo " + bankBuff);
            log("Fulfilled " + inventoryLoadout.isFulfilled());
            if (bankBuff != null && !inventoryLoadout.isFulfilled() && !Bank.isOpen()) {
                // open bank buffalo
                log("Open buffalo bank");
                bankBuff.interact("Bank");
                return true;
            }
            return false;
        };
    }

    @Override
    public int onLoop() {
        if (Bank.isOpen()) {
            log("Close bank");
            Bank.close();
        }

        if (Dialogues.canEnterInput() && BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 50) {
            log("Near GE enter amount");
            Keyboard.type("1 ", true);
            return ReactionGenerator.getNormal();

        }

        if (PlayerSettings.getBitValue(firstTimeVarbit) == 0) {
            log("Do first time warning");

            if (Dialogues.inDialogue()) {
                log("Dialogue solve");
                Dialog.solve("Yes");
                return ReactionGenerator.getNormal();
            }

            NPC taala = NPCs.closest("Taala");
            if (taala != null) {
                log("Talk to taala");
                taala.interact();
                Sleep.sleepUntil(Dialogues::inDialogue, 4000);
                return ReactionGenerator.getNormal();
            }

            if (!BANK_LOCATION.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(BANK_LOCATION);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        WidgetChild needMoreMoney = Widgets.get(x -> x.getText().contains("You need 50,000 Coins to face the Hueycoatl"));
        if (needMoreMoney != null) {
            log("Needs more money to start instance.");
            new MuleRequestEvent("Huey start instance money")
                    .addRequiredItem(ItemID.COINS_995, 250_000)
                    .execute();
            return ReactionGenerator.getNormal();
        }

        HueyTeam team = HueyCommsClient.currentTeam;
        if (settings.instanceTeam && settings.teamSize > 0 && !Client.isDynamicRegion()) {
            if (team == null) {
                log("Could not get team, disable team instance or start server, trying to reconnect");
                Alerts.addAlert(6_000, Color.pink, "Attempt to reconnect to huey comms server");
                try {
                    if (HueyCommsClient.getInstance(5).isOpen()) {
                        HueyCommsClient.getInstance(5).getUpdate();
                    } else {
                        HueyCommsClient.getInstance(5).reconnectBlocking();
                    }
                } catch (InterruptedException e) {
                    log("Failed to reconnect " + e);
                }
                return ReactionGenerator.getNormal();
            }

            if (Worlds.getCurrentWorld() != team.getWorld()) {
                log("Hop to world " + team.getWorld());
                WorldHopper.hopWorld(team.getWorld());
                return ReactionGenerator.getNormal();
            }

            log("Get into instance team");
            if (Players.getLocal().getName().equals(team.getTeamLeader())) {
                log("We're team leader, add all the team members & start the party");
                String needToAdd = team.getMembers().stream()
                        .filter(x -> !x.equals(Players.getLocal().getName()) && !Friends.haveFriend(x))
                        .findFirst().orElse(null);
                if (needToAdd != null && !needToAdd.isEmpty()) {
                    log("Add " + AnalyticsReporter.hashStringSHA256(needToAdd));
                    Friends.addFriend(needToAdd);
                    return ReactionGenerator.getNormal();
                }

                // start party.
                if (Dialogues.inDialogue()) {
                    if (Dialogues.canEnterInput()) {
                        Keyboard.type("1 ", true);
                        return ReactionGenerator.getNormal();
                    }
                    log("Start a team instance");
                    Dialog.solve("Start a pub");
                    Sleep.sleepUntil(Client::isDynamicRegion, 4000);
                    return ReactionGenerator.getNormal();
                }

                if (!BANK_LOCATION.contains(Players.getLocal())) {
                    if (Walking.shouldWalk()) Walking.walk(BANK_LOCATION);
                    return ReactionGenerator.getNormal();
                }

                // interact with flag
                GameObject flag = GameObjects.closest("Meeting flag");
                if (flag != null) {
                    flag.interact();
                    Sleep.sleepUntil(Dialogues::inDialogue, 4000);
                    return ReactionGenerator.getNormal();
                }
                return ReactionGenerator.getNormal();
            }
            // we are not team leader, add team leader and then join the group, which we will just assume exists.
            if (!team.getTeamLeader().equals(Players.getLocal().getName()) && !Friends.haveFriend(team.getTeamLeader())) {
                log("Add team leader " + team.getTeamLeader());
                Friends.addFriend(team.getTeamLeader());
                return ReactionGenerator.getNormal();
            }

            // join team instance
            if (Dialogues.canEnterInput()) {
                log("Join team leaders instance " + team.getTeamLeader());
                Keyboard.type(team.getTeamLeader(), true);
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                log("Join a team isntance");
                Dialog.solve("Join a publi");
                Sleep.sleepUntil(Client::isDynamicRegion, 4000);
                return ReactionGenerator.getNormal();
            }

            if (!BANK_LOCATION.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(BANK_LOCATION);
                return ReactionGenerator.getNormal();
            }

            // interact with flag
            GameObject flag = GameObjects.closest("Meeting flag");
            if (flag != null) {
                flag.interact();
                Sleep.sleepUntil(Dialogues::inDialogue, 4000);
                return ReactionGenerator.getNormal();
            }

            return ReactionGenerator.getNormal();
        }

        if (instanceSolo && !Client.isDynamicRegion()) {
            if (Bank.isCached() && OwnedItems.count(ItemID.COINS_995) < 50_000) {
                log("Dont have 50k, get 50k");
                new MuleRequestEvent("Huey-Instance-Money")
                        .addRequiredItem(ItemID.COINS_995, 150_000)
                        .execute();
                return ReactionGenerator.getNormal();
            }
            // select solo
            if (Dialogues.inDialogue()) {
                log("Start a solo isntance");
                Dialog.solve("solo");
                Sleep.sleepUntil(Client::isDynamicRegion, 4000);
                return ReactionGenerator.getNormal();
            }

            if (!BANK_LOCATION.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(BANK_LOCATION);
                return ReactionGenerator.getNormal();
            }

            // interact with flag
            GameObject flag = GameObjects.closest("Meeting flag");
            if (flag != null) {
                flag.interact();
                Sleep.sleepUntil(Dialogues::inDialogue, 4000);
                return ReactionGenerator.getNormal();
            }
        }

        if (!Client.isDynamicRegion() && !BANK_LOCATION.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(BANK_LOCATION);
            return ReactionGenerator.getNormal();
        }

        /*
        When a team leader starts a fight, team members can join after he started, every huey fight we need to check
        add friends, not just every time we start a party
         */
        if (settings.instanceTeam && team != null && team.getTeamLeader().equals(Players.getLocal().getName())) {
            log("We are team leader, check friends :^)");
            String needToAdd = team.getMembers().stream()
                    .filter(x -> !x.equals(Players.getLocal().getName()) && !Friends.haveFriend(x))
                    .findFirst().orElse(null);
            if (needToAdd != null && !needToAdd.isEmpty()) {
                log("Add " + AnalyticsReporter.hashStringSHA256(needToAdd));
                Friends.addFriend(needToAdd);
                return ReactionGenerator.getNormal();
            }
        }

        GameObject chain = GameObjects.closest("Chain");
        if (chain != null) {
            if (Dialogues.inDialogue()) {
                log("handle < lvl 85 warning");
                if (Dialogues.canEnterInput()) {
                    Keyboard.type(" 1 ", true);
                }
                Dialog.solve("Yes");
                Sleep.sleepUntil(HueyData::isInHueyFight, 1000);
                return ReactionGenerator.getNormal();
            }
            log("Get into huey fight");
            chain.interact("Quick-climb");
            Sleep.sleepUntil(HueyData::isInHueyFight, 4000);
        }
        return ReactionGenerator.getNormal();
    }
}
