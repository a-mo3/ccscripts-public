package org.dreambot.behaviour.method.barrows.handlecrypt;

import org.dreambot.api.methods.map.Area;
import org.dreambot.behaviour.method.barrows.handlecrypt.decisions.*;
import org.dreambot.behaviour.method.barrows.killbrothers.decisions.BarrowsEat;
import org.dreambot.behaviour.method.barrows.killbrothers.decisions.BarrowsPotion;
import org.dreambot.fractals.TickFractal;
import org.dreambot.scriptdata.BarrowsSettings;

import java.util.function.Supplier;

public class HandleCryptBranch extends TickFractal {
    public static final Area BARROWS_CRYPT = new Area(3522, 9725, 3581, 9663);
    public HandleCryptBranch(Supplier<Boolean> acceptCondition, BarrowsSettings settings) {
        super(acceptCondition);
        setSimpleName("Handle crypt");

        addChildren(
                // always ensure you have something autocasted if you have a staff
                new BarrowsSetAutocast().setSimpleName("Set autocast"),
                // leave tomb
                new EnterCrypt().setSimpleName("Enter Crypt"),

                new BarrowsEat().setSimpleName("Barrows eat"),

//                new BarrowsPotion().setSimpleName("Crypt potion"),
                new CryptKillBrother(settings.loadout).setSimpleName("Crypt brother"),
                // get the points up to 750, 750 - brother points if brother isnt dead
                new GetBarrowsPoints().setSimpleName("Barrows points"),
                new SolveBarrowsPuzzle().setSimpleName("Solve"),
                // find the door path and scurry through
                new GetToChest().setSimpleName("Get to chest")
        );

    }
}
