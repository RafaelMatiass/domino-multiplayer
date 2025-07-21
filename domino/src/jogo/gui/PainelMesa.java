package jogo.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import jogo.Pedra;

public class PainelMesa extends JPanel {
    private List<Pedra> pedras;

    public PainelMesa() {
        pedras = new ArrayList<>();
        configurarPainel();
    }

    private void configurarPainel() {
        setBackground(new Color(34, 139, 34)); // Verde mesa
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setPreferredSize(new Dimension(800, 300));
    }

    public void adicionarPedra(Pedra pedra, boolean esquerda) {
        if (esquerda) {
            pedras.add(0, pedra);
        } else {
            pedras.add(pedra);
        }
        repaint(); 
    }

    public void limparMesa() {
        pedras.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (pedras.isEmpty()) {
            desenharMesaVazia(g);
        } else {
            desenharPedras(g);
        }
    }

    private void desenharMesaVazia(Graphics g) {
        g.setColor(Color.WHITE);
        String texto = "Mesa vazia - Aguardando primeira jogada";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(texto);
        int textHeight = fm.getHeight();
        g.drawString(texto, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2);
    }

    private void desenharPedras(Graphics g) {

        int larguraPedraComEspacamento = 60 + 2;

        int larguraTotalDasPedras = pedras.size() * larguraPedraComEspacamento;
  
        int startX = (getWidth() - larguraTotalDasPedras) / 2;
       
        int centroY = getHeight() / 2 - 15;

        int currentX = startX;
        for (Pedra pedra : pedras) {
            desenharPedra(g, pedra, currentX, centroY);
            currentX += larguraPedraComEspacamento; 
        }
    }

    private void desenharPedra(Graphics g, Pedra pedra, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, 60, 30, 10, 10); 

        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, 60, 30, 10, 10);

        g.drawLine(x + 30, y, x + 30, y + 30);

        desenharPontos(g, pedra.getLadoA(), x + 15, y + 15); 
        desenharPontos(g, pedra.getLadoB(), x + 45, y + 15); 
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