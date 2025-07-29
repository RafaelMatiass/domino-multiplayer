package jogo.gui;

import jogo.Pedra;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PainelControles extends JPanel {
    private JButton btnEsquerda, btnDireita, btnPassar;
    private JanelaJogo janela;

    public PainelControles(JanelaJogo janela) {
        this.janela = janela;
        configurarPainel();
        inicializarComponentes();
    }

    private void configurarPainel() {
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createTitledBorder("Controles"));
        setPreferredSize(new Dimension(200, 400));
        setLayout(new GridLayout(4, 1, 10, 10)); 
    }

    private void inicializarComponentes() {
        btnEsquerda = new JButton("Jogar na Esquerda");
        btnDireita = new JButton("Jogar na Direita");
        btnPassar = new JButton("Passar a Vez");
        JButton btnHistorico = new JButton("Ver Histórico");
        btnHistorico.addActionListener(e -> { 
            SoundPlayer.playSound("click.wav"); 
            new Historico().setVisible(true);
        });


        btnEsquerda.addActionListener(e -> {
            Pedra selecionada = janela.getPainelMao().getPedraSelecionada();
            if (selecionada != null) {
                janela.jogarPedra(selecionada, true);
                janela.getPainelMao().limparSelecao(); 
            } else {
                SoundPlayer.playSound("aviso.wav");
                JOptionPane.showMessageDialog(janela, "Selecione uma pedra primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDireita.addActionListener(e -> {
            Pedra selecionada = janela.getPainelMao().getPedraSelecionada();
            if (selecionada != null) {
                janela.jogarPedra(selecionada, false);
                janela.getPainelMao().limparSelecao(); 
            } else {
                JOptionPane.showMessageDialog(janela, "Selecione uma pedra primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnPassar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(janela,
                "Tem certeza que deseja passar a vez?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                janela.passarVez();
                janela.getPainelMao().limparSelecao(); 
            }
        });

        add(btnEsquerda);
        add(btnDireita);
        add(btnPassar);
        add(btnHistorico); 
    }

    public void habilitarControles(boolean habilitar) {
        btnEsquerda.setEnabled(habilitar);
        btnDireita.setEnabled(habilitar);
        btnPassar.setEnabled(habilitar);
    }
}