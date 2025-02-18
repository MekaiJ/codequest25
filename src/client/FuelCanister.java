package client;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class FuelCanister {
    private int x, y, width, height, speed, verticalSpeed;
    private Image texture;
    public FuelCanister(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = new Random().nextBoolean() ? 6 : -6;
        this.verticalSpeed = new Random().nextBoolean() ? 4 : -4;
        this.texture = loadTexture();
    }

    public void draw(Graphics g) {
        g.drawImage(this.texture, this.x, this.y, this.width, this.height, null);
    }

    private static Image loadTexture() {
        String imagePath = "src/client/resources/fuelcanister.png";
        ImageIcon icon = new ImageIcon(imagePath);

        // Check if the image was loaded successfully
        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("Failed to load asteroid texture: " + imagePath);
            return null;
        }

        return icon.getImage();
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public int getVerticalSpeed() { return verticalSpeed; }
    public void setVerticalSpeed(int verticalSpeed) { this.verticalSpeed = verticalSpeed; }
}