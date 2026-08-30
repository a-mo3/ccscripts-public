package org.dreambot.behaviour.method.greendragon;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.pktrie.PKTrie;
import org.dreambot.settings.timing.ReactionGenerator;

// a simpler anti pk for green dragons only that basically just teleports out.
public class GDKAntiPK extends Fractal {
    private static boolean lock;
    private static int world = -1;

    public GDKAntiPK() {
        super(() -> {
            Player threat = Players.closest(x -> (PKTrie.checkString(x.getName()) || x.isSkulled()) && CombatUtil.canAttackMe(x));
            if (threat != null) {
                lock = true;
                world = Worlds.getCurrentWorld();
            }
            return lock;
        });
        CombatUtil.get(); // this will init combat util instance which will adjust tp nodes when TB state changes
    }

    Timer consumeTimer = new Timer(800);

    @Override
    public int onLoop() {
        if (!Combat.isInWild()) {
            if (Worlds.getCurrentWorld() == world) {
                log("GDK Anti PK world hop");
                WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel()));
                return ReactionGenerator.getNormal();
            } else {
                world = -1;
            }

            lock = false;
        }

        if (Combat.getHealthPercent() < 80 && consumeTimer.finished()) {
            Logger.info("Anti PK Eat");
            Inventory.interact(x -> KillGreenDragons.foodIds.contains(x.getId()), Inventory.contains(ItemID.JUG_OF_WINE) ? "Drink" : "Eat");
            consumeTimer.reset();
        }

        slowLog("GDK Anti PK booking it");
        Walking.walk(CombatUtil.get().isTeleblocked() ? BankLocation.EDGEVILLE : BankLocation.FEROX_ENCLAVE);
        return ReactionGenerator.getNormal();
    }
}
