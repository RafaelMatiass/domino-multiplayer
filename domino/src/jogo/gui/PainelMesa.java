package jogo.gui;

import jogo.Pedra;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PainelMesa extends JPanel {
    private List<Pedra> pedras;

    public PainelMesa() {
        pedras = new ArrayList<>();
        configurarPainel();
    }

    private void configurarPainel() {
        setBackground(new Color(34, 139, 34)); // Verde para a mesa
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setLayout(new FlowLayout(FlowLayout.CENTER, 3, 5)); 
    }

    public void setPedrasMesa(List<Pedra> novasPedras) {
        this.pedras = new ArrayList<>(novasPedras);
        atualizarMesaGUI();
    }

    public void adicionarPedra(Pedra pedra, boolean esquerda) {
        if (esquerda) {
            pedras.add(0, pedra);
        } else {
            pedras.add(pedra);
        }
    }

    public void limparMesa() {
        pedras.clear();
        atualizarMesaGUI();
    }

    private void atualizarMesaGUI() {
        this.removeAll(); 

        if (pedras.isEmpty()) {
            JLabel msgVazia = new JLabel("Mesa vazia - Aguardando primeira jogada");
            msgVazia.setForeground(Color.WHITE);
            this.add(msgVazia);
        } else {
            for (int i = 0; i < pedras.size(); i++) {
                Pedra pedra = pedras.get(i);
                JPanel painelPedra = new JPanel(new GridLayout(1, 2));
                painelPedra.setBackground(Color.WHITE); // Fundo da peça
                painelPedra.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

                JLabel ladoA_img = new JLabel(new ImageIcon(getClass().getResource("/images/" + pedra.getLadoA() + ".png")));
                JLabel ladoB_img = new JLabel(new ImageIcon(getClass().getResource("/images/" + pedra.getLadoB() + ".png")));

                // Carroções não precisam ser girados visualmente.
                if (pedra.ehCarrocao()) {
                    painelPedra.add(ladoA_img);
                    painelPedra.add(ladoB_img);
                } else if (i == 0) { 
                    if (pedras.size() > 1 && pedra.getLadoA() == pedras.get(1).getLadoA() || pedra.getLadoA() == pedras.get(1).getLadoB()) {
                        painelPedra.add(ladoB_img);
                        painelPedra.add(ladoA_img);
                    } else {
                        painelPedra.add(ladoA_img);
                        painelPedra.add(ladoB_img);
                    }
                } else if (i == pedras.size() - 1) { 
                    if (pedras.size() > 1 && pedra.getLadoB() == pedras.get(i-1).getLadoA() || pedra.getLadoB() == pedras.get(i-1).getLadoB()) {
                        painelPedra.add(ladoB_img);
                        painelPedra.add(ladoA_img);
                    } else {
                        painelPedra.add(ladoA_img);
                        painelPedra.add(ladoB_img);
                    }
                }
                else {
                    painelPedra.add(ladoA_img);
                    painelPedra.add(ladoB_img);
                }
                
                this.add(painelPedra);
            }
        }
        this.revalidate();
        this.repaint();
    }
}