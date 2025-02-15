package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private int rocketX = 225;
    private int rocketY = 500;
    private int velocityY = 0;
    private boolean thrust = false;
    private Timer timer;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(30, this); // Game loop
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillRect(rocketX, rocketY, 50, 80);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thrust) velocityY -= 1;
        velocityY += 1; // Simulated gravity
        rocketY += velocityY;

        if (rocketY > getHeight() - 80) {
            rocketY = getHeight() - 80;
            velocityY = 0;
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) thrust = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) thrust = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

