package jogo.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Font; 
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

public class PainelStatus extends JPanel {
    private JLabel lblJogador, lblVez, lblMensagem;

    public PainelStatus(String jogadorId, boolean minhaVez) {
        configurarPainel();
        inicializarComponentes(jogadorId, minhaVez);
    }

    private void configurarPainel() {
        setBackground(new Color(230, 230, 230)); 
        
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Status do Jogo");
        titledBorder.setTitleFont(new Font("Roboto", Font.BOLD, 18)); 
        setBorder(titledBorder);
        
        setPreferredSize(new Dimension(800, 80));
        setLayout(new GridLayout(1, 3, 10, 0)); 
    }

    private void inicializarComponentes(String jogadorId, boolean minhaVez) {
 
        lblJogador = new JLabel("Jogador: " + jogadorId, SwingConstants.CENTER);
        lblJogador.setFont(new Font("Arial", Font.BOLD, 16)); 
        lblJogador.setForeground(new Color(50, 50, 50)); 
        lblJogador.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); 

        lblVez = new JLabel(minhaVez ? "Sua vez!" : "Aguardando...", SwingConstants.CENTER);
        lblVez.setFont(new Font("Arial", Font.BOLD, 18)); 
        lblVez.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); 

        // Estiliza o JLabel da Mensagem
        lblMensagem = new JLabel("Bem-vindo ao jogo!", SwingConstants.CENTER);
        lblJogador.setFont(new Font("Arial", Font.BOLD, 16)); 
        lblJogador.setForeground(new Color(50, 50, 50)); 
        lblJogador.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); 

        atualizarVez(minhaVez); 

        add(lblJogador);
        add(lblVez);
        add(lblMensagem);
    }

    public void atualizarVez(boolean minhaVez) {
        lblVez.setText(minhaVez ? "Sua vez!" : "Aguardando...");
        lblVez.setForeground(minhaVez ? new Color(0, 170, 0) : new Color(200, 50, 50)); 
    }

    public void atualizarMensagem(String mensagem) {
        lblMensagem.setText(mensagem);
    }
}