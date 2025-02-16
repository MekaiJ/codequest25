package client;

import java.io.Serializable;

public enum RocketLevel implements Serializable {
    LEVEL_1(3, 1, "Rust Bucket", 100, 2000),
    LEVEL_2(5, 2, "The Comet", 200, 3500),
    LEVEL_3(7, 3, "Time Traveler", 350, 100000);

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
