package enums;

/**
 * for holding pickpocket targets.
 */
public enum TargetNPC {
    MAN("Man", 1, 5000, 1),
    FARMER("Farmer", 10, 5000, 1),
    HAM_MEMBER("H.A.M. Member", 20, 4000, 3),
    WARRIOR_WOMAN("Warrior woman", 25, 5000, 2),
    AL_KHARID_WARRIOR("Al-Kharid warrior", 25, 5000, 2),
    VILLAGER("Villager", 30, 5000, 2),
    ROUGE("Rouge", 32, 5000, 2),
    CAVE_GOBLIN("Cave goblin",36, 5000, 1),
    MASTER_FARMER("Master Farmer", 38, 5000, 3),
    GUARD("Guard", 40, 5000, 2),
    FREMENNIK_CITIZEN("Fremennik citizen", 45, 5000, 2),
    BANDIT("Bandit", 55, 5000, 5),
    KNIGHT_OF_ARDOUGNE("Knight of Ardougne", 55, 5000, 3),
    WATCHMAN("Watchman", 65, 5000, 3),
    MENAPHITE_THUG("Menaphite Thug", 65, 5000, 5),
    PALADIN("Paladin", 70, 5000, 3),
    GNOME("Gnome", 75, 5000, 1),
    HERO("Hero", 80, 6000, 4),
    VYRE("Vyre", 82, 6000, 5),
    ELF("use a list", 85, 6000, 5),
    TZHAAR("TzHaar-Hur", 90, 6000, 4);

    public String NAME;
    public int REQLVL;
    public int STUN_TIME; // in MS
    public int MAX_DMG;

    TargetNPC(String NAME, int REQLVL, int STUN_TIME, int MAX_DMG) {
        this.NAME = NAME;
        this.REQLVL = REQLVL;
        this.STUN_TIME = STUN_TIME;
        this.MAX_DMG = MAX_DMG;
    }

    public static void main(String[] args) {
        for (TargetNPC trgt : TargetNPC.values()) {
            System.out.println(trgt.NAME);
        }
    }
}
