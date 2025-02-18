package client;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

import static java.lang.Math.random;

public class Asteroid extends Obstacle {
    private int x, y, width, height, xSpeed, ySpeed;

    public Asteroid(int x, int y, int width, int height) {
        super(x, y, width, height, loadTexture());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xSpeed = (int) ((random() - 0.5) * 6);
        this.ySpeed = (int) ((random() - 0.5) * 4);
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

    public void draw(Graphics g) {
        g.drawImage(this.texture, this.x, this.y, this.width, this.height, null);
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getXSpeed() { return xSpeed; }
    public int getYSpeed() { return ySpeed; }

    public void setXSpeed(int xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setYSpeed(int ySpeed) {
        this.ySpeed = ySpeed;
    }
}