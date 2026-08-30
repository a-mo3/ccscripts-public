package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.method.antipk.AntiPkLeaveBosses;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.training.sailing.SailingBranch;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.GryphonSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.function.Supplier;

/**
 * Preemptive script template for a method that will come with the sailing release
 */
public class GryphonScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<GryphonSettings> tree = new FractalRoot<>(new GryphonSettings(), getScriptName());

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCGryphonFarm");

        MuleOff.LOOT = new int[]{
                ItemID.RUNE_DART,
                ItemID.RUNE_KNIFE,
                ItemID.ADAMANT_2H_SWORD,
                ItemID.ADAMANT_PLATEBODY,
                ItemID.RUNE_AXE,
                ItemID.RUNE_KITESHIELD,
                ItemID.RUNE_LONGSWORD,
                ItemID.RUNE_MED_HELM,
                ItemID.RUNE_FULL_HELM,
                ItemID.LAVA_BATTLESTAFF,

                ItemID.RUNE_JAVELIN,
                ItemID.BLOOD_RUNE,
                ItemID.RUNITE_BOLTS,
                ItemID.LAW_RUNE,
                ItemID.LAVA_RUNE,

                ItemID.GRIMY_RANARR_WEED,
                ItemID.GRIMY_IRIT_LEAF,
                ItemID.GRIMY_KWUARM,
                ItemID.GRIMY_LANTADYME,

                ItemID.DRAGON_JAVELIN_HEADS,
                ItemID.FIRE_ORB,
                ItemID.ADAMANTITE_BAR,
                ItemID.LOOP_HALF_OF_KEY,
                ItemID.TOOTH_HALF_OF_KEY,
                ItemID.AMULET_OF_GLORY,

                ItemID.GRIMY_DWARF_WEED,
                ItemID.GRIMY_AVANTOE,
                ItemID.GRIMY_CADANTINE,
                ItemID.STEEL_ARROW,
                ItemID.RUNE_ARROW,
                ItemID.SILVER_ORE,
                ItemID.FIRE_TALISMAN,
                ItemID.DRAGON_MED_HELM,
                ItemID.DRAGON_SPEAR,
                ItemID.DRAGONSTONE,
                ItemID.UNCUT_DIAMOND,
                ItemID.UNCUT_RUBY,
                ItemID.ADAMANT_JAVELIN,
                ItemID.RUNE_BATTLEAXE,
                ItemID.RUNE_SQ_SHIELD,
                ItemID.RUNE_2H_SWORD,
                ItemID.RUNE_SPEAR,
                ItemID.RUNITE_BAR,
                ItemID.DEATH_RUNE,
                ItemID.NATURE_RUNE,
                ItemID.SHIELD_LEFT_HALF,

                ItemID.ENSOULED_DRAGON_HEAD_13511,
                ItemID.ENSOULED_DRAGON_HEAD,
                ItemID.LAVA_DRAGON_BONES,
                ItemID.BLACK_DRAGONHIDE,
                ItemID.ONYX_BOLT_TIPS,
                ItemID.LAVA_SCALE,
                ItemID.SAPPHIRE_RING,
                ItemID.RING_OF_RECOIL,
                ItemID.EMERALD_RING,
                ItemID.RING_OF_DUELING8,
                ItemID.COSMIC_RUNE,

                ItemID.DRACONIC_VISAGE,
                ItemID.LAVA_SCALE_SHARD
        };

        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        Supplier<Boolean> rechargedExitDanger = () -> {
            if (Combat.isInWild()) {
                AntiPkLeaveBosses.leaveBosses();
                return true;
            }
            return !SpecialWalker.leaveAvasRoom();
        };

        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Web", "Slash"));
        tree.addChildren(
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new EmptyDeathsCoffer().setSimpleName("Empty grave"),

                // todo combat

                new SlayerBranch(() -> Skill.SLAYER.getLevel() < 51).setSimpleName("51 slayer"),
                new SailingBranch(() -> Skills.getRealLevel(Skill.SAILING) < 45),

                new MuleOff().setSimpleName("Mule off")

        );
    }

    public static void logout() {
        Client.setIdleTime(30_000);
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) tree.run();


        Item lootingBag = ItemVariants.LOOTING_BAG.getItem();
        if (Combat.isInWild() && lootingBag != null) {
            if (!LootingBag.refreshLootBagCache()) return ReactionGenerator.getNormal();
        }

        if (lootingBag != null && !LootingBag.lootingBagCache.isEmpty() && Bank.getClosestBankLocation().distance(Players.getLocal().getTile()) < 5) {
            Logger.info("Empty looting bag " + new EmptyLootingBagEvent().executed());
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isWildernessLeversWarningEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Disable wilderness level warnings");
            ClientSettings.toggleWildernessLeversWarning(false);
            return ReactionGenerator.getNormal();
        }

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
                "Deaths " + deathCount
        };
    }

    @Override
    public String getScriptName() {
        return "cCGryphonFarm";
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
