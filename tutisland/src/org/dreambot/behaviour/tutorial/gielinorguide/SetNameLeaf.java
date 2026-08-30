package org.dreambot.behaviour.tutorial.gielinorguide;

import org.dreambot.api.methods.settings.Varcs;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Random;


public class SetNameLeaf extends Fractal {

    public SetNameLeaf() {
    }

    private final String USERNAME = "penis";
    private int tryCounter = 0;
    private static final int TUT_ISLAND_NAME = 436; // client str id

    // apperance org.dreambot.settings, int is goal.
//    HashMap<Integer, ApperanceWidgets> org.dreambot.settings = new HashMap<>() {{
//        put(3, ApperanceWidgets.SKIN_COLOUR);
//        put(27, ApperanceWidgets.TORSO_COLOUR);
//        put(2, ApperanceWidgets.LEGS_COLOUR);
//        put(12, ApperanceWidgets.HAIR_COLOUR);
//
//        put(460, ApperanceWidgets.HEAD_DESIGN);
//        put(277, ApperanceWidgets.TORSO_DESIGN);
//    }};

    @Override
    public boolean isValid() {
        return MyVarps.getTutVarp() == 1;
    }

    @Override
    public int onLoop() {
        WidgetChild enterName = Widgets.get(x -> x.hasAction("Enter name"));
        WidgetChild lookUpNameButton = Widgets.get(w -> w.hasAction("Look up name"));
        WidgetChild suggestedNameOne = Widgets.get(558, 15);
        WidgetChild setNameButton = Widgets.get(w -> w.hasAction("Set name") && w.isVisible());
        WidgetChild confirmButton = Widgets.get(w -> w.isVisible() && w.hasAction("Confirm"));
        if (confirmButton != null) {
//            new CreateCharacterEvent()
//                    .setHeadDesign(HeadDesign.AFRO).setHairColour(HairColour.LIGHT_BLUE)
//                    .setTorsoDesign(TorsoDesign.getRandom()).setTorsoColour(TorsoColour.getRandom())
//                    .setLegDesign(LegDesign.getRandom()).setLegColour(LegColour.getRandom())
//                    .setJawDesign(JawDesign.getRandom())
//                    .setSkinColour(SkinColour.getRandom())
//                    .execute();
            confirmButton.interact("Confirm");
            Logger.info("confirming appearance");
            return 1800;
        }

        if (setNameButton != null && setNameButton.interact("Set name")) {
            return ReactionGenerator.getNormal() + 2000;
        }

        String name = generateName();
        Logger.info("name = " + name);
        Varcs.setString(TUT_ISLAND_NAME, name);
        Sleep.sleepTicks(2);
        if (enterName != null) {
            enterName.interact("Enter name");
        }
        if (lookUpNameButton != null && lookUpNameButton.interact("Look up name")) {
            Logger.info("Look up name");
            return ReactionGenerator.getNormal() + 3000;
        }

        return ReactionGenerator.getNormal() + 2000;
    }

    public static String generateName() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        int length = random.nextInt(12 - 8 + 1) + 8;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
