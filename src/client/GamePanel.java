package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static client.Client.*;

public class GamePanel extends JPanel implements KeyListener {
    private Image backgroundImage1; // First background image
    private Image backgroundImage2; // Second background image
    private Image backgroundImage3; // Third background image
    private Image launchpadTexture;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Load background images
        backgroundImage1 = new ImageIcon("src/client/resources/cloudsbackground.jpg").getImage();
        backgroundImage2 = new ImageIcon("src/client/resources/spacebackground.png").getImage();
        backgroundImage3 = new ImageIcon("src/client/resources/moon.png").getImage();
        launchpadTexture = new ImageIcon("src/client/resources/launchpad.png").getImage();
    }

    public void render() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        float scaleFactor = 2f;

        g2d.translate(0, cameraY);
        g2d.scale(scaleFactor, scaleFactor);

        g2d.drawImage(backgroundImage1, 0, -2000, null);
        g2d.drawImage(backgroundImage2, 0, -4200, null);
        g2d.scale(1 / scaleFactor, 1 / scaleFactor);
        g2d.drawImage(backgroundImage3, 0, -8000, null);

        // Draw rocket texture
        Image rocketTexture = mainRocket.getTexture();
        if (rocketTexture != null) {
            g.drawImage(rocketTexture, mainRocket.getX(), mainRocket.getY(), 36, 80, null);
        } else {
            // Fallback: Draw a red rectangle if no texture is loaded
            g.setColor(Color.RED);
            g.fillRect(mainRocket.getX(), mainRocket.getY(), 36, 80);
        }

        if (serverHandler != null) {
            Image rocketTextureOther = serverHandler.getOtherRocket().getTexture();
            if (rocketTextureOther != null) {
                g.drawImage(rocketTextureOther, serverHandler.getOtherRocket().getX(), serverHandler.getOtherRocket().getY(), 36, 80, null);
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
                    -300, -150,
                    startingPlatform.width, startingPlatform.height,
                    null
            );
        } else {
            // Fallback: Draw a blue rectangle if no texture is loaded
            g.setColor(Color.BLUE);
            g.fillRect(startingPlatform.x, startingPlatform.y, startingPlatform.width, startingPlatform.height);
        }

        if (mainRocket.getY() < -100) {
            spawnFuelCanisters();
            spawnAsteroids();
        }
        
        moveFuelCanisters();
        moveAsteroids();
        checkCollisions();

        for (Asteroid asteroid : asteroids) {
            asteroid.draw(g);
        }

        for (FuelCanister fuelCanister : fuelCanisters) {
            fuelCanister.draw(g);
        }

        g2d.translate(0, -cameraY);

        HUD(g);
    }

    private void HUD(Graphics g) {
        // Set text color and font
        g.setColor(Color.WHITE);
        g.setFont(new Font("Comic Sans", Font.BOLD, 18));

        // Display velocity, height, and fuel as text
        g.drawString("Exp: " + mainRocket.getXp() + ", Needed Exp: " + mainRocket.getLevel().getLevelUpXp() , 10, 20);
        g.drawString("Velocity: " + -Math.round(velocityY) + " m/s", 10, 40);
        g.drawString("Height: " + -Math.round(mainRocket.getY()) + " m", 10, 60);
        g.drawString("Ship: " + mainRocket.getLevel().getName(), 10, 80);

        // Fill the fuel bar (changes color based on level)
        if (mainRocket.getFuel() > mainRocket.getLevel().getFuelCap() * 0.6) {
            g.setColor(Color.GREEN); // Green if fuel is above 50%
        } else if (mainRocket.getFuel() > mainRocket.getLevel().getFuelCap() * 0.3) {
            g.setColor(Color.YELLOW); // Yellow if fuel is 20-50%
        } else {
            g.setColor(Color.RED); // Red if fuel is below 20%
        }
        g.drawString("Fuel: " + mainRocket.getFuel(), 20, 625);

        if (mainRocket.getDurability() > mainRocket.getLevel().getDurability() * 0.6) {
            g.setColor(Color.GREEN);
        } else if (mainRocket.getDurability() > mainRocket.getLevel().getDurability() * 0.3) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.RED);
        }

        g.drawString("Durability: " + mainRocket.getDurability(), 20, 650);

        if(serverHandler == null) {
            g.setColor(Color.BLACK);
            g.drawRect(-1000, -1000, 5000, 5000);
            g.setColor(Color.RED);
            g.drawString("WAITING FOR SERVER CONNECTION", 80, 200);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
            if(!thrust && serverHandler != null) {
                new Thread(() -> thrustPlayer.playAudio("src/client/resources/thrust.wav", true)).start();
            }
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
            thrustPlayer.stopAudio();
        } else if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
            xThrustLeft = false;
        } else if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            xThrustRight = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}