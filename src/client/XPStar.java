package client;

import javax.swing.*;
import java.awt.*;

public class XPStar extends Obstacle {
    private int xpValue; // Amount of XP this star gives

    public XPStar(int x, int y, int width, int height, Image texture, int xpValue) {
        super(x, y, width, height, texture); // Call the parent constructor
        this.xpValue = xpValue;
    }

    // Method to check if the rocket collides with the XP star and award XP
    public boolean checkCollision(Rocket rocket) {
        Rectangle rocketBounds = new Rectangle(rocket.getX(), rocket.getY(), 50, 80); // Rocket bounds
        Rectangle starBounds = new Rectangle(x, y, width, height); // XP Star bounds

        if (rocketBounds.intersects(starBounds)) {
            rocket.addXp(xpValue); // Award XP to the rocket
            return true; // Collision occurred
        }
        return false; // No collision
    }

    private static Image loadTexture() {
        String imagePath = "resources/xp_star.png"; // Path to the XP Star texture
        ImageIcon icon = new ImageIcon(imagePath);

        // Check if the image was loaded successfully
        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("Failed to load XP Star texture: " + imagePath);
            return null; // Return null if the image fails to load
        }

        return icon.getImage(); // Return the loaded image
    }

    // Getter for XP value (optional)
    public int getXpValue() {
        return xpValue;
    }
}