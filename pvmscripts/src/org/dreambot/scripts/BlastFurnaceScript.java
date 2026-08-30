package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.WebNodeType;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.blastfurnace.BlastFurnaceBranch;
import org.dreambot.behaviour.method.blastfurnace.BlastFurnaceModes;
import org.dreambot.behaviour.method.blastfurnace.BlastFurnaceRestock;
import org.dreambot.behaviour.method.blastfurnace.CoalBag;
import org.dreambot.behaviour.method.motherlode.EnterMLM;
import org.dreambot.behaviour.method.motherlode.GetCoalBag;
import org.dreambot.behaviour.method.motherlode.MLMMining;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.doricsquest.DoricsQuest;
import org.dreambot.behaviour.quests.icegloves.GetIceGloveBranch;
import org.dreambot.behaviour.quests.theknightssword.TheKnightsSword;
import org.dreambot.behaviour.training.mining.MixedMining;
import org.dreambot.behaviour.training.smithing.SmithingBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.events.EventExitCondition;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.BuyLimitManager;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.muling.Log;
import org.dreambot.scriptdata.BlastFurnaceSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;
import org.dreambot.webnodes.KeldagrimNodes;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class BlastFurnaceScript extends PseudoScript implements ItemContainerListener {
    FractalRoot tree = new FractalRoot(new BlastFurnaceSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();

    private BlastFurnaceSettings getSettings() {
        return SettingsRepository.getSetting(getScriptName(), new BlastFurnaceSettings());
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

        KeldagrimNodes.addNodes();

        MuleOff.LOOT = new int[]{
                ItemID.IRON_BAR,
                ItemID.BRONZE_BAR,
                ItemID.STEEL_BAR,
                ItemID.ADAMANTITE_BAR,
                ItemID.RUNITE_BAR,
                ItemID.MITHRIL_BAR,
                ItemID.RING_OF_WEALTH,
                ItemID.SAPPHIRE_RING,
                ItemID.RING_OF_RECOIL,
                ItemID.CHAOS_RUNE,
                ItemID.DRAGON_BONES,
                ItemID.TELEPORT_TO_HOUSE
        };

        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.IRON_BAR,
                ItemID.BRONZE_BAR,
                ItemID.STEEL_BAR,
                ItemID.ADAMANTITE_BAR,
                ItemID.RUNITE_BAR,
                ItemID.MITHRIL_BAR,
                ItemID.RING_OF_WEALTH,
        };

        WebFinder wf = WebFinder.getWebFinder();

        AbstractResponseEvent.addGlobalExitCondition(new EventExitCondition(() -> {
            if (Inventory.contains(ItemID.PAYDIRT) && BankLocation.MOTHERLODE_MINE.distance(Players.getLocal().getTile()) > 75) {
                Log.info("Far away from mine, dropping paydirt");
                Inventory.dropAll(ItemID.PAYDIRT);
            }
            return false;
        }, "Drop pay dirt"));

        EntranceWebNode khorvakStairs = new EntranceWebNode(2820, 3484, 0, "Stairs", "Climb-down");
        wf.getNearest(khorvakStairs, 15).addDualConnections(khorvakStairs);
        wf.addWebNode(khorvakStairs);
        EntranceWebNode khorvakStairsExit = new EntranceWebNode(2820, 9883, 0, "Stairs", "Climb-up");

        AbstractWebNode awb = wf.getNearest(khorvakStairsExit);
        Logger.info("Awb " + awb.getType());
        awb.getConnections().stream().filter(w -> w.getType() == WebNodeType.ENTRANCE_NODE).collect(Collectors.toList())
                .forEach(wf::removeNode);

        wf.addWebNode(khorvakStairsExit);
        khorvakStairsExit.addDualConnections(khorvakStairs);
        awb.addDualConnections(khorvakStairsExit);
        // delete death plateu nodes so you will walk to keldagrim from camelot
        Area deathPlateuRemoveNodes = new Area(2808, 3616, 2885, 3552);
        List<AbstractWebNode> n = WebFinder.getWebFinder().getAll();
        n.removeIf(x -> x != null && x.getTile() != null && deathPlateuRemoveNodes.contains(x.getTile()));


        tree.setSimpleName("cCBlastFurnace");
        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Empty death coffer"),

                new Fractal(() -> Skills.getRealLevel(Skill.SMITHING) < 20 && !FreeQuest.THE_KNIGHTS_SWORD.isFinished()).addChildren(
                        new DoricsQuest().setSimpleName("Dorics Quest"),
                        new TheKnightsSword().setSimpleName("Knights sword")
                ),
                new SmithingBranch(() -> Skills.getRealLevel(Skill.SMITHING) < getSettings().mode.getLevel())
                        .setSimpleName("Getting required smithing level"),
                // todo handle an opened coal bag
                new Fractal(() -> Bank.isCached() && !OwnedItems.contains(ItemID.COAL_BAG_12019))
                        .addChildren(
                                new MixedMining(() -> Skills.getRealLevel(Skill.MINING) < 30).setSimpleName("Mining training"),
                                new GetCoalBag().setSimpleName("Buy coal bag"),
                                new MLMMining().setSimpleName("Mining"),
                                new EnterMLM(false).setSimpleName("Enter mlm")
                        )
                        .setSimpleName("Get coal bag"),

                new TalkToFractal(() -> PlayerSettings.getBitValue(571) < 5 || Client.isInCutscene(),
                        new Tile(2842, 10129),
                        () -> NPCs.closest("Dwarven Boatman"))
                        .setDialogueOptions("Yes", "deal", "idea")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5)
                                .addItem(ItemID.VARROCK_TELEPORT, 1, 5)
                        )
                        .setSimpleName("Get access to keldagrim"),

                new GetIceGloveBranch(() -> Bank.isCached() && !OwnedItems.contains(ItemID.ICE_GLOVES))
                        .setSimpleName("Get ice gloves"),

                new MuleOff()
                        .setSimpleName("Mule off"),

                new BlastFurnaceRestock(getSettings().mode.getRequiredOres())
                        .setSimpleName("Restock/Break"),
                new Fractal(() -> getSettings().mode == BlastFurnaceModes.PROGRESSIVE)
                        .addChildren(
                                new BlastFurnaceBranch(() -> Skills.getRealLevel(Skill.SMITHING) < 50, BlastFurnaceModes.STEEL).setSimpleName("Steel"),
                                new BlastFurnaceBranch(() -> Skills.getRealLevel(Skill.SMITHING) < 70, BlastFurnaceModes.MITHRIL).setSimpleName("Mithril"),
                                new BlastFurnaceBranch(() -> Skills.getRealLevel(Skill.SMITHING) < 85, BlastFurnaceModes.ADAMANT).setSimpleName("Adamant"),
                                new BlastFurnaceBranch(() -> true, BlastFurnaceModes.RUNE).setSimpleName("Rune")
                        )
                        .setSimpleName("Progressive"),
                new BlastFurnaceBranch(() -> true, getSettings().mode)
                        .setSimpleName("Blast Furnace")
        );
//        new AIAntiban();
    }

    boolean wasInWild;

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (!Bank.isCached()) {
            Logger.info("Get bank cache");
            if (Bank.isOpen()) Bank.updateCache();
            if (Walking.shouldWalk()) Bank.open();
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.PAYDIRT) && BankLocation.MOTHERLODE_MINE.distance(Players.getLocal().getTile()) > 75) {
            Log.info("Far away from mine, dropping paydirt");
            Inventory.dropAll(ItemID.PAYDIRT);
        }

        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public String[] getPaintInfo() {
        StringBuilder oreLimits = new StringBuilder();
        for (Integer i : getSettings().mode.getRequiredOres()) {
            if (BuyLimitManager.get() == null) continue;
            oreLimits.append(String.format("%s: %s/13,000 ",
                    new Item(i, 0).getName(),
                    df.format(BuyLimitManager.get().getBrought(i)))
            );
        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Coal bag stock " + CoalBag.getStock(),
                oreLimits.toString()
        };
    }

    @Override
    public String getScriptName() {
        return "cCBlastFurnaceFarm";
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


    public static final Area BLAST_FURNACE_AREA = new Area(1934, 4975, 1957, 4955);

    public void onInventoryItemAdded(Item item) {
        Logger.info("item added");
        if (!item.getName().contains("bar")) return;
        if (item.isNoted()) return;
        if (!BLAST_FURNACE_AREA.contains(Players.getLocal())) return;
        grossGp += (getBarNetValue(item.getId()) + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("item changed");
        if (!incoming.getName().contains("bar")) return;
        if (incoming.isNoted()) return;
        if (!BLAST_FURNACE_AREA.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        grossGp += (getBarNetValue(incoming.getId()) + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        Logger.info("item swapped");
        if (!incoming.getName().contains("bar")) return;
        if (!BLAST_FURNACE_AREA.contains(Players.getLocal())) return;
        if (incoming.isNoted()) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (getBarNetValue(incoming.getId()) + 1) * quantity;
    }

    private int getBarNetValue(int barID) {
        int coalPrice = LivePrices.get(ItemID.COAL);
        if (ItemID.ADAMANTITE_BAR == barID)
            return LivePrices.get(ItemID.ADAMANTITE_BAR) - LivePrices.get(ItemID.ADAMANTITE_ORE) - (coalPrice * 3);
        if (ItemID.RUNITE_BAR == barID)
            return LivePrices.get(ItemID.RUNITE_BAR) - LivePrices.get(ItemID.RUNITE_ORE) - (coalPrice * 4);
        if (ItemID.STEEL_BAR == barID)
            return LivePrices.get(ItemID.STEEL_BAR) - LivePrices.get(ItemID.IRON_ORE) - (coalPrice);
        if (ItemID.MITHRIL_BAR == barID)
            return LivePrices.get(ItemID.MITHRIL_BAR) - LivePrices.get(ItemID.MITHRIL_ORE) - (coalPrice * 2);
        return 100;
    }
}
