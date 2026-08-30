import config.Config;
import gui.GUI;
import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import task.impl.BankNode;
import task.impl.MineNode;
import task.impl.NoPickaxeNode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

@ScriptManifest(name = "cCMiner",
        description = "Mines",
        author = "camalCase",
        version = 1,
        category = Category.MINING,
        image = "https://i.imgur.com/EIMw4L4.png")

public class main extends TaskScript implements ItemContainerListener {
    private final Config config = Config.getConfig();


    // VARS FOR PAINT
    private final int bottom = 470;
    private final int left = 30;
    private Timer runtime = new Timer();
    private int beginningXP;
    private int currentXp;
    private int xpGained;
    private GUI gui;
    private Image image;

    @Override
    public void onStart() {
        try {
            image = ImageIO.read(new URL("https://i.imgur.com/EIMw4L4.png"));
        } catch (IOException e) {
            Logger.log("failed to load img");
        }
        Sleep.sleepUntil(Client::isLoggedIn, 20000);

        // todo returns 0 if not logged in maybe move to minenode
        beginningXP = Skills.getExperience(Skill.MINING);
//        config.setRockType(Rock.IRON);
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            this.gui = gui;
            gui.setVisible(true);
        });
        addNodes(new MineNode(),
                new BankNode(),
                new NoPickaxeNode());

        super.onStart();
    }

    @Override
    public void onExit() {
        gui.dispose();
    }

    @Override
    public void onPaint(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(0, 335, 550, 165);
        g.drawImage(image, 400, 330, null);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("cCMiner -- " + config.getStatus(), left, bottom - 170);
        if (config.isRunning()) {
            g.drawString(config.getRockType().toString(), left, bottom);
            g.drawString(config.getMineLocation().toString(), left, bottom - 20);
            if (config.shouldBank()) {
                g.drawString("Banking: " + config.shouldBank(), left, bottom - 40);
                g.drawString("Progression: " + config.isProgression(), left, bottom - 60);
                if (config.isCustomBank()) {
                    g.drawString("Bank @ " + config.getBankLocation().toString(), left, bottom - 80);
                }
            }
        }
        g.drawString("Runtime: " + runtime.formatTime(), left + 220, bottom);
        xpGained = Skills.getExperience(Skill.MINING) - beginningXP;
        g.drawString("Xp Gained: " + xpGained, left + 220, bottom - 20);
        g.drawString("Ore mined: " + config.getOreCount(), left + 220, bottom - 40);


    }

    @Override
    public void onInventoryItemAdded(Item i) {
        if (i.getAmount() > 0) {
            if (i.getName().contains("ore")
                    || i.getName().equalsIgnoreCase("Coal")
                    || i.getName().equalsIgnoreCase("Clay")
                    || i.getName().contains("uncut")) {
                config.setOreCount((config.getOreCount() + i.getAmount()));
            }
        }
    }
}
