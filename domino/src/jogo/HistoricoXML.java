package jogo;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class HistoricoXML {

    private static final String NOME_DO_ARQUIVO = "historico.xml";

    //Tenta salvar uma jogada no arquivo 
    public static void salvarJogadas(String jogador, Pedra pedra, String lado) {

        try {
            File arquivo = new File(NOME_DO_ARQUIVO);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            if (arquivo.exists()) {
                doc = builder.parse(arquivo);
            } else {
                doc = builder.newDocument();
                Element raiz = doc.createElement("historico");
                doc.appendChild(raiz);
            }

            //Vai criar um elemento da jogada
            Element jogada = doc.createElement("jogada");
            jogada.setAttribute("jogador", jogador);
            jogada.setAttribute("pedra", pedra.getLadoA() + "-" + pedra.getLadoB());
            jogada.setAttribute("lado", lado.equals("l") ? "esquerda" : "direita");

            doc.getDocumentElement().appendChild(jogada);

            // Salva no arquivo a jogada
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(new DOMSource(doc), new StreamResult(arquivo));

        } catch (Exception e) {
            System.err.println("Erro ao registrar jogada no arquivo XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Metodo par limpar o historico
    public static void limpar() {
        File file = new File(NOME_DO_ARQUIVO);
        if (file.exists()) {
            file.delete();
        }
    }
}
