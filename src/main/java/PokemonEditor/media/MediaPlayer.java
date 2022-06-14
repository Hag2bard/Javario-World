package PokemonEditor.media;


import javax.sound.sampled.*;
import java.io.File;

public class MediaPlayer {

    File yourFile;
    AudioInputStream stream;
    AudioFormat format;
    DataLine.Info info;
    Clip clip;
    private final String FILENAME = "mario/theme.wav";

    public void playWav() {
File royksopp = new File(FILENAME);

        try {
            stream = AudioSystem.getAudioInputStream(royksopp);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Fehler unbekannter Art");
        }
    }

}
