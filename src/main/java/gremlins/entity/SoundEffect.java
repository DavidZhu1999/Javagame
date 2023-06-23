package gremlins.entity;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Enum music
 */
public enum SoundEffect {
    DEATH("src/main/resources/gremlins/Death.wav"),
    POWERUP("src/main/resources/gremlins/Powerup.wav"),
    ;

    private Clip clip;

    private SoundEffect(String soundFileName) {
        // sets the sound effect
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFileName));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * play the music
     */
    public void play() {
        // plays the sound effect
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }
}
