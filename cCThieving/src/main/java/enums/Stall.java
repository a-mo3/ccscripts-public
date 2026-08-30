package enums;

public enum Stall {
    TEA_STALL("Tea stall", 5),
    BAKERS_STALL("Baker's stall", 5),
    FRUIT_STALL("Fruit Stall", 25);

    public String NAME;
    public int REQLVL;
    Stall(String name, int reqlvl) {
        this.NAME = name;
        this.REQLVL = reqlvl;
    }
}
