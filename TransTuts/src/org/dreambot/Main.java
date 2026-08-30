package org.dreambot;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.*;
import org.dreambot.behaviour.banktutorial.BankLeaf;
import org.dreambot.behaviour.combattutorial.HandleCombatTabsLeaf;
import org.dreambot.behaviour.combattutorial.MeleeRatLeaf;
import org.dreambot.behaviour.combattutorial.RangeRatLeaf;
import org.dreambot.behaviour.cooktutorial.TalkToChef;
import org.dreambot.behaviour.gielinorguide.GielinorGuideLeaf;
import org.dreambot.behaviour.gielinorguide.SetNameLeaf;
import org.dreambot.behaviour.prayertutorial.PrayerRoomLeaf;
import org.dreambot.behaviour.questtutorial.QuestGuyLeaf;
import org.dreambot.behaviour.smithingtutorial.MineLeaf;
import org.dreambot.behaviour.smithingtutorial.SmeltLeaf;
import org.dreambot.behaviour.survivaltutorial.FishAndCookShirmpLeaf;
import org.dreambot.behaviour.survivaltutorial.SurvivalExpertLeaf;
import org.dreambot.behaviour.transtree.TransLeaf;
import org.dreambot.behaviour.wizardtutorial.WizardLeaf;
import org.dreambot.framework.Tree;
import org.dreambot.paint.FluffeesPaint;
import org.dreambot.paint.PaintInfo;
import org.dreambot.util.MyVarps;
import org.dreambot.util.ScriptStage;

import java.awt.*;

@ScriptManifest(category = Category.MISC, name = "PrideTuts", author = "camalCase", version = 0.0)
public class Main extends AbstractScript implements PaintInfo {
    Color[] txtcolors = new Color[]{Color.green};
    Color[] backingcolors = new Color[]{new Color(0, 0, 0, 220)};
    Color[] bordercolors = new Color[]{Color.darkGray};
    Tree tutorialTree = new Tree();
    ScriptStage scriptStage = ScriptStage.getScriptStage();
    Tree transTree = new Tree();
    private final Timer runtime = new Timer();
    FluffeesPaint fluffeesPaint = new FluffeesPaint(this,
            FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN,
            txtcolors,
            "test",
            backingcolors,
            bordercolors,
            1,
            false,
            3,
            3, 3
    );

    public void initTree() {
        tutorialTree.addBranches(
                new GielinorGuideBranch().addLeafs(new SetNameLeaf(), new GielinorGuideLeaf()),
                new SurvivalTrainingBranch().addLeafs(new SurvivalExpertLeaf(), new FishAndCookShirmpLeaf()),
                new CookTutorialBranch().addLeafs(new TalkToChef()),
                new QuestTutorialBranch().addLeafs(new QuestGuyLeaf()),
                new SmithingTutorialBranch().addLeafs(new MineLeaf(), new SmeltLeaf()),
                new CombatTutorialBranch().addLeafs(new HandleCombatTabsLeaf(), new MeleeRatLeaf(), new RangeRatLeaf()),
                new BankTutorialBranch().addLeafs(new BankLeaf()),
                new PrayerTutorialBranch().addLeafs(new PrayerRoomLeaf()),
                new WizardTutorialBranch().addLeafs(new WizardLeaf())
        );
        transTree.addBranches(new TransLeaf());
    }


    @Override
    public void onStart() {
        initTree();
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) {
            return tutorialTree.onLoop();
        }
        return transTree.onLoop();
    }


    @Override
    public void onPaint(Graphics graphics) {
        fluffeesPaint.paint(graphics);
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "Timer: " + runtime.formatTime() + " " + scriptStage.getActiveLeaf(),
                "Varp 281: " + MyVarps.getTutVarp(),
                "Varp 406: " + PlayerSettings.getConfig(406),
                "VarBit 3756: " + PlayerSettings.getBitValue(3756)
        };
    }

    public static void solveDialogue(String... options) {
        if (Dialogues.canContinue()) {
            Dialogues.continueDialogue();
        }
        if (Dialogues.areOptionsAvailable()) {
            Dialogues.chooseFirstOptionContaining(options);
        }
    }
}
