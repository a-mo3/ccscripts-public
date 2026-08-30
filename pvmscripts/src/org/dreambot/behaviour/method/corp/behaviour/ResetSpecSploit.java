package org.dreambot.behaviour.method.corp.behaviour;

import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.method.corp.CorpClient;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.NPCUtil;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * you can go to lms casual world, go into lms casual, hop back to corp world
 * it reset spec, circumvents POH req to do corp with spec
 * yeah nigga im like that i know all the shits.
 */
@Setter
public class ResetSpecSploit extends Fractal {
    public ResetSpecSploit(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Splot Spec reset");
    }

    final Area LMS_CASUAL = new Area(3140, 3640, 3143, 3637, 1);
    final Area LISA_AREA= new Area(3140, 3637, 3143, 3634);
    Area feroxArea = new Area(3124, 3645, 3155, 3615);

    @Override
    public int onLoop() {
        if (feroxArea.contains(Players.getLocal()) && !hasFinishedLMSTutorial()) {
            log("Going to lisa to do tutorial");
            if (!LISA_AREA.contains(Players.getLocal())) {
                log("Go to lisa");
                if (Walking.shouldWalk()) Walking.walk(LISA_AREA);
                return ReactionGenerator.getNormal();
            }

            boolean notInLMSWorld = !Worlds.getCurrent().isLastManStanding();
            if (notInLMSWorld) {
                log("Hop to LMS world");
                WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isLastManStanding() && x.isNormal() && x.isMembers()));
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                Dialog.solve("more", "play");
                return ReactionGenerator.getNormal();
            }

            NPCUtil.interact("Lisa");
            return ReactionGenerator.getNormal();
        }

        // get to ferox
        if (!LMS_CASUAL.contains(Players.getLocal())) {
            if (Client.isDynamicRegion() && ItemVariants.RING_OF_DUELING.getItem() == null) {
                Logger.warn("We are expecting to have a ring of dueling here, which we seem to not, to avoid walking through wildy, do nothign");
                return ReactionGenerator.getNormal();
            }
            log("Go to LMS");
            if (Client.isDynamicRegion()) {
                ItemVariants.RING_OF_DUELING.interact("Ferox Enclave");
            } else {
                Prayers.toggleQuickPrayer(false);
            }

            if (Walking.shouldWalk()) Walking.walk(LMS_CASUAL);
            return ReactionGenerator.getNormal();
        }

        boolean notInLMSWorld = !Worlds.getCurrent().isLastManStanding();
        if (notInLMSWorld) {
            log("Hop to LMS world");
            WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isLastManStanding() && x.isNormal() && x.isMembers()));
            return ReactionGenerator.getNormal();
        }

        if (!isInLMSCasual()) {
            GameObject casPortal = GameObjects.closest("Casual");
            if (casPortal != null) {
                casPortal.interact();
                log("Enter casual");
            }
            return ReactionGenerator.getNormal();
        } else {
            WorldHopper.hopWorld(CorpClient.getCorpWorld());
        }

        return ReactionGenerator.getNormal();
    }

    public static boolean isInLMSCasual() {
        return PlayerSettings.getBitValue(14283) == 2;
    }

    public static boolean hasFinishedLMSTutorial() {
        return PlayerSettings.getBitValue(5304) == 1;
    }
}
