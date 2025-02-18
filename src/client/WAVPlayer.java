package client;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WAVPlayer {
    private Clip clip;  // Store the clip so it can be controlled (stopped) later

    // Method to play a .wav file
    public void playAudio(String filePath, boolean loop) {
        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.err.println("File not found: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);  // Loop indefinitely
            } else {
                clip.loop(0); // Play once
            }

            clip.start(); // Start playback

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to stop the currently playing audio
    public void stopAudio() {
        if (clip != null && clip.isRunning()) {
            clip.stop();  // Stop the playback
        }
    }
}
