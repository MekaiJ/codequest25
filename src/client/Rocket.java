package client;

import java.io.Serializable;
import javax.swing.*;

public class Rocket implements Serializable {
    //handling is for horizontal move speed
    private int handling;

    //durability is how many times the ship can get hit before breaking
    private int durability;

    private String name;

    //maybe add fuel tank
    private int fuel;

    private RocketLevel level;

    public Rocket(RocketLevel level) {
        this.level = level;
    }

    private void setStats(RocketLevel level) {
        this.handling = level.getHandling();
        this.durability = level.getDurability();
        this.name = level.getName();
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getHandling() {
        return handling;
    }

    public int getDurability() {
        return durability;
    }

    public RocketLevel getLevel() {
        return level;
    }

}
