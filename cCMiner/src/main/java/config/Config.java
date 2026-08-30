package config;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankLocation;

public class Config {
    private Rock rockType;
    private MineLocation mineLocation;
    private BankLocation bankLocation; // used for custom bank locations, by default will just get closest
    private static final Config config = new Config();
    private boolean shouldBank = true;
    private boolean isRunning;
    private boolean progression;
    private boolean customBank;
    private int oreCount = 0; // for paint
    private String status = "Loading";
    // ANTIBAN SHIT
    private int sleepLow = 200;
    private int sleepHigh = 500;


    // normally i hate methods that you could replace with just writing 1 line
    // but i might change this to gaussian dist or some other math func rather than random
    // plus it'd be long to write outside of here
    public int getSleep() {
        return Calculations.random(sleepLow, sleepHigh);
    }


    public Rock getRockType() {
        return rockType;
    }

    public void setRockType(Rock rockType) {
        this.rockType = rockType;
    }

    public static Config getConfig() {
        return config;
    }

    public boolean shouldBank() {
        return shouldBank;
    }

    public void setShouldBank(boolean shouldBank) {
        this.shouldBank = shouldBank;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public MineLocation getMineLocation() {
        return mineLocation;
    }

    public void setMineLocation(MineLocation mineLocation) {
        this.mineLocation = mineLocation;
    }

    public boolean isProgression() {
        return progression;
    }

    public void setProgression(boolean progression) {
        this.progression = progression;
    }

    public BankLocation getBankLocation() {
        return bankLocation;
    }

    public void setBankLocation(BankLocation bankLocation) {
        this.bankLocation = bankLocation;
    }

    public int getOreCount() {
        return oreCount;
    }

    public void setOreCount(int oreCount) {
        this.oreCount = oreCount;
    }

    public boolean isCustomBank() {
        return customBank;
    }

    public void setCustomBank(boolean customBank) {
        this.customBank = customBank;
    }

    public int getSleepHigh() {
        return sleepHigh;
    }

    public void setSleepHigh(int sleepHigh) {
        this.sleepHigh = sleepHigh;
    }

    public int getSleepLow() {
        return sleepLow;
    }

    public void setSleepLow(int sleepLow) {
        this.sleepLow = sleepLow;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
