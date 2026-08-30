package org.dreambot.behaviour.training.hunter;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * gets box traps manually because black chins will hit buy limit
 */
public class GetBoxTraps extends Fractal {
    public GetBoxTraps() {
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5)
                .addItem(ItemID.VARROCK_TELEPORT, 1, 5)
                .addItem(ItemID.COINS_995, 45_000, 50_000)
                .setStrict(true)
                .strictIgnore(ItemID.BOX_TRAP_PACK)
        ;

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
