package org.dreambot;

import org.dreambot.alerts.Alerts;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.entertheabyss.EnterTheAbyss;
import org.dreambot.behaviour.runecraft.EarthRunes;
import org.dreambot.behaviour.runemysteries.RuneMysteries;
import org.dreambot.behaviour.templeoftheeye.TempleOfTheEye;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.generic.GetMembership;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.ui.Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCGotr", author = "camalCase", version = 0.0)
public class GotrAIO extends AbstractScript implements PaintInfo, HumanMouseListener {
    Timer runtime = new Timer();
    FluffeesPaint scriptPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    FluffeesPaint fractalPaint = new FluffeesPaint(FluffeesPaint.PaintLocations.BOTTOM_LEFT_PLAY_SCREEN, new FractalAPI());
    Fractal tree = new Fractal();

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    @Override
    public void onStart(String... params) {
        super.onStart(params);
        init();
    }

    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();

    @Override
    public boolean onSolverStart(RandomSolver solver) {
        if (solver instanceof BreakSolver) isBreaking.set(true);
        return super.onSolverStart(solver);
    }

    @Override
    public void onSolverEnd(RandomSolver solver) {
        if (solver instanceof BreakSolver) isBreaking.set(false);
        super.onSolverEnd(solver);
    }

    private void init() {
        tree.setSimpleName("cCGotrAIO")
                .addChildren(
                        new LampHandler().setSimpleName("lamp handler"),
                        new GetMembership().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                        new RuneMysteries().setSimpleName("Rune Mysteries"),
                        new EnterTheAbyss().setSimpleName("Enter the Abyss"),
                        new EarthRunes(() -> Skills.getRealLevel(Skill.RUNECRAFTING) < 10)
                                .setSimpleName("Get 10 rune crating"),
                        new TempleOfTheEye().setSimpleName("Temple of the eye")
                );
    }

    @Override
    public int onLoop() {
        if (ClientSettings.isAcceptAidEnabled()) {
            Logger.info("Toggling off accept aid");
            if (Bank.isOpen()) Bank.close();
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
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
    public void onMouseClicked(MouseEvent e) {
        if (Gui.wasDiscordButtonClicked(e.getPoint())) {
            try {
                Desktop.getDesktop().browse(new URI(""));
            } catch (IOException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (Gui.wasButtonClicked(e.getPoint())) {
            SwingUtilities.invokeLater(Gui::new);
        }
    }

    @Override
    public void onPaint(Graphics graphics) {
        Alerts.renderList(graphics);
        Gui.paintDiscordButton(graphics);
        Gui.paintButton(graphics);
        scriptPaint.paint(graphics);
        fractalPaint.paint(graphics);
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "cCGotr" + runtime.formatTime(),
                Arrays.toString(FractalAPI.hierarchy) + " " + FractalAPI.status,
        };
    }
}
