package jogo.gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Menu extends JFrame {

    class ImagePanel extends JPanel {
        private Image backgroundImage;

        public ImagePanel(String imagePath) {
            try {
                URL imageUrl = getClass().getResource(imagePath);
                if (imageUrl != null) {
                    backgroundImage = new ImageIcon(imageUrl).getImage();
                } else {
                    System.err.println("Erro: Imagem não encontrada no caminho: " + imagePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    class RoundedButton extends JButton {
        private final int radius = 30;

        public RoundedButton(String text) {
            super(text);
            setFont(new Font("Arial", Font.BOLD, 36));
            setForeground(Color.WHITE);
            setBackground(new Color(0, 128, 0));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorderPainted(false);
            setPreferredSize(new Dimension(250, 80));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radius, radius);

            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 4 / 2;
            g2.setColor(getForeground());
            g2.drawString(text, x, y);

            g2.dispose();
        }

    }

    public Menu() {
        super("Domino Multiplayer - Menu Inicial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        ImagePanel backgroundPanel = new ImagePanel("/images/domino.jpg");
        backgroundPanel.setLayout(new GridBagLayout());

        JLabel mensagem = new JLabel("Aperte ENTER ou clique no botão para jogar");
        mensagem.setFont(new Font("Arial", Font.BOLD, 22));
        mensagem.setForeground(Color.WHITE);

        RoundedButton startButton = new RoundedButton("Iniciar");
        startButton.addActionListener(e -> iniciarJogo());

        getRootPane().setDefaultButton(startButton); 

        GridBagConstraints gbcBotao = new GridBagConstraints();
        gbcBotao.gridx = 0;
        gbcBotao.gridy = 0;
        gbcBotao.insets = new Insets(300, 0, 20, 0); 
        backgroundPanel.add(startButton, gbcBotao);

        GridBagConstraints gbcTexto = new GridBagConstraints();
        gbcTexto.gridx = 0;
        gbcTexto.gridy = 1;
        gbcTexto.insets = new Insets(0, 0, 0, 0); 
        backgroundPanel.add(mensagem, gbcTexto);

        add(backgroundPanel, BorderLayout.CENTER);
       
        SoundPlayer.playSound("game_start.wav"); 
    }

    private void iniciarJogo() {
        SoundPlayer.stopSound();
        dispose();
        SwingUtilities.invokeLater(() -> {
            try {
                new JanelaJogo().setVisible(true);
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao iniciar o jogo: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu().setVisible(true));
    }
}
