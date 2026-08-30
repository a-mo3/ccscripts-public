package org.dreambot.behaviour.method.moonsofperil;

import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.fractals.TickDecision;

public class MoonsPrayFlick extends TickDecision {
    public static boolean enabled = true;
    @Override
    public boolean evaluate() {
        if (!enabled || !Menu.isMenuManipulationActive()) return false;
        Prayers.toggleQuickPrayer(false);
        Sleep.sleep(50);
        Prayers.toggleQuickPrayer(true);
        return false;
    }
}
