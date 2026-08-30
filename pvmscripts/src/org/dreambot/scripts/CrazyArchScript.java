package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.method.chaosfanatic.ChaosFanatic;
import org.dreambot.behaviour.method.crazyarch.FightCrazyArch;
import org.dreambot.behaviour.method.spindel.ExitWithLoot;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.TurnInLootKeys;
import org.dreambot.behaviour.quests.ClientOfKourend;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.XMarksTheSpot;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.animalmagnetism.util.LeaveAvaRoom;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.range.ConfigurableRangeTraining;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.discordwebhook.scouter.ScoutFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.fractals.util.RatConfigureQuickPrayers;
import org.dreambot.scriptdata.ChaosFanaticSettings;
import org.dreambot.scriptdata.CrazySettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

public class CrazyArchScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<CrazySettings> tree = new FractalRoot<>(new CrazySettings(), getScriptName());

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCCrazyArch");

        MuleOff.LOOT = new int[]{
                ItemID.ODIUM_SHARD_1,
                ItemID.MALEDICTION_SHARD_1,

                ItemID.BATTLESTAFF,
                ItemID.SPLITBARK_BODY,
                ItemID.SPLITBARK_LEGS,
                ItemID.ZAMORAK_MONK_TOP,
                ItemID.ZAMORAK_MONK_BOTTOM,
                ItemID.ANCIENT_STAFF,

                ItemID.FIRE_RUNE,
                ItemID.SMOKE_RUNE,
                ItemID.CHAOS_RUNE,
                ItemID.BLOOD_RUNE,

                ItemID.MONKFISH,
                ItemID.SHARK,
                ItemID.ANCHOVY_PIZZA,

                ItemID.GRIMY_LANTADYME,
                ItemID.RING_OF_LIFE,
                ItemID.WINE_OF_ZAMORAK,
                ItemID.UNCUT_EMERALD,
                ItemID.UNCUT_SAPPHIRE,
                ItemID.SINISTER_KEY,
                ItemID.PURE_ESSENCE,
                ItemID.LOOP_HALF_OF_KEY,
                ItemID.TOOTH_HALF_OF_KEY,
                ItemID.RUNE_LONGSWORD,
                ItemID.NATURE_RUNE,
                ItemID.RUNE_BATTLEAXE,
                ItemID.PRAYER_POTION4,
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ReactionSettingsFractal(),
                new ScoutFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new LampHandler().setSimpleName("Lamp handler"),

                new GetMembershipBranch().setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Empty grave"),

                // prayer training
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget)
                        .setSimpleName("Prayer training: " + tree.getSettings().prayerTarget),

                // combat / magic training
                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < tree.getSettings().magicTarget)
                        .setSimpleName("Magic training"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.ATTACK) < tree.getSettings().meleeTarget)
                        .setSimpleName("Combat training"),

                new TurnInLootKeys(),

                new MuleOff().setSimpleName("Mule off"),
                new Fractal(() -> ExitWithLoot.inventoryValue() > tree.getSettings().exitLootValue)
                        .setInventoryLoadout(new InventoryLoadout().setStrict(true))
                        .setSimpleName("Bank"),

                new FightCrazyArch(() -> true, tree.getSettings())
                        .setSimpleName("Cray fight")
        );
    }


    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) tree.run();

        if (ClientSettings.isLevelUpInterfaceEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Disable level up message");
            ClientSettings.toggleLevelUpInterface(false);
            return ReactionGenerator.getNormal();
        }

        if (!Client.isLoggedIn()) return ReactionGenerator.getNormal();
        if (Client.getGameStateID() == 45) return ReactionGenerator.getNormal();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "In combat " + Players.getLocal().isInCombat(),
                "Tick " + Client.getGameTick(),
        };
    }

    @Override
    public String getScriptName() {
        return "cCChaosFanaticFarm";
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

    @Override
    public void onInventoryItemAdded(Item item) {
        if (!Combat.isInWild()) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!Combat.isInWild()) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) return;

        grossGp += incoming.getLivePrice() * quantity;
    }

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossGp += item.getLivePrice() * item.getAmount();
    }

    int deathCount = 0;

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            deathCount++;
        }
    }
}
