package client;

import javax.swing.*;

public class Client {
    public void handleServerConnection() {


    }

    public static void main(String[] args) {
        // Create the frame (window)
        JFrame frame = new JFrame("Swing Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // Create a label and add it to the frame
        JLabel label = new JLabel("Hello, Swing!");
        frame.add(label);

        // Make the frame visible
        frame.setVisible(true);
    }
}