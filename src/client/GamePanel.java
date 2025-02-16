package client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import static client.Client.*;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private static final int SPAWN_HEIGHT_THRESHOLD = 1000;
    private final long asteroidSPAWN_COOLDOWN = 1000;
    private final long fuelCanisterSPAWN_COOLDOWN = 1000;
    private long asteroidlastSpawnTime = 0;
    private long fuelcanisterlastSpawnTime = 0;
    private int velocityY = 0; // Vertical speed
    private boolean thrust = false;
    private boolean xThrustLeft = false;
    private boolean xThrustRight = false;
    private Timer timer;
    private int cameraY = 0; // Camera Offset
    private Rectangle startingPlatform = new Rectangle(-300, 0, 1032, 256); // Landing pad
    private int MAX_UP_VELOCITY = -20; // Reduced max speed for smoother scrolling
    private int MAX_DOWN_VELOCITY = 20; // Reduced max speed for smoother scrolling
    private int THRUST_POWER = 2; // Reduced thrust power for slower movement
    private int maxAchievedHeight = 0;
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
    private WAVPlayer explosionPlayer = new WAVPlayer();

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

        spawnAsteroids();
        spawnFuelCanisters();

        new Timer(30, e -> moveAsteroids()).start();
        timer = new Timer(30, this); // Game loop running every 30ms
        timer.start();
    }

    private void spawnFuelCanisters() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - fuelcanisterlastSpawnTime >= fuelCanisterSPAWN_COOLDOWN) {
            fuelcanisterlastSpawnTime = currentTime;

            // Spawn a smaller, controlled number of canisters
            int numToSpawn = 1; // Only spawn 1 canister at a time

            for (int i = 0; i < numToSpawn; i++) {
                // Use rocket's position as the center point for spawning
                int x = Client.mainRocket.getX() + random.nextInt(100) - 50; // Offset the spawn horizontally
                int y = Client.mainRocket.getY() - 100 - random.nextInt(100); // Spawn above the rocket
                int size = 30 + random.nextInt(20); // Random size for the canisters

                // Create a new fuel canister and add it to the list
                fuelCanisters.add(new FuelCanister(x, y, size, size));
            }
        }
    }

    private void spawnAsteroids() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - asteroidlastSpawnTime >= asteroidSPAWN_COOLDOWN) {
            asteroidlastSpawnTime = currentTime;
            int numToSpawn = 1; // Random number of canisters to spawn

            for (int i = 0; i < numToSpawn; i++) {
                // Use rocket's position as the center point for spawning
                int x = Client.mainRocket.getX() + random.nextInt(100) - 50; // Offset the spawn horizontally
                int y = Client.mainRocket.getY() - 100 - random.nextInt(100); // Spawn above the rocket
                int size = 30 + random.nextInt(20); // Random size for the canisters

                // Create a new fuel canister and add it to the list
                asteroids.add(new Asteroid(x, y, size, size));
            }
        }
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
        g.drawString("Exp: " + mainRocket.getXp() + ", Needed Exp: " + mainRocket.getLevel().getLevelUpXp() , 10, 20);
        g.drawString("Velocity: " + -(velocityY) + " m/s", 10, 40);
        g.drawString("Height: " + -(Client.mainRocket.getY()) + " m", 10, 60);
        g.drawString("Ship: " + mainRocket.getLevel().getName(), 10, 80);
        // --- Draw Fuel Bar ---
        int fuelBarWidth = 150;
        int fuelBarHeight = 15;
        int fuelFillWidth = (int) ((mainRocket.getFuel() / 100.0) * fuelBarWidth);

        int durabilityBarWidth = 150;
        int durabilityBarHeight = 15;
        int durabilityFillWidth = (int) ((mainRocket.getDurability() / 100.0) * mainRocket.getDurability());

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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thrust && mainRocket.getFuel() > 0) {
            velocityY -= THRUST_POWER; // Move up when thrusting
            mainRocket.setFuel(mainRocket.getFuel() - 1);
        }
        if(mainRocket.getFuel() <= 0) {
            thrustPlayer.stopAudio();
            if(velocityY >= 0) {
                gameOver();
            }
        }

        if(!thrust) {
            thrustPlayer.stopAudio();
        }

        if(Math.abs(mainRocket.getY()) > maxAchievedHeight) {
            maxAchievedHeight = Math.abs(mainRocket.getY());
        }

        if (xThrustLeft) {
            Client.mainRocket.setX(Client.mainRocket.getX() - 5);
        }
        if (xThrustRight) {
            Client.mainRocket.setX(Client.mainRocket.getX() + 5);
        }

        if (Client.mainRocket.getY() < -3) {
            velocityY += 1;
        }

        if (Client.mainRocket.getY() > startingPlatform.y) {
            Client.mainRocket.setY(startingPlatform.y);
            velocityY = 0;
            thrust = false;
        }

        if (velocityY < MAX_UP_VELOCITY)
            velocityY = MAX_UP_VELOCITY;
        if (velocityY > MAX_DOWN_VELOCITY)
            velocityY = MAX_DOWN_VELOCITY;

        Client.mainRocket.setY(Client.mainRocket.getY() + velocityY);

        // Update Camera
        cameraY = -(Client.mainRocket.getY() - getHeight() / 2);

        // Scroll the background based on rocket movement
        backgroundScrollY += velocityY * 0.1; // Adjust scrolling speed

        if (Client.mainRocket.getX() < -20)
            Client.mainRocket.setX(-20);
        if (Client.mainRocket.getX() > 425)
            Client.mainRocket.setX(425);

        // Check if the rocket has reached the spawn height threshold
        if (Client.mainRocket.getY() < -SPAWN_HEIGHT_THRESHOLD) {
            spawnFuelCanisters();
            spawnAsteroids();
        }

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

        if (mainRocket.getFuel() > mainRocket.getLevel().getFuelCap())
            mainRocket.setFuel(mainRocket.getLevel().getFuelCap());

        moveFuelCanisters();
        checkCollisions();
        repaint();

        if (mainRocket.getDurability() == 0) {
            gameOver();
        }
    }

    private void checkCollisions() {
        Rectangle rocketHitbox = new Rectangle(Client.mainRocket.getX(), Client.mainRocket.getY(), 50, 80);
        for (Asteroid asteroid : asteroids) {
            Rectangle asteroidHitbox = new Rectangle(asteroid.getX(), asteroid.getY(), asteroid.getWidth(), asteroid.getHeight());
            if (rocketHitbox.intersects(asteroidHitbox)) {
                mainRocket.setDurability(mainRocket.getDurability()-1);
                asteroid.setY(-random.nextInt(400) - 100); // Reset asteroid position
                asteroid.setX(random.nextInt(400));
                new Thread(() -> explosionPlayer.playAudio("src/client/resources/explosion.wav", false)).start();
                if (mainRocket.getDurability() <= 0) {
                    gameOver();
                }
            }
        }

        for (FuelCanister fuelCanister : fuelCanisters) {
            Rectangle feuelCanisterHitbox = new Rectangle(fuelCanister.getX(), fuelCanister.getY(), 50, 80);
            if (rocketHitbox.intersects(feuelCanisterHitbox)) {
                mainRocket.setFuel(mainRocket.getFuel() + 30);
                fuelCanister.setY(-random.nextInt(400) - 100);
                fuelCanister.setX(random.nextInt(400));
            }
        }
    }

    private void resetGame() {
        Client.mainRocket.setX(225);
        Client.mainRocket.setY(0);
        velocityY = 0;
        cameraY = 0;
        thrust = false;
        xThrustRight = false;
        xThrustLeft = false;
        backgroundScrollY = 0; // Reset background scroll

        mainRocket.setXP(mainRocket.getXp() + maxAchievedHeight);
        if (mainRocket.getXp() >= mainRocket.getLevel().getLevelUpXp()) {
            if(mainRocket.getLevel() == RocketLevel.LEVEL_1) {
                mainRocket.setXP(mainRocket.getXp() - mainRocket.getLevel().getLevelUpXp());
                mainRocket.setLevel(RocketLevel.LEVEL_2);
            }
            else if(mainRocket.getLevel() == RocketLevel.LEVEL_2) {
                mainRocket.setXP(mainRocket.getXp() - mainRocket.getLevel().getLevelUpXp());
                mainRocket.setLevel(RocketLevel.LEVEL_3);
            }
        }

        mainRocket.setFuel(Client.mainRocket.getLevel().getFuelCap());
        mainRocket.setDurability(mainRocket.getLevel().getDurability());

        maxAchievedHeight = 0;
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
        JOptionPane.showMessageDialog(this, "You died / ran out of fuel.");
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