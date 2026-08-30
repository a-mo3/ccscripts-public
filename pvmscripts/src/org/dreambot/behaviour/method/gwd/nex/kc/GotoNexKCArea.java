package org.dreambot.behaviour.method.gwd.nex.kc;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.gwd.nex.NexKCLoadout;
import org.dreambot.behaviour.method.gwd.nex.NexNodes;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GotoNexKCArea extends Fractal {
    public GotoNexKCArea(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Nex KC");
        this.loadoutCondition = () -> (ItemVariants.SARADOMIN_BREW.getItem() == null || ItemVariants.SUPER_RESTORE.getItem() == null)
                || Players.getLocal().getZ() == 0
                || Players.getLocal().getY() < 4000;

        this.inventoryLoadout = NexKCLoadout.BLOWPIPE.inventoryLoadout;
        this.equipmentLoadout = NexKCLoadout.BLOWPIPE.equipmentLoadout;
        NexNodes.init();
    }

    public static final Area KC_AREA = new Area(
            new Tile(2862, 5224, 0),
            new Tile(2876, 5231, 0),
            new Tile(2893, 5225, 0),
            new Tile(2894, 5211, 0),
            new Tile(2900, 5212, 0),
            new Tile(2899, 5192, 0),
            new Tile(2888, 5189, 0),
            new Tile(2869, 5191, 0),
            new Tile(2846, 5202, 0),
            new Tile(2849, 5210, 0),
            new Tile(2857, 5214, 0),
            new Tile(2863, 5214, 0));


    static final Area ROCK_THROW_AREA = new Area(
            new Tile(2879, 3692, 0),
            new Tile(2884, 3696, 0),
            new Tile(2894, 3701, 0),
            new Tile(2904, 3697, 0),
            new Tile(2907, 3702, 0),
            new Tile(2901, 3710, 0),
            new Tile(2876, 3700, 0));


    @Override
    public int onLoop() {
        // todo hop to a long ping world so we can prayer flick properly

        if (Skill.PRAYER.getBoostedLevel() <= 2) ItemVariants.SUPER_RESTORE.interact("Drink");
        if (Skill.HITPOINTS.getBoostedLevel() <= 30) Inventory.interact(ItemID.SHARK);

        Player lp = Players.getLocal();
        if (ROCK_THROW_AREA.contains(lp)) {
            log("Prot missle for troll rocks");
            Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
        } else if (lp.getCharactersInteractingWithMe().stream().anyMatch(x -> x.distance() < 3 && x.getName().toLowerCase().contains("wolf"))) {
            log("Pray against wolf");
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
        } else {
            PrayerUtils.disableAll();
        }

        if (!KC_AREA.contains(Players.getLocal())) {
            log("Go to KC Area");
            if (Walking.shouldWalk()) Walking.walk(KC_AREA);
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }
}

