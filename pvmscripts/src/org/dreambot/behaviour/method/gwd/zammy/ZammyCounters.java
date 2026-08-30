package org.dreambot.behaviour.method.gwd.zammy;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.gwd.zammy.range.TickRangeZammyBranch;
import org.dreambot.fractals.TickDecision;

public class ZammyCounters extends TickDecision implements AnimationListener {

    public static int zamCounter;
    public static int meleeCounter;
    public static int magicCounter;
    public static int rangeCounter;
    public static int ourCounter;

    public ZammyCounters() {
        Client.getInstance().addEventListener(this);
    }

    @Override
    public boolean evaluate() {
        Logger.info("----- Tick " + Client.getGameTick() + " --------- " + Client.getGameCycle() + " index " + TickRangeZammyBranch.rotationIndex);
        if (zamCounter > 0) zamCounter--;
        if (meleeCounter > 0) meleeCounter--;
        if (magicCounter > 0) magicCounter--;
        if (rangeCounter > 0) rangeCounter--;
        if (ourCounter > 0) ourCounter--;
        return false;
    }


    final String MELEE_GUARD_NAME = "Tstanon Karlak";
    final String RANGE_GUARD_NAME = "Zakl'n Gritch";
    final String MAGIC_GUARD_NAME = "Balfrug Kreeyath";
    public static final String ZAMMY_NAME = "K'ril Tsutsaroth";

    int RANGE_ANI_ID = 7077; // gritch animation
    int MAGE_ANI_ID = 4630;

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (npc == null) return;
        String npcName = npc.getName();
        if (npcName == null) return;
//        log("NPC animated " + npcName+ " " + animation);
        if (npcName.equals(MELEE_GUARD_NAME)) meleeCounter = 5;
        if (npcName.equals(RANGE_GUARD_NAME) && RANGE_ANI_ID == animation) rangeCounter = 4;
        if (npcName.equals(MAGIC_GUARD_NAME) && MAGE_ANI_ID == animation) magicCounter = 4;
        if (npcName.equals(ZAMMY_NAME)) zamCounter = 5;
    }


    int whipAni = 1658;

    @Override
    public void onPlayerAnimation(Player player, int animation, int animationDelay) {
        if (player.equals(Players.getLocal())) {
//            log("Player animated " + animation);
            if (animation == whipAni) ourCounter = 4;
        }
    }
}
