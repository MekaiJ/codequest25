package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//TODO: Need Liam to make this work with LAN multiplayer

public class StartingScreen extends JPanel {
    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private JLabel statusLabel;
    private JFrame parentFrame;

    public StartingScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Create buttons for players
        JButton player1Button = new JButton("Player 1: Ready");
        JButton player2Button = new JButton("Player 2: Ready");

        // Status label
        statusLabel = new JLabel("Waiting for players to ready up...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Add action listeners to buttons
        player1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player1Ready = true;
                player1Button.setEnabled(false); // Disable button after clicking
                checkReady();
            }
        });

        player2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player2Ready = true;
                player2Button.setEnabled(false); // Disable button after clicking
                checkReady();
            }
        });

        // Add components to the panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(player1Button);
        buttonPanel.add(player2Button);

        add(buttonPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    // Method to check if both players are ready
    private void checkReady() {
        if (player1Ready && player2Ready) {
            statusLabel.setText("Both players are ready! Starting game...");

            // Switch to the GamePanel after a short delay
            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parentFrame.getContentPane().removeAll(); // Remove the starting screen
                    parentFrame.add(new GamePanel()); // Add the GamePanel
                    parentFrame.revalidate();
                    parentFrame.repaint();
                }
            });
            timer.setRepeats(false); // Run only once
            timer.start();
        } else {
            statusLabel.setText("Waiting for players to ready up...");
        }
    }
}