package jogo.gui;

import javax.swing.*;
import java.awt.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class Historico extends JFrame {

    private JTextArea areaTexto;

    public Historico() {
        super("Histórico de Jogadas");

        areaTexto = new JTextArea(20, 40);
        areaTexto.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaTexto);

        add(scroll);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        carregarHistorico();
    }

    private void carregarHistorico() {
        File arquivo = new File("historico.xml");
        if (!arquivo.exists()) {
            areaTexto.setText("Nenhuma jogada registrada ainda.");
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(arquivo);
            doc.getDocumentElement().normalize();

            NodeList jogadas = doc.getElementsByTagName("jogada");
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < jogadas.getLength(); i++) {
                Element jogada = (Element) jogadas.item(i);
                String jogador = jogada.getAttribute("jogador");
                String pedra = jogada.getAttribute("pedra");
                String lado = jogada.getAttribute("lado");

                sb.append("Jogador ").append(jogador)
                  .append(" jogou a pedra ").append(pedra)
                  .append(" no lado ").append(lado)
                  .append("\n");
            }

            areaTexto.setText(sb.toString());
        } catch (Exception e) {
            areaTexto.setText("Erro ao ler histórico: " + e.getMessage());
        }
    }
}
