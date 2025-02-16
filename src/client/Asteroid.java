package client;

import javax.swing.*;
import java.awt.*;

public class Asteroid extends Obstacle {
    public Asteroid(int x, int y, int width, int height) {
        super(x, y, width, height, loadTexture()); // Load the texture
    }

    // Method to load the asteroid texture
    private static Image loadTexture() {
        String imagePath = "src/client/resources/asteroid.png"; // Path to the asteroid texture
        ImageIcon icon = new ImageIcon(imagePath);

        // Check if the image was loaded successfully
        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("Failed to load asteroid texture: " + imagePath);
            return null;
        }

        return icon.getImage();
    }
}
