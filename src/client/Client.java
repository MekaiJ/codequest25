package client;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Client {

    public static Rocket mainRocket = new Rocket(RocketLevel.LEVEL_1);
    public static ServerHandler serverHandler = null;


    public static ServerHandler handleServerConnection(Rocket mainRocket) throws IOException {
        Socket serverSocket = new Socket(readFirstToken("src/client/resources/serverIP.txt"), 8888);
        ServerHandler serverHandler = new ServerHandler(serverSocket, mainRocket);
        Thread serverThread = new Thread(serverHandler);
        serverThread.start();
        System.out.println("Server Connected At IP: " + serverSocket.getRemoteSocketAddress());
        return serverHandler;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        //String[] audioFiles = {"src/client/resources/song.wav"};  // Replace with actual file paths
        //audioFiles.add("src/client/resources/themeSong.wav");


        int currentTick = 0;
        JFrame frame = new JFrame("Rocket Game");
        frame.setSize(500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.setVisible(true);

        WAVPlayer themeSong = new WAVPlayer();
        new Thread(() -> themeSong.playAudio("src/client/resources/song.wav", true)).start();

        while(true) {
            Thread.sleep(16);
            currentTick++;
            if(serverHandler == null) {
                try {
                    serverHandler = handleServerConnection(mainRocket);
                }catch(Exception e) {
                    if(currentTick % 60 == 0) {
                        System.out.println("Attempting To Connect To Server");
                    }
                }
            }else {
                if(currentTick % 2 == 0) {
                    serverHandler.writeToServer(new RocketData(mainRocket.getX(), mainRocket.getY(), mainRocket.getLevel()));
                    //System.out.println("Sending to server: " + mainRocket.getX() + " " + mainRocket.getY()+ " " + mainRocket.getLevel());
                }
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

    public static void playAudio(String filePath) {
        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.err.println("File not found: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

            // Wait until the clip finishes playing
            while (true) {

                if(!clip.isRunning()) {
                    clip.close();
                    break;
                }
                Thread.sleep(100);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
