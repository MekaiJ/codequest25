package client;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class PlayMultipleWAV {
    public static void playAudio(String filePath) {
        new Thread(() -> {
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

                while (clip.isRunning()) {
                    Thread.sleep(100);
                }
                clip.close();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}