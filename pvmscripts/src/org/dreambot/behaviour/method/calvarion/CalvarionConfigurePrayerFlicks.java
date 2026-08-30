package org.dreambot.behaviour.method.calvarion;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.HashSet;

public class CalvarionConfigurePrayerFlicks extends Fractal {
    static HashSet<Prayer> prayers = new HashSet<>(Arrays.asList(Prayer.PROTECT_FROM_MELEE, Prayer.ULTIMATE_STRENGTH, Prayer.INCREDIBLE_REFLEXES));

    public CalvarionConfigurePrayerFlicks() {
        super(() -> !Prayers.getQuickPrayers().contains(Prayer.PROTECT_FROM_MELEE) || isPietyUnlocked() ? !Prayers.getQuickPrayers().contains(Prayer.PIETY)
                : !Prayers.getQuickPrayers().containsAll(prayers));
        setSimpleName("Configure Quick Prayers");
    }

    @Override
    public int onLoop() {
        if (Widgets.isOpen()) {
            Widgets.closeAll();
        }

        if (Dialogues.inDialogue()) {
            log("Walk onto current tile to exit dialogue");
            Walking.walkExact(Players.getLocal().getTile());
            return ReactionGenerator.getNormal();
        }

        if (isPietyUnlocked()) {
            Prayers.setupQuickPrayers(Prayer.PROTECT_FROM_MELEE, Prayer.PIETY);
        } else {
            Prayers.setupQuickPrayers(Prayer.PROTECT_FROM_MELEE, Prayer.ULTIMATE_STRENGTH, Prayer.INCREDIBLE_REFLEXES);
        }
        return ReactionGenerator.getNormal();
    }

    public static boolean isPietyUnlocked() {
        return Skill.PRAYER.getLevel() >= 70 && PlayerSettings.getBitValue(3909) == 8;
    }
}
