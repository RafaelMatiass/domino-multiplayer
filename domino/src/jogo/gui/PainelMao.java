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
    private int indicePedraSelecionada = -1;
    private JScrollPane scrollPane;
    private JPanel panelPedras;

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
        setLayout(new BorderLayout());
        
        panelPedras = new JPanel();
        panelPedras.setBackground(new Color(220, 220, 220));
        panelPedras.setLayout(new WrapLayout(FlowLayout.CENTER, 5, 5));
        
        scrollPane = new JScrollPane(panelPedras);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(new Color(220, 220, 220));
        
        add(scrollPane, BorderLayout.CENTER);
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
        panelPedras.removeAll();
        botoesPedras = new JButton[mao.size()];

        for (int i = 0; i < mao.size(); i++) {
            Pedra pedra = mao.get(i);
            JPanel painelPedra = new JPanel(new BorderLayout());
            painelPedra.setBackground(new Color(220, 220, 220));
            painelPedra.setPreferredSize(new Dimension(80, 60)); // Tamanho original

            // Painel para os lados da pedra - sem espaçamento entre eles
            JPanel painelLados = new JPanel(new GridLayout(1, 2, 0, 0));
            painelLados.setBackground(Color.WHITE);
            painelLados.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));

            ImageIcon iconA = new ImageIcon(getClass().getResource("/images/" + pedra.getLadoA() + ".png"));
            ImageIcon iconB = new ImageIcon(getClass().getResource("/images/" + pedra.getLadoB() + ".png"));
            
            // Tamanho original das imagens
            iconA = new ImageIcon(iconA.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            iconB = new ImageIcon(iconB.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            
            JLabel ladoA = new JLabel(iconA, SwingConstants.CENTER);
            JLabel ladoB = new JLabel(iconB, SwingConstants.CENTER);

            painelLados.add(ladoA);
            painelLados.add(ladoB);

            // Botão de seleção
            JButton botaoSelecao = new JButton(String.valueOf(i + 1));
            botaoSelecao.setPreferredSize(new Dimension(80, 15));
            botaoSelecao.setFont(new Font("Arial", Font.PLAIN, 10));
            
            int currentIndex = i;
            botaoSelecao.addActionListener(e -> {
                if (pedra.equals(pedraSelecionada)) {
                    pedraSelecionada = null;
                    indicePedraSelecionada = -1;
                } else {
                    pedraSelecionada = pedra;
                    indicePedraSelecionada = currentIndex;
                }
                atualizarMaoGUI();
            });

            if (pedra.equals(pedraSelecionada)) {
                botaoSelecao.setBackground(new Color(0, 100, 200));
                botaoSelecao.setForeground(Color.WHITE);
            } else {
                botaoSelecao.setBackground(UIManager.getColor("Button.background"));
                botaoSelecao.setForeground(UIManager.getColor("Button.foreground"));
            }

            painelPedra.add(painelLados, BorderLayout.CENTER);
            painelPedra.add(botaoSelecao, BorderLayout.SOUTH);
            panelPedras.add(painelPedra);
            botoesPedras[i] = botaoSelecao;
        }

        panelPedras.revalidate();
        panelPedras.repaint();
        
        // Ajustar o viewport para garantir que nada seja cortado
        SwingUtilities.invokeLater(() -> {
            scrollPane.getViewport().setViewPosition(new Point(0, 0));
            scrollPane.revalidate();
            scrollPane.repaint();
            
            // Garantir que o tamanho do painel interno seja adequado
            Dimension preferredSize = panelPedras.getPreferredSize();
            Dimension viewportSize = scrollPane.getViewport().getSize();
            
            if (preferredSize.width < viewportSize.width) {
                preferredSize.width = viewportSize.width;
            }
            
            panelPedras.setPreferredSize(preferredSize);
        });
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (panelPedras != null) {
            // Ajustar o tamanho preferido quando o painel for redimensionado
            Dimension preferredSize = panelPedras.getPreferredSize();
            Dimension viewportSize = scrollPane.getViewport().getSize();
            
            if (preferredSize.width < viewportSize.width) {
                preferredSize.width = viewportSize.width;
            }
            
            panelPedras.setPreferredSize(preferredSize);
            panelPedras.revalidate();
        }
    }
}