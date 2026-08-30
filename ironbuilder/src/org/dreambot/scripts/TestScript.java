package org.dreambot.scripts;

import lombok.extern.slf4j.Slf4j;
import org.dreambot.PseudoScript;
import org.dreambot.analytics.impl.AnalyticsReporter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebPathResponse;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.behaviour.goldfarming.KillCowsForHide;
import org.dreambot.behaviour.quest.RomeoAndJuliet;
import org.dreambot.behaviour.quest.SheepShearer;
import org.dreambot.behaviour.quest.WitchsPotion;
import org.dreambot.behaviour.quest.XMarksTheSpot;
import org.dreambot.behaviour.quest.cooksassistant.CooksAssistant;
import org.dreambot.fractals.IronFractal;
import org.dreambot.loadouts.behavior.RestockStackFractal;
import org.dreambot.utility.PaintUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

@Slf4j
public class TestScript extends PseudoScript {

    @Override
    protected String scriptName() {
        return "ScriptTest";
    }

    @Override
    public void init(IronFractal tree, String[] args) {
        tree.addChildren(
                new KillCowsForHide(() -> true),
                new XMarksTheSpot(() -> !FreeQuest.X_MARKS_THE_SPOT.isFinished()),
                new SheepShearer(() -> !FreeQuest.SHEEP_SHEARER.isFinished()),
                new RomeoAndJuliet(() -> !FreeQuest.ROMEO_AND_JULIET.isFinished()),
                new CooksAssistant(() -> !FreeQuest.COOKS_ASSISTANT.isFinished()),
                new WitchsPotion(() -> !FreeQuest.WITCHS_POTION.isFinished())
        ).setSimpleName("Test");
    }

    @Override
    public void onPaint(Graphics g) {
        PaintUtil.paint(g, new String[]{
                IronFractal.decisionPath.toString(),
                String.valueOf(RestockStackFractal.getRestockTasks().size()),
                Arrays.toString(RestockStackFractal.getRestockTasks().toArray()),
                FreeQuest.COOKS_ASSISTANT.getConfigValue() + ""
        });
    }
}
