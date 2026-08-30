import config.Config;
import gui.GUI;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.utilities.Logger;
import task.impl.BankItems;
import task.impl.Fill;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

@ScriptManifest(name = "camalCaseFiller",
        description = "Fills containers with water",
        author = "camalCase",
        version = 2.1,
        category = Category.MONEYMAKING,
        image = "https://i.imgur.com/xZDK0OO.png")


/**
 * @author camalCase
 * @version 1
 * 25th aug updates
 * desc: main, adds nodes & starts gui.
 */
public class Main extends TaskScript {
    //FOR PAINT
    Image image;
    Config config = Config.getConfig();
    private long timeBegan;
    private long timeRan;


    private int gpGained;

    private int totalGpPerHour;
    private int gpPerHour;
    DecimalFormat df = new DecimalFormat("#");

    @Override
    public void onStart() {

        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.createGUI();
        });

        // NODES
        addNodes(new BankItems(), new Fill());

        // FOR PAINT
        try {
            image = ImageIO.read(new URL("https://i.imgur.com/zet4Los.png")); // https://i.imgur.com/zet4Los.png
        } catch (IOException e) {
            Logger.log("failed to load img");
        }
        timeBegan = System.currentTimeMillis();
    }

    public void onPaint(Graphics g) {
        timeRan = System.currentTimeMillis() - this.timeBegan;
        if (image != null) {
            g.drawImage(image, 0, 335, null);
        } else {
            g.setColor(Color.cyan);
            g.fillRect(0, 335, 520, 150);
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Time ran: " + ft(timeRan), 10, 365);
        g.drawString(config.getStatus(), 10, 385);

        // GP PER HOUR
        gpGained = config.getAmountFilled() * config.getProfit();
        gpPerHour = (int) (gpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));

        g.drawString("GP MADE: " + gpGained, 10, 405);
        g.drawString("GP/HOUR: " + df.format((gpPerHour) / 1000) + "K", 10, 425);

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
