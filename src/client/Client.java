package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {

    public static Rocket mainRocket = new Rocket(RocketLevel.LEVEL_1);

    public static ServerHandler handleServerConnection(Rocket mainRocket) throws IOException {
        Socket serverSocket = new Socket("10.104.160.41", 8888);
        System.out.println("Server Connected At IP: " + serverSocket.getRemoteSocketAddress());
        ServerHandler serverHandler = new ServerHandler(serverSocket, mainRocket);
        Thread serverThread = new Thread(serverHandler);
        serverThread.start();
        return serverHandler;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        ServerHandler serverHandler = null;

        int currentTick = 0;
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
                gameLoop(currentTick);
            }
        }
    }

    static void gameLoop(int currentTick) {

    }

}
