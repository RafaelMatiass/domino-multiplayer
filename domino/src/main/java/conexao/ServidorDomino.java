/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexao;

/**
 *
 * @author rafaelmatias
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import jogo.Config;
import jogo.GerenciadorDeJogadas;

public class ServidorDomino {
    public static void main(String[] args) throws Exception {
        
        ServerSocket servidor = new ServerSocket(Config.getPorta(), 2, InetAddress.getByName(Config.getIp()));
        System.out.println("Servidor Dominó Inicializado (" + servidor + ").\n");
        
        System.out.println("Esperando por conexão (Jogador 1)...");
        Socket jogador1 = servidor.accept();
        System.out.println("Conexão recebida: " + jogador1.toString() + ":" + jogador1.getPort() + "\n");
        
        ObjectOutputStream outJogador1 = new ObjectOutputStream(jogador1.getOutputStream());
        outJogador1.flush();
        outJogador1.writeObject("J1;true");
        
        ObjectInputStream inJogador1 = new ObjectInputStream(jogador1.getInputStream());
        
        System.out.println("Esperando por conexão (Jogador 2)...");
        Socket jogador2 = servidor.accept();
        System.out.println("Conexão recebida: " + jogador2.toString() + ":" + jogador2.getPort() + "\n");
        
        ObjectOutputStream outJogador2 = new ObjectOutputStream(jogador2.getOutputStream());
        outJogador2.flush();
        outJogador2.writeObject("J2;false");
        
        ObjectInputStream inJogador2 = new ObjectInputStream(jogador2.getInputStream());
        
        Thread threadJogador1 = new Thread(new GerenciadorDeJogadas(inJogador1, outJogador2));
        Thread threadJogador2 = new Thread(new GerenciadorDeJogadas(inJogador2, outJogador1));
        
        threadJogador1.start();
        threadJogador2.start();
    }
}

