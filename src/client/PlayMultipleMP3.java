package client;
import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlayMultipleMP3 {
    public static void playMP3(ArrayList<String> fileNames) {
        List<Clip> clips = new ArrayList<>();

        for (String fileName : fileNames) {
            try {
                URL resource = PlayMultipleMP3.class.getClassLoader().getResource(fileName);
                if (resource == null) {
                    throw new IOException("File not found: " + fileName);
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(resource);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
                clips.add(clip);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }

        // Keep the program running until all clips finish
        while (clips.stream().anyMatch(Clip::isRunning)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}