package org.dreambot.behaviour.method.nightmare;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.nightmare.phosani.*;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class PhosaniBranch extends Fractal {
    // Nightmare's attack animations
    public static final int NIGHTMARE_HUSK_SPAWN = 8565;
    public static final int NIGHTMARE_PARASITE_TOSS = 8606;
    public static final int NIGHTMARE_CHARGE = 8609;
    public static final int NIGHTMARE_PRE_MUSHROOM = 37738;
    public static final int NIGHTMARE_MUSHROOM = 37739;

    public static boolean isMushroom(int id) {
        return id == NIGHTMARE_MUSHROOM || id == NIGHTMARE_PRE_MUSHROOM;
    }

    public static final int NIGHTMARE_SHADOW_GRAPHIC_OBJ = 1767;

    public static final List<Integer> INACTIVE_TOTEMS = Arrays.asList(9435, 9438, 9441, 9444);
    // FLOWERS OBJECT ID
    public static final int GOOD_FLOWERS = 37744;
    public static final int GOOD_FLOWER_ACTIVE = 37745;

    public static boolean isGoodFlower(int id) {
        return id == GOOD_FLOWERS || id == GOOD_FLOWER_ACTIVE;
    }

    public static final int BAD_FLOWERS = 37741;

    public static boolean isMagicPhase() {
        return NPCs.closest(x -> INACTIVE_TOTEMS.contains(x.getId())) != null;
    }

    public PhosaniBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        AbstractWebNode webNode0 = new BasicWebNode(3648, 3485, 0);
        AbstractWebNode webNode1 = new BasicWebNode(3648, 3477, 0);
        AbstractWebNode webNode2 = new BasicWebNode(3648, 3468, 0);
        AbstractWebNode webNode3 = new BasicWebNode(3642, 3469, 0);
        AbstractWebNode webNode4 = new BasicWebNode(3636, 3465, 0);
        AbstractWebNode webNode5 = new BasicWebNode(3632, 3456, 0);
        AbstractWebNode webNode6 = new BasicWebNode(3635, 3449, 0);
        webNode0.addDualConnections(WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15));
        WebFinder.getWebFinder().getNearestGlobal(webNode0.getTile(), 15).addDualConnections(webNode0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);
        webNode1.addDualConnections(webNode2);
        webNode2.addDualConnections(webNode1);
        webNode2.addDualConnections(webNode3);
        webNode3.addDualConnections(webNode2);
        webNode3.addDualConnections(webNode4);
        webNode4.addDualConnections(webNode3);
        webNode4.addDualConnections(webNode5);
        webNode5.addDualConnections(webNode4);
        webNode5.addDualConnections(webNode6);
        webNode6.addDualConnections(webNode5);
        webNode6.addDualConnections(WebFinder.getWebFinder().getNearestGlobal(webNode6.getTile(), 15));
        WebFinder.getWebFinder().getNearestGlobal(webNode6.getTile(), 15).addDualConnections(webNode6);

        AbstractWebNode[] webNodes = {webNode0, webNode1, webNode2, webNode3, webNode4, webNode5, webNode6,};
        WebFinder.getWebFinder().addWebNodes(webNodes);

        setSimpleName("Phosani");
        addChildren(
                new GoToPhosani(() -> !Client.isDynamicRegion()).setSimpleName("Enter phosani"),

                new PhosaniDrinkPrayer(() -> Skills.getBoostedLevel(Skill.PRAYER) < 5).setSimpleName("Prayer"),
                new PhosaniPrayerSwitch().setSimpleName("Prayer switch"),
                new PhosaniEat(() -> Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS) > 30).setSimpleName("Eat"),
                new HuskHandle(() -> NPCs.closest(x -> "Husk".equalsIgnoreCase(x.getName()) && x.getHealthPercent() > 0) != null).setSimpleName("Handle husks"),
                new SleepWalkers().setSimpleName("Lil Dirk shooting"),
                new PhosaniPregnant().setSimpleName("Plop plop plop"),
                new PhosaniRunEnable().setSimpleName("Lace up jordans"),
                new PhosaniBlackHoles(() -> GraphicsObjects.closest(NIGHTMARE_SHADOW_GRAPHIC_OBJ) != null).setSimpleName("Avoid blacks"),
                new PhosaniQuartersPhase().setSimpleName("stay safe in corners"),
                new ParasiteHandle(() -> NPCs.closest("Parasite") != null).setSimpleName("Parasite"),
                new PhosaniAvoidTackle().setSimpleName("Avoid tackle"),
                new PhosaniBoostPot().setSimpleName("Super Combat"),
                new PhosaniAvoidMushrooms().setSimpleName("Avoid mushroom"),
//
                new Fractal(PhosaniBranch::isMagicPhase)
                        .setSimpleName("Magic")
                        .addChildren(
                                new PhosaniMageAttack().setSimpleName("Magic attack")
                        ),
                new Fractal(() -> true)
                        .setSimpleName("Melee")
                        .addChildren(
                                new PhosaniMeleeAttack().setSimpleName("Hiya!")
                        )
        );
    }

    public static boolean exitPhosani() {
        Item tp = Inventory.get(ItemID.VARROCK_TELEPORT);
        if (tp == null) {
            return false;
        }

        tp.interact("Break");
        return true;
    }
}
