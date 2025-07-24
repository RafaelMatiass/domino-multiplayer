package conexao;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import jogo.Config;
import jogo.GerenciadorDeJogadas;
import jogo.HistoricoXML;
import jogo.MesaDeJogo;
import jogo.Pedra;

public class ServidorDomino {

    private static List<ObjectOutputStream> todosOuts;

    public static void main(String[] args) throws Exception {

        todosOuts = new ArrayList<>();

        ServerSocket servidor = new ServerSocket(Config.getPorta(), 2, InetAddress.getByName(Config.getIp()));
        System.out.println("Servidor Dominó Inicializado (" + servidor + ").\n");

        MesaDeJogo mesaDeJogo = new MesaDeJogo();
        HistoricoXML.limpar();

        System.out.println("Esperando por conexão (Jogador 1)...");
        Socket jogador1 = servidor.accept();
        System.out.println("Conexão recebida: " + jogador1.toString() + ":" + jogador1.getPort() + "\n");

        ObjectOutputStream outJogador1 = new ObjectOutputStream(jogador1.getOutputStream());
        outJogador1.flush();
        todosOuts.add(outJogador1); // Adiciona o stream do jogador 1

        StringBuilder maoJ1 = new StringBuilder("J1;true");
        for (Pedra p : mesaDeJogo.getMaoDoJogador("J1")) {
            maoJ1.append(";").append(p.getLadoA()).append("-").append(p.getLadoB());
        }
        outJogador1.writeObject(maoJ1.toString());

        ObjectInputStream inJogador1 = new ObjectInputStream(jogador1.getInputStream());

        System.out.println("Esperando por conexão (Jogador 2)...");
        Socket jogador2 = servidor.accept();
        System.out.println("Conexão recebida: " + jogador2.toString() + ":" + jogador2.getPort() + "\n");

        ObjectOutputStream outJogador2 = new ObjectOutputStream(jogador2.getOutputStream());
        outJogador2.flush();
        todosOuts.add(outJogador2); // Adiciona o stream do jogador 2

        StringBuilder maoJ2 = new StringBuilder("J2;false");
        for (Pedra p : mesaDeJogo.getMaoDoJogador("J2")) {
            maoJ2.append(";").append(p.getLadoA()).append("-").append(p.getLadoB());
        }
        outJogador2.writeObject(maoJ2.toString());

        ObjectInputStream inJogador2 = new ObjectInputStream(jogador2.getInputStream());

        // Aqui estou enviando o estado inicial da mesa para todos os jogadores
        enviarEstadoMesaParaTodos(mesaDeJogo.getStringEstadoMesa());

        Thread threadJogador1 = new Thread(new GerenciadorDeJogadas("J1", inJogador1, outJogador2, mesaDeJogo, outJogador1));
        Thread threadJogador2 = new Thread(new GerenciadorDeJogadas("J2", inJogador2, outJogador1, mesaDeJogo, outJogador2));

        threadJogador1.start();
        threadJogador2.start();
    }

    private static void enviarEstadoMesaParaTodos(String estadoMesa) throws IOException {
        for (ObjectOutputStream out : todosOuts) {
            out.writeObject(estadoMesa);
            out.flush();
        }
        System.out.println("Servidor (MAIN): Enviou estado inicial da mesa: " + estadoMesa);
    }
}