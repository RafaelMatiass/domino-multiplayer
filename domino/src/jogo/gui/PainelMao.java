package jogo.gui;

import jogo.Pedra;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.TitledBorder;

public class PainelMao extends JPanel {
    private List<Pedra> mao;
    private JButton[] botoesPedras;
    private Pedra pedraSelecionada;
    private int indicePedraSelecionada = -1; // Para rastrear qual botão/pedra foi selecionada

    public PainelMao(List<Pedra> maoInicial) {
        this.mao = new ArrayList<>(maoInicial);
        configurarPainel();
        atualizarMaoGUI();
    }

    private void configurarPainel() {
        setBackground(new Color(220, 220, 220)); 
        TitledBorder border = BorderFactory.createTitledBorder("Sua Mão");
        border.setTitleFont(new Font("Roboto", Font.BOLD, 18));
        setBorder(border);
        setLayout(new FlowLayout(FlowLayout.CENTER, 7, 7)); 
    }

    public Pedra getPedraSelecionada() {
        return pedraSelecionada;
    }

    public int getIndicePedraSelecionada() {
        return indicePedraSelecionada;
    }

    public void adicionarPedra(Pedra pedra) {
        mao.add(pedra);
        atualizarMaoGUI();
    }

    public void removerPedra(Pedra pedra) {
        mao.remove(pedra);
        pedraSelecionada = null; 
        indicePedraSelecionada = -1;
        atualizarMaoGUI();
    }

    public void setMao(List<Pedra> novaMao) {
        this.mao = new ArrayList<>(novaMao);
        this.pedraSelecionada = null;
        this.indicePedraSelecionada = -1;
        atualizarMaoGUI();
    }

    public void limparSelecao() {
        this.pedraSelecionada = null;
        this.indicePedraSelecionada = -1;
        atualizarMaoGUI();
    }

    private void atualizarMaoGUI() {
        this.removeAll(); 
        botoesPedras = new JButton[mao.size()];

        for (int i = 0; i < mao.size(); i++) {
            Pedra pedra = mao.get(i);
            JPanel painelPedra = new JPanel(new GridLayout(2, 1));
            painelPedra.setBackground(new Color(220, 220, 220));

            JPanel painelLados = new JPanel(new GridLayout(1, 2));
            painelLados.setBackground(Color.WHITE); // Fundo da pedra em si

            JLabel ladoA = new JLabel(new ImageIcon(getClass().getResource("/images/" + pedra.getLadoA() + ".png")));
            JLabel ladoB = new JLabel(new ImageIcon(getClass().getResource("/images/" + pedra.getLadoB() + ".png")));

            painelLados.add(ladoA);
            painelLados.add(ladoB);

            JButton botaoSelecao = new JButton(String.valueOf(i + 1));
            botaoSelecao.setPreferredSize(new Dimension(0, 30));
            
            int currentIndex = i;
            botaoSelecao.addActionListener(e -> {
 
                if (pedra.equals(pedraSelecionada)) {
                    pedraSelecionada = null;
                    indicePedraSelecionada = -1;
                } else {
                    pedraSelecionada = pedra;
                    indicePedraSelecionada = currentIndex;
                }
                SoundPlayer.playSound("click.wav", false); 
                atualizarMaoGUI();
            });

            if (pedra.equals(pedraSelecionada)) {
                botaoSelecao.setBackground(Color.BLUE.brighter());
                botaoSelecao.setForeground(Color.LIGHT_GRAY); 
            } else {
                botaoSelecao.setBackground(UIManager.getColor("Button.background"));
                botaoSelecao.setForeground(UIManager.getColor("Button.foreground"));
            }

            painelPedra.add(painelLados);
            painelPedra.add(botaoSelecao);
            this.add(painelPedra);
            botoesPedras[i] = botaoSelecao;
        }

        this.revalidate();
        this.repaint();
    }
}