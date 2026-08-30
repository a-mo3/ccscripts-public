package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.misc.AdvStandardCombat;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.farming.FarmingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.herblore.HerbloreBranch;
import org.dreambot.behaviour.training.magic.EnchantDueling;
import org.dreambot.behaviour.training.magic.EnchantRecoils;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerLoadouts;
import org.dreambot.behaviour.training.slayer.SlayerTaskMap;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.SpectreSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class SpectresScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<SpectreSettings> tree = new FractalRoot<>(new SpectreSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    Area TOWER_SPECTRES = new Area(
            new Tile(3408, 3552, 1),
            new Tile(3419, 3552, 1),
            new Tile(3419, 3547, 1),
            new Tile(3431, 3547, 1),
            new Tile(3431, 3536, 1),
            new Tile(3423, 3533, 1),
            new Tile(3415, 3534, 1),
            new Tile(3412, 3531, 1),
            new Tile(3408, 3531, 1),
            new Tile(3405, 3535, 1),
            new Tile(3405, 3538, 1),
            new Tile(3417, 3539, 1),
            new Tile(3417, 3543, 1),
            new Tile(3408, 3543, 1));
    // reset every time we get the bag full message
    Timer sackFullRecently = new Timer(2400);
    boolean hasHerbInSack = true;

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.AMULET_OF_GLORY
        };

        SlayerTaskMap.minLootValue = tree.getSettings().minLootValue;
        tree.setSimpleName("cCSpectreFarm");
        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

                new Fractal(() -> Skills.getRealLevel(Skill.SLAYER) < 60)
                        .setSimpleName("Training")
                        .addChildren(
                                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().preSlayerCombatTarget)
                                        .setSimpleName("Pre Slayer Combat Training"),
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget)
                                        .setSimpleName("Prayer Training"),

                                new ImpCatcher().setSimpleName("Impcatcher")
                                        .setPrependLogic(() -> {
                                            if (Client.isDynamicRegion()) {
                                                Magic.castSpell(Normal.HOME_TELEPORT);
                                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                                            }
                                            return false;
                                        }),
                                new EnchantRecoils().setSimpleName("Enchant Recoils "),
                                new EnchantDueling().setSimpleName("Enchant Duelings "),
                                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS)
                                        .setSimpleName("Burn logs need it for slayer"),

                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 60)
                                        .setPrependLogic(() -> {
                                            if (!Combat.isAutoRetaliateOn()) {
                                                if (Widgets.isOpen()) Widgets.closeAll();
                                                Combat.toggleAutoRetaliate(true);
                                            }

                                            return false;
                                        })
                                        .setSimpleName("Slayer Until Spectres unlocked")
                        ),
                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().preSlayerCombatTarget)
                        .setSimpleName("Pre Slayer Combat Training"),
                new HerbloreBranch(() -> Skills.getRealLevel(Skill.HERBLORE) < 58, false),
                new FarmingBranch(() -> Bank.isCached() && !OwnedItems.contains(ItemID.HERB_SACK)).setSimpleName("Farming"),
                new RestlessGhost().setSimpleName("Restless ghost"),
                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                new PriestInPeril().setSimpleName("Priest in peril"),

                new MuleOff()
                        .setSimpleName("Mule Off"),
                new AdvStandardCombat(TOWER_SPECTRES, "Aberrant spectre", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > tree.getSettings().minLootValue || x.getName().contains("Grimy"))

                        .setOverhead(Prayer.PROTECT_FROM_MAGIC)
                        .setFlickPrayer(true)
                        .setFlickTiming(1000)
                        .setStyleSupplier(() -> {
                            int atk = Skills.getRealLevel(Skill.ATTACK);
                            int def = Skills.getRealLevel(Skill.DEFENCE);
                            int str = Skills.getRealLevel(Skill.STRENGTH);

                            // force whip quickly
                            if (atk < 70) {
                                return str > atk ? CombatStyle.ATTACK : CombatStyle.STRENGTH;
                            }
                            return def < 60 ? CombatStyle.DEFENCE : CombatStyle.STRENGTH;
                        })
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.PRAYER_LOADOUT)
                                .addItem(EquipmentSlot.HAT, ItemID.NOSE_PEG)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 1, 4)
                                .setRefill(200)
                                .addItem(ItemVariants.PRAYER_POTION, 8, 8)
                                .setEnabledCondition(() -> ItemVariants.PRAYER_POTION.getItem() == null || BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 20)
                                .setRefill(50)
                                .addItem(ItemID.HERB_SACK)
                                .addItem(ItemID.FENKENSTRAINS_CASTLE_TELEPORT, 1, 5)
                                .setEnabledCondition(() -> BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 50)
                                .setStrictSupplier(() -> BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 50)
                        )
                        .setPrependLogic(() -> {
                            if (Inventory.isFull()) {
                                if (!sackFullRecently.finished()) {
                                    Logger.info("Full, Bank!");
                                    new BankAllInventoryEvent().execute();
                                } else {
                                    Inventory.interact(ItemID.HERB_SACK);
                                    hasHerbInSack = true;
                                }
                            }

                            return false;
                        })
                        .setSimpleName("Spectres")
        );
//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (!ChangeAlchWarning.setHighAlchWarning(500_000)) {
            Logger.info("Setting alch warning price");
            return ReactionGenerator.getNormal();
        }


        if (!Combat.isInWild() && !Bank.isCached()) {
            if (!SpecialWalker.leaveAvasRoom()) return ReactionGenerator.getNormal();
            if (Bank.isOpen()) Bank.close();
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        }

        if (hasHerbInSack && !Inventory.isFull() && BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 50) {
            Inventory.interact(ItemID.HERB_SACK, "Empty");
        }

//        GameObjects.all(x -> x.getId() == FarmTheTithe.EMPTY_ALLOTMENT_ID)
//                .forEach(x -> {
//                    Logger.info("Allotment to instance " + x.getTile());
//                    Logger.info(Region.fromInstance(x.getTile()));
//                });

        return tree.run();
//        return ReactionGenerator.getNormal();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public String[] getPaintInfo() {

        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        Player local = Players.getLocal();
        String target = "";
        if (local != null) {
            Character tgt = local.getInteractingCharacter();
            if (tgt != null) target = tgt.getName();
        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
        };
    }

    @Override
    public String getScriptName() {
        return "cCSpectresFarm";
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


    public void onInventoryItemAdded(Item item) {
        if (!TOWER_SPECTRES.contains(Players.getLocal())) return;
        Logger.info("item added");
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!TOWER_SPECTRES.contains(Players.getLocal())) return;
        Logger.info("item changed");
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (!TOWER_SPECTRES.contains(Players.getLocal())) return;
        Logger.info("item swapped");
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onScriptPaint(Graphics g) {
//        GameObjects.all(x -> x.getId() == FarmTheTithe.EMPTY_ALLOTMENT_ID)
//                .forEach(x -> {
//                    g.drawString(Region.fromInstance(x.getTile()) + " tile", x.getCenterPoint().x, x.getCenterPoint().y);
//                    g.drawPolygon(x.getTile().getPolygon());
//                });

    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        // todo sack full message reset timer
        String msg = message.getMessage();
        if (msg.contains("have no grimy herbs in your inventory") || msg.contains("for the herbs")) {
            sackFullRecently.reset();
        }
        if (msg.contains("herb sack is empt")) {
            hasHerbInSack = false;
        }
    }
}
