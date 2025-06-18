/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rafaelmatias
 */

package jogo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class JogoDomino {

    private Scanner console;

    private List<Pedra> mesa;
    private List<Pedra> mao;

    private boolean fim;
    private String jogadorId;
    private boolean suaVez;

    private Socket servidorConexao;
    private ObjectInputStream servidorEntrada;
    private ObjectOutputStream servidorSaida;

    public JogoDomino() throws Exception {
        console = new Scanner(System.in);

        conectar();
        iniciar();
        jogar();
    }

    private void conectar() throws Exception {
        servidorConexao = new Socket(InetAddress.getByName(Config.getIp()), Config.getPorta());

        servidorSaida = new ObjectOutputStream(servidorConexao.getOutputStream());
        servidorSaida.flush();

        servidorEntrada = new ObjectInputStream(servidorConexao.getInputStream());

        String mensagem = (String) servidorEntrada.readObject();
        String[] info = mensagem.split(";");

        jogadorId = info[0]; 
        suaVez = info[1].equals("true");

        System.out.println("Você é o Jogador: " + jogadorId);
        System.out.println(suaVez ? "Você começa jogando!" : "Aguarde sua vez...");
    }

    private void iniciar() {
        mesa = new ArrayList<>();
        mao = new ArrayList<>();
        fim = false;
    }

    private void jogar() throws Exception {
        while (!fim) {
            if (!suaVez) {
                System.out.println(this);
                System.out.println("Aguarde sua vez...");

                String mensagem = (String) servidorEntrada.readObject();
                String[] info = mensagem.split(";");

                if (info[0].equals("fim")) {
                    fim = true;
                    exibirResultadoFinal(info[1]);
                    continue;
                }

                if (info[1].equals("passar")) {
                    System.out.println("O outro jogador passou a vez.");
                } else {
                    String[] lados = info[1].split("-");
                    int ladoA = Integer.parseInt(lados[0]);
                    int ladoB = Integer.parseInt(lados[1]);
                    mesa.add(new Pedra(ladoA, ladoB));
                    System.out.println("Jogador " + info[0] + " jogou a pedra: [" + ladoA + "|" + ladoB + "]");
                }

                suaVez = true;
            }

            if (!fim) {
                System.out.println(this);
                exibirMao();

                System.out.print("Deseja jogar ou passar? (jogar/passar): ");
                String acao = console.nextLine();

                if (acao.equalsIgnoreCase("passar")) {
                    servidorSaida.writeObject(jogadorId + ";passar");
                } else {
                    System.out.print("Informe o lado A da pedra: ");
                    int ladoA = console.nextInt();
                    System.out.print("Informe o lado B da pedra: ");
                    int ladoB = console.nextInt();
                    console.nextLine(); 

                    System.out.print("Jogar na esquerda ou direita da mesa? (l/r): ");
                    String ladoMesa = console.nextLine();

                    String mensagem = jogadorId + ";" + ladoA + "-" + ladoB + ";" + ladoMesa;
                    servidorSaida.writeObject(mensagem);
                }

                suaVez = false;
                checarTermino();
            }
        }
    }

    private void checarTermino() throws Exception {
        String mensagem = (String) servidorEntrada.readObject();

        if (mensagem.startsWith("fim")) {
            String[] info = mensagem.split(";");
            fim = true;
            exibirResultadoFinal(info[1]);
        }
    }

    private void exibirResultadoFinal(String vencedorId) {
        System.out.println(this);
        if (vencedorId.equals("empate")) {
            System.out.println("Jogo terminou em EMPATE!");
        } else if (vencedorId.equals(jogadorId)) {
            System.out.println("VOCÊ VENCEU! :)");
        } else {
            System.out.println("VOCÊ PERDEU! :(");
        }

        checarReinicio();
    }

    private void checarReinicio() {
        char resposta = ' ';
        while (resposta != 'S' && resposta != 'N') {
            System.out.print("Deseja jogar novamente (S/N)? ");
            resposta = console.nextLine().toUpperCase().charAt(0);
            if (resposta != 'S' && resposta != 'N') {
                System.out.println("Resposta inválida!");
            }
        }

        if (resposta == 'S') {
            try {
                iniciar();
                jogar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String ganhador() {
        if (mao.isEmpty()) {
            return jogadorId;
        }
        return null;
    }

    private void exibirMao() {
        System.out.println("Sua mão:");
        for (Pedra p : mao) {
            System.out.print("[" + p.getLadoA() + "|" + p.getLadoB() + "] ");
        }
        System.out.println();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nMESA:\n");
        for (Pedra p : mesa) {
            sb.append("[").append(p.getLadoA()).append("|").append(p.getLadoB()).append("] ");
        }
        sb.append("\n");
        return sb.toString();
    }
}

