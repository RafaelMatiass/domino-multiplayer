package jogo.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import jogo.Pedra;

public class PainelMao extends JPanel {
    private List<Pedra> mao;
    private Pedra selecionada;

    public PainelMao(List<Pedra> maoInicial) {
        mao = new ArrayList<>(maoInicial);
        configurarPainel();
        configurarListeners();
    }

    private void configurarPainel() {
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createTitledBorder("Sua Mão"));
        setPreferredSize(new Dimension(800, 150));
    }

    private void configurarListeners() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

           
                for (int i = 0; i < mao.size(); i++) {                
                    Rectangle bounds = new Rectangle(10 + i * 70, 30, 60, 30);
                    if (bounds.contains(x, y)) {
                        if (mao.get(i).equals(selecionada)) {
                            selecionada = null;
                        } else {
                            selecionada = mao.get(i);
                        }
                        repaint(); 
                        break;
                    }
                }
            }
        });
    }

    public Pedra getPedraSelecionada() {
        return selecionada;
    }

    public void adicionarPedra(Pedra pedra) {
        mao.add(pedra);
        repaint();
    }

    public void removerPedra(Pedra pedra) {
        mao.remove(pedra);
        if (selecionada != null && selecionada.equals(pedra)) {
            selecionada = null; 
        }
        repaint();
    }

    public void setMao(List<Pedra> novaMao) {
        this.mao = new ArrayList<>(novaMao);
        this.selecionada = null; 
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < mao.size(); i++) {
            Pedra pedra = mao.get(i);
            boolean isSelecionada = (selecionada != null && selecionada.equals(pedra));

            desenharPedra(g, pedra, 10 + i * 70, 30, isSelecionada);
        }
    }

    private void desenharPedra(Graphics g, Pedra pedra, int x, int y, boolean isSelecionada) {
        // Fundo da pedra: cor diferente se selecionada
        g.setColor(isSelecionada ? new Color(200, 230, 255) : Color.WHITE);
        g.fillRoundRect(x, y, 60, 30, 10, 10);

        // Borda da pedra: cor diferente se selecionada
        g.setColor(isSelecionada ? Color.BLUE : Color.BLACK);
        g.drawRoundRect(x, y, 60, 30, 10, 10);

        // Divisória no meio da pedra
        g.drawLine(x + 30, y, x + 30, y + 30);

        // Desenha os pontos/números de cada lado
        desenharPontos(g, pedra.getLadoA(), x + 15, y + 15); // Lado A
        desenharPontos(g, pedra.getLadoB(), x + 45, y + 15); // Lado B
    }

    private void desenharPontos(Graphics g, int valor, int centerX, int centerY) {
        g.setColor(Color.BLACK);
        String sValor = String.valueOf(valor);
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(sValor);
        int stringHeight = fm.getHeight();

        int textX = centerX - (stringWidth / 2);
        int textY = centerY + (stringHeight / 2) - fm.getDescent();
        g.drawString(sValor, textX, textY);
    }
}