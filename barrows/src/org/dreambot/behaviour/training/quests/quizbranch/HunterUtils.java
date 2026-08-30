package org.dreambot.behaviour.training.quests.quizbranch;


import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public class HunterUtils {

    public static final int MAIN_WIDGET_ID = 533; // 533 is the main widget for the quiz interface
    public static final int QUESTION_ID = 28;


    public static int getTrapLimit() {
        int level = Skills.getRealLevel(Skill.HUNTER);
        int limit = 0;
        if (level < 20) {
            limit = 1;
        } else if (level < 40) {
            limit = 2;
        } else if (level < 60) {
            limit = 3;
        } else if (level < 80) {
            limit = 4;
        } else {
            limit = 5;
        }

        return Combat.isInWild() ? limit + 1 : limit;
    }

    public static void solve() {
        if (Widgets.getWidget(MAIN_WIDGET_ID).isVisible()) {
            String question = Widgets.get(MAIN_WIDGET_ID, QUESTION_ID).getText();
            String answer = QuizAnswers.getAnswer(question);
            Logger.info(question + " " + answer);
            if (answer == null) {
                Logger.info("Walking away cant solve this puzzle");
                Walking.clickTileOnMinimap(Players.getLocal().getSurroundingArea(2).getRandomTile());
                Sleep.sleep(600);
                return;
            }
            if (question != null) {
                WidgetChild answerWidget = Widgets.get(x -> x.getParentID() == MAIN_WIDGET_ID
                        && x.getText() != null
                        && x.getText().contains(QuizAnswers.getAnswer(question))); // certain answers would return the widget for quests or other things
                // eg, How many legs does a wyvern have, answer is two, tail of two cats was the selected widget.
                if (answerWidget != null) {
                    Logger.info("Target widget = " + answerWidget.getText());
                    answerWidget.interact("Ok");
                    Sleep.sleep(Calculations.random(350, 500));
                } else {
                    Widgets.closeAll();
                }
                Sleep.sleepUntil(Dialogues::canContinue, 3000);
                if (Dialogues.canContinue()) {
                    Dialogues.continueDialogue();
                }
            }
        }
    }
}
