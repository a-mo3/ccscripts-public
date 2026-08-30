package org.dreambot.behaviour.method.scurrius;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Queue;
import java.util.Stack;

public class ScurriusAttack extends TickDecision implements SpawnListener {
    public ScurriusAttack() {
        setSimpleName("Rats");
        Client.getInstance().addEventListener(this);
    }

    Stack<NPC> ratStack = new Stack<>();

    @Override
    public boolean evaluate() {
        NPC rat = NPCs.closest(x -> x.getHealthPercent() != 0 && x.getName().equals("Giant rat"));
        NPC scurrius = NPCs.closest("Scurrius");
        Character tgt = Players.getLocal().getInteractingCharacter();
        if (rat == null) {
            // attack scurrius
            if (scurrius == null) {
                log("No scurrius");
                return false;
            }

            if (!scurrius.equals(tgt)) {
                log("Attack scurrius");
                scurrius.interact("Attack");
            }
            return true;
        }

        if (!ratStack.isEmpty()) {
            if (Equipment.contains(ItemID.BONE_MACE, ItemID.BONE_STAFF, ItemID.BONE_SHORTBOW)) {
                log("Attack from ratstack");
                ratStack.pop().interact("Attack");
                return true;
            } else {
                log("No bone weapon dump the ratstack");
                ratStack.empty();
            }
        }

        if (rat.equals(tgt)) {
            log("Already targetting rat");
            return true;
        }
        log("Attack rat");
        rat.interact("Attack");
        return true;
    }

    @Override
    public void onNpcSpawn(NPC npc) {
        if (!Client.isDynamicRegion()) return;
        if (!npc.getName().equals("Giant rat")) return;
        log("Rat spawn");
        ratStack.push(npc);

    }
}
