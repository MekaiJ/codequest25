package client;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private static final long asteroidSPAWN_COOLDOWN = 200;
    private static final long fuelCanisterSPAWN_COOLDOWN = 500;
    private static long asteroidSpawnTime = 0;
    private static long fuelLastSpawnTime = 0;
    public static float velocityY = 0; // Vertical speed
    public static boolean thrust = false;
    public static boolean xThrustLeft = false;
    public static boolean xThrustRight = false;
    public static int cameraY = 0; // Camera Offset
    public static Rectangle startingPlatform = new Rectangle(-300, 0, 1032, 256); // Landing pad
    private static int maxAchievedHeight = 0;
    public static CopyOnWriteArrayList<Asteroid> asteroids = new CopyOnWriteArrayList<>();
    public static  CopyOnWriteArrayList<FuelCanister> fuelCanisters = new CopyOnWriteArrayList<>();
    private static final Random random = new Random();

    public static WAVPlayer thrustPlayer = new WAVPlayer();
    private static final WAVPlayer explosionPlayer = new WAVPlayer();

    public static Rocket mainRocket = new Rocket(RocketLevel.LEVEL_1);
    public static ServerHandler serverHandler = null;

    private static GamePanel panel;

    private static ServerHandler handleServerConnection(Rocket mainRocket) throws IOException {
        Socket serverSocket = new Socket(readFirstToken("src/client/resources/serverIP.txt"), 8888);
        ServerHandler serverHandler = new ServerHandler(serverSocket, mainRocket);
        Thread serverThread = new Thread(serverHandler);
        serverThread.start();
        System.out.println("Server Connected At IP: " + serverSocket.getRemoteSocketAddress());
        return serverHandler;
    }

    public static void main(String[] args) {
        int currentTick = 0;
        JFrame frame = new JFrame("Rocket Game");
        frame.setSize(500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        panel = new GamePanel();
        frame.add(panel);
        frame.setVisible(true);

        WAVPlayer themeSong = new WAVPlayer();
        new Thread(() -> themeSong.playAudio("src/client/resources/song.wav", true)).start();
        boolean running = true;
        while(running) {
            try {
                Thread.sleep(16);
                currentTick++;

                SwingUtilities.invokeLater(() -> panel.repaint());

                if (serverHandler == null) {
                    try {
                         serverHandler = handleServerConnection(mainRocket);
                    } catch (Exception e) {
                        if (currentTick % 60 == 0) {
                            System.out.println("Attempting To Connect To Server");

                        }
                    }
                } else {
                    if (currentTick % 5 == 0) {
                        serverHandler.writeToServer(new RocketData(mainRocket.getX(), mainRocket.getY(), mainRocket.getLevel()));
                    }
                    gameLoop();
                }
            }catch(Exception e) {
                System.out.println("Something Funky is going on");
                running = false;
            }
        }
    }

    public static void gameLoop() {
        if (thrust && mainRocket.getFuel() > 0) {
            // Variable thrust that mathematically maxes out at 15
            float THRUST_POWER = ((0.1f * velocityY) + 2.5f) * mainRocket.getLevel().getThrustPower();
            velocityY -= THRUST_POWER; // Move up when thrusting

            mainRocket.setFuel((int) (mainRocket.getFuel() - 1.5f));
        }

        if(mainRocket.getDurability() <= 0) {
            gameOver();
        } else if(mainRocket.getFuel() <= 0) {
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
            mainRocket.setX(mainRocket.getX() - 5);
        }
        if (xThrustRight) {
            mainRocket.setX(mainRocket.getX() + 5);
        }
        if (mainRocket.getY() < -1) {
            velocityY += 1;
        }
        if (mainRocket.getY() > startingPlatform.y) {
            mainRocket.setY(startingPlatform.y);
            velocityY = 0;
            thrust = false;
        }

        // Reduced max speed for smoother scrolling
        int MAX_DOWN_VELOCITY = 20;
        if (velocityY > MAX_DOWN_VELOCITY)
            velocityY = MAX_DOWN_VELOCITY;

        mainRocket.setY((int) (mainRocket.getY() + velocityY));

        // Update Camera
        cameraY = -(mainRocket.getY() - 700 / 2);

        if (mainRocket.getX() < 0)
            mainRocket.setX(0);
        if (mainRocket.getX() > 450)
            mainRocket.setX(450);
        // Check if the rocket has reached the spawn height threshold
        if (mainRocket.getFuel() > mainRocket.getLevel().getFuelCap())
            mainRocket.setFuel(mainRocket.getLevel().getFuelCap());
    }

    public static void spawnFuelCanisters() {
        long currentTime = System.currentTimeMillis();
        int maxFuelCanisters = 1;
        System.out.println(fuelCanisters.size());
        if(fuelCanisters.size() >= maxFuelCanisters) {
            if (mainRocket.getY() - fuelCanisters.getFirst().getY() < -400) {
                fuelCanisters.removeFirst();
            }
        }else {
            if (currentTime - fuelLastSpawnTime >= asteroidSPAWN_COOLDOWN) {
                fuelLastSpawnTime = currentTime;

                // Use rocket's position as the center point for spawning
                int x = mainRocket.getX() + random.nextInt(200) - 100; // Offset the spawn horizontally
                int y = mainRocket.getY() - 700; // Spawn above the rocket
                int size = 30 + random.nextInt(20); // Random size for the canisters

                fuelCanisters.add(new FuelCanister(x, y, size, size));
            }
        }
    }

    public static void spawnAsteroids() {
        long currentTime = System.currentTimeMillis();
        int maxAsteroids = 6;

        if(asteroids.size() >= maxAsteroids) {
            if (mainRocket.getY() - asteroids.getFirst().getY() < -400) {
                asteroids.removeFirst();
            }
        }else {
            if (currentTime - asteroidSpawnTime >= asteroidSPAWN_COOLDOWN) {
                asteroidSpawnTime = currentTime;

                // Use rocket's position as the center point for spawning
                int x = mainRocket.getX() + random.nextInt(200) - 100; // Offset the spawn horizontally
                int y = mainRocket.getY() - 400 - random.nextInt(300); // Spawn above the rocket
                int size = 30 + random.nextInt(20); // Random size for the canisters

                asteroids.add(new Asteroid(x, y, size, size));
            }
        }
    }

    private static void gameOver() {
        JOptionPane.showMessageDialog(panel, "You died / ran out of fuel.");
        resetGame();
        asteroids.clear();
        fuelCanisters.clear();
    }

    public static void checkCollisions() {
        Rectangle rocketHitbox = new Rectangle(mainRocket.getX(), mainRocket.getY(), 35, 80);
        for (Asteroid asteroid : asteroids) {
            Rectangle asteroidHitbox = new Rectangle(asteroid.getX(), asteroid.getY(), asteroid.getWidth(), asteroid.getHeight());
            if (rocketHitbox.intersects(asteroidHitbox)) {
                mainRocket.setDurability(mainRocket.getDurability()-1);
                int x = mainRocket.getX() + random.nextInt(200) - 100; // Offset the spawn horizontally
                int y = mainRocket.getY() - 400 - random.nextInt(300); // Spawn above the rocket
                asteroid.setX(x);
                asteroid.setY(y);
                new Thread(() -> explosionPlayer.playAudio("src/client/resources/explosion.wav", false)).start();
            }
        }

        for (FuelCanister fuelCanister : fuelCanisters) {
            Rectangle fuelCanisterHitbox = new Rectangle(fuelCanister.getX(), fuelCanister.getY(), 50, 80);
            if (rocketHitbox.intersects(fuelCanisterHitbox)) {
                mainRocket.setFuel(mainRocket.getFuel() + 60);
                int x = mainRocket.getX() + random.nextInt(200) - 100; // Offset the spawn horizontally
                int y = mainRocket.getY() - 400 - random.nextInt(300); // Spawn above the rocket
                fuelCanister.setX(x);
                fuelCanister.setY(y);
                mainRocket.setXP(mainRocket.getXp() + 300);
            }
        }
    }

    private static void resetGame() {
        thrustPlayer.stopAudio();
        mainRocket.setX(225);
        mainRocket.setY(0);
        velocityY = 0;
        cameraY = 0;
        thrust = false;
        xThrustRight = false;
        xThrustLeft = false;

        mainRocket.setXP(mainRocket.getXp() + (maxAchievedHeight / 10));
        if (mainRocket.getXp() >= mainRocket.getLevel().getLevelUpXp()) {
            if(mainRocket.getLevel() == RocketLevel.LEVEL_1) {
                mainRocket.setXP(mainRocket.getXp() - mainRocket.getLevel().getLevelUpXp());
                mainRocket.setLevel(RocketLevel.LEVEL_2);
            }else if(mainRocket.getLevel() == RocketLevel.LEVEL_2) {
                mainRocket.setXP(mainRocket.getXp() - mainRocket.getLevel().getLevelUpXp());
                mainRocket.setLevel(RocketLevel.LEVEL_3);
            }
        }
        maxAchievedHeight = 0;
        mainRocket.setStats();
    }

    public static void moveAsteroids() {
        for (Asteroid asteroid : asteroids) {
            asteroid.setX(asteroid.getX() + asteroid.getXSpeed());
            asteroid.setY(asteroid.getY() + asteroid.getYSpeed());

            // Bounce horizontally
            if (asteroid.getX() > 500 - asteroid.getWidth() || asteroid.getX() < 0) {
                asteroid.setXSpeed(-asteroid.getXSpeed());
            }

            // Bounce vertically
            if (asteroid.getY() > 700 - asteroid.getHeight() || asteroid.getY() < 0) {
                asteroid.setYSpeed(-asteroid.getYSpeed());
            }
        }
    }

    public static void moveFuelCanisters() {
        for (FuelCanister fuelCanister : fuelCanisters) {
            fuelCanister.setX(fuelCanister.getX() + fuelCanister.getSpeed());
            fuelCanister.setY(fuelCanister.getY() + fuelCanister.getVerticalSpeed());

            // Bounce horizontally
            if (fuelCanister.getX() > 500 - fuelCanister.getWidth() || fuelCanister.getX() < 0) {
                fuelCanister.setSpeed(-fuelCanister.getSpeed());
            }

            // Bounce vertically
            if (fuelCanister.getY() > 700 - fuelCanister.getHeight() || fuelCanister.getY() < 0) {
                fuelCanister.setVerticalSpeed(-fuelCanister.getVerticalSpeed());
            }
        }
    }

    public static String readFirstToken(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.hasNext()) {
                return scanner.next();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null; // Return null if the file is empty or not found
    }
}
