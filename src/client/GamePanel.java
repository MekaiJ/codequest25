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
    private int MAX_UP_VELOCITY = -200;
    private int MAX_DOWN_VELOCITY = 200;
    private int THRUST_POWER = 2;
    private int DECELRATION = 1;

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

        // Information
        g.setColor(Color.WHITE);
        g.setFont(new Font("Times Roman", Font.BOLD, 12));
        g.drawString("Height: " + (worldHeight - rocketY), 20, 20);
        g.drawString("Velocity: " + -velocityY, 20, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thrust)
            velocityY -= THRUST_POWER; // Move up when thrusting
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
            velocityY = 0;
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
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            thrust = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

