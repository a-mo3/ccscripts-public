package config;


import org.dreambot.api.methods.magic.Normal;

/**
 * @author camalCase
 * @version 1
 * created 28 aug 2021
 * desc: singleton class to hold global settings
 */

public class Config {
    private static final Config config = new Config();
    private String status = "starting script...";
    private boolean initialised = false; // will be changed after initnode has ran
    private Normal bestSpell;

    private Config(){}

    public static Config getConfig() {
        return config;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public Normal getBestSpell() {
        return bestSpell;
    }

    public void setBestSpell(Normal bestSpell) {
        this.bestSpell = bestSpell;
    }
}
