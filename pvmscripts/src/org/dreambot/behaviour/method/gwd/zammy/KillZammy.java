package org.dreambot.behaviour.method.gwd.zammy;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.NPCUtil;
import org.dreambot.fractals.util.ObjectUtil;
import org.dreambot.scriptdata.ZammySettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class KillZammy extends Fractal implements AnimationListener {
    public static int earnedGP = 0;

    final ZammySettings settings;

    // door to altar tiles
    final Tile a1 = new Tile(2920, 5329, 2);
    final Tile a2 = new Tile(2926, 5329, 2);
    final Tile a3 = new Tile(2933, 5329, 2);
    // altar to door
    final Tile d1 = new Tile(2936, 5329, 2);
    final Tile d2 = new Tile(2930, 5329, 2);
    final Tile d3 = new Tile(2923, 5329, 2);
    Map<Tile, Tile> tileMap = new HashMap<>();

    public KillZammy(Supplier<Boolean> acceptCondition, ZammySettings settings) {
        super(acceptCondition);
        this.settings = settings;
        Client.getInstance().addEventListener(this);

        tileMap.put(a1, a2);
        tileMap.put(a2, a3);
        tileMap.put(a3, d1);
        tileMap.put(d1, d2);
        tileMap.put(d2, d3);
        tileMap.put(d3, a1);

        paintArraySupplier = () -> new String[]{
                "sincePlayerAtk " + tickSinceLastPlayerAttack(),
                "Magic cycle " + magicGuardCycle,
                "Range cycle " + rangeGuardCycle
        };
    }

    List<Integer> food = Arrays.asList(
            ItemID.MONKFISH,
            ItemID.SUMMER_PIE,
            ItemID.MANTA_RAY
    );

    Timer consumeDelay = new Timer(1000);

    public static final Area ZAMMY_ARENA = new Area(2917, 5332, 2937, 5317, 2);

    public static final Tile ZAMMY_DOOR = new Tile(2925, 5333, 2);


    // ticks until player can attack, +Weapon speed when attack and +3 when eat food
    int lastPlayerAtk = -1;

    int tickSinceLastPlayerAttack() {
        return Client.getGameTick() - lastPlayerAtk;
    }

    Tile target = null;


    @Override
    public int onLoop() {
        if (!ZAMMY_ARENA.contains(Players.getLocal())) {
            log("Enter Zammy arena");
            if (!ZAMMY_DOOR.equals(Players.getLocal().getTile())) {
                log("Get infront of door");
                if (Walking.shouldWalk()) Walking.walkExact(ZAMMY_DOOR);
                return 10;
            }

            if (Combat.isAutoRetaliateOn()) {
                log("Turn off auto retaliation");
                Combat.toggleAutoRetaliate(false);
                return ReactionGenerator.getNormal();
            }

            if (Inventory.contains(x -> ZammyConsts.primaryWeapons.contains(x.getId()))) {
                log("Has primary in inventory, equip");
                Inventory.interact(x -> ZammyConsts.primaryWeapons.contains(x.getId()));
                return ReactionGenerator.getNormal();
            }

            // hop

            // eat up

            // put prayers on

            // enter

            return 10;
        }
        // configure prayers

        toggle(true, getAppropriatePrayer());

        // eat
        if (consumeDelay.finished()) {
            // stamina
            Item staminaPot = ItemVariants.STAMINA_POTION.getItem();
            if (Walking.getRunEnergy() < 20) {
                if (staminaPot != null) {
                    log("Drinking stamina");
                    staminaPot.interact("Drink");
                    consumeDelay.reset();
                } else {
                    log("No more stamina, leaving");
                    exitToGE();
                }
                return 10;
            }

            // eat
            // todo handle using brews
            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 50) {
                if (Inventory.contains(x -> food.contains(x.getId()))) {
                    log("Eating");
                    Inventory.interact(x -> food.contains(x.getId()), "Eat");
                    consumeDelay.reset();
                } else {
                    Item brew = ItemVariants.SARADOMIN_BREW.getItem();
                    if (brew != null) {
                        log("Drinking brew");
                        consumeDelay.reset();
                        brew.interact("Drink");
                        return 10;
                    }

                    log("Out of food leaving");
                    exitToGE();
                }
                return 10;
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
                    consumeDelay.reset();
                    return 10;
                } else {
                    Item restore = ItemVariants.SUPER_RESTORE.getItem();
                    if (restore != null) {
                        log("Drinking restore");
                        consumeDelay.reset();
                        restore.interact("Drink");
                    } else {
                        log("Leave no restore");
                        exitToGE();
                    }
                    return 10;
                }

            }

            // drink prayer
            Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
            if (Skills.getBoostedLevel(Skill.PRAYER) < 20) {
                if (prayerPot == null) {
                    log("No prayer pots using restores");
                    prayerPot = ItemVariants.SUPER_RESTORE.getItem();
                }
                if (prayerPot != null) {
                    log("Drink prayer");
                    Inventory.interact(prayerPot);
                    consumeDelay.reset();
                } else {
                    log("Out of prayer.");
                    exitToGE();
                }
            }
        }

        if (target == null) {
            log("Target reset to d3");
            target = d3;
        }
        // target is not null at this point, we dont want to check if we are on any target only the next one, because you walk over the tiles you come back over
        Tile nextTile = target.equals(Players.getLocal().getServerTile()) ? tileMap.get(target) : null;
        if (nextTile != null) {
            target = nextTile;
            if (tickSinceLastPlayerAttack() >= 4) {
                log("Attack kril");
                NPCUtil.interact("K'ril Tsutsaroth", "Attack");
                return ReactionGenerator.getNormal();
            }
        }

        if (Walking.getDestination() == null) {
            log("Walk to target");
            if (Players.getLocal().getServerTile().equals(a1)) {
                log("Altar one, click on altar");
                ObjectUtil.interact("Zamorak altar");
                return 100;
            }

            if (Players.getLocal().getServerTile().equals(d1)) {
                log("Door one");
                ObjectUtil.interact("Big door");
                return 100;
            }

            log("Normal walk");
            Walking.walkExact(target);
            return 100;
        }

        return 10;
    }

    private void exitToGE() {
        GroundItem expensiveLoot = GroundItems.closest(x -> x.getItem().getLivePrice() > 50_000);
        if (expensiveLoot != null) {
            log("We need to leave but theres something expensive im gonna try and get " + expensiveLoot);
            expensiveLoot.interact("Take");
            return;
        }

        log("Out of resource leaving zilyana");
        Walking.walk(BankLocation.GRAND_EXCHANGE);
    }

    final String MELEE_GUARD_NAME = "Tstanon Karlak";
    final String RANGE_GUARD_NAME = "Zakl'n Gritch";
    final String MAGIC_GUARD_NAME = "Balfrug Kreeyath";
    final String ZAMMY_NAME = "K'ril Tsutsaroth";

    int meleeGuardCycle = -1;
    int rangeGuardCycle = -1;
    int magicGuardCycle = -1;
