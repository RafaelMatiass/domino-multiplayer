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

        while (true) {
            System.out.println("Aguardando novos jogadores...");

            todosOuts.clear();
            MesaDeJogo mesaDeJogo = new MesaDeJogo();
            HistoricoXML.limpar();

            // Jogador 1
            Socket jogador1 = servidor.accept();
            ObjectOutputStream out1 = new ObjectOutputStream(jogador1.getOutputStream());
            out1.flush();
            ObjectInputStream in1 = new ObjectInputStream(jogador1.getInputStream());
            todosOuts.add(out1);
            enviarMaoInicial(out1, mesaDeJogo.getMaoDoJogador("J1"), "J1", true);

            // Jogador 2
            Socket jogador2 = servidor.accept();
            ObjectOutputStream out2 = new ObjectOutputStream(jogador2.getOutputStream());
            out2.flush();
            ObjectInputStream in2 = new ObjectInputStream(jogador2.getInputStream());
            todosOuts.add(out2);
            enviarMaoInicial(out2, mesaDeJogo.getMaoDoJogador("J2"), "J2", false);

            enviarEstadoMesaParaTodos(mesaDeJogo.getStringEstadoMesa());

            // Threads do jogo
            Thread t1 = new Thread(new GerenciadorDeJogadas("J1", in1, out2, mesaDeJogo, out1));
            Thread t2 = new Thread(new GerenciadorDeJogadas("J2", in2, out1, mesaDeJogo, out2));

            t1.start();
            t2.start();

            t1.join();
            t2.join();
            System.out.println("Partida finalizada. Reiniciando...\n");
        }
    }

    private static void enviarMaoInicial(ObjectOutputStream out, List<Pedra> mao, String id, boolean comeca) throws IOException {
        StringBuilder maoInicial = new StringBuilder(id + ";" + comeca);
        for (Pedra p : mao) {
            maoInicial.append(";").append(p.getLadoA()).append("-").append(p.getLadoB());
        }
        out.writeObject(maoInicial.toString());
        out.flush();
    }

    private static void enviarEstadoMesaParaTodos(String estadoMesa) throws IOException {
        for (ObjectOutputStream out : todosOuts) {
            out.writeObject(estadoMesa);
            out.flush();
        }
        System.out.println("Servidor: Enviou estado da mesa: " + estadoMesa);
    }

}
