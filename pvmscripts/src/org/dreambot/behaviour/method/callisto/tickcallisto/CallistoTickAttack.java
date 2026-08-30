package org.dreambot.behaviour.method.callisto.tickcallisto;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.SpotAnimation;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.method.callisto.CallistoData;
import org.dreambot.behaviour.misc.tickcombat.decisions.GenericTickEat;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.HashMap;
import java.util.Map;

import static org.dreambot.behaviour.method.artio.MagicFightArtio.ARTIO_CLEAR_ENTANGLE_ANI;

public class CallistoTickAttack extends TickDecision implements AnimationListener, ChatListener {
    public static int frozenUntil = -1;
    Map<Integer, Integer> spotAnimationTimings = new HashMap<>();

    public CallistoTickAttack() {
        setSimpleName("Callisto attack");
        Client.getInstance().addEventListener(this);
        // todo barrage
        spotAnimationTimings.put(179, 24); // entangle, 14.4 seconds
        spotAnimationTimings.put(180, 16); // snare 9.6 seconds
        spotAnimationTimings.put(181, 8); // bind 4.8 seconds

    }

    private Spell bestBind() {
        if (Magic.canCast(Normal.ENTANGLE)) return Normal.ENTANGLE;
        if (Magic.canCast(Normal.SNARE)) return Normal.SNARE;
        if (Magic.canCast(Normal.BIND)) return Normal.BIND;
        return null;
    }

    @Override
    public boolean evaluate() {
        if (!Walking.isRunEnabled() && Walking.getRunEnergy() >= 5) Walking.toggleRun();

        NPC callisto = NPCs.closest(CallistoData.CALLISTO_NAME);
        if (callisto == null) {
            log("Failed to find callisto");
            return false;
        }
//        if (callisto.isMoving()) frozenUntil = Client.getGameTick();
        if (frozenUntil <= Client.getGameTick() && bestBind() != null) {
            log("Freeze callisto " + Client.getGameTick());
//            if (!Magic.canCast(Normal.ENTANGLE)) return false;
            // entangle callisto

            Magic.castSpellOn(bestBind(), callisto);
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 600);
            return false;
        }

        if (GenericTickEat.lastAteTick != 0 && Client.getGameTick() - GenericTickEat.lastAteTick < 3) {
            log("On eat delay");
            return false;
        }

        Character target = Players.getLocal().getInteractingCharacter();
        if (Equipment.contains(ItemID.ACCURSED_SCEPTRE) && Combat.getSpecialPercentage() == 100)
            Combat.toggleSpecialAttack(true);
        if (!callisto.equals(target)) callisto.interact("Attack");
        return false;
    }


    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (npc.getName().equals(CallistoData.CALLISTO_NAME)) {
            log(npc + " Animated " + animation + " Delay " + animationDelay);
            if (animation == ARTIO_CLEAR_ENTANGLE_ANI) {// todo i havent checked if its the same animation on callisto
                log("Callisto roared reset entangle");
                frozenUntil = Client.getGameTick() + 3;
                return;
            }
        }
    }

    @Override
    public void onNPCSpotAnimation(NPC npc, SpotAnimation animation) {
        log(npc + " Spot Animated " + animation.getAnimationId() + " Delay " + animation.getDelay() + " Tick " + animation.getTick());
        if (npc == null || npc.getName() == null || !npc.getName().equals(CallistoData.CALLISTO_NAME)) return;

        if (!spotAnimationTimings.containsKey(animation.getAnimationId())) return;
        // extra 5 ticks because hes immune for a bit
        log("Callisto frozen");
        frozenUntil = Client.getGameTick() + spotAnimationTimings.get(animation.getAnimationId()) + 3;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().contains("target is already held")) frozenUntil = Client.getGameTick() + 10;
    }
}
