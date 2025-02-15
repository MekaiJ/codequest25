package client;

import java.io.Serializable;
import javax.swing.*;

public class Rocket implements Serializable {
    //handling is for horizontal move speed
    private int handling;

    //durability is how many times the ship can get hit before breaking
    private int durability;

    private String name;

    private int fuel;

    private int xp;
    private int xpToLevelUp;

    private RocketLevel level;

    public Rocket(RocketLevel level) {
        this.level = level;
        this.xp = 0;
    }

    private void setStats(RocketLevel level) {
        this.handling = level.getHandling();
        this.durability = level.getDurability();
        this.name = level.getName();
        this.fuel = level.getFuelCap();
        this.level = level;
    }

    public void addXp(int xpToAdd) {
        this.xp += xpToAdd;
    }

    // Method to check if the rocket should level up
    private void checkLevelUp() {
        while (this.xp >= level.getLevelUpXp() && level != RocketLevel.LEVEL_3) {
            levelUp();
        }
    }

    // Method to handle leveling up
    private void levelUp() {
        RocketLevel[] levels = RocketLevel.values();
        int nextLevelIndex = level.ordinal() + 1;

        if (nextLevelIndex < levels.length) {
            level = levels[nextLevelIndex];
            this.xp -= level.getLevelUpXp(); // Carry over excess XP
            updateStats(); // Update rocket stats based on the new level
            System.out.println("Level up! You are now level " + (level.ordinal() + 1));
        } else {
            System.out.println("You are at the maximum level!");
        }
    }

    // Method to update rocket stats when leveling up
    private void updateStats() {
        System.out.println("Stats updated: Handling=" + level.getHandling() +
                ", Durability=" + level.getDurability() +
                ", Name=" + level.getName());
    }

    public int getXpToNextLevel() {
        return level.getLevelUpXp();
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

    public int getFuel() {
        return fuel;
    }

    public RocketLevel getLevel() {
        return level;
    }

}
