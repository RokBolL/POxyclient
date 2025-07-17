package ru.shun.arasakafabric.stats;
public class PlayerStats {
    private static int killCount = 0;
    public static void incrementKills() {
        killCount++;
    }
    public static void setKills(int kills) {
        killCount = kills;
    }
    public static void resetKills() {
        killCount = 0;
    }
    public static int getKills() {
        return killCount;
    }
} 
