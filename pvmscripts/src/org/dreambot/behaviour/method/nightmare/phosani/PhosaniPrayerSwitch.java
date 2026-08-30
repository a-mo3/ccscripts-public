package org.dreambot.behaviour.method.nightmare.phosani;

import lombok.Getter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PhosaniPrayerSwitch extends Fractal implements ChatListener {
    private static final int NIGHTMARE_MELEE_ATTACK = 8594;
    private static final int NIGHTMARE_RANGE_ATTACK = 8596;
    private static final int NIGHTMARE_MAGIC_ATTACK = 8595;

    @Getter
    private static boolean cursed = false;

    private final Map<Integer, Supplier<Prayer>> prayerMap = new HashMap<>();

    public PhosaniPrayerSwitch() {
        prayerMap.put(NIGHTMARE_MAGIC_ATTACK, () -> isCursed() ? Prayer.PROTECT_FROM_MELEE : Prayer.PROTECT_FROM_MAGIC);
        prayerMap.put(NIGHTMARE_RANGE_ATTACK, () -> isCursed() ? Prayer.PROTECT_FROM_MAGIC : Prayer.PROTECT_FROM_MISSILES);
        prayerMap.put(NIGHTMARE_MELEE_ATTACK, () -> isCursed() ? Prayer.PROTECT_FROM_MISSILES : Prayer.PROTECT_FROM_MELEE);
        Client.getInstance().addEventListener(this);
    }

    @Override
    public boolean isValid() {
        NPC nightmare = NPCs.closest("Phosani's Nightmare");
        if (nightmare == null) return false;
        int ani = nightmare.getAnimation();
        return prayerMap.containsKey(ani) && !Prayers.isActive(prayerMap.get(ani).get());
    }

    @Override
    public int onLoop() {
        NPC nightmare = NPCs.closest("Phosani's Nightmare");
        if (nightmare == null) {
            Logger.error("Null nightmare prayer switch");
            return ReactionGenerator.getQuick();
        }
        Prayers.toggle(true, prayerMap.get(nightmare.getAnimation()).get());
        return ReactionGenerator.getQuick();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        String msg = message.getMessage().toLowerCase();

        if (msg.contains("the nightmare has cursed you, shuffling your prayers!")) {
            cursed = true;
        }

        if (msg.contains("you feel the effects of the nightmare's curse wear off.")) {
            cursed = false;
        }
    }
}
