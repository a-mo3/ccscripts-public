package org.dreambot;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.netfishing.NetFishingLeaf;
import org.dreambot.framework.API;
import org.dreambot.framework.Tree;
import org.dreambot.paint.FluffeesPaint;
import org.dreambot.paint.PaintInfo;

import java.awt.*;

@ScriptManifest(category = Category.FISHING, name = "cCMinnowsAIO", author = "camalCase", version = 1.0)
public class Main extends AbstractScript implements PaintInfo {
    Color[] txtcolors = new Color[]{Color.WHITE};
    Color[] backingcolors = new Color[]{Color.BLACK};
    Color[] bordercolors = new Color[]{Color.WHITE};
    private final Timer runtime = new Timer();


    FluffeesPaint fluffeesPaint = new FluffeesPaint(this,
            FluffeesPaint.PaintLocations.TOP_LEFT_CHATBOX,
            txtcolors,
            "test",
            backingcolors,
            bordercolors,
            1,
            false,
            3,
            3, 3
    );
    Tree tree;

    @Override
    public void onStart() {
        initTree();
    }

    private void initTree() {
        tree = new Tree();
        tree.addBranches(
            new NetFishingLeaf()
        );
    }

    @Override
    public int onLoop() {
         return tree.onLoop();
    }

    @Override
    public void onPaint(Graphics graphics) {
        fluffeesPaint.paint(graphics);
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "cCMinnowsAIO - " + API.currentBranch + " - " + API.currentLeaf,
                "Runtime - " + runtime.formatTime(),
                "Level - " + Skills.getRealLevel(Skill.FISHING)
        };
    }
}
