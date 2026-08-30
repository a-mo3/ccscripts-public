package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.bankdump.DumpBank;
import org.dreambot.behaviour.bankdump.OpenTemporossCrate;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.BankDumpSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class BankDumpScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<BankDumpSettings> tree = new FractalRoot<>(new BankDumpSettings(), getScriptName());


    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCBankDump");

        // remove web nodes to enter mage bank because they have no knife checks
        WebFinder wf = WebFinder.getWebFinder();
        Area mageBankEntrance = new Area(3086, 3961, 3099, 3955);
        List<AbstractWebNode> nodes = wf.getAll().stream()
                .filter(x -> mageBankEntrance.contains(x.getTile())).collect(Collectors.toList());
        nodes.forEach(wf::removeNode);

        tree.addChildren(
                new TutorialTree().setSimpleName("Tutorial island"),
                new OpenTemporossCrate(),
                new DumpBank(() -> true, tree.getSettings().ignoreUnder),
                new MuleOff()
        );
    }


    @Override
    public int onLoop() {
        if (Inventory.count("Coin pouch") > 27) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Inventory.interact("Coin pouch"); // stack is open-all single is open
        }

        if (ClientSettings.isSellPriceWarningEnabled()) {
            if (Bank.isOpen()) Bank.close();
            Logger.info("Disabling sell warning");
            ClientSettings.toggleSellPriceWarning(false);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.PAYDIRT)) {
            Logger.info("Drop all paydirt");
            Inventory.dropAll(ItemID.PAYDIRT);
            return ReactionGenerator.getNormal();
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
        return "cCBankDump";
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
}
