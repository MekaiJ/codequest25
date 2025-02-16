package client;

import java.io.Serializable;

public enum RocketLevel implements Serializable {
    LEVEL_1(3, 3, "Rust Bucket", 100, 100),
    LEVEL_2(5, 5, "The Comet", 500, 300),
    LEVEL_3(7, 7, "Time Traveler", 1000, 600);

    private final int handling;
    private final int durability;
    private final String name;
    private final int fuelCap;
    private final int levelUpXp;

    RocketLevel(int handling, int durability, String name, int fuelCap, int levelUpXp) {
        this.handling = handling;
        this.durability = durability;
        this.name = name;
        this.fuelCap = fuelCap;
        this.levelUpXp = levelUpXp;
    }

    public int getHandling() {
        return handling;
    }

    public int getDurability() {
        return durability;
    }

    public int getFuelCap() {return fuelCap;}

    public int getLevelUpXp() {return levelUpXp;}

    public String getName() {
        return name;
    }
}
