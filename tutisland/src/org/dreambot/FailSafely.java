package org.dreambot;


import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;


// kill the client if it has lasted longer than 20 minutes
public class FailSafely extends Fractal {
    Timer failSafe = new Timer();

    @Override
    public boolean isValid() {
        return failSafe.elapsed() > (90 * 60 * 1000);
    }

    @Override
    public int onLoop() {
//        DiscordWebhook failHook = new DiscordWebhook("https://discord.com/api/webhooks/1003201637822775296/H1n6jQf9Epcgk67JU0xfzp13KIvNyrCHn6fIXsv15CRhriVY3QOHAtmwrFIhpqWaptwI");
//        failHook.setContent("Failing safely - ||" + Client.getLoginUsername() + ":" + Client.getLoginPassword() + "||"
//                + " | Varp " + MyVarps.getTutVarp());

        /*
        try {
            EFUtils.updateCategoryByID(BUSTED_TUTS_ID);
            failHook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

         */
        System.exit(0);
        return 0;
    }
}
