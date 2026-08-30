package org.dreambot.fractals.generic;

import org.dreambot.api.Client;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.RedeemBondEvent;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.Log;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetMembership extends Fractal {
    final ItemVariant BOND = new ItemVariant(ItemID.OLD_SCHOOL_BOND, ItemID.OLD_SCHOOL_BOND_UNTRADEABLE);
    Filter<World> membersWorldFilter = x -> x.isNormal() && x.getMinimumLevel() <= Skills.getTotalLevel() && x.isMembers();

    public GetMembership(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    public GetMembership() {
        super(() -> Client.isLoggedIn() && !Worlds.getCurrent().isMembers());
    }

    @Override
    public int onLoop() {
        if (Client.isLoggedIn() && Client.isMembers() && !Worlds.getCurrent().isMembers()) {
            Log.info("Hopping to a members world");
            WorldHopper.hopWorld(Worlds.getRandomWorld(membersWorldFilter));
            return ReactionGenerator.getNormal();
        }

        if (!OwnedItems.contains(BOND) && !GrandExchange.contains(ItemID.OLD_SCHOOL_BOND, ItemID.OLD_SCHOOL_BOND_UNTRADEABLE)
                && OwnedItems.count(ItemID.COINS_995) < ScriptSettings.getMinGP()) {
            new MuleRequestEvent()
                    .addRequiredItem(ItemID.COINS_995, ScriptSettings.getMinGP() - OwnedItems.count(ItemID.COINS_995))
                    .execute();
            return ReactionGenerator.getNormal();
        }

        new RedeemBondEvent().execute();
        return ReactionGenerator.getNormal();
    }
}
