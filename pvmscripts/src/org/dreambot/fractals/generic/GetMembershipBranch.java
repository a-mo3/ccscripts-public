package org.dreambot.fractals.generic;

import lombok.experimental.Accessors;
import org.dreambot.PvmMain;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.bankdump.DumpBank;
import org.dreambot.behaviour.bankdump.OpenTemporossCrate;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.RedeemBondEvent;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.function.Supplier;

@Accessors(chain = true)
public class GetMembershipBranch extends Fractal implements ConfigurableFractal<GetMembershipSettings> {
    final ItemVariant BOND = new ItemVariant(ItemID.OLD_SCHOOL_BOND, ItemID.OLD_SCHOOL_BOND_UNTRADEABLE);
    Filter<World> membersWorldFilter = x -> x.isNormal() && x.getWorld() != 401 && x.getMinimumLevel() <= Skills.getTotalLevel() && x.isMembers();

    public GetMembershipBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    private static int maxGP = Integer.MAX_VALUE;

    public GetMembershipBranch() {
        super(() -> Client.isLoggedIn() && (!Worlds.getCurrent().isMembers()
                || (Worlds.getCurrent().isMembers() && OwnedItems.count(ItemID.COINS_995) > maxGP))
        );
        maxGP = getSettings().maxGP;

        setSimpleName("Bond branch");
        boolean shouldDump = getSettings().bankDump || Arrays.stream(PvmMain.qsParams).anyMatch(x -> x.equalsIgnoreCase("bankDump"));
        addChildren(

                new MuleOff().setSimpleName("Max gp mule off")
                        .setAcceptCondition(() -> {
//                            log("Penis");
                            return Client.isLoggedIn() && Worlds.getCurrent().isMembers();
                        }),
                // bank dump if you are using bankDump instead of bonding
                new Fractal(() -> shouldDump).setSimpleName("Dump bank")
                        .addChildren(
                                new OpenTemporossCrate(),
                                new DumpBank(() -> true, 5)
                        ),
                new GetMembership().setSimpleName("Bond up")
        );
    }

    @Override
    public GetMembershipSettings getSettings() {
        return SettingsRepository.getSetting(settingName(), new GetMembershipSettings());
    }

    @Override
    public String settingName() {
        return "Membership";
    }
}
