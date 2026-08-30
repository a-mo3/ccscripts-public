package org.dreambot.behaviour.method.motherlode;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.List;
import java.util.function.Supplier;

public class EnterMLM extends Fractal {
    Area MLM = new Area(3726, 5689, 3729, 5685);
    Area MINING_AREA = new Area(3712, 5648, 3726, 5633);
    Area MLM_BANK = new Area(3755, 5670, 3760, 5664);


    Area ROCKFALL_AREA = new Area(
            new Tile(3732, 5677, 0),
            new Tile(3736, 5680, 0),
            new Tile(3732, 5687, 0),
            new Tile(3731, 5693, 0),
            new Tile(3722, 5694, 0),
            new Tile(3728, 5683, 0));

    public static List<Tile> lastPath = null;

    public EnterMLM(boolean useDragonPickaxe) {
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(() -> {
                            int mineLvl = Skills.getRealLevel(Skill.MINING);
                            if (mineLvl >= 61 && useDragonPickaxe) return ItemID.DRAGON_PICKAXE;
                            if (mineLvl >= 41) return ItemID.RUNE_PICKAXE;
                            if (mineLvl >= 21) return ItemID.MITHRIL_PICKAXE;
                            return ItemID.BRONZE_PICKAXE;
                        },

                        1)
                .strictIgnore(ItemID.PAYDIRT)
                .setStrict(true);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int onLoop() {
//        if (ROCKFALL_AREA.contains(Players.getLocal())) {
//            GameObject rockfall = GameObjects.closest(x -> x.getName().equals("Rockfall") && ROCKFALL_AREA.contains(x));
//            if (rockfall != null) {
//                rockfall.interact("Mine");
//                Sleep.sleepUntil(() -> !rockfall.exists(), 2600);
//                return ReactionGenerator.getNormal();
//            }
//        }

        if (Walking.shouldWalk(8)) {
            Walking.walk(MLM_BANK);
            return ReactionGenerator.getQuick();
        }
        return ReactionGenerator.getNormal();
    }
}
