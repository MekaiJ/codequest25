package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static client.Client.serverHandler;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private int velocityY = 0;// Vertical speed
    private int velocityX = 0;// Horizontal speed
    private boolean thrust = false;
    private boolean xThrustLeft = false;
    private boolean xThrustRight = false;
    private Timer timer;
    private int cameraY = 0; // Camera Offset
    private int worldHeight = 200000; // World height
    private Rectangle startingPlatform = new Rectangle(100, 0, 600, 250);// Landing pad
    private int MAX_UP_VELOCITY = -45;
    private int MAX_DOWN_VELOCITY = 45;
    private int THRUST_POWER = 2;
    private int DECELRATION = 1;
    private int fuelCapacity = 100;
    private boolean onPlatform = true;
    private int durability = 100;

    private Image backgroundImage;
    private Image launchpadTexture;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        backgroundImage = new ImageIcon("src/client/resources/cloudsbackground.jpg").getImage();
        launchpadTexture = new ImageIcon("src/client/resources/launchpad.png").getImage();

        timer = new Timer(30, this); // Game loop running every 30ms
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // Draw the background image
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, cameraY, getWidth(), getHeight(), null);
        }

        g2d.translate(0, cameraY);

        // Draw rocket texture
        ImageIcon rocketTexture = Client.mainRocket.getTexture();
        if (rocketTexture != null) {
            rocketTexture.paintIcon(this, g, Client.mainRocket.getX(), Client.mainRocket.getY());
        } else {
            // Fallback: Draw a red rectangle if no texture is loaded
            g.setColor(Color.RED);
            g.fillRect(Client.mainRocket.getX(), Client.mainRocket.getY(), 50, 80);
        }

        if(serverHandler != null) {
            ImageIcon rocketTextureOther = serverHandler.getOtherRocket().getTexture();
            if (rocketTexture != null) {
                rocketTexture.paintIcon(this, g, serverHandler.getOtherRocket().getX(), serverHandler.getOtherRocket().getY());
            } else {
                // Fallback: Draw a red rectangle if no texture is loaded
                g.setColor(Color.RED);
                g.fillRect(serverHandler.getOtherRocket().getX(), serverHandler.getOtherRocket().getY(), 50, 80);
            }
        }
        // Draw launchpad texture
        if (launchpadTexture != null) {
            g.drawImage(
                    launchpadTexture,
                    startingPlatform.x, startingPlatform.y,
                    startingPlatform.width, startingPlatform.height,
                    null
            );
        } else {
            // Fallback: Draw a blue rectangle if no texture is loaded
            g.setColor(Color.BLUE);
            g.fillRect(startingPlatform.x, startingPlatform.y, startingPlatform.width, startingPlatform.height);
        }

        g2d.translate(0, -cameraY);

        HUD(g);
    }

    private void HUD(Graphics g) {
        // Set text color and font
        g.setColor(Color.WHITE);
        g.setFont(new Font("Comic Sans", Font.BOLD, 18));

        // Display velocity, height, and fuel as text
        g.drawString("Velocity: " + -(velocityY) + " px/s", 20, 30);
        g.drawString("Height: " + -(Client.mainRocket.getY() + 80) + " px", 20, 50);
        g.drawString("Fuel:", 20, 625);
        g.drawString("Durability: ", 20, 650);

        // --- Draw Fuel Bar ---
        int fuelBarWidth = 150;
        int fuelBarHeight = 15;
        int fuelFillWidth = (int) ((fuelCapacity / 100.0) * fuelBarWidth);

        int durabilityBarWidth = 150;
        int durabilityBarHeight = 15;
        int durabilityFillWidth = (int) ((durability / 100.0) * durability);

        // Outline of the fuel bar
        g.setColor(Color.GRAY);
        g.fillRect(70, 610, fuelBarWidth, fuelBarHeight);

        // Fill the fuel bar (changes color based on level)
        if (fuelCapacity > 50) {
            g.setColor(Color.GREEN); // Green if fuel is above 50%
        } else if (fuelCapacity > 20) {
            g.setColor(Color.YELLOW); // Yellow if fuel is 20-50%
        } else {
            g.setColor(Color.RED); // Red if fuel is below 20%
        }
        g.fillRect(70, 610, fuelFillWidth, fuelBarHeight);

        g.setColor(Color.GRAY);
        g.fillRect(125, 639, durabilityFillWidth, durabilityBarHeight);

        if (durability > 50) {
            g.setColor(Color.GREEN);
        } else if (durability > 20) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.RED);
        }
        g.fillRect(125, 639, durabilityBarWidth, durabilityBarHeight);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thrust && fuelCapacity > 0) {
            velocityY -= THRUST_POWER;// Move up when thrusting
            fuelCapacity -= 1;
            onPlatform = false;
        }
        else {
            if (velocityY < 0)
                velocityY += DECELRATION;
        }

        if (xThrustLeft) {
            Client.mainRocket.setX(Client.mainRocket.getX() - 5);
        }
        if (xThrustRight) {
            Client.mainRocket.setX(Client.mainRocket.getX() + 5);
        }

        velocityY += 1; // Simulate gravity

        if (Client.mainRocket.getY() > startingPlatform.y) {
            Client.mainRocket.setY(startingPlatform.y);
            velocityY = 0;
        }

        if (velocityY < MAX_UP_VELOCITY)
            velocityY = MAX_UP_VELOCITY;
        if (velocityY > MAX_DOWN_VELOCITY)
            velocityY = MAX_DOWN_VELOCITY;

        Client.mainRocket.setY(Client.mainRocket.getY() + velocityY);

        // Update Camera
        cameraY = -(Client.mainRocket.getY() - getHeight() / 2);

        // Prevent rocket from going off-screen
        if (Client.mainRocket.getY() > worldHeight - 80) {
            Client.mainRocket.setY(worldHeight - 80);
            velocityY = 0;
        }

        repaint();
    }

    private void resetGame() {
        Client.mainRocket.setX(225);
        Client.mainRocket.setY(0);
        velocityY = 0;
        cameraY = 0;
        fuelCapacity = Client.mainRocket.getFuel();
        durability = Client.mainRocket.getDurability();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
            thrust = true;
        } else if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
            xThrustLeft = true;
        } else if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            xThrustRight = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
            thrust = false;
        } else if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
            xThrustLeft = false;
        } else if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            xThrustRight = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

