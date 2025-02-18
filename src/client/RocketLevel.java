package client;

import java.io.Serializable;

public enum RocketLevel implements Serializable {
    LEVEL_1( 1, "Rust Bucket", 200, 3000, 1f),
    LEVEL_2( 3, "The Comet", 350, 4000, 1.25f),
    LEVEL_3(5, "Time Traveler", 500, 100000, 1.5f);

    private final float thrustPower;
    private final int durability;
    private final String name;
    private final int fuelCap;
    private final int levelUpXp;

    RocketLevel(int durability, String name, int fuelCap, int levelUpXp, float thrustPower) {
        this.durability = durability;
        this.name = name;
        this.fuelCap = fuelCap;
        this.levelUpXp = levelUpXp;
        this.thrustPower = thrustPower;
    }

    public int getDurability() {
        return durability;
    }

    public float getThrustPower() {return thrustPower;}

    public int getFuelCap() {return fuelCap;}

    public int getLevelUpXp() {return levelUpXp;}

    public String getName() {
        return name;
    }
}
