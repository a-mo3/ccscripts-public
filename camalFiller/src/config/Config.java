package config;
/**
 * @author camalCase
 * @version 1
 * 25th aug refactor
 * desc: holds global settings, mostly used for turning on script after gui is finished & passing the container name to tasks.
 */
public class Config {

    private static final Config config = new Config();

    private int amountFilled = 0;
    private int profit;


    private String status = "Selecting container...";
    // string for what container to use, bucket, jug, bowl, vial
    private String container;

    // bool for if script is a go
    private boolean isRunning = false;

    private Config () {} // hiding constructor

    // isRunning getter
    public boolean isScriptRunning() {
        return isRunning;
    }
    // isRunning setter
    public void setScriptRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public static Config getConfig() {
        return config;
    }

    // container getter
    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    // status getter and setter
    public String getStatus() {
        return status;
    }
    // status setter
    public void setStatus(String status) {
        this.status = status;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public int getAmountFilled() {
        return amountFilled;
    }

    public void setAmountFilled(int amountFilled) {
        this.amountFilled = amountFilled;
    }
}
