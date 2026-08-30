package org.dreambot.behaviour.method.gwd.zammy;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.HitSplatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.graphics.SpotAnimation;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.scriptdata.ZammySettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetZammyKC extends Fractal implements AnimationListener, HitSplatListener {
    Area KC_AREA = new Area(
            new Tile(2883, 5350, 2),
            new Tile(2891, 5351, 2),
            new Tile(2910, 5341, 2),
            new Tile(2914, 5334, 2),
            new Tile(2923, 5337, 2),
            new Tile(2931, 5337, 2),
            new Tile(2938, 5349, 2),
            new Tile(2934, 5359, 2),
            new Tile(2912, 5366, 2),
            new Tile(2883, 5367, 2));

    public static final Area ROCK_THROW_AREA = new Area(
            new Tile(2879, 3692, 0),
            new Tile(2884, 3696, 0),
            new Tile(2894, 3701, 0),
            new Tile(2904, 3697, 0),
            new Tile(2907, 3702, 0),
            new Tile(2901, 3710, 0),
            new Tile(2876, 3700, 0));

    // imps spawn in a wide enough area if you are on one side you cant see the others, walk to middle when non are found
    public static final Tile IMP_RESET_TILE = new Tile(2914, 5347, 2);

    public static int getZammyKC() {
        return PlayerSettings.getBitValue(3976);
    }

    final ZammySettings settings;

    public GetZammyKC(Supplier<Boolean> acceptCondition, ZammySettings settings) {
        super(acceptCondition);
        this.settings = settings;

        // todo make this zammy
        this.loadoutCondition = () -> Players.getLocal().getZ() == 0 || Players.getLocal().getY() < 4000;
        this.inventoryLoadout = settings.loadout.inventoryLoadout;
        this.equipmentLoadout = settings.loadout.equipmentLoadout;

        this.paintArraySupplier = () -> new String[]{
        };

        Client.getInstance().addEventListener(this);
    }

    int flickTick = -1;
    long lagFlickTiming = -1;

    @Override
    public int onLoop() {
        if (Inventory.contains(ItemID.ROPE)) {
            log("Had rope assumed no gear");
            new BankAllInventoryEvent().execute();
        }

        if (Combat.isAutoRetaliateOn()) {
            log("Turn off auto retaliation");
            Combat.toggleAutoRetaliate(false);
            return ReactionGenerator.getNormal();
        }

        // pray against guys that throw rocks
        Player lp = Players.getLocal();
        if (ROCK_THROW_AREA.contains(lp)) {
            log("Prot missle for troll rocks");
            Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
        } else if (lp.getCharactersInteractingWithMe().stream().anyMatch(x -> x.distance() < 3 && x.getName().toLowerCase().contains("wolf"))) {
            log("Pray against wolf");
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
        } else {
            // todo consider how this will effect flicking, should be fine if are only killing priests
            Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
            Prayers.toggle(false, Prayer.PROTECT_FROM_MISSILES);
        }

        // with stat drain at the start
        // by the time you are done getting KC it seems like its fine

        // todo eat, shouldnt be needed but just incase
        if (Combat.getHealthPercent() < 50 && Inventory.contains(ItemID.MANTA_RAY)) {
            Inventory.interact(ItemID.MANTA_RAY);
            return ReactionGenerator.getNormal();
        }

        if (!KC_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(KC_AREA);
            return ReactionGenerator.getNormal();
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) < 20) {
            Item pot = ItemVariants.PRAYER_POTION.getItem();
            if (pot == null) {
                Logger.warn("No prayer pot found");
                pot = ItemVariants.SUPER_RESTORE.getItem();
                if (pot == null) {
                    log("No restores found");
                    Walking.walk(BankLocation.GRAND_EXCHANGE);
                    return ReactionGenerator.getQuick();
                }
            }

            pot.interact("Drink");
        }

        if (Skills.getBoostedLevel(Skill.HITPOINTS) < 50) {
            Item brew = ItemVariants.SARADOMIN_BREW.getItem();
            if (brew != null) {
                log("Drinking brew");
                brew.interact("Drink");
                return 150;
            }
            return 150;
        }

        // super restoring after brew
        int missingRange = Skills.getRealLevel(Skill.RANGED) - Skills.getBoostedLevel(Skill.RANGED);
        if (missingRange > 0) {
            log("reduced range level from brew");
            int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
            int possibleRestore = (int) (Skills.getRealLevel(Skill.RANGED) * 0.25 + 8);
            // the amount by which another brew will reduce range
            int brewReduction = (int) (Skills.getBoostedLevel(Skill.RANGED) * 0.1 + 2);
            log(String.format("Missing %d ranged & %d HP, possible range restore: %d - brew reduction: %d",
                    missingRange, missingHP, possibleRestore, brewReduction));
            Item brew = ItemVariants.SARADOMIN_BREW.getItem();
            if (missingHP > 0 && possibleRestore - (missingRange + brewReduction) >= 0 && brew != null) {
                log("We can sip another brew here");
                brew.interact("Drink");
                return 150;
            } else {
                Item restore = ItemVariants.SUPER_RESTORE.getItem();
                if (restore != null) {
                    log("Drinking restore");
                    restore.interact("Drink");
                } else {
                    log("Leave no restore");
                }
                return 50;
            }

        }

        if (Inventory.contains(x -> ZammyConsts.secondaryWeapons.contains(x.getId()))) {
            if (Inventory.isFull()) {
                log("Drop steel arrow");
                Inventory.dropAll(ItemID.STEEL_ARROW);
            }
            if (Inventory.isFull() && Inventory.contains(ItemID.TOXIC_BLOWPIPE)) {
                log("Drop cheapest for BP equip");
                PVMUtil.dropCheapest();
            }
            log("Equip secondary");
            Inventory.interact(x -> ZammyConsts.secondaryWeapons.contains(x.getId()));
            return ReactionGenerator.getNormal();
        }

        // atk another priest
        if (!lp.isInCombat()) {
            NPC imp = NPCs.closest(x -> "Imp".equals(x.getName()) && KC_AREA.contains(x));
            if (imp != null) {
                imp.interact("Attack");
                lagFlickTiming = -1;
                flickTick = -1;
            } else {
                // walk to middle
                if (!IMP_RESET_TILE.equals(Players.getLocal().getTile())) {
                    if (Walking.shouldWalk()) Walking.walk(IMP_RESET_TILE);
                }
            }
        }
        return ReactionGenerator.getQuick();
    }

    @Override
    public void onHitSplatAdded(Entity entity, int type, int damage, int id, int special, int gameCycle) {
        if (!Players.getLocal().equals(entity)) return;
        flickTick = (Client.getGameTick()) % 5;

    }

    @Override
    public void onPlayerSpotAnimation(Player player, SpotAnimation animation) {
        if (!player.equals(Players.getLocal())) return;
        if (animation.getAnimationId() == 85)
            flickTick = (Client.getGameTick()) % 5;
    }
}
