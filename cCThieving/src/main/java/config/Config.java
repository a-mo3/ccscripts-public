package config;

import enums.Stall;
import enums.TargetNPC;
import org.dreambot.api.methods.map.Tile;


public class Config {
    private final static Config config = new Config();

    private Config() {}

    public static Config getConfig() {
        return config;
    }
    // banking
    private boolean bankingMode; // for GUI toggle.
    private boolean shouldBank;
    private Tile activityTile;
    private boolean returning = false;
    // misc
    private boolean eatFood = false;
    private int foodAmount = 1;
    // pickpocketing
    private boolean pickpocketing = false;
    private TargetNPC pickpocketTarget;
    private boolean useNecklace = false;
    private int necklaceAmount = 1; // amount of necklaces to withdraw when banking
    // stalls
    private boolean stallMode = false;
    private Stall stallTarget;
    // chest
    private boolean chestMode = false;
    private int chestTimerRespawn;
    // death handle
    private boolean handleDeath;

    public TargetNPC getPickpocketTarget() {
        return pickpocketTarget;
    }

    public void setPickpocketTarget(TargetNPC pickpocketTarget) {
        this.pickpocketTarget = pickpocketTarget;
    }

    public boolean isPickpocketing() {
        return pickpocketing;
    }

    public void setPickpocketing(boolean pickpocketing) {
        this.pickpocketing = pickpocketing;
    }

    public boolean isStallMode() {
        return stallMode;
    }

    public void setStallMode(boolean stallMode) {
        this.stallMode = stallMode;
    }

    public Stall getStallTarget() {
        return stallTarget;
    }

    public void setStallTarget(Stall stallTarget) {
        this.stallTarget = stallTarget;
    }

    public boolean isBankingMode() {
        return bankingMode;
    }

    public void setBankingMode(boolean bankingMode) {
        this.bankingMode = bankingMode;
    }

    public Tile getActivityTile() {
        return activityTile;
    }

    public void setActivityTile(Tile activityTile) {
        this.activityTile = activityTile;
    }

    public boolean isUseNecklace() {
        return useNecklace;
    }

    public void setUseNecklace(boolean useNecklace) {
        this.useNecklace = useNecklace;
    }

    public boolean isReturning() {
        return returning;
    }

    public void setReturning(boolean returning) {
        this.returning = returning;
    }

    public boolean isEatFood() {
        return eatFood;
    }

    public void setEatFood(boolean eatFood) {
        this.eatFood = eatFood;
    }

    public int getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(int foodAmount) {
        this.foodAmount = foodAmount;
    }

    public int getNecklaceAmount() {
        return necklaceAmount;
    }

    public void setNecklaceAmount(int necklaceAmount) {
        this.necklaceAmount = necklaceAmount;
    }

    public boolean isShouldBank() {
        return shouldBank;
    }

    public void setShouldBank(boolean shouldBank) {
        this.shouldBank = shouldBank;
    }

    public int getChestTimerRespawn() {
        return chestTimerRespawn;
    }

    public void setChestTimerRespawn(int chestTimerRespawn) {
        this.chestTimerRespawn = chestTimerRespawn;
    }

    public boolean isChestMode() {
        return chestMode;
    }

    public void setChestMode(boolean chestMode) {
        this.chestMode = chestMode;
    }

    public boolean isHandleDeath() {
        return handleDeath;
    }

    public void setHandleDeath(boolean handleDeath) {
        this.handleDeath = handleDeath;
    }
}
