package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
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
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
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
import org.dreambot.behaviour.training.slayer.behaviour.StandardCombat;
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
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.TurothSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class TurothScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<TurothSettings> tree = new FractalRoot<>(new TurothSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    final Area TUROTH = new Area(2713, 10015, 2732, 9993);

    List<Integer> TurothLoot = Arrays.asList(
            ItemID.ADAMANT_PLATEBODY,
            ItemID.MITHRIL_KITESHIELD,
            ItemID.RUNE_LONGSWORD,
            ItemID.RUNE_AXE,
            ItemID.LEAFBLADED_BATTLEAXE,
            ItemID.MYSTIC_ROBE_TOP,
            ItemID.NATURE_RUNE
    );

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.AMULET_OF_GLORY
        };

        SlayerTaskMap.minLootValue = tree.getSettings().minLootValue;
        tree.setSimpleName("cCTuroths");
        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

                new Fractal(() -> Skills.getRealLevel(Skill.SLAYER) < 55)
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

                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 55)
                                        .setPrependLogic(() -> {
                                            if (!Combat.isAutoRetaliateOn()) {
                                                if (Widgets.isOpen()) Widgets.closeAll();
                                                Combat.toggleAutoRetaliate(true);
                                            }
                                            return false;
                                        })
                                        .setSimpleName("Slayer Until Turoth unlocked @ 55")
                        ),
                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().preSlayerCombatTarget)
                        .setSimpleName("Pre Slayer Combat Training"),
                new HerbloreBranch(() -> Skills.getRealLevel(Skill.HERBLORE) < 58, false),
                new FarmingBranch(() -> Bank.isCached() && !OwnedItems.contains(ItemID.HERB_SACK)).setSimpleName("Farming"),
                new MuleOff()
                        .setSimpleName("Mule Off"),
                new StandardCombat(TUROTH, "Turoth", ItemID.LOBSTER)
                        .setLootStrategy(x -> TurothLoot.contains(x.getId()) || x.getItem().isNoted() || x.getId() == ItemID.COINS_995 || x.getItem().getLivePrice() > 400)
                        .setAlchableIds(
                                ItemID.ADAMANT_PLATEBODY,
                                ItemID.MITHRIL_KITESHIELD,
                                ItemID.RUNE_LONGSWORD,
                                ItemID.RUNE_AXE,
                                ItemID.LEAFBLADED_BATTLEAXE,
                                ItemID.MYSTIC_ROBE_BOTTOM_LIGHT
                        )
                        .setEquipmentLoadout(new EquipmentLoadout(SlayerLoadouts.LEAF_BLADED)
                                .addItem(EquipmentSlot.HAT, ItemID.OBSIDIAN_HELMET) // not on task so we add this here rather than slayer loadouts
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.LOBSTER, 1, 22)
                                .setRefill(3_000)
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 10)
                                .setRefill(100)
                                .addItem(ItemID.NATURE_RUNE, 100)
                                .setRefill(500)
                                .setEnabledCondition(() -> !TUROTH.contains(Players.getLocal()))
                                .addItem(ItemID.FIRE_RUNE, 500)
                                .setRefill(2500)
                                .setEnabledCondition(() -> !TUROTH.contains(Players.getLocal()))
                                .addItem(ItemID.HERB_SACK)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.HERB_SACK))
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
                        .setSimpleName("Turoths")

        );
//        new AIAntiban();
    }

    Area TITHE_FARM_ENTRANCE = new Area(1793, 3508, 1808, 3496);
    boolean hasHerbInSack = false;
    Timer sackFullRecently = new Timer(2400);

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (!ChangeAlchWarning.setHighAlchWarning(500_000)) {
            Logger.info("Setting alch warning price");
            return ReactionGenerator.getNormal();
        }

        if (hasHerbInSack && !Inventory.isFull() && BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 50) {
            Inventory.interact(ItemID.HERB_SACK, "Empty");
        }

        if (!Client.isDynamicRegion() && !TITHE_FARM_ENTRANCE.contains(Players.getLocal())) {
            if (Inventory.contains(ItemID.BOLOGANO_SEED, ItemID.GOLOVANOVA_SEED, ItemID.LOGAVANO_SEED)) {
                Logger.info("Dropping seeds");
                Inventory.dropAll(ItemID.BOLOGANO_SEED, ItemID.GOLOVANOVA_SEED, ItemID.LOGAVANO_SEED);
                return ReactionGenerator.getNormal();
            }
        }

        return tree.run();
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
                "cCTuroth: " + runtime.formatTime(),
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Time Until Mule off: " + muleOff,
                String.format("Earned %s (%s / hr))", df.format(grossGp), df.format(runtime.getHourlyRate(grossGp))),
                "target: " + target,
        };
    }

    @Override
    public String getScriptName() {
        return "cCTurothFarm";
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
        if (!TUROTH.contains(Players.getLocal())) return;
        Logger.info("item added");
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!TUROTH.contains(Players.getLocal())) return;
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
        if (!TUROTH.contains(Players.getLocal())) return;
        Logger.info("item swapped");
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
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