//    int ZAMMY_GUARD_CYCLE = -1;

    int RANGE_ANI_ID = 7077; // gritch animation
    int MAGE_ANI_ID = 4630;


    private Prayer getAppropriatePrayer() {
        return Prayer.PROTECT_FROM_MELEE;
    }

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (npc == null || npc.getName() == null) return;
        if (RANGE_GUARD_NAME.equals(npc.getName()) && animation == RANGE_ANI_ID)
            rangeGuardCycle = Client.getGameCycle() % 5;
        if (MAGIC_GUARD_NAME.equals(npc.getName()) && animation == MAGE_ANI_ID)
            magicGuardCycle = Client.getGameCycle() % 5;
    }

    private void toggle(boolean active, Prayer targetPrayer) {
        if (!Menu.isMenuManipulationActive()) {
            slowLog("Enable menu manipulation for a better experience");
            Prayers.toggle(active, targetPrayer);
            return;
        }

        if (targetPrayer == null) {
            toggle(false, Prayer.PROTECT_FROM_MISSILES);
            toggle(false, Prayer.PROTECT_FROM_MELEE);
            toggle(false, Prayer.PROTECT_FROM_MAGIC);
            return;
        }

        if (Prayers.isActive(targetPrayer) == active) return;


        WidgetChild wc = targetPrayer.getWidgetChild();
        if (wc == null) {
            Tabs.open(Tab.PRAYER);
            wc = targetPrayer.getWidgetChild();
        }

        if (wc == null) return;
        wc.interact();
    }

    final List<Integer> playerAtkAnimations = Arrays.asList(
            7552, // cross bow
            426 // crystal bow
    );

    @Override
    public void onPlayerAnimation(Player player, int animation, int animationDelay) {
        if (!player.equals(Players.getLocal())) return;
        if (playerAtkAnimations.contains(animation)) {
            log("Player attack");
            lastPlayerAtk = Client.getGameTick();
        }
    }
}
