package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client {
    public void handleServerConnection() {


    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Rocket Game");
        frame.setSize(500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.setVisible(true);


    }
}
