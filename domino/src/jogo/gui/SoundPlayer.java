package jogo.gui;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundPlayer {
    private static Clip clip;
    private static boolean looping = false;
    
    public static synchronized void playSound(final String soundFileName, final boolean loop) {
        new Thread(() -> {
            try {
                
                if (loop) {
                    stopSound();
                }
                
                URL soundUrl = SoundPlayer.class.getResource("/sounds/" + soundFileName);
                if (soundUrl == null) {
                    System.err.println("Arquivo de som não encontrado: " + "/sounds/" + soundFileName);
                    return;
                }

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundUrl);
                Clip soundClip = AudioSystem.getClip();
                soundClip.open(audioInputStream);
                
                if (loop) {
                    clip = soundClip;
                    looping = true;
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    soundClip.start(); 
                    
                    soundClip.addLineListener(event -> {
                        if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                            soundClip.close();
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Erro ao tocar o som: " + soundFileName + " - " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
   
    public static synchronized void stopSound() {
        if (clip != null) {
            looping = false;
            clip.stop();
            clip.close();
            clip = null;
        }
    }
}