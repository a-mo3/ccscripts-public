package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.clan.chat.ClanChat;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.friends.JoinClanChat;
import org.dreambot.behaviour.method.lavadragons.LavaDragonNodes;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.rfd.GetRockCake;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.agility.GetGoblinHammer;
import org.dreambot.behaviour.training.agility.wild.TickWilderness;
import org.dreambot.behaviour.training.agility.wild.WildernessAgilityMode;
import org.dreambot.behaviour.training.agility.wild.WildyCourseExit;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.comms.impl.agility.BoxingClient;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.discordwebhook.scouter.ScoutFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.WildernessAgilitySettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;

public class WildernessAgilityScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<WildernessAgilitySettings> tree = new FractalRoot<>(new WildernessAgilitySettings(), getScriptName());

    @Override
    public void onArgs(String... args) {
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

        MuleOff.LOOT = new int[]{
                ItemID.BLIGHTED_ANGLERFISH,
                ItemID.BLIGHTED_MANTA_RAY,
                ItemID.BLIGHTED_KARAMBWAN,
                ItemID.BLIGHTED_SUPER_RESTORE4,

                ItemID.ADAMANT_PLATEBODY,
                ItemID.RUNE_MED_HELM,
                ItemID.ADAMANT_FULL_HELM,
                ItemID.ADAMANT_PLATELEGS,
                ItemID.MITHRIL_CHAINBODY,
                ItemID.MITHRIL_PLATELEGS,
                ItemID.MITHRIL_PLATESKIRT,
                ItemID.STEEL_PLATEBODY,

                ItemID.RUNE_CHAINBODY,
                ItemID.RUNE_KITESHIELD,
        };

        Client.getInstance().addEventListener(this);
        LavaDragonNodes.init();
        tree.setSimpleName("cCWildernessAgil");
        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ReactionSettingsFractal(),
                new ScoutFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("Lamp handler"),
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),
                new GetMembershipBranch().setSimpleName("Get Membership"),

                new AgilityBranch(() -> Skills.getRealLevel(Skill.AGILITY) < 52),
                new MuleOff().setSimpleName("Mule Off"),

//                GenericCombatBranch.builder()
//                        .lootFilter(x -> x.getId() == ItemID.LOOTING_BAG_CLOSED || x.getId() == ItemID.LOOTING_BAG_OPENED)
//                        .area(ratArea)
//                        .mobFilter(x -> ratArea.contains(x) && x.getName().equals("Rat"))
//
//                        .flickPrayers(false)
//                        .disablePrayer(true)
//                        .build()
//                        .init()
//                        .setAcceptCondition(() -> Bank.isCached() && !OwnedItems.contains(ItemVariants.LOOTING_BAG))
//                        .setSimpleName("Get looting bag")
//                        .setInventoryLoadout(new InventoryLoadout()
//                                .addItem(ItemVariants.BURNING_AMULET)
//                                .addItem(ItemID.JUG_OF_WINE, 15).setRefill(50)
//                                .setStrict(true))
//                        .setEquipmentLoadout(new EquipmentLoadout()
//                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
//                                .setStrict(true)),

                new WildyCourseExit(() -> ItemVariants.LOOTING_BAG.getItem() != null && LootingBag.value() >= tree.getSettings().exitLootValue,
                        tree.getSettings().mode)
                        .setSimpleName("Exit with loot"),

