package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.puropuro.EnterOverworldPuroPuro;
import org.dreambot.behaviour.method.puropuro.EnterPuroPuro;
import org.dreambot.behaviour.method.puropuro.GetMagicNet;
import org.dreambot.behaviour.method.puropuro.PuroPuroHunt;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.LostCity;
import org.dreambot.behaviour.training.hunter.HunterBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.PuroPuroSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;

public class PuroPuroScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<PuroPuroSettings> tree = new FractalRoot<>(new PuroPuroSettings(), getScriptName());

    Area DRAYNOR = new Area(3073, 3256, 3097, 3245);

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCPuroPuro");
        PuroPuroSettings settings = tree.getSettings();

        MuleOff.LOOT = new int[]{
                ItemID.BABY_IMPLING_JAR,
                ItemID.YOUNG_IMPLING_JAR,
                ItemID.GOURMET_IMPLING_JAR,
                ItemID.EARTH_IMPLING_JAR,
                ItemID.ESSENCE_IMPLING_JAR,
                ItemID.ECLECTIC_IMPLING_JAR,
                ItemID.NATURE_IMPLING_JAR,
                ItemID.MAGPIE_IMPLING_JAR,

                ItemID.SAPPHIRE_RING,
                ItemID.RING_OF_RECOIL,
                ItemID.EMERALD_RING,
                ItemID.RING_OF_DUELING8,
                ItemID.BOW_STRING,
                ItemID.RING_OF_WEALTH
        };

        tree.addChildren(
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),

                new EmptyDeathsCoffer().setSimpleName("Empty grave"),

                new HunterBranch(() -> Skill.HUNTER.getLevel() < Math.max(27, settings.hunterTarget)).setSimpleName("Hunter training"),
//                new EnsureLeftFalconry().setSimpleName("Leave falcon"),
                new Fractal(() -> !tree.getSettings().overworldCircles && !PaidQuest.LOST_CITY.isFinished())
                        .addChildren(
                                new LostCity().setSimpleName("Lost city")
                        )
                        .setSimpleName("quest"),
                new MuleOff().setSimpleName("Mule Off"),
                new GetMagicNet(() -> !tree.getSettings().overworldCircles && Bank.isCached() && !OwnedItems.contains(ItemID.MAGIC_BUTTERFLY_NET)),
                new EnterOverworldPuroPuro(() -> tree.getSettings().overworldCircles),
                new EnterPuroPuro(settings),
                new PuroPuroHunt(settings)
        );
    }


    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();

        if (!Worlds.getCurrent().isNormal()) {
            Logger.info("Get off abnormal world");
            WorldHopper.hopWorld(Worlds.getRandomWorld(World::isNormal));

        }

        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
        };
    }

    @Override
    public String getScriptName() {
        return "cCPuroPuro";
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
        if (Widgets.isOpen()) return;
        if (!DRAYNOR.contains(Players.getLocal())) return;
        if (!item.getName().contains("seed")) return;
        grossGp += item.getLivePrice();

    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!DRAYNOR.contains(Players.getLocal())) return;
        if (!existing.getName().contains("seed")) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }
        int gp = quantity * existing.getLivePrice();
        grossGp += gp;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (outgoing.getId() != ItemID.IMPLING_JAR) return;
        Logger.info("impling jar swap " + incoming);
        grossGp += incoming.getLivePrice() - LivePrices.get(ItemID.IMPLING_JAR);
    }
}
