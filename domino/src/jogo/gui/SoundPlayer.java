package jogo.gui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.net.URL; 

public class SoundPlayer {

    public static synchronized void playSound(final String soundFileName) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL soundUrl = SoundPlayer.class.getResource("/sounds/" + soundFileName);
                    if (soundUrl == null) {
                        System.err.println("Arquivo de som não encontrado: " + "/sounds/" + soundFileName);
                        return;
                    }

                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundUrl);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                    clip.addLineListener(event -> {
                        if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Erro ao tocar o som: " + soundFileName + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
