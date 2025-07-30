package jogo.gui;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundPlayer {
    private static Clip clip; 
    
    public static synchronized void playSound(final String soundFileName) {
        new Thread(() -> {
            try {
                stopSound();
                
                URL soundUrl = SoundPlayer.class.getResource("/sounds/" + soundFileName);
                if (soundUrl == null) {
                    System.err.println("Arquivo de som não encontrado: " + "/sounds/" + soundFileName);
                    return;
                }

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundUrl);
                clip = AudioSystem.getClip(); 
                clip.open(audioInputStream);
                clip.start();
                
                clip.addLineListener(event -> {
                    if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                        clip.close();
                        clip = null;
                    }
                });
            } catch (Exception e) {
                System.err.println("Erro ao tocar o som: " + soundFileName + " - " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
    
    public static synchronized void stopSound() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }
}