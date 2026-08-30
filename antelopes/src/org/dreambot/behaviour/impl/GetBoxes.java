package org.dreambot.behaviour.impl;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

public class GetBoxes extends Fractal {
    public GetBoxes() {
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5)
                .addItem(ItemID.VARROCK_TELEPORT, 1, 5)
                .addItem(ItemID.COINS_995, 45_000, 50_000)
                .setStrict(true)
                .strictIgnore(ItemID.BOX_TRAP_PACK)
        ;

        this.appendLogic = () -> {
            if (NewBlacksChins.FALCONRY_AREA.contains(Players.getLocal())) {
                Magic.castSpell(Normal.HOME_TELEPORT);
                Sleep.sleepUntil(() -> !NewBlacksChins.FALCONRY_AREA.contains(Players.getLocal()), 30_0000);
                return true;
            }
            return false;
        };
    }

    @Override
    public boolean isValid() {
//        return true;
        return Bank.getLastBankHistoryCacheTime() > 1
                && OwnedItems.count(ItemID.BOX_TRAP) < 20
                && !Combat.isInWild();
    }

    @Override
    public int onLoop() {
        Logger.info("Get box trap event " + new GetBoxTrapEvent().executed());
        return ReactionGenerator.getNormal();
    }
}
