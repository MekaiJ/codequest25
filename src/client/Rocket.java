package client;

import java.io.Serializable;
import javax.swing.*;

public class Rocket implements Serializable {
    //handling is for horizontal move speed
    private int handling;

    //durability is how many times the ship can get hit before breaking
    private int durability;

    private String name;

    private int level;

    public Rocket(int handling, int durability, String name, int level) {
        this.handling = handling;
        this.durability = durability;
        this.name = name;
        this.level = level;
    }

    private void setStats() {
        if(this.getLevel() == 1) {
            this.handling  = 3;
            this.durability = 3;
            this.name = "Rust Bucket";
        }
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

    public int getLevel() {
        return level;
    }

}
