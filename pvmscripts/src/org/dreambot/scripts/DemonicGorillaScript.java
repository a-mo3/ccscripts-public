package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.method.gorillas.DemonicGorillaBranch;
import org.dreambot.behaviour.method.gorillas.GoToGorillas;
import org.dreambot.behaviour.method.pirates.RechargeAtFerox;
import org.dreambot.behaviour.misc.*;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.discordwebhook.scouter.ScoutFractal;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.pktrie.PKTrie;
import org.dreambot.scriptdata.GorillaSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;

public class DemonicGorillaScript extends PseudoScript implements PaintInfo, ChatListener, ItemContainerListener, SpawnListener {
    Timer runtime = new Timer();
    FractalRoot<GorillaSettings> tree = new FractalRoot<>(new GorillaSettings(), getScriptName());
    public static int deathCount = 0;
    boolean shouldHop = false;
    int grossGp = 0;
    boolean needsToRecharge = false;
    Area GORILLA_AREA = GoToGorillas.GORILLA_AREAS[Calculations.random(GoToGorillas.GORILLA_AREAS.length)];

    public void init() {
        Client.getInstance().addEventListener(this);

        MuleOff.LOOT = new int[]{
                ItemID.ZENYTE_SHARD,
                ItemID.BALLISTA_LIMBS,
                ItemID.BALLISTA_SPRING,
                ItemID.LIGHT_FRAME,
                ItemID.HEAVY_FRAME,
                ItemID.MONKEY_TAIL,

                ItemID.RUNE_PLATELEGS,
                ItemID.RUNE_PLATESKIRT,
                ItemID.RUNE_CHAINBODY,
                ItemID.DRAGON_SCIMITAR,

                ItemID.LAW_RUNE,
                ItemID.DEATH_RUNE,
                ItemID.RUNITE_BOLTS,
                ItemID.GRIMY_KWUARM,
                ItemID.GRIMY_CADANTINE,
                ItemID.GRIMY_DWARF_WEED,
                ItemID.GRIMY_LANTADYME,
                ItemID.DRAGONFRUIT_TREE_SEED,
                ItemID.MAGIC_SEED,
                ItemID.SNAPDRAGON_SEED,
                ItemID.TORSTOL_SEED,
                ItemID.RANARR_SEED,

                ItemID.SARADOMIN_BREW2,
                ItemID.JAVELIN_SHAFT,
                ItemID.RUNE_JAVELIN_TIPS,
                ItemID.DRAGON_JAVELIN_TIPS,
                ItemID.ADAMANTITE_BAR,
                ItemID.DIAMOND,
                ItemID.RUNITE_BAR
        };

        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        Area FULL_GORILLA = new Area(2064, 5689, 2166, 5633);
        // mule off items should be good with default
        tree.setSimpleName("cCGorillas")
                .addChildren(
                        new AutoProggy().setSimpleName("Auto proggy"),
                        new AntibanFractal().setSimpleName("Antiban"),
                        new ScoutFractal(),
                        new ReactionSettingsFractal(),
                        new PutPetAway(),
                        new TutorialTree().setSimpleName("Tutorial island"),
                        new EmptyDeathsCoffer().setSimpleName("Empty death"),
                        new GetMembershipBranch(),

                        new LampHandler().setSimpleName("lamp handler"),

                        new MuleOff().setSimpleName("Mule off"),
                        new GetMoreAvas().setSimpleName("More avas"),
                        new FixBarrows().setSimpleName("Fix barrows"),
                        new RechargeBlowpipe().setSimpleName("Blowpipe recharge"),
                        new RechargeAtFerox(),
                        new GoToGorillas(GORILLA_AREA)
                                .setInventoryLoadout(tree.getSettings().gorillaLoadout.inventoryLoadout)
                                .setEquipmentLoadout(tree.getSettings().gorillaLoadout.equipmentLoadout)
                                .setLoadoutCondition(() -> !FULL_GORILLA.contains(Players.getLocal()))
                                .setSimpleName("Go Gorillas"),

                        new DemonicGorillaBranch(() -> true, GORILLA_AREA, tree.getSettings().flickPrayer).setSimpleName("Kill gorillas")
                );
    }

    public static boolean hasLootInBag = true;
    private long loopSpd;
    private long lastTimestamp;
    boolean hasLoadedTrie = false;
    Timer trieRefresh = new Timer(60 * 1000 * 45);
    Timer playerLogTimer = new Timer(60 * 1000);

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        // todo anti pk
//        if (!ScriptSettings.getSettingsData().disablePkList && trieRefresh.finished() || !hasLoadedTrie) {
//            PKTrie.refreshPkerList();
//            trieRefresh.reset();
//            hasLoadedTrie = true;
//        }

