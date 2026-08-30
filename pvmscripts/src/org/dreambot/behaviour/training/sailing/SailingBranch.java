package org.dreambot.behaviour.training.sailing;

import org.dreambot.behaviour.quests.pandemonium.Pandemonium;
import org.dreambot.behaviour.training.sailing.ticksalvage.TickSalvageBranch;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class SailingBranch extends Fractal {
    public SailingBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        setSimpleName("Sailing");
        // 19175 = boat speed
        // 19174 Sail set 1 = set 2 = unset
        addChildren(
                new Pandemonium(), // quest to access sailing
                // todo exploring and sight seeing is probably going to be the fastest way to get initial levels
                // for this you need knights sword (maybe only for certain locations) https://secure.runescape.com/m=news/prepare-for-sailing---launching-november-19th?oldschool=1

                /*
                A nice and easy way to rack up Sailing XP is by taking a Courier Task (or two) while you're out at sea.
                 Courier Tasks can be taken via notice boards found at most major ports.
                 Initially, you'll be using the boards at Port Sarim and the Pandemonium, but as you level up,
                 more will become available. You will start with one task slot,
                 increasing up to five as your Sailing level increases (the second slot opens up at Level 7 Sailing).
                 */

                // todo see gnomemonkey prep video for 3t trawler
                new TickSalvageBranch(() -> true)
        );
    }
}
