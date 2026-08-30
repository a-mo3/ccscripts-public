import config.Config;
import gui.ThievingGUI;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.message.Message;
import paint.FluffeesPaint;
import paint.PaintInfo;
import task.impl.*;

import javax.swing.*;
import java.awt.*;
@ScriptManifest(category = Category.THIEVING, name = "cCThieving", author = "camalCase", version = 0.0, description = "pickpockets & steals from stalls / chests", image = "https://i.imgur.com/8yX4uki.png")
public class Main extends TaskScript implements PaintInfo, ChatListener {
    Color[] txtcolors = new Color[]{Color.green};
    Color[] backingcolors = new Color[]{new Color(0, 0, 0, 220)};
    Color[] bordercolors = new Color[]{Color.darkGray};
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
    Config config = Config.getConfig();

    @Override
    public void onStart() {
        runtime.reset();
        addNodes(new PickPocketNode(), new StallNode(), new BankNode(), new ChestNode(), new DeathHandleNode());
        SwingUtilities.invokeLater(() -> {
            ThievingGUI gui = new ThievingGUI();
            gui.setVisible(true);
        });
    }

    @Override
    public void onPaint(Graphics g) {
        fluffeesPaint.paint(g);
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "cCThieving - " + getVersion(),
                "Runtime - " + runtime.formatTime(),
//                "Target: " + config.getPickpocketTarget().NAME,
                "Thieving lvl: " + Skills.getRealLevel(Skill.THIEVING),
                "Banking: " + config.isBankingMode() + " - " + config.isShouldBank(),
                "Eating: " + config.isEatFood(),
                "Use dodgy necklaces: " + config.isUseNecklace(),
                "handling death: " + config.isHandleDeath()
        };
    }

    @Override
    public void onGameMessage(Message m) {
        if (m.getMessage().equalsIgnoreCase("oh dear, you are dead!")) {
            config.setHandleDeath(true);
        }
    }
}
