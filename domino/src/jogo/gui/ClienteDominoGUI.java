package jogo.gui;

import javax.swing.SwingUtilities;

public class ClienteDominoGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Menu().setVisible(true);
            } catch (Exception e) {
                System.err.println("Erro ao iniciar o jogo: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}