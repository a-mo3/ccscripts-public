package org.dreambot.behaviour.quests.perilousmoon;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * get
 * grub paste (mortar pestle + moon worm from grubby bush)
 * bream scales (raw bream + knife)
 * lizard tail (lizard you catch with a net + knife)
 */
public class PerilousMoonGetItems extends Fractal {
    public static final Area STEAMBOUND_SUPPLIES = new Area(1508, 9696, 1512, 9690);
    public static final Area STEAMBOUND_WORM_BUSH = new Area(1513, 9693, 1519, 9689);
    public static final Area STEAMBOUND_FISH_SPOT = new Area(1518, 9691, 1520, 9686);

    public static final Area EARTHBOUND_HUNTING_SPOT = new Area(1386, 9712, 1396, 9705);
    public static final Tile HUNTING_ROCK_ONE = new Tile(1388, 9709);
    public static final Tile HUNTING_ROCK_TWO = new Tile(1390, 9707);

    public PerilousMoonGetItems(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Get items");
        this.inventoryLoadout = new InventoryLoadout().setStrictSupplier(() -> Inventory.contains(Item::isNoted));
    }

    @Override
    public int onLoop() {
        if (!Inventory.containsAll(ItemID.KNIFE, ItemID.PESTLE_AND_MORTAR,
                ItemID.BIG_FISHING_NET, ItemID.ROPE)) {
            log("Get supplies");
            if (!STEAMBOUND_SUPPLIES.contains(Players.getLocal())) {
                log("Walk to supply crate");
                if (Walking.shouldWalk()) Walking.walk(STEAMBOUND_SUPPLIES);
                return ReactionGenerator.getNormal();
            }

            GameObject supplyCrate = GameObjects.closest("Supply crates");
            if (supplyCrate == null) {
                log("Failed to find supply crates");
                return ReactionGenerator.getNormal();
            }

            if (!Inventory.contains(ItemID.PESTLE_AND_MORTAR)) {
                log("Get herblore supply");
                supplyCrate.interact(x -> x.contains("Herblore"));
                return ReactionGenerator.getNormal();
            }

            if (!Inventory.contains(ItemID.BIG_FISHING_NET)) {
                log("Get net (fishing) supply");
                supplyCrate.interact( x-> x.contains("Fishing"));
                return ReactionGenerator.getNormal();
            }

            if (!Inventory.contains(ItemID.ROPE)) {
                log("Get net (fishing) supply");
                supplyCrate.interact(x -> x.contains("Hunting"));
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        // have all the supplies now
        // get paste
        if (!Inventory.contains(ItemID.MOONLIGHT_GRUB_PASTE)) {
            log("Get grub paste");
            if (Inventory.contains(ItemID.MOONLIGHT_GRUB)) {
                log("Smash grub");
                Inventory.combine(ItemID.PESTLE_AND_MORTAR, ItemID.MOONLIGHT_GRUB);
                return ReactionGenerator.getNormal();
            }

            GameObject grubBush = GameObjects.closest("Grubby sapling");
            if (grubBush == null) {
                log("Failed to find grub bush");
                return ReactionGenerator.getNormal();
            }

            log("Harvest a grub");
            grubBush.interact();
            Sleep.sleepUntil(() -> Inventory.contains(ItemID.MOONLIGHT_GRUB), 2400);
            return ReactionGenerator.getNormal();
        }

        // get fish scales
        if (!Inventory.contains(ItemID.BREAM_SCALES)) {
            log("Get bream scales");
            if (Inventory.contains(ItemID.RAW_BREAM)) {
                log("Cut fish");
                Inventory.combine(ItemID.KNIFE, ItemID.RAW_BREAM);
                return ReactionGenerator.getNormal();
            }

            // fish, this spot is actually a game object!
            log("Get a fish");
            GameObject fishingSpot = GameObjects.closest("Fishing spot");
            if (fishingSpot != null) {
                log("Fish");
                fishingSpot.interact();
                return ReactionGenerator.getNormal();
            }


            return ReactionGenerator.getNormal();
        }

        // varbit 9875, 1 while fishing, 0 while not fishing
        // 11:17:47 PM: Varbit 9875: 1 -> 0 (stopped fishing)

        // the fishing activity here is strange, you cannot just walk away to exit, you have to click on the spot again, or hop worlds
        boolean currentlyFishing = PlayerSettings.getBitValue(9875) == 1;
        if (currentlyFishing) {
            GameObject fishingSpot = GameObjects.closest("Fishing spot");
            log("Stop fishing");
            if (fishingSpot != null) fishingSpot.interact();
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.RAW_BREAM)) {
            log("Have scales, drop all bream");
            Inventory.dropAll(ItemID.RAW_BREAM);
            return ReactionGenerator.getNormal();
        }

        // get tail, hubba hubba...
        if (!EARTHBOUND_HUNTING_SPOT.contains(Players.getLocal())) {
            log("Walk to hunting spot");
            if (Walking.shouldWalk()) Walking.walk(EARTHBOUND_HUNTING_SPOT);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.RAW_MOSS_LIZARD)) {
            log("Cut lizard");
            Inventory.combine(ItemID.KNIFE, ItemID.RAW_MOSS_LIZARD);
            return ReactionGenerator.getNormal();
        }

        GroundItem groundLizard = GroundItems.closest(ItemID.RAW_MOSS_LIZARD);
        if (groundLizard != null) {
            log("Take lizard");
            groundLizard.interact("Take");
            Sleep.sleepUntil(() -> Inventory.contains(ItemID.RAW_MOSS_LIZARD), 2400);
            return ReactionGenerator.getNormal();
        }

        // varbit for the spot we're using is 9871
        int hunterState = PlayerSettings.getBitValue(9871);
        if (hunterState == 1) {
            log("Rustle bushes");
            GameObject bush = GameObjects.closest("Bush");
            if (bush != null) {
                bush.interact("Rustle");
            }
            return ReactionGenerator.getNormal();
        }

        GameObject hRockOne = GameObjects.closest(x -> x.getTile().equals(HUNTING_ROCK_ONE));
        GameObject hRockTwo = GameObjects.closest(x -> x.getTile().equals(HUNTING_ROCK_TWO));
        if (hRockOne == null || hRockTwo == null) {
            log("Failed to find both hunter rocks");
            return ReactionGenerator.getNormal();
        }

        // cheap and easy, you have to interact with each of these but theres no indication of which one you've already set
        hRockOne.interact();
        Sleep.sleep(3000);
        hRockTwo.interact();
        Sleep.sleep(3000);
        return ReactionGenerator.getNormal();
    }
}