        if (ClientSettings.isFeroxExitWarningEnabled()) {
            Logger.info("Disable ferox exit warnings");
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleFeroxExitWarning(false);
            return ReactionGenerator.getNormal();
        }

        loopSpd = System.currentTimeMillis() - lastTimestamp;
        lastTimestamp = System.currentTimeMillis();
        if (ClientSettings.isAcceptAidEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }


        if (!Combat.isAutoRetaliateOn()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Combat.toggleAutoRetaliate(true);
            return ReactionGenerator.getNormal();
        }

//        Player attackingMe = Players.closest(x -> x.isSkulled() && x.isInteracting(Players.getLocal()));
//        if (Combat.isInWild() && Players.getLocal().isInCombat() && attackingMe != null) {
//            Logger.info("Being attack by " + attackingMe.getName());
//            Logger.info("Level: " + attackingMe.getLevel());
//            attackingMe.getEquipment().forEach(x -> Logger.info("Equipment " + x.getId() + " " + x.getName()));
//        }


        Player attckingMe = Players.closest(x -> x.isSkulled() && x.isInteracting(Players.getLocal()));
        if (Combat.isInWild() && Players.getLocal().isInCombat() && attckingMe != null && playerLogTimer.finished()) {
            Logger.info("Being attack by " + attckingMe.getName());
            Logger.info("Level: " + attckingMe.getLevel());
            Logger.info("My Level: " + Combat.getCombatLevel());
            Logger.info("Wilderness level: " + Combat.getWildernessLevel());
//            Logger.info("Predicted: " + AntiPkNode.canAttackMe(attckingMe));
            attckingMe.getEquipment().forEach(x -> Logger.info("Equipment " + x.getId() + " " + x.getName()));
            playerLogTimer.reset();
            PKTrie.reportPker(attckingMe.getName());
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            Logger.info("Disabling hop confirmations");
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getQuick();
        }


        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getNormal();
        }

        if (shouldHop && Client.hasMembersAccess() && !Players.getLocal().isInCombat()) {
            if (WorldHopper.hopWorld(
                    Worlds.getRandomWorld(x -> !x.isF2P() && x.isNormal() && x.getWorld() != 401 && x.getMinimumLevel() < Combat.getCombatLevel())
            )) shouldHop = false;
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.LOOTING_BAG_CLOSED)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Opening looting bag");
            Inventory.interact(ItemID.LOOTING_BAG_CLOSED, "Open");
        }

        if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null && hasLootInBag) {
            Logger.info("Emptying looting bag");
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) BankUtil.openClosest();
                return ReactionGenerator.getQuick();
            }

            EmptyLootingBagEvent.Response r = new EmptyLootingBagEvent().executed();
            Logger.info("Empty bag: " + r);
            if (r == EmptyLootingBagEvent.Response.BAG_EMPTY) hasLootInBag = false;
            return ReactionGenerator.getQuick();
        }

        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 30) {
            Walking.toggleRun();
        }

        if (ClientSettings.isSellPriceWarningEnabled()) {
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleSellPriceWarning(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isBuyPriceWarningEnabled()) {
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleBuyPriceWarning(false);
            return ReactionGenerator.getNormal();
        }
        return tree.run();
    }

    @Override
    public String[] getPaintInfo() {
        String muleOff = "-";
        Player local = Players.getLocal();
        String target = "";
        if (local != null) {
            Character tgt = local.getInteractingCharacter();
            if (tgt != null) target = tgt.getName();
        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Deaths: " + deathCount,
                "LoopSpd: " + loopSpd,
                "target: " + target,
        };
    }

    @Override
    public String getScriptName() {
        return "cCDemonicGorillas";
    }

    @Override
    public int getMoneyMade() {
        return grossGp;
    }

    @Override
    public Timer getRuntime() {
        return runtime;
    }

    @Override
    public long getMuleOffTime() {
        return MuleOff.timer == null ? 0 : MuleOff.timer.remaining();
    }

    @Override
    public Fractal getFractal() {
        return tree;
    }

    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        DecimalFormat format = new DecimalFormat("00");
        return String.format("%s:%s:%s",
                format.format(hours),
                format.format(minutes),
                format.format(seconds));
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            shouldHop = true;
            deathCount++;
        }

        if (message.getMessage().toLowerCase().contains("not enough revenant ether")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("has run out of revenant")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("chainmace is out of charges")) {
            needsToRecharge = true;
        }
        if (message.getMessage().toLowerCase().contains("giving it a total of")) {
            needsToRecharge = false;
        }
    }

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossGp += item.getLivePrice() * item.getAmount();
    }


    @Override
    public void onInventoryItemAdded(Item item) {
        if (!GORILLA_AREA.contains(Players.getLocal())) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!GORILLA_AREA.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity >= 0) return;

        grossGp += incoming.getLivePrice() * quantity;
    }
}
