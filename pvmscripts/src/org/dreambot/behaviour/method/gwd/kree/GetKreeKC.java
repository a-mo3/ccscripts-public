package org.dreambot.behaviour.method.gwd.kree;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
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
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.HitSplatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.SpotAnimation;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.*;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.scriptdata.KreearraSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetKreeKC extends Fractal implements AnimationListener, HitSplatListener {
    public static Area ARMADYL_EYRiE = new Area(
            new Tile(2816, 5312, 2),
            new Tile(2846, 5312, 2),
            new Tile(2844, 5300, 2),
            new Tile(2847, 5289, 2),
            new Tile(2856, 5280, 2),
            new Tile(2867, 5276, 2),
            new Tile(2880, 5274, 2),
            new Tile(2880, 5246, 2),
            new Tile(2823, 5253, 2));

    public static Area THROW_GRAPPLE_AREA = new Area(2869, 5283, 2875, 5279, 2);

    public static final Area ROCK_THROW_AREA = new Area(
            new Tile(2879, 3692, 0),
            new Tile(2884, 3696, 0),
            new Tile(2894, 3701, 0),
            new Tile(2904, 3697, 0),
            new Tile(2907, 3702, 0),
            new Tile(2901, 3710, 0),
            new Tile(2876, 3700, 0));

    public static int getArmadylKC() {
        return PlayerSettings.getBitValue(3973);
    }

    final KreearraSettings settings;

    public GetKreeKC(Supplier<Boolean> acceptCondition, KreearraSettings settings) {
        super(acceptCondition);
        this.settings = settings;

        // blacklist tile where rocks are
        LocalPathFinder lp = LocalPathFinder.getLocalPathFinder();
        lp.addBlacklistedTile(new Tile(2901, 3680, 0));
        lp.addBlacklistedTile(new Tile(2902, 3680, 0));
        lp.addBlacklistedTile(new Tile(2908, 3682, 0));
        lp.addBlacklistedTile(new Tile(2909, 3683, 0));
        lp.addBlacklistedTile(new Tile(2871, 3671, 0));
        lp.addBlacklistedTile(new Tile(2870, 3671, 0));

        // GWD is > 0 except for the entrance for zilyana, but we wont go there in this fractal
        this.loadoutCondition = () -> Players.getLocal().getZ() == 0 || Players.getLocal().getY() < 4000;
        this.inventoryLoadout = settings.loadout.inventoryLoadout
                .addItem(ItemID.MITH_GRAPPLE_9419);
        this.equipmentLoadout = settings.loadout.equipmentLoadout;

        this.paintArraySupplier = () -> new String[]{
                "Tick " + Client.getGameTick(),
                "Flick " + flickTick,
                "Eval " + ((Client.getGameTick() - flickTick) % 4 == 0),
                "Ping " + Worlds.getCurrent().getPing()
        };

        Client.getInstance().addEventListener(this);
    }

    int flickTick = -1;
    long lagFlickTiming = -1;

    @Override
    public int onLoop() {
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
        }

        // with stat drain at the start
        // by the time you are done getting KC it seems like its fine

        // todo eat, shouldnt be needed but just incase
        if (Combat.getHealthPercent() < 50 && Inventory.contains(ItemID.MANTA_RAY)) {
            Inventory.interact(ItemID.MANTA_RAY);
            return ReactionGenerator.getNormal();
        }

        if (!ARMADYL_EYRiE.contains(Players.getLocal())) {
            if (!THROW_GRAPPLE_AREA.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(THROW_GRAPPLE_AREA);
                return ReactionGenerator.getQuick();
            }

            if (!Equipment.contains(x -> x.getName().toLowerCase().contains("crossbow"))) {
                log("Equip crossbow");
                Equipment.equip(EquipmentSlot.WEAPON, x -> x.getName().toLowerCase().contains("crossbow"));
                return ReactionGenerator.getNormal();
            }

            // equip grapple
            if (!Equipment.contains(ItemID.MITH_GRAPPLE_9419)) {
                log("Equip grapple");
                Equipment.equip(EquipmentSlot.ARROWS, ItemID.MITH_GRAPPLE_9419);
                return ReactionGenerator.getNormal();
            }

            GameObject pillar = GameObjects.closest(x -> x.hasAction("Grapple"));
            if (pillar != null) {
                log("Grapple into eyrie");
                pillar.interact("Grapple");
                Sleep.sleep(300);
            }
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(x -> x.getName().toLowerCase().contains("blessing"))) {
            log("Equip blessing");
            Equipment.equip(EquipmentSlot.ARROWS, x -> x.getName().toLowerCase().contains("blessing"));
        }

        int missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
        if (missingHP >= 20) {
            if (Equipment.contains(ItemID.TOXIC_BLOWPIPE) && Combat.getSpecialPercentage() >= 50 && !Combat.isSpecialActive()) {
                log("BP spec for some HP back");
                Combat.toggleSpecialAttack(true);
                return 50;
            }
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) < 20) {
            Item pot = ItemVariants.PRAYER_POTION.getItem();
            if (pot == null) {
                Logger.warn("No prayer pot found");
                pot = ItemVariants.SUPER_RESTORE.getItem();
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
            missingHP = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
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

        if (Inventory.contains(x -> KreeConsts.secondaryWeapons.contains(x.getId()))) {
            if (Inventory.isFull()) {
                log("Drop steel arrow");
                Inventory.dropAll(ItemID.STEEL_ARROW, ItemID.VIAL);
            }
            if (Inventory.isFull() && Inventory.contains(ItemID.TOXIC_BLOWPIPE)) {
                log("Drop cheapest for BP equip");
                PVMUtil.dropCheapest();
            }
            log("Equip secondary");
            Inventory.interact(x -> KreeConsts.secondaryWeapons.contains(x.getId()));
            return ReactionGenerator.getNormal();
        }

        // pray flick <lvl 95 aviansies
        Prayers.toggle(shouldPray(), Prayer.PROTECT_FROM_MISSILES);
//        Prayers.toggle(lp.isInCombat(), KillZilyana.getBestRangePray());

        if (lp.isInCombat() && Players.getLocal().getInteractingCharacter() == null) {
            log("Should Attack back");
            Character atkingMe = Players.getLocal().getCharactersInteractingWithMe().stream()
                    .filter(x -> x.hasAction("Attack"))
                    .findFirst().orElse(null);
            if (atkingMe != null && atkingMe.interact("Attack")) {
                log("Attack back");
            }
            return 150;
        }

        // atk another priest
        if (!lp.isInCombat()) {
            flickTick = -1;
            lagFlickTiming = -1;
            NPC avian = NPCs.closest(x -> x.getLevel() < 100 && x.getName().equals("Aviansie"));
            if (avian != null) {
                avian.interact("Attack");
            }
            return ReactionGenerator.getQuick();
        }
        return ReactionGenerator.getQuick();
    }

    private boolean shouldPray() {
        if (settings.safePrayerWhenGettingKC) return true;
        // no need to pray if not being attacked
        if (Players.getLocal().getCharactersInteractingWithMe().isEmpty()) return false;
        // todo add this back
//        if (SettingsRepository.findInstanceOf().lagAdjustedFlicking) {
//            // offset flick timing for lag
//            int timeSinceLastBreeAttack = (int) ((System.currentTimeMillis() - lagFlickTiming) % 3000);
//            int timeRemainingOnBreeAttack = (3000 - timeSinceLastBreeAttack) - Worlds.getCurrent().getPing();
//            return timeRemainingOnBreeAttack < 0 && timeRemainingOnBreeAttack > -600;
//        }

        return (flickTick < 0 || (Client.getGameTick() - flickTick) % 4 < 2);
    }

    @Override
    public void onHitSplatAdded(Entity entity, int type, int damage, int id, int special, int gameCycle) {
        if (!Players.getLocal().equals(entity)) return;
//        flickTick = ((Client.getGameTick() - 1) % 4);
    }

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (!npc.equals(Players.getLocal().getInteractingCharacter())) return;
        log("Avian animated " + animation + " " + animationDelay);
        if (animation == 6956)
            flickTick = ((Client.getGameTick()) % 4);
    }

    // 6956

    @Override
    public void onPlayerSpotAnimation(Player player, SpotAnimation animation) {
        if (!player.equals(Players.getLocal())) return;
        if (animation.getAnimationId() == 85)
            flickTick = (Client.getGameTick()) % 4;
    }
}
