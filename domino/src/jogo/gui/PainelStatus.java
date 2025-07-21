package jogo.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PainelStatus extends JPanel {
    private JLabel lblJogador, lblVez, lblMensagem;

    public PainelStatus(String jogadorId, boolean minhaVez) {
        configurarPainel();
        inicializarComponentes(jogadorId, minhaVez);
    }

    private void configurarPainel() {
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createTitledBorder("Status"));
        setPreferredSize(new Dimension(800, 80));
        setLayout(new GridLayout(1, 3)); 
    }

    private void inicializarComponentes(String jogadorId, boolean minhaVez) {
        lblJogador = new JLabel("Jogador: " + jogadorId, SwingConstants.CENTER);
        lblVez = new JLabel(minhaVez ? "Sua vez!" : "Aguardando...", SwingConstants.CENTER);
        lblMensagem = new JLabel("Bem-vindo ao jogo!", SwingConstants.CENTER);

        atualizarVez(minhaVez);

        add(lblJogador);
        add(lblVez);
        add(lblMensagem);
    }

    public void atualizarVez(boolean minhaVez) {
        lblVez.setText(minhaVez ? "Sua vez!" : "Aguardando...");
        lblVez.setForeground(minhaVez ? Color.GREEN.darker() : Color.RED.darker());
    }

    public void atualizarMensagem(String mensagem) {
        lblMensagem.setText(mensagem);
    }
}