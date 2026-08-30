package config;

public enum Pickaxe {
    BRONZE_PICKAXE(1265, 1, 1),
    IRON_PICKAXE(1267, 1, 1),
    STEEL_PICKAXE(1269, 6, 5),
    BLACK_PICKAXE(12297, 11, 10),
    MITHRIL_PICKAXE(1273, 21, 20),
    ADAMANT_PICKAXE(1271, 31, 30),
    RUNE_PICKAXE(1275, 41, 40),
    DRAGON_PICKAXE(11920, 61, 60);



    public final int ID;
    public final int REQ;
    public final int ATKREQ;

    Pickaxe(int ID, int REQ, int ATKREQ) {
        this.ID = ID;
        this.REQ = REQ;
        this.ATKREQ = ATKREQ;
    }

    public int getID() {
        return ID;
    }

    public int getREQ() {
        return REQ;
    }

    public int getATKREQ() {
        return ATKREQ;
    }
}
