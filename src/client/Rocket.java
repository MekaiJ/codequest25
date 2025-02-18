package client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.swing.*;

public class Rocket {
    //handling is for horizontal move speed
    private int handling;

    //durability is how many times the ship can get hit before breaking
    private int durability;

    private String name;

    private int fuel;

    int x;
    int y;

    private int xp;
    private Image texture;
    private RocketLevel level;

    public Rocket(RocketLevel level) {
        setDurability(100);
        setX(225);
        this.level = level;
        this.xp = 0;
        this.texture = loadRocketImage(level).getImage();
        setStats();
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
    public Image getTexture() {
        return texture;
    }

    public void setStats() {
        this.durability = this.level.getDurability();
        this.name = this.level.getName();
        this.fuel = this.level.getFuelCap();
        this.texture = loadRocketImage(this.level).getImage();
    }

    public void addXp(int xpToAdd) {
        this.xp += xpToAdd;
    }

    public String getName() {
        return name;
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
