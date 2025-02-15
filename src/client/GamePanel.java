package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private int rocketX = 225;  // Initial X position
    private int rocketY = 500;  // Initial Y position
    private int velocityY = 0;  // Vertical speed
    private boolean thrust = false;
    private Timer timer;
    private int cameraY = 0; // Camera Offset
    private int worldHeight = 200000; // World height
    private Rectangle landingPad = new Rectangle(200, 600, 100, 10); // Landing pad
    private int MAX_UP_VELOCITY = -45;
    private int MAX_DOWN_VELOCITY = 45;
    private int THRUST_POWER = 2;
    private int DECELRATION = 1;
    private boolean isOnLandingPad = false;
    private int fuelCapacity = 100;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(30, this); // Game loop running every 30ms
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(0, cameraY);

        // Draw rocket
        g.setColor(Color.RED);
        g.fillRect(rocketX, rocketY, 50, 80);

        // Draw landing pad
        g.setColor(Color.GREEN);
        g.fillRect(landingPad.x, landingPad.y, landingPad.width, landingPad.height);

        g2d.translate(0, -cameraY);

        HUD(g);
    }

    private void HUD(Graphics g) {
        // Set text color and font
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));

        // Display velocity, height, and fuel as text
        g.drawString("Velocity: " + -(velocityY) + " px/s", 20, 30);
        g.drawString("Height: " + -(rocketY - 500) + " px", 20, 50);

        // --- Draw Fuel Bar ---
        int fuelBarWidth = 150;
        int fuelBarHeight = 15;
        int fuelFillWidth = (int) ((fuelCapacity / 100.0) * fuelBarWidth);

        // Outline of the fuel bar
        g.setColor(Color.GRAY);
        g.fillRect(20, 650, fuelBarWidth, fuelBarHeight);

        // Fill the fuel bar (changes color based on level)
        if (fuelCapacity > 50) {
            g.setColor(Color.GREEN); // Green if fuel is above 50%
        } else if (fuelCapacity > 20) {
            g.setColor(Color.YELLOW); // Yellow if fuel is 20-50%
        } else {
            g.setColor(Color.RED); // Red if fuel is below 20%
        }
        g.fillRect(20, 650, fuelFillWidth, fuelBarHeight);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thrust && fuelCapacity > 0) {
            velocityY -= THRUST_POWER;// Move up when thrusting
            fuelCapacity -= 1;
        }
        else {
            if (velocityY < 0)
                velocityY += DECELRATION;
        }

        velocityY += 1; // Simulate gravity

        if (velocityY < MAX_UP_VELOCITY)
            velocityY = MAX_UP_VELOCITY;
        if (velocityY > MAX_DOWN_VELOCITY)
            velocityY = MAX_DOWN_VELOCITY;

        rocketY += velocityY;

        // Update Camera
        cameraY = -(rocketY - getHeight() / 2);

        // Prevent rocket from going off-screen
        if (rocketY > worldHeight - 80) {
            rocketY = worldHeight - 80;
            velocityY = 0;
        }

        // Check if rocket lands on the pad
        if (new Rectangle(rocketX, rocketY, 50, 80).intersects(landingPad)) {
            isOnLandingPad = true;
        }

        repaint();
    }

    private void resetGame() {
        rocketX = 225;
        rocketY = 0;
        velocityY = 0;
        cameraY = 0;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            thrust = true;
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            rocketX -= 15;
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            rocketX += 15;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            thrust = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

