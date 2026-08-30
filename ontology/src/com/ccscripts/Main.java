package com.ccscripts;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.ActionType;
import com.ccscripts.actions.StateChangeAction;
import com.ccscripts.actions.impl.hard.CameraRotation;
import com.ccscripts.actions.impl.hard.EntityInteraction;
import com.ccscripts.actions.impl.hard.KeyPressAction;
import com.ccscripts.actions.impl.hard.WalkAction;
import com.ccscripts.actions.impl.soft.MouseMoveAction;
import com.ccscripts.cballs.framework.ItemID;
import com.ccscripts.cballs.framework.Node;
import com.ccscripts.cballs.framework.QuickNode;
import com.ccscripts.cballs.framework.ScriptNode;
import com.piler.ActionLogParser;
import com.piler.StateTransition;
import com.piler.constraints.NodeConstraint;
import com.ccscripts.listener.camera.CameraMovement;
import com.ccscripts.listener.camera.CameraMovementListener;
import com.ccscripts.listener.camera.CameraWatcher;
import com.ccscripts.listener.mouse.MouseMovement;
import com.ccscripts.listener.mouse.MouseMovementListener;
import com.ccscripts.listener.mouse.MouseWatcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ActionListener;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.MenuRow;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * the point of this script is to record real player actions, with respect to a certain expected behavior graph
 * and then be able to give that play time to a script, to behave similarly to that profile, in efforts of
 * creating antiban
 */
@ScriptManifest(category = Category.MISC, name = "Ontology", author = "cc", version = 0.0)
public class Main extends AbstractScript implements HumanMouseListener, MouseMovementListener, ActionListener, CameraMovementListener, ItemContainerListener {

    ExecutorService listenerExecutor = Executors.newFixedThreadPool(2);
    List<AbstractAction> actionLog = new LinkedList<>();
    Node cballsTree = new Node();

    boolean trainingEnabled;
    public static final int BLAST_FURNACE_IRON_BAR = 942;
    Timer anticipatoryTimer = new Timer(9_000);

