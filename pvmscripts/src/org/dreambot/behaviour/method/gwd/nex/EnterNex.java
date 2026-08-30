package org.dreambot.behaviour.method.gwd.nex;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class EnterNex extends Fractal {
    public EnterNex(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Enter Nex");
        this.loadoutCondition = () -> (ItemVariants.SARADOMIN_BREW.getItem() == null || ItemVariants.SUPER_RESTORE.getItem() == null)
                || Players.getLocal().getZ() == 0
                || Players.getLocal().getY() < 4000;

        this.inventoryLoadout = NexLoadout.BREW_RAINBOW_DHIDE_DCB_BLOWPIPE.inventoryLoadout;
        this.equipmentLoadout = NexLoadout.BREW_RAINBOW_DHIDE_DCB_BLOWPIPE.equipmentLoadout;
        NexNodes.init();
    }

    Area NEX_BANK = new Area(2901, 5207, 2906, 5199);
    public static final Area ROCK_THROW_AREA = new Area(
            new Tile(2879, 3692, 0),
            new Tile(2884, 3696, 0),
            new Tile(2894, 3701, 0),
            new Tile(2904, 3697, 0),
            new Tile(2907, 3702, 0),
            new Tile(2901, 3710, 0),
            new Tile(2876, 3700, 0));


    @Override
    public int onLoop() {
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

        if (!NEX_BANK.contains(Players.getLocal())) {
            log("Go to next bank");
            if (Walking.shouldWalk()) Walking.walk(NEX_BANK);
            return ReactionGenerator.getNormal();
        }

        return ReactionGenerator.getNormal();
    }
}

