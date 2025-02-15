package client;

import javax.swing.*;
import java.awt.*;

public class Moon {
    private int x, y; // Position
    private int width, height; // Size
    private Image texture; // Texture for the moon

    public Moon(int x, int y, int width, int height, Image texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    // Method to draw the moon
    public void draw(Graphics g) {
        if (texture != null) {
            g.drawImage(texture, x, y, width, height, null); // Draw the texture
        } else {
            // Fallback: Draw a colored circle if no texture is loaded
            g.setColor(Color.LIGHT_GRAY);
            g.fillOval(x, y, width, height);
        }
    }

    // Method to check if the rocket has reached the moon
    public boolean hasReachedMoon(Rectangle rocketBounds) {
        Rectangle moonBounds = new Rectangle(x, y, width, height);
        return moonBounds.intersects(rocketBounds);
    }

    private static Image loadMoonTexture() {
        String imagePath = "resources/moon.png"; // Path to the moon texture
        ImageIcon icon = new ImageIcon(imagePath);

        // Check if the image was loaded successfully
        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("Failed to load moon texture: " + imagePath);
            return null; // Return null if the image fails to load
        }

        return icon.getImage(); // Return the loaded image
    }
}