    @Override
    public void onStart() {
//        Client.getInstance().enableInputs();
        listenerExecutor.execute(new MouseWatcher().register(this));
        listenerExecutor.execute(new CameraWatcher().register(this));

        AtomicBoolean emptyLock = new AtomicBoolean(false);


        cballsTree.addChildren(
                new Node(() -> {
                    if (Inventory.isFull() && Inventory.contains(x -> x.getName().contains("ore")))
                        anticipatoryTimer.reset();
                    return false;
                }),

                new Node(() -> Inventory.isEmpty() || Inventory.contains(x -> x.getName().contains("bar"))).addChildren(
                        QuickNode.builder()
                                .acceptCondition(ItemProcessing::isOpen)
                                .identifier("CollectBarsMenu")
                                .expectedNextState("OpenBank")
                                .build(),

                        QuickNode.builder()
                                .acceptCondition(() -> PlayerSettings.getBitValue(BLAST_FURNACE_IRON_BAR) > 0)
                                .identifier("CollectBars")
                                .expectedNextState("CollectBarsMenu")
                                .build(),

                        QuickNode.builder()
                                .acceptCondition(() -> !Bank.isOpen() && !Inventory.isFull() && !anticipatoryTimer.finished())
                                .identifier("Anticipate")
                                .expectedNextState("CollectBars")
                                .build(),

                        // withdraw iron
                        QuickNode.builder()
                                .acceptCondition(Bank::isOpen)
                                .identifier("Withdraw")
                                .expectedNextState("Close")
                                .build(),

                        // open bank
                        QuickNode.builder()
                                .acceptCondition(() -> !Bank.isOpen())
                                .identifier("OpenBank")
                                .expectedNextState("Withdraw")
                                .build().setSleepTime(6000)
                ),

                new Node(() -> Inventory.contains(x -> x.getName().contains("ore")))
                        .addChildren(
                                // close bank
                                QuickNode.builder()
                                        .acceptCondition(() -> Inventory.isFull() && Bank.isOpen() && Inventory.contains(x -> x.getName().contains("ore")))
                                        .identifier("Close")
                                        .expectedNextState("Smelt")
                                        .build(),

                                // put ore in the smelter
                                QuickNode.builder()
                                        .acceptCondition(() -> Inventory.isFull() && Inventory.contains(x -> x.getName().contains("ore")))
                                        .identifier("Smelt")
                                        .expectedNextState("Anticipate")
                                        .build()

                        )

//                // open bank
//                QuickNode.builder()
//                        .acceptCondition(() -> !Bank.isOpen())
//                        .identifier("OpenBank")
//                        .expectedNextState("Withdraw")
//                        .build()


//                // todo implicit preconditions are no one else here and no gems
//                QuickNode.builder()
//                        .identifier("EmptyInv")
//                        .expectedNextState("Mine")
//                        .acceptCondition(() -> {
//                            // is full check implicit because its followed by it
//                            if (Players.getLocal().isAnimating() || Inventory.isEmpty()) emptyLock.set(false);
//                            if (!Inventory.contains(x -> x.getName().contains("ore"))) emptyLock.set(false);
//                            if (Inventory.isFull()) emptyLock.set(true);
//                            return emptyLock.get();
//                        })
//                        .build()
//                        .setConstraint(new NodeConstraint().disallowTypes(ActionType.KEY_PRESS))
//                        .setDontResetContinuity(true), // because this is an escape, the main ontology is wait <-> mine
//                // todo configure inventory interactions to consider slot
//
//                QuickNode.builder()
//                        .acceptCondition(() -> Players.getLocal().isAnimating())
//                        .identifier("Wait")
//                        .expectedNextState("Mine")
//                        .build()
//                        .setConstraint(new NodeConstraint().disallowTypes(ActionType.KEY_PRESS))
//                        .setSleepAfterFinished(() -> Sleep.sleepUntil(() -> !Players.getLocal().isAnimating(), 4000)),
//
//                QuickNode.builder()
//                        .acceptCondition(() -> !Players.getLocal().isAnimating())
//                        .identifier("Mine")
//                        .expectedNextState("Wait")
//                        .build()
//                        .addEntityReproConfig(x -> {
//                            if (!x.getAction().getRow().getObject().contains("rocks")) return;
//                            x.setMustMatchTile(true);
//                        })
//                        .setConstraint(new NodeConstraint().disallowTypes(ActionType.KEY_PRESS))

                // non trained tasks
//                new RestockBars(),
//                new GetToEdgeville(),
//                new GetAmmoMouldOut(),
//
//                // trained
//                new WaitForCrafting(),
//                new Node(() -> !Inventory.contains(ItemID.STEEL_BAR)).addChildren(
//                        new OpenBank(),
//                        new Withdrawing()
//                ),
//                new CloseBank(),
//                new HandleCraftMenu(),
//                new FurnaceInteraction()
        );

//        trainingEnabled = true;

        trainingEnabled = false;
        if (!trainingEnabled) {
            ActionLogParser p = new ActionLogParser();
            log("Parsed");
            for (StateTransition stateTransition : p.parse()) {
                log(stateTransition);
            }
            p.populate(cballsTree, p.parse());
        } else {
            Client.getInstance().enableInputs();
        }

//        ScriptManager.getScriptManager().stop();
        // register key input listener
        log("Key listener");
        Client.getCanvas().addKeyListener(k);
    }

    KeyAdapter k = new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            log(Color.CYAN, "Key type " + e.getKeyChar());
            if (trainingEnabled) actionLog.add(new KeyPressAction(e.getKeyChar(), e.getKeyCode()));
        }
    };

    ScriptNode lastNode;

    @Override
    public int onLoop() {
        ScriptNode n = cballsTree.validChild();
        if (n == null) {
            log("null valid child");
            return 300;
        }

        String nodeId = n.getIdentifier();
        if (nodeId == null) log("Null node id " + n.getClass());

        if (lastNode == null) {
            lastNode = n;
            StateChangeAction s = new StateChangeAction(null, nodeId, false);
            log(Color.YELLOW, "Script state change " + s);
            if (trainingEnabled) actionLog.add(s);
            return 30;
        }

        if (!lastNode.getIdentifier().equals(nodeId)) {
            StateChangeAction s = new StateChangeAction(lastNode.getIdentifier(), nodeId,
                    // string id not class anymore, class would be fucked up for obf and having to make explicit classes is smell
                    lastNode.getExpectedNextState().equals(n.getIdentifier()));
            log(Color.YELLOW, "Script state change " + s);
            if (trainingEnabled) actionLog.add(s);
            lastNode = n;
        }

        if (!trainingEnabled) n.execute();
        return 10;
    }

    Color highlight = new Color(60, 125, 255, 125);
    PaintButton trainButton = new PaintButton()
            .setLabel("Train")
            .setOnClick(c -> trainingEnabled = true);
    int gpMade = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");
    Timer runtime = new Timer();

    @Override
    public void onPaint(Graphics graphics) {
        if (lastNode != null) graphics.drawString(lastNode.getIdentifier() + " ", 10, 30);
        if (lastNode != null) graphics.drawString(ScriptNode.getCurrentContinuityNumber() + " ", 10, 50);
        graphics.drawString("Gp made " + df.format(gpMade) + " hrly " + df.format(runtime.getHourlyRate(gpMade)), 10, 70);
        graphics.drawString("Runtime " + runtime.formatTime(), 10, 90);

        trainButton.paintButton(graphics);

        if (lastNode != null && trainingEnabled) {
            graphics.setColor(highlight);
            try {
                for (Rectangle trainingHighlight : lastNode.trainingHighlights()) {
                    graphics.fillRect(trainingHighlight.x, trainingHighlight.y, trainingHighlight.width, trainingHighlight.height);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onMouseMovement(MouseMovement currentMovement) {
        Logger.info("Mouse movement " + currentMovement);
        if (trainingEnabled) actionLog.add(new MouseMoveAction(currentMovement));
    }

    @Override
    public void onExit() {
//         do not for loop, you will remove the games key listeners.
        Client.getCanvas().removeKeyListener(k);

        if (trainingEnabled) {
            Gson gson = new GsonBuilder().create();
            try (FileWriter writer = new FileWriter(System.getProperty("scripts.path") + "/actionLog" + ".json")) {
                gson.toJson(actionLog, writer);
                Logger.info("Saved");
            } catch (IOException e) {
                Logger.info("E " + e.getMessage());
            }
        }
    }

    @Override
    public void onWalk(Tile destination) {
        log("Walk " + destination);
        if (trainingEnabled) actionLog.add(new WalkAction(destination, Players.getLocal().getTile()));
    }

    @Override
    public void onAction(MenuRow eventRow, int mouseX, int mouseY) {
        log(eventRow);
        if (trainingEnabled) actionLog.add(new EntityInteraction(eventRow));
    }

    @Override
    public void onCameraMovement(CameraMovement currentMovement) {
        log("Camera movement " + currentMovement);
        if (trainingEnabled) actionLog.add(new CameraRotation(currentMovement));
    }

    @Override
    public void onInventoryItemRemoved(Item item) {
        if (Bank.isOpen()) return;
        if (item.getId() == ItemID.STEEL_BAR) {
            int profit = (LivePrices.get(ItemID.CANNONBALL) * 4) - LivePrices.get(ItemID.STEEL_BAR);
            log("-1 steel bar, profit +" + df.format(profit));
            gpMade += profit;
        }

        if (item.getId() == ItemID.IRON_ORE) {
            gpMade += LivePrices.get(ItemID.IRON_BAR) - LivePrices.get(ItemID.IRON_ORE);
            anticipatoryTimer.reset();
        }
    }
}
