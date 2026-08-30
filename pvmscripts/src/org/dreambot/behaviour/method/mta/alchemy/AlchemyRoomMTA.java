package org.dreambot.behaviour.method.mta.alchemy;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.method.mta.MTANodes;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Supplier;

public class AlchemyRoomMTA extends Fractal implements ChatListener, SpawnListener {
    private static final int MTA_ALCH_REGION = 13462;

    public static final int EXIT_TELEPORT = 23677;
    public static final int CUPBOARD_23678 = 23678;
    public static final int CUPBOARD_23679 = 23679;
    public static final int CUPBOARD_23680 = 23680;
    public static final int CUPBOARD_23681 = 23681;
    public static final int CUPBOARD_23682 = 23682;
    public static final int CUPBOARD_23683 = 23683;
    public static final int CUPBOARD_23684 = 23684;
    public static final int CUPBOARD_23685 = 23685;
    public static final int CUPBOARD_23686 = 23686;
    public static final int CUPBOARD_23687 = 23687;
    public static final int CUPBOARD_23688 = 23688;
    public static final int CUPBOARD_23689 = 23689;

    private static final int IMAGE_Z_OFFSET = 150;
    private static final int NUM_CUPBOARDS = 6;
    private static final int INFO_ITEM_START = 7;
    private static final int INFO_POINT_START = 12;
    private static final int INFO_LENGTH = 5;
    private static final int BEST_POINTS = 30;

    // mta points interface ID
    public static final int MTA_ALCHEMY = 194;
    // 194, 6 is the pizazz points widgetchild, text is points, does not seem to have a varbit
    // 553, 11

    private static final String YOU_FOUND = "You found:";
    private static final String EMPTY = "The cupboard is empty.";

    private final Cupboard[] cupboards = new Cupboard[NUM_CUPBOARDS];

    // the current target alch item, eg rune longsword
    private AlchemyItem best;
    // the cupboard that the item is in
    private Cupboard suggestion;

    public AlchemyRoomMTA(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        MTANodes.init();
        Client.getInstance().addEventListener(this);
        setSimpleName("Alchemy room");

        this.setEquipmentLoadout(new EquipmentLoadout()
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
        );

        this.setInventoryLoadout(new InventoryLoadout()
                .addItem(ItemID.NATURE_RUNE, 1, 1600)
        );
    }

    public static final Area ALCHEMY_ROOM = new Area(3345, 9653, 3385, 9615, 2);

    @Override
    public int onLoop() {
        // todo something for disabling teleport nodes once you are in here
        if (!ALCHEMY_ROOM.contains(Players.getLocal())) {
            if (Inventory.emptySlotCount() < 5 || Inventory.contains(ItemID.COINS_995)) {
                // cant take coins in here
                log("Cant take coins in and need at least 5 empty slots, banking items");
                new BankAllInventoryEvent().execute();
                return ReactionGenerator.getNormal();
            }

            slowLog("Walk into alchemy room");
            if (Walking.shouldWalk()) Walking.walk(ALCHEMY_ROOM);
            return ReactionGenerator.getNormal();
        }

        // 100 coins = 1 point, we will need 300 points for bones to peaches,
        // coins in MTA alchemy are 8890 not 995
        if (Inventory.count(8890) >= 3_000) { // every 30 points, 10%
            GameObject coinDeposit = GameObjects.closest("Coin Collector");
            log("Deposit coins");
            if (coinDeposit == null) {
                log("Failed to find coin collector");
            } else {
                coinDeposit.interact("Deposit");
                Sleep.sleepUntil(() -> Inventory.count(ItemID.COINS_995) < 100, 3500);
                return ReactionGenerator.getNormal();
            }
        }

        if (Arrays.stream(cupboards).anyMatch(Objects::isNull)) {
            log("Init cupboards");
            initCuboards();
            return ReactionGenerator.getNormal();
        }

        AlchemyItem bestItem = getBest();
        if (best == null || best != bestItem) {
            log("Item change to " + best);
            best = bestItem;
            // Reset items to unknown
            Arrays.stream(cupboards)
                    .filter(Objects::nonNull)
                    .forEach(e -> e.alchemyItem = AlchemyItem.UNKNOWN);
            return ReactionGenerator.getNormal();
        }

        // if you have items alch them
        Item alchable = Inventory.get(x -> Arrays.stream(AlchemyItem.values()).anyMatch(a -> x.getId() == a.getId()));
        if (alchable != null) {
            log("Alch " + alchable);
            Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY, alchable);
            return 5 * 600;
        }

        // if you dont have items take 5 from the best cupboard
        // todo set suggested on message or something so we dont compute this every loop
        Cupboard c = Arrays.stream(cupboards).filter(x -> x.alchemyItem == best).findFirst().orElse(null);
        if (c != null) {
            log("Take 5 " + c.gameObject.exists());
            if (!c.gameObject.exists()) initCuboards();
            c.gameObject.interact("Take-5");
        } else {
            log("failed to find suggested");
            GameObject cupboard = GameObjects.closest("Cupboard");
            if (cupboard != null) cupboard.interact("Search");
        }
        return ReactionGenerator.getNormal() * 2;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (!isValid()) return;

        String msg = message.getMessage();
        if (msg.contains(YOU_FOUND)) {
            log("Found message");
            String item = msg.replace(YOU_FOUND, "").trim();
            AlchemyItem alchemyItem = AlchemyItem.find(item);
            Cupboard c = Arrays.stream(cupboards).min(Comparator.comparingDouble(x -> x.gameObject.distance())).orElse(null);
            if (c.alchemyItem != alchemyItem) {
                log("Set new found");
                fill(c, alchemyItem);
            }
        } else if (EMPTY.equals(msg)) {
            Cupboard c = Arrays.stream(cupboards).min(Comparator.comparingDouble(x -> x.gameObject.distance())).orElse(null);
            if (c.alchemyItem != AlchemyItem.EMPTY) {
                fill(c, AlchemyItem.EMPTY);
            }
        }
    }


    private void fill(Cupboard cupboard, AlchemyItem alchemyItem) {
        int idx = Arrays.asList(cupboards).indexOf(cupboard);
        assert idx != -1;

        int itemIdx = alchemyItem.ordinal();

        log(String.format("Filling cupboard %s with %s", idx, alchemyItem));

        for (int i = 0; i < NUM_CUPBOARDS; ++i) {
            int cupIdx = (idx + i) % NUM_CUPBOARDS;
            int itemIndex = (itemIdx + i) % NUM_CUPBOARDS;
            cupboards[cupIdx].alchemyItem = itemIndex <= 4 ? AlchemyItem.values()[itemIndex] : AlchemyItem.EMPTY;
        }
    }

    /**
     * gets the highest point alchemy item to alch at the current time, bitten direct from runelite
     */
    private AlchemyItem getBest() {
        for (int i = 0; i < INFO_LENGTH; i++) {
            WidgetChild textWidget = Widgets.get(MTA_ALCHEMY, INFO_ITEM_START + i);
            if (textWidget == null) {
                return null;
            }

            String item = textWidget.getText();
            WidgetChild pointsWidget = Widgets.get(MTA_ALCHEMY, INFO_POINT_START + i);
            int points = Integer.parseInt(pointsWidget.getText());

            if (points == BEST_POINTS) {
                return AlchemyItem.find(item);
            }
        }

        return null;
    }

    public void initCuboards() {
        for (GameObject object : GameObjects.all(x -> x.getName().equals("Cupboard"))) {

            int cupboardId;
            switch (object.getId()) {
                // Closed and opened versions of each
                case CUPBOARD_23678:
                case CUPBOARD_23679:
                    cupboardId = 0;
                    break;

                case CUPBOARD_23680:
                case CUPBOARD_23681:
                    cupboardId = 1;
                    break;

                case CUPBOARD_23682:
                case CUPBOARD_23683:
                    cupboardId = 2;
                    break;

                case CUPBOARD_23684:
                case CUPBOARD_23685:
                    cupboardId = 3;
                    break;

                case CUPBOARD_23686:
                case CUPBOARD_23687:
                    cupboardId = 4;
                    break;

                case CUPBOARD_23688:
                case CUPBOARD_23689:
                    cupboardId = 5;
                    break;

                default:
                    return;

            }
            Cupboard cupboard = cupboards[cupboardId];
            if (cupboard != null) {
                cupboard.gameObject = object;
            } else {
                cupboard = new Cupboard();
                cupboard.gameObject = object;
                cupboard.alchemyItem = AlchemyItem.UNKNOWN;
                cupboards[cupboardId] = cupboard;
            }
        }
    }
}
