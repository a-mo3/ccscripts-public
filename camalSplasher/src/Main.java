import config.Config;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.utilities.Logger;
import task.impl.InitNode;
import task.impl.SplashNode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

@ScriptManifest(name = "cCSplasher",
        description = "Casts your highest lvl spell on seagulls!",
        author = "camalCase",
        version = 2.0,
        category = Category.MAGIC,
        image = "https://i.imgur.com/v1xNrGH.png")

public class Main extends TaskScript {
    Image image;
    private int beginningXP = 0;
    private int currentXp = 0;
    private int xpGained;
    private int xpPerHour;

    private long timeBegan;
    private long timeRan;
    DecimalFormat df = new DecimalFormat("#");
    Config config = Config.getConfig();
    private int totalXpPerHour;

    @Override
    public void onStart() {
        addNodes(new InitNode(), new SplashNode());
        currentXp = beginningXP + 1;
        beginningXP = Skills.getExperience(Skill.MAGIC);
        timeBegan = System.currentTimeMillis();


        try {
            image = ImageIO.read(new URL("https://i.imgur.com/rh2KuTK.png"));
        } catch (IOException e) {
            Logger.log("failed to load img");
        }

    }

    @Override
    public void onPaint(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 335, null);
        }

        g.setFont(new Font("Arial", Font.BOLD, 16));
        timeRan = System.currentTimeMillis() - this.timeBegan;
        g.drawString("Time ran: " + ft(timeRan), 10, 325);

        currentXp = Skills.getExperience(Skill.MAGIC);
        xpGained = currentXp - beginningXP;
        xpPerHour = (int) (xpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
        totalXpPerHour = xpPerHour / 1000; // for showing xp in thousands rather than large unreadable number
        g.drawString("Xp/Hour: " + df.format(totalXpPerHour) + "K", 55, 355);
        g.drawString("Xp gained: " + xpGained, 55, 375);

        g.drawString("Status: " + config.getStatus(), 55, 425);
        g.drawString("Casting spell: " + config.getBestSpell(), 300, 350);
        g.drawString("Magic Level: " + Skills.getRealLevel(Skill.MAGIC), 55, 465);

    }


    private String ft(long duration) {

        String res = "";

        long days = TimeUnit.MILLISECONDS.toDays(duration);

        long hours = TimeUnit.MILLISECONDS.toHours(duration)

                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));

        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)

                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS

                .toHours(duration));

        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)

                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS

                .toMinutes(duration));

        if (days == 0) {

            res = (hours + ":" + minutes + ":" + seconds);

        } else {

            res = (days + ":" + hours + ":" + minutes + ":" + seconds);

        }

        return res;

    }


}

