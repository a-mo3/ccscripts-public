package org.dreambot.scripts;

import org.dreambot.LocalSDNOwnershipCache;
import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.method.amethyst.Amethyst;
import org.dreambot.behaviour.method.motherlode.EnterMLM;
import org.dreambot.behaviour.method.motherlode.GetProspector;
import org.dreambot.behaviour.method.motherlode.MLMMining;
import org.dreambot.behaviour.method.motherlode.MLMTopFloor;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.training.mining.MixedMining;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.AutoProggy;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.MotherlodeSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

public class AmethystScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<MotherlodeSettings> tree = new FractalRoot<>(new MotherlodeSettings(), getScriptName());

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCAmethystFarm");

        MuleOff.LOOT = new int[]{
                ItemID.COAL,
                ItemID.GOLD_ORE,
                ItemID.MITHRIL_ORE,
                ItemID.ADAMANTITE_ORE,
                ItemID.RUNITE_ORE,
                ItemID.AMETHYST,

                ItemID.UNCUT_SAPPHIRE,
                ItemID.UNCUT_DIAMOND,
                ItemID.UNCUT_EMERALD,
                ItemID.UNCUT_RUBY,
                ItemID.UNCUT_DRAGONSTONE,
                ItemID.UNCUT_ONYX
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;
        boolean ownsMLM = LocalSDNOwnershipCache.ownsAny(1685, 1684);
        tree.addChildren(

                new MixedMining(() -> tree.getSettings().trainInFTP && Skills.getRealLevel(Skill.MINING) < tree.getSettings().miningTarget)
                        .setSimpleName("Mining training"),

                new GetMembershipBranch().setSimpleName("Getting membership.")
                        .setPrependLogic(() -> {
                            if (Inventory.contains(ItemID.PAYDIRT)) {
                                Inventory.dropAll(ItemID.PAYDIRT);
                                return true;
                            }

                            if (tree.getSettings().stopAfterFTPMining) {
                                ScriptManager.getScriptManager().stop();
                                Logger.info("Stopping script");
                                return true;
                            }
                            return false;
                        }),

                new MixedMining(() -> Skills.getRealLevel(Skill.MINING) < tree.getSettings().miningTarget)
                        .setSimpleName("Mining training(P2P)"),

                new MuleOff().setSimpleName("Mule off")
                        .setPrependLogic(() -> {
                            if (Inventory.contains(ItemID.PAYDIRT)) {
                                Inventory.dropAll(ItemID.PAYDIRT);
                                return true;
                            }
                            return false;
                        }),

                // once over 92 only do amethyst
                new Amethyst(() -> Skill.MINING.getLevel() >= 92),

                new MixedMining(() -> !ownsMLM).setSimpleName("Mining training(P2P)"),

                new GetProspector(tree.getSettings().buyProspector).setSimpleName("Get prospector"),
                new MLMTopFloor(tree.getSettings().useTopFloor).setSimpleName("Top floor mining"),
                new MLMMining().setSimpleName("Mining"),
                new EnterMLM(tree.getSettings().useDragonPickaxe).setSimpleName("Going to MLM")
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
        return "cCAmethystFarm";
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
        if (Bank.isOpen()) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (Bank.isOpen()) return;
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
