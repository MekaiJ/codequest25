package client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.swing.*;

public class Rocket implements Serializable {
    //handling is for horizontal move speed
    private int handling;

    //durability is how many times the ship can get hit before breaking
    private int durability;

    private String name;

    private int fuel;

    int x;
    int y;

    private int xp;
    private int xpToLevelUp;

    private ImageIcon texture;

    private RocketLevel level;

    public Rocket(RocketLevel level) {
        setDurability(100);
        setX(225);
        this.level = level;
        this.xp = 0;
        this.texture = loadRocketImage(level);
        setStats(level);
    }

    public void consumeFuel(int amount) {
        this.fuel -= amount;
        if (this.fuel < 0) this.fuel = 0;
    }

    public void refuel(int amount) {
        this.fuel += amount;
        if (this.fuel > level.getFuelCap()) this.fuel = level.getFuelCap();
    }

    public int getXp() {
        return this.xp;
    }

    public ImageIcon loadRocketImage(RocketLevel level) {
        String imagePath = "src/client/resources/rocket_level" + (level.ordinal() + 1) + ".png";
        ImageIcon icon = new ImageIcon(imagePath);

        // Check if the image was loaded successfully
        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("Failed to load image: " + imagePath);
            // Create a fallback image (e.g., a red rectangle)
            Image fallbackImage = new BufferedImage(50, 80, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D) fallbackImage.getGraphics();
            g2d.setColor(Color.RED);
            g2d.fillRect(0, 0, 100, 100);
            g2d.dispose();
            icon = new ImageIcon(fallbackImage);
        }

        // Scale the image to 100x100 pixels
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    // Method to get the rocket image
    public ImageIcon getTexture() {
        return texture;
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
    public void checkLevelUp() {
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

    public void setXP(int toSet) {
        this.xp = toSet;
    }

    public RocketLevel getLevel() {
        return level;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    public void setLevel(RocketLevel level) {
        this.level = level;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }
    public void setFuel(int fuel) {
        this.fuel = fuel;
    }
}
