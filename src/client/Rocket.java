package client;

import java.io.Serializable;
import javax.swing.*;

public abstract class Rocket implements Serializable {
    //handling is for horizontal move speed
    protected int handling;

    //durability is how many times the ship can get hit before breaking
    protected int durability;

    protected String name;

    public Rocket(int handling, int durability, String name) {
        this.handling = handling;
        this.durability = durability;
        this.name = name;
    }

    public Rocket() {
        this.handling = 0;
        this.durability = 0;
        this.name = "Abstract Rocket Class";
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

}
