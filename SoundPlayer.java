package prisonersdilemma;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundPlayer {
    static {
        // Initialisation de JavaFX pour éviter les erreurs dans Swing
        new JFXPanel();
    }

    public static void play(String path) {
        try {
            Media sound = new Media(new File(path).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture audio : " + path);
            e.printStackTrace();
        }
    }
}