//                new WildernessCourse(() -> true)
                new GetRockCake(() -> tree.getSettings().mode == WildernessAgilityMode.SUICIDE)
                        .setSimpleName("Get rock cake for killing yourself"),

                new JoinClanChat(() -> tree.getSettings().mode == WildernessAgilityMode.BH_RAG_WORLD
                        && tree.getSettings().clanChat != null
                        && !tree.getSettings().clanChat.isEmpty()
                        && !tree.getSettings().clanChat.equalsIgnoreCase(ClanChat.getOwner()),
                        tree.getSettings().clanChat).setSimpleName("Join CC " + tree.getSettings().clanChat),

                new GetGoblinHammer(() -> tree.getSettings().mode == WildernessAgilityMode.BOXING
                        && !OwnedItems.contains(ItemID.CURSED_GOBLIN_HAMMER)).setSimpleName("Get goblin hammer"),
                new TickWilderness(() -> true, tree.getSettings())
                        .setLoadoutCondition(() -> !Combat.isInWild() || !Inventory.contains(ItemID.KNIFE)) // check for knife so we still bank after getting loot bag
                        .setEventBreakCondition(Combat::isInWild)
                        .setInventoryLoadout(tree.getSettings().payFee ? new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 150_000)
                                .addItem(ItemID.ARAXYTE_VENOM_SACK, 1)
                                .setRefill(10)
                                .setEnabledCondition(() -> tree.getSettings().mode == WildernessAgilityMode.SUICIDE)
                                .addItem(ItemID.DWARVEN_ROCK_CAKE_7510)
                                .setEnabledCondition(() -> tree.getSettings().mode == WildernessAgilityMode.SUICIDE)
                                .addItem(ItemID.SARDINE, 20).setRefill(200) // sardines heal a 4 so its good for staying on low hp
                                .setEnabledCondition(() -> tree.getSettings().mode == WildernessAgilityMode.SUICIDE)
                                .addItem(ItemID.BLIGHTED_MANTA_RAY, 20).setRefill(200) // sardines heal a 4 so its good for staying on low hp
                                .setEnabledCondition(() -> tree.getSettings().mode == WildernessAgilityMode.BH_RAG_WORLD)

                                .addItem(ItemID.KNIFE).setRefill(5)
                                .addItem(ItemVariants.LOOTING_BAG).setEnabledCondition(() -> Bank.isCached() && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                                .addItem(ItemID.JUG_OF_WINE, 15).setRefill(100).setEnabledCondition(() -> tree.getSettings().mode != WildernessAgilityMode.SUICIDE && Skills.getRealLevel(Skill.HITPOINTS) < 40)
                                .setStrict(true) :
                                // coinless loadout when not paying fee
                                new InventoryLoadout()
                                        .addItem(ItemID.ARAXYTE_VENOM_SACK, 1)
                                        .setRefill(10)
                                        .setEnabledCondition(() -> tree.getSettings().mode == WildernessAgilityMode.SUICIDE)
                                        .addItem(ItemID.DWARVEN_ROCK_CAKE_7510)
                                        .setEnabledCondition(() -> tree.getSettings().mode == WildernessAgilityMode.SUICIDE)
                                        .addItem(ItemID.SARDINE, 20).setRefill(200) // sardines heal a 4 so its good for staying on low hp
                                        .setEnabledCondition(() -> tree.getSettings().mode == WildernessAgilityMode.SUICIDE)
                                        .addItem(ItemID.BLIGHTED_MANTA_RAY, 20).setRefill(200) // sardines heal a 4 so its good for staying on low hp
                                        .setEnabledCondition(() -> tree.getSettings().mode == WildernessAgilityMode.BH_RAG_WORLD)

                                        .addItem(ItemID.KNIFE).setRefill(5)
                                        .addItem(ItemVariants.LOOTING_BAG).setEnabledCondition(() -> Bank.isCached() && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                                        .addItem(ItemID.JUG_OF_WINE, 15).setRefill(100).setEnabledCondition(() -> tree.getSettings().mode != WildernessAgilityMode.SUICIDE && Skills.getRealLevel(Skill.HITPOINTS) < 40)
                                        .setStrict(true)

                        )
                        .setEquipmentLoadout(tree.getSettings().mode == WildernessAgilityMode.BH_RAG_WORLD ? tree.getSettings().ragLoadout.getEquipmentLoadout()
                                : new EquipmentLoadout()
                                // todo on suicide mode bring some armour
                                .addItem(EquipmentSlot.WEAPON, ItemID.CURSED_GOBLIN_HAMMER).enabledIfOwned()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setEnabledCondition(() -> !Combat.isInWild()).setRefill(10)
                                .setStrict(true)
                        )
                        .setSimpleName("Course")
        );
        // make it more than 1 lap per check
        LootingBag.lastCacheTimer = new Timer(60 * 1000 * 3);

    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (ClientSettings.areItemPilesOnDeathEnabled()) {
            if (Bank.isOpen()) Bank.close();
            Logger.info("Disabling item piles on death");
            ClientSettings.toggleItemPilesOnDeath(false);
            return ReactionGenerator.getNormal();
        }


        if (ClientSettings.isSkullPreventionActive()) {
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            Logger.info("Disable skull prev");
            ClientSettings.toggleSkullPrevention(false);
            return ReactionGenerator.getNormal();
        }


        if (ClientSettings.isWildernessLeversWarningEnabled()) {
            if (Bank.isOpen()) Bank.close();
            Logger.info("Disabling Wilderness lever warnings");
            ClientSettings.toggleWildernessLeversWarning(false);
            return ReactionGenerator.getNormal();
        }

        if (!ChangeAlchWarning.setHighAlchWarning(500_000)) {
            Logger.info("Setting alch warning price");
            return ReactionGenerator.getNormal();
        }
//        Client.logout();
//        WorldHopper.quickHop(390);
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
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
                "Bag: " + LootingBag.value(),
                "Boxing partner " + BoxingClient.getTeammate(),
                "Box world " + BoxingClient.getWorld()
        };
    }

    @Override
    public String getScriptName() {
        return "cCWildernessAgil";
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
    public void onLootBagItemRemoved(Item item) {
        if (!Bank.isOpen()) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemRemoved(Item item) {
        Logger.info("Inv item removed");
        if (!Combat.isInWild()) return;
        if (item.getId() == ItemID.COINS_995) {
            grossGp = grossGp - item.getAmount();
        }
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        if (!Client.isDynamicRegion()) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (!Combat.isInWild()) return;
        if (outgoing.getId() == ItemID.COINS_995) {
            grossGp -= outgoing.getAmount();
        }
    }
}
