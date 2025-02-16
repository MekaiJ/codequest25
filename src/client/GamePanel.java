package client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import static client.Client.serverHandler;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private int velocityY = 0; // Vertical speed
    private boolean thrust = false;
    private boolean xThrustLeft = false;
    private boolean xThrustRight = false;
    private Timer timer;
    private int cameraY = 0; // Camera Offset
    private Rectangle startingPlatform = new Rectangle(-300, 0, 1032, 256); // Landing pad
    private int MAX_UP_VELOCITY = -20; // Reduced max speed for smoother scrolling
    private int MAX_DOWN_VELOCITY = 20; // Reduced max speed for smoother scrolling
    private int THRUST_POWER = 1; // Reduced thrust power for slower movement
    private int fuelCapacity = 100000;
    private int durability = 100;

    private ArrayList<Asteroid> asteroids = new ArrayList<>();
    private ArrayList<FuelCanister> fuelCanisters = new ArrayList<>();
    private Random random = new Random();

    private int asteroidSpeed = 5;
    private boolean asteroidMovingRight = true;
    private boolean fuelCanisterMovingRight = true;
    private int fuelCanisterSpeed = 5;


    private Image backgroundImage1; // First background image
    private Image backgroundImage2; // Second background image
    private Image backgroundImage3; // Third background image
    private Image backgroundImageMoon; // Moon background image
    private Image currentBackgroundImage; // Current background image
    private Image launchpadTexture;
    private Image asteroidImage;

    private double backgroundScrollY = 0; // Tracks the vertical scroll position of the background

    Asteroid asteroid = new Asteroid(0, 300, 315, 250);
    FuelCanister fuelCanister = new FuelCanister(0, 300, 315, 250);

    private WAVPlayer thrustPlayer = new WAVPlayer();

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Load background images
        backgroundImage1 = new ImageIcon("src/client/resources/cloudsbackground.jpg").getImage();
        backgroundImage2 = new ImageIcon("src/client/resources/spacebackground.png").getImage();
        backgroundImage3 = new ImageIcon("src/client/resources/moon.png").getImage();
        backgroundImageMoon = new ImageIcon("src/client/resources/moon.png").getImage();
        currentBackgroundImage = backgroundImage1; // Set initial background

        launchpadTexture = new ImageIcon("src/client/resources/launchpad.png").getImage();
        asteroidImage = new ImageIcon("src/client/resources/asteriod.png").getImage();

        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(400);
            int y = random.nextInt(400) - 500;
            int size = 30 + random.nextInt(20); // Smaller than rocket
            asteroids.add(new Asteroid(x, y, size, size));
        }
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(400);
            int y = random.nextInt(400) - 500;
            int size = 30 + random.nextInt(20); // Smaller than rocket
            fuelCanisters.add(new FuelCanister(x, y, size, size));
        }

        new Timer(30, e -> moveAsteroids()).start();
        timer = new Timer(30, this); // Game loop running every 30ms
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // Scale up the background images
        double scaleFactor = 2.0; // Scale factor for the background images
        g2d.scale(scaleFactor, scaleFactor);

        // Draw the stacked background images
        drawStackedBackground(g2d);

        // Reset scaling for other elements
        g2d.scale(1 / scaleFactor, 1 / scaleFactor);

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

        if (serverHandler != null) {
            ImageIcon rocketTextureOther = serverHandler.getOtherRocket().getTexture();
            if (rocketTextureOther != null) {
                rocketTextureOther.paintIcon(this, g, serverHandler.getOtherRocket().getX(), serverHandler.getOtherRocket().getY());
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

        for (Asteroid asteroid : asteroids) {
            asteroid.draw(g);
        }

        for(FuelCanister fuelCanister : fuelCanisters) {
            fuelCanister.draw(g);
        }

        g2d.translate(0, -cameraY);

        HUD(g);
    }

    private void drawStackedBackground(Graphics2D g2d) {
        int imageHeight = backgroundImage1.getHeight(null);

        // Calculate the number of images needed to cover the screen
        int numImages = (int) Math.ceil(getHeight() / (imageHeight * 2.0)) + 1;

        // Draw the stacked background images in reverse order
        for (int i = 0; i < numImages; i++) {
            int yPos = (int) (i * imageHeight * 2 - backgroundScrollY); // Adjust for scrolling

            // Draw the images in reverse order
            g2d.drawImage(backgroundImage1, 0, yPos, null); // Moon background at the bottom
            g2d.drawImage(backgroundImage2, 0, yPos + imageHeight * 2, null); // Space background 2
            g2d.drawImage(backgroundImage3, 0, yPos + imageHeight * 4, null); // Space background 1
            g2d.drawImage(backgroundImageMoon, 0, yPos + imageHeight * 6, null); // Clouds background at the top
        }
    }

    private void HUD(Graphics g) {
        // Set text color and font
        g.setColor(Color.WHITE);
        g.setFont(new Font("Comic Sans", Font.BOLD, 18));

        // Display velocity, height, and fuel as text
        g.drawString("Velocity: " + -(velocityY) + " m/s", 20, 30);
        g.drawString("Height: " + -(Client.mainRocket.getY()) + " m", 20, 50);
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
        int tempVelocity = velocityY;
        if (thrust && fuelCapacity > 0) {
            tempVelocity -= THRUST_POWER; // Move up when thrusting
            fuelCapacity -= 1;
        }
        if(fuelCapacity <= 0) {
            thrustPlayer.stopAudio();
        }

        if (xThrustLeft) {
            Client.mainRocket.setX(Client.mainRocket.getX() - 5);
        }
        if (xThrustRight) {
            Client.mainRocket.setX(Client.mainRocket.getX() + 5);
        }

        if (Client.mainRocket.getY() < -79) {
            tempVelocity += 1;
        }

        if (Client.mainRocket.getY() > startingPlatform.y) {
            Client.mainRocket.setY(startingPlatform.y);
            tempVelocity = 0;
            thrust = false;
        }

        if (velocityY < MAX_UP_VELOCITY)
            tempVelocity = MAX_UP_VELOCITY;
        if (velocityY > MAX_DOWN_VELOCITY)
            tempVelocity = MAX_DOWN_VELOCITY;

        Client.mainRocket.setY(Client.mainRocket.getY() + velocityY);

        // Update Camera
        cameraY = -(Client.mainRocket.getY() - getHeight() / 2);

        // Scroll the background based on rocket movement
        backgroundScrollY += velocityY * 0.1; // Adjust scrolling speed

        if (Client.mainRocket.getX() < -20)
            Client.mainRocket.setX(-20);
        if (Client.mainRocket.getX() > 425)
            Client.mainRocket.setX(425);

        if (asteroidMovingRight) {
            asteroid.setX(asteroid.getX() + asteroidSpeed);
            if (asteroid.getX() >= getWidth() - asteroid.getWidth()) {
                asteroidMovingRight = false;
            }
        } else {
            asteroid.setX(asteroid.getX() - asteroidSpeed);
            if (asteroid.getX() <= 0) {
                asteroidMovingRight = true;
            }
        }

        if (fuelCanisterMovingRight) {
            fuelCanister.setX(fuelCanister.getX() + fuelCanisterSpeed);
            if (fuelCanister.getX() >= getWidth() - fuelCanister.getWidth()) {
                fuelCanisterMovingRight = false;
            }
        } else {
            fuelCanister.setX(fuelCanister.getX() - fuelCanisterSpeed);
            if (fuelCanister.getX() <= 0) {
                fuelCanisterMovingRight = true;
            }
        }


// Randomly reposition asteroid vertically
        if (Math.random() < 0.01) { // 1% chance each frame
            int newY = (int) (Math.random() * (getHeight() - asteroid.getHeight()));
            if (newY > 0) {
                asteroid.setY(newY);
            }
        }

        if (Math.random() < 0.01) { // 1% chance each frame
            int newY = (int) (Math.random() * (getHeight() - fuelCanister.getHeight()));
            if (newY > 0) {
                fuelCanister.setY(newY);
            }
        }

        moveFuelCanisters();
        checkCollisions();
        repaint();
        velocityY = tempVelocity;

        if (durability == 0) {
            resetGame();
        }
    }

    private void checkCollisions() {
        Rectangle rocketHitbox = new Rectangle(Client.mainRocket.getX(), Client.mainRocket.getY(), 50, 80);
        for (Asteroid asteroid : asteroids) {
            Rectangle asteroidHitbox = new Rectangle(asteroid.getX(), asteroid.getY(), asteroid.getWidth(), asteroid.getHeight());
            if (rocketHitbox.intersects(asteroidHitbox)) {
                durability -= 10;
                asteroid.setY(-random.nextInt(400) - 100); // Reset asteroid position
                asteroid.setX(random.nextInt(400));
                if (durability <= 0) {
                    gameOver();
                }
            }
        }
    }

    private void resetGame() {
        Client.mainRocket.setX(225);
        Client.mainRocket.setY(0);
        velocityY = 0;
        cameraY = 0;
        backgroundScrollY = 0; // Reset background scroll
        fuelCapacity = Client.mainRocket.getFuel();
        durability = Client.mainRocket.getDurability();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
            if(!thrust) {
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

    private void gameOver() {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over! You hit an asteroid.");
        resetGame();
        timer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private void moveAsteroids() {
        for (Asteroid asteroid : asteroids) {
            asteroid.setX(asteroid.getX() + asteroid.getSpeed());
            asteroid.setY(asteroid.getY() + asteroid.getVerticalSpeed());

            // Bounce horizontally
            if (asteroid.getX() > getWidth() - asteroid.getWidth() || asteroid.getX() < 0) {
                asteroid.setSpeed(-asteroid.getSpeed());
            }

            // Bounce vertically
            if (asteroid.getY() > getHeight() - asteroid.getHeight() || asteroid.getY() < 0) {
                asteroid.setVerticalSpeed(-asteroid.getVerticalSpeed());
            }
        }
        repaint();
    }

    private void moveFuelCanisters() {
        for (FuelCanister fuelCanister : fuelCanisters) {
            fuelCanister.setX(fuelCanister.getX() + fuelCanister.getSpeed());
            fuelCanister.setY(fuelCanister.getY() + fuelCanister.getVerticalSpeed());

            // Bounce horizontally
            if (fuelCanister.getX() > getWidth() - fuelCanister.getWidth() || fuelCanister.getX() < 0) {
                fuelCanister.setSpeed(-fuelCanister.getSpeed());
            }

            // Bounce vertically
            if (fuelCanister.getY() > getHeight() - fuelCanister.getHeight() || fuelCanister.getY() < 0) {
                fuelCanister.setVerticalSpeed(-fuelCanister.getVerticalSpeed());
            }
        }
        repaint();
    }

    class Asteroid {
        private int x, y, width, height, speed, verticalSpeed;

        public Asteroid(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speed = new Random().nextBoolean() ? 3 : -3;
            this.verticalSpeed = new Random().nextBoolean() ? 2 : -2;
        }

        public void draw(Graphics g) {
            g.setColor(Color.GRAY);
            g.fillOval(x, y, width, height);
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

    class FuelCanister {
        private int x, y, width, height, speed, verticalSpeed;

        public FuelCanister(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speed = new Random().nextBoolean() ? 3 : -3;
            this.verticalSpeed = new Random().nextBoolean() ? 2 : -2;
        }

        public void draw(Graphics g) {
            g.setColor(Color.RED);
            g.fillOval(x, y, width, height);
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
}