package client;

import java.awt.*;

public abstract class Obstacle {
    protected int x, y; // Position
    protected int width, height; // Size
    protected Image texture; // Texture for the obstacle

    public Obstacle(int x, int y, int width, int height, Image texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    // Method to draw the obstacle
    public void draw(Graphics g) {
        if (texture != null) {
            g.drawImage(texture, x, y, width, height, null); // Draw the texture
        } else {
            // Fallback: Draw a colored rectangle if no texture is loaded
            g.setColor(Color.GRAY);
            g.fillRect(x, y, width, height);
        }
    }

    // Method to check if the rocket collides with the obstacle
    public boolean collidesWith(Rectangle rocketBounds) {
        Rectangle obstacleBounds = new Rectangle(x, y, width, height);
        return obstacleBounds.intersects(rocketBounds);
    }

    // Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}