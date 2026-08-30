package org.dreambot.behaviour.method.scorpia;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.ScorpiaSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class FightScorpia extends Fractal implements AnimationListener, ItemContainerListener {
    Area SCORPIA_LAIR = new Area(3218, 10353, 3248, 10330);

    public FightScorpia(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.loadoutCondition = () -> !Combat.isInWild();

        this.inventoryLoadout = SettingsRepository.findInstanceOf(new ScorpiaSettings()).loadout.loadout;
        this.equipmentLoadout = SettingsRepository.findInstanceOf(new ScorpiaSettings()).loadout.equipmentLoadout;

        // web nodes for wildy lever
        EntranceWebNode edgevilleWildernessLever = new EntranceWebNode(
                3090, 3475, 0,
                "Lever", "Pull");

        EntranceWebNode wildernessEdgevilleLever = new EntranceWebNode(
                3153, 3923, 0,
                "Lever", "Edgeville"
        );

        BasicWebNode wildernessBasic = new BasicWebNode(3156, 3936, 0);

        WebFinder wf = WebFinder.getWebFinder();

        wf.addWebNode(wildernessBasic);
        edgevilleWildernessLever.addDualConnections(wildernessEdgevilleLever);
        wildernessEdgevilleLever.addDualConnections(wildernessBasic);
        wf.getNearest(edgevilleWildernessLever.getTile(), 12).addDualConnections(edgevilleWildernessLever);

        wf.addWebNode(edgevilleWildernessLever);
        wf.addWebNode(wildernessEdgevilleLever);

        AbstractWebNode webNode0 = new BasicWebNode(3158, 3950, 0);
        AbstractWebNode webNode1 = new BasicWebNode(3159, 3942, 0);
        webNode0.addDualConnections(WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15));
        WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15).addDualConnections(webNode0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);

        AbstractWebNode[] webNodes = {webNode0, webNode1,};
        WebFinder.getWebFinder().addWebNodes(webNodes);
        webNode1.addDualConnections(wildernessBasic);

        // web nodes to enter scorpia
        EntranceWebNode toScorpiaEntrance = new EntranceWebNode(3231, 3936, 0);
        toScorpiaEntrance.setAction("Enter");
        toScorpiaEntrance.setEntityName("Cavern");
        wf.getNearest(toScorpiaEntrance, 20).addDualConnections(toScorpiaEntrance);

        EntranceWebNode scorpiaExit = new EntranceWebNode(3233, 10331);
        scorpiaExit.setEntityName("Crevice");
        scorpiaExit.setAction("Use");
        toScorpiaEntrance.addDualConnections(scorpiaExit);

        BasicWebNode scorpiaBasic = new BasicWebNode(3233, 10337);
        scorpiaExit.addDualConnections(scorpiaBasic);

        wf.addWebNode(toScorpiaEntrance);
        wf.addWebNode(scorpiaExit);
        wf.addWebNode(scorpiaBasic);

        Client.getInstance().addEventListener(this);
    }

    @Override
    public int onLoop() {
        if (!SCORPIA_LAIR.contains(Players.getLocal())) {
            slowLog("Walking to scorpia");
            if (Walking.shouldWalk()) Walking.walk(SCORPIA_LAIR);
            return ReactionGenerator.getQuick();
        }

        if (Combat.isPoisoned()) {
            Item anti = ItemVariants.ANTI_POISON.getItem();
            log("poisoned " + anti);
            if (anti != null && anti.interact("Drink")) {
                log("Drank");
                return ReactionGenerator.getQuick();
            }
        }

        if (Combat.getHealthPercent() < 50 && Inventory.contains(ItemID.BLIGHTED_MANTA_RAY)) {
            Character tgt = Players.getLocal().getInteractingCharacter();
            log("Eating");
            Inventory.interact(ItemID.BLIGHTED_MANTA_RAY);
            Sleep.sleep(200, 600);
            if (tgt != null) tgt.interact("Attack");
            return ReactionGenerator.getQuick();
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) < 15) {
            Item restore = ItemVariants.BLIGHTED_SUPER_RESTORE.getItem();
            if (restore != null) {
                log("Drinking restore");
                restore.interact("Drink");
                return ReactionGenerator.getQuick();
            }
        }

        GroundItem loot = GroundItems.closest(x -> x.getItem().getLivePrice() * x.getAmount() > 500);
        if (loot != null && !Inventory.isFull()) {
            log("Looting");
            loot.interact("Take");
            return ReactionGenerator.getQuick();
        }

        NPC scorpia = NPCs.closest("Scorpia");
        Prayer shouldPray = (Client.getGameTick() - lastScorpiaAtkTick) % 4 == 0 && scorpia != null ? Prayer.PROTECT_FROM_MELEE : Prayer.PROTECT_FROM_MISSILES;
        Prayers.toggle(true, shouldPray);
        // attempt 1t flick
//        int thisTick = Client.getGameTick();
//        if (Client.getGameTick() > lastJuanTick) {
//            if (!Prayers.isActive(Prayer.PROTECT_FROM_MISSILES) && !Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
//                Prayers.toggle(true, shouldPray);
//                lastJuanTick = thisTick;
//                return ReactionGenerator.getQuick();
//            }
//            Prayers.toggle(false, shouldPray);
//            Sleep.sleep(100);
//            Prayers.toggle(true, shouldPray);
//            lastJuanTick = thisTick;
//        }


        NPC guardian = NPCs.closest(x -> x.getName().contains("guardian"));
        Character tgt = Players.getLocal().getInteractingCharacter();
        if (guardian != null && (tgt == null || !tgt.getName().contains("guardian"))) {
            log("Need to kill guardian");
            guardian.interact("Attack");
            return ReactionGenerator.getQuick();
        }

        if (guardian == null && (tgt == null || !tgt.getName().equals("Scorpia"))) {
            log("Attacking scorpia " + scorpia);
            if (scorpia != null) scorpia.interact("Attack");
        }

        return ReactionGenerator.getQuick();
    }


    int lastJuanTick = 0;
    int lastScorpiaAtkTick = -1;
    public static int netGp = 0;

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (animation == 6254 && npc.getName().equals("Scorpia")) {
            log(String.format("NPC: %s animated %d %d", npc.getName(), animation, animationDelay));
            lastScorpiaAtkTick = Client.getGameTick() % 4;
        }
    }

    @Override
    public void onPlayerAnimation(Player player, int animation, int animationDelay) {
        if (player.equals(Players.getLocal())) {
            log(String.format("LP animated %d %d", animation, animationDelay));
            if (!SCORPIA_LAIR.contains(Players.getLocal())) return;
            if (animation == 1167) netGp -= 307;
        }
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        if (!SCORPIA_LAIR.contains(Players.getLocal())) return;
        int value = (item.getLivePrice() + 1) * item.getAmount();
        log(String.format("Counting loot %s Value: %d", item.getName(), value));
        netGp += value;
    }

    @Override
    public void onInventoryItemRemoved(Item item) {
        if (!SCORPIA_LAIR.contains(Players.getLocal())) return;
        if (item.getId() == ItemID.BLIGHTED_MANTA_RAY) {
            log("Ate manta ray");
            netGp -= LivePrices.get(ItemID.BLIGHTED_MANTA_RAY);
        }
    }
}
