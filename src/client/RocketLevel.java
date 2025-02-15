package client;

import java.io.Serializable;

public enum RocketLevel implements Serializable {
    LEVEL_1(3, 3, "Rust Bucket"),
    LEVEL_2(5, 5, "The Comet"),
    LEVEL_3(7, 7, "Time Traveler");

    private final int handling;
    private final int durability;
    private final String name;

    RocketLevel(int handling, int durability, String name) {
        this.handling = handling;
        this.durability = durability;
        this.name = name;
    }

    public int getHandling() {
        return handling;
    }

    public int getDurability() {
        return durability;
    }

    public String getName() {
        return name;
    }
}
