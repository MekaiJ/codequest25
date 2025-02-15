package client;
import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static client.PlayMultipleMP3.playMP3;

public class Client {
    public static ServerHandler handleServerConnection(Rocket mainRocket) throws IOException {
        //System.out.println(readFirstToken("src/client/resources/serverIP.txt"));
        Socket serverSocket = new Socket(readFirstToken("src/client/resources/serverIP.txt"), 8888);
        System.out.println("Server Connected At IP: " + serverSocket.getRemoteSocketAddress());
        ServerHandler serverHandler = new ServerHandler(serverSocket, mainRocket);
        Thread serverThread = new Thread(serverHandler);
        serverThread.start();
        return serverHandler;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        ArrayList<String> mp3Files = new ArrayList<>();  // Replace with actual file paths
        mp3Files.add("src/client/resources/themeSong.wav");

        ServerHandler serverHandler = null;

        int currentTick = 0;
        Rocket mainRocket = new Rocket(RocketLevel.LEVEL_1);
        JFrame frame = new JFrame("Rocket Game");
        frame.setSize(500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.setVisible(true);

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
                    serverHandler.writeToServer(mainRocket);
                }

                //Game loop code goes here:
                gameLoop(currentTick, mp3Files);
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

    static void gameLoop(int currentTick, ArrayList<String> mp3Files) {
        playMP3(mp3Files);
    }
}
