package org.dreambot.behaviour.method.gwd.zilyana;

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
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.HitSplatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.graphics.SpotAnimation;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.scriptdata.ZilyanaSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetZilyanaKC extends Fractal implements AnimationListener, HitSplatListener {
    Area KC_AREA = new Area(2913, 5301, 2924, 5276, 1);
    public static final Area ROCK_THROW_AREA = new Area(
            new Tile(2879, 3692, 0),
            new Tile(2884, 3696, 0),
            new Tile(2894, 3701, 0),
            new Tile(2904, 3697, 0),
            new Tile(2907, 3702, 0),
            new Tile(2901, 3710, 0),
            new Tile(2876, 3700, 0));

    public static int getSaradominKC() {
        return PlayerSettings.getBitValue(3972);
    }

    Area[] possibleKCAreas = new Area[]{
            new Area( // top floor (close to rope)
                    new Tile(2898, 5310, 2),
                    new Tile(2912, 5301, 2),
                    new Tile(2912, 5295, 2),
                    new Tile(2899, 5296, 2)
            ),
            new Area(2913, 5301, 2924, 5276, 1), // default
            new Area(2880, 5300, 2892, 5284, 2) // top floor sort of in the middle
    };

    final ZilyanaSettings zs;

    public GetZilyanaKC(Supplier<Boolean> acceptCondition, ZilyanaSettings zs) {
        super(acceptCondition);
        this.zs = zs;

        if (zs.useOtherKCAreas) {
            int roll = ShuffleFractal.getLoginValue() % possibleKCAreas.length;
            log("Set kc area " + roll);
            KC_AREA = possibleKCAreas[roll];
        }

        // GWD is > 0 except for the entrance for zilyana, but we wont go there in this fractal
        this.loadoutCondition = () -> (ItemVariants.SARADOMIN_BREW.getItem() == null || ItemVariants.SUPER_RESTORE.getItem() == null)
                || Players.getLocal().getZ() == 0
                || Players.getLocal().getY() < 4000;
        this.inventoryLoadout = zs.loadout.inventoryLoadout;
        this.equipmentLoadout = zs.loadout.equipmentLoadout;

        this.paintArraySupplier = () -> new String[]{
                "Tick " + Client.getGameTick(),
                "Flick " + flickTick,
                "Eval " + ((Client.getGameTick() - flickTick) % 5 == 0)
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

        if (!Combat.isAutoRetaliateOn()) {
            log("Enable auto retaliate");
            Combat.toggleAutoRetaliate(true);
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
                return 150;
            }

        }

        if (Inventory.contains(x -> ZilyanaConsts.secondaryWeapons.contains(x.getId()))) {
            if (Inventory.isFull()) {
                log("Drop steel arrow");
                Inventory.dropAll(ItemID.STEEL_ARROW);
            }
            if (Inventory.isFull() && Inventory.contains(ItemID.TOXIC_BLOWPIPE)) {
                log("Drop cheapest for BP equip");
                PVMUtil.dropCheapest();
            }
            log("Equip secondary");
            Inventory.interact(x -> ZilyanaConsts.secondaryWeapons.contains(x.getId()));
            return ReactionGenerator.getNormal();
        }

        // pray flick saradomin priests
        // spirtual creatures could be used here but we would need to check for slayer req, theres no slayer training
        // for this script so we wont expect that here

        Prayers.toggle(shouldPray(), Prayer.PROTECT_FROM_MAGIC);
        Prayers.toggle(lp.isInCombat(), KillZilyana.getBestRangePray());
//        Prayers.toggle(Players.getLocal().getRenderableHeight() == 1000, Prayer.PROTECT_FROM_MAGIC);

        // atk another priest
        if (!lp.isInCombat()) {
            NPC priest = NPCs.closest("Saradomin priest");
            if (priest != null) {
                priest.interact("Attack");
                lagFlickTiming = -1;
                flickTick = -1;
            }
        }
//        else {
//            Character attackingMe = Players.getLocal().getCharactersInteractingWithMe().stream()
//                    .filter(x -> "Saradomin priest".equals(x.getName()))
//                    .peek(x -> log(x.getName()))
//                    .findFirst().orElse(null);
//            log("In combat targetted by: " + attackingMe);
//            Character target = Players.getLocal().getInteractingCharacter();
//            log("Target: " + target);
//            if (attackingMe != null
//                    &&  (target == null || !"Saradomin priest".equals(target.getName()))) {
//                log("Retaliate");
//                attackingMe.interact();
//            }
//        }
        return ReactionGenerator.getQuick();
    }

    private boolean shouldPray() {
        // no need to pray if not being attacked
        ZilyanaSettings zs = SettingsRepository.findInstanceOf(new ZilyanaSettings());
        if (Players.getLocal().getCharactersInteractingWithMe().isEmpty()) return false;
//        if (zs.lagAdjustedFlicking) {
//            // offset flick timing for lag
//            int timeSinceLastBreeAttack = (int) ((System.currentTimeMillis() - lagFlickTiming) % 3000);
//            int timeRemainingOnBreeAttack = (3000 - timeSinceLastBreeAttack) - Worlds.getCurrent().getPing();
//            return timeRemainingOnBreeAttack < 0 && timeRemainingOnBreeAttack > -600;
//        }

        return (flickTick < 0 || (Client.getGameTick() - flickTick) % 5 < 2);
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
