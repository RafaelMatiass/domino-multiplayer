/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rafaelmatias
 */

package jogo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException; 
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException; 
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

    public JogoDomino() { // Removido throws Exception para tratar a conexão no construtor
        console = new Scanner(System.in);
        try {
            conectar();
            iniciar();
            jogar();
        } catch (ConnectException e) {
            System.err.println("Não foi possível conectar ao servidor. Verifique o IP/Porta e se o servidor está online. " + e.getMessage());
            System.exit(1); // Sai do programa se não conectar
        } catch (IOException e) {
            System.err.println("Erro de comunicação com o servidor ao iniciar o jogo: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println("Erro de protocolo (classe não encontrada) ao iniciar o jogo: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado ao iniciar o jogo: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (console != null) {
                console.close();
            }
        }
    }

    private void conectar() throws IOException, ClassNotFoundException { // Lançar exceções para o construtor tratar
        try {
            servidorConexao = new Socket(InetAddress.getByName(Config.getIp()), Config.getPorta());
            servidorSaida = new ObjectOutputStream(servidorConexao.getOutputStream());
            servidorSaida.flush();
            servidorEntrada = new ObjectInputStream(servidorConexao.getInputStream());

            String mensagemInicial = (String) servidorEntrada.readObject();
            String[] infoInicial = mensagemInicial.split(";");

            jogadorId = infoInicial[0];
            suaVez = infoInicial[1].equals("true");

            mao = new ArrayList<>();
            for (int i = 2; i < infoInicial.length; i++) {
                String[] lados = infoInicial[i].split("-");
                mao.add(new Pedra(Integer.parseInt(lados[0]), Integer.parseInt(lados[1])));
            }

            System.out.println("Você é o Jogador: " + jogadorId);
            System.out.println(suaVez ? "Você começa jogando!" : "Aguarde sua vez...");
        } catch (ConnectException e) {
            throw new ConnectException("Falha na conexão com o servidor. " + e.getMessage());
        } catch (IOException e) {
            throw new IOException("Erro de I/O ao conectar com o servidor: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Erro ao receber informações iniciais do servidor: " + e.getMessage(), e);
        }
    }

    private void iniciar() {
        mesa = new ArrayList<>();
        // mao = new ArrayList<>(); // A mão agora é preenchida no conectar()
        fim = false;
    }

    private void jogar() { // Tratamento de exceções dentro do loop
        try {
            while (!fim) {
                if (!suaVez) {
                    System.out.println(this);
                    exibirMao();
                    System.out.println("Aguarde sua vez...");

                    String mensagemRecebida = (String) servidorEntrada.readObject();
                    String[] infoRecebida = mensagemRecebida.split(";");

                    switch (infoRecebida[0]) {
                        case "fim":
                            fim = true;
                            exibirResultadoFinal(infoRecebida[1]);
                            break;
                        case "comprar":
                            String[] ladosPedraComprada = infoRecebida[1].split("-");
                            Pedra pedraComprada = new Pedra(Integer.parseInt(ladosPedraComprada[0]), Integer.parseInt(ladosPedraComprada[1]));
                            mao.add(pedraComprada);
                            System.out.println("Você comprou uma pedra: [" + pedraComprada.getLadoA() + "|" + pedraComprada.getLadoB() + "]");
                            if (infoRecebida.length > 2 && infoRecebida[2].equals("passar")) {
                                System.out.println("O outro jogador passou a vez.");
                                suaVez = true;
                            } else {
                                // Se comprou e não foi uma passagem, ainda não é a vez do jogador
                                continue;
                            }
                            break;
                        case "J1":
                        case "J2": // Recebe jogada do oponente
                            if (infoRecebida[1].equals("passar")) {
                                System.out.println("O outro jogador (" + infoRecebida[0] + ") passou a vez.");
                            } else {
                                String[] lados = infoRecebida[1].split("-");
                                int ladoA = Integer.parseInt(lados[0]);
                                int ladoB = Integer.parseInt(lados[1]);
                                mesa.add(new Pedra(ladoA, ladoB));
                                System.out.println("Jogador " + infoRecebida[0] + " jogou a pedra: [" + ladoA + "|" + ladoB + "]");
                            }
                            suaVez = true; // É a vez do meu jogador
                            break;
                        case "ok":
                            System.out.println(infoRecebida[1]); // Mensagem de sucesso da sua jogada
                            // SuaVez já foi setada para false após o envio
                            break;
                        case "erro":
                            System.out.println("Mensagem do servidor: " + infoRecebida[1]);
                            // A jogada foi inválida, então a vez ainda é do jogador atual para tentar novamente
                            suaVez = true; // Permanece na sua vez
                            continue; // Volta para o início do loop para pedir nova jogada
                        default:
                            System.out.println("Mensagem desconhecida do servidor: " + mensagemRecebida);
                            break;
                    }
                }

                if (!fim && suaVez) {
                    System.out.println(this);
                    exibirMao();

                    String acao = "";
                    boolean jogadaValidaCliente = false;

                    while(!jogadaValidaCliente) {
                        System.out.print("Deseja jogar ou passar? (jogar/passar): ");
                        acao = console.nextLine();

                        if (acao.equalsIgnoreCase("passar")) {
                            servidorSaida.writeObject(jogadorId + ";passar");
                            jogadaValidaCliente = true; // Será validado pelo servidor, mas localmente enviamos
                        } else if (acao.equalsIgnoreCase("jogar")) {
                            System.out.print("Informe o lado A da pedra: ");
                            int ladoA = console.nextInt();
                            System.out.print("Informe o lado B da pedra: ");
                            int ladoB = console.nextInt();
                            console.nextLine(); 

                            System.out.print("Jogar na esquerda ou direita da mesa? (l/r): ");
                            String ladoMesa = console.nextLine();

                            boolean pedraNaMao = false;
                            Pedra pedraParaRemoverLocal = null;
                            for (Pedra p : mao) {
                                if ((p.getLadoA() == ladoA && p.getLadoB() == ladoB) ||
                                    (p.getLadoA() == ladoB && p.getLadoB() == ladoA)) {
                                    pedraNaMao = true;
                                    pedraParaRemoverLocal = p;
                                    break;
                                }
                            }

                            if (pedraNaMao) {
                                String mensagemEnvio = jogadorId + ";" + ladoA + "-" + ladoB + ";" + ladoMesa;
                                servidorSaida.writeObject(mensagemEnvio);
                                
                                String respostaServidor = (String) servidorEntrada.readObject();
                                if (respostaServidor.startsWith("ok")) {
                                    System.out.println("Jogada validada pelo servidor.");
                                    if (pedraParaRemoverLocal != null) {
                                        mao.remove(pedraParaRemoverLocal);
                                    }
                                    jogadaValidaCliente = true;
                                } else if (respostaServidor.startsWith("erro")) {
                                    System.out.println("Erro na sua jogada: " + respostaServidor.split(";")[1]);
                                    // Permanece no loop para nova tentativa, sua vez continua
                                } else if (respostaServidor.startsWith("comprar")) {
                                    String[] ladosComprada = respostaServidor.split(";")[1].split("-");
                                    mao.add(new Pedra(Integer.parseInt(ladosComprada[0]), Integer.parseInt(ladosComprada[1])));
                                    System.out.println("Você comprou uma pedra: [" + ladosComprada[0] + "|" + ladosComprada[1] + "]");
                                    // Continua no loop, pois o jogador pode tentar jogar com a nova pedra
                                }
                            } else {
                                System.out.println("Você não possui esta pedra na mão. Tente novamente.");
                            }
                        } else {
                            System.out.println("Opção inválida. Digite 'jogar' ou 'passar'.");
                        }
                    }
                    suaVez = false; // Se a jogada for validada ou o passar aceito, a vez termina
                }
            }
        } catch (SocketException e) {
            System.err.println("Conexão com o servidor perdida: " + e.getMessage());
            // Lidar com a desconexão: informar o usuário, desabilitar o jogo, talvez tentar reconectar
            fim = true; // Encerra o loop do jogo
        } catch (IOException e) {
            System.err.println("Erro de comunicação durante o jogo: " + e.getMessage());
            e.printStackTrace();
            fim = true;
        } catch (ClassNotFoundException e) {
            System.err.println("Erro de protocolo (classe não encontrada) durante o jogo: " + e.getMessage());
            e.printStackTrace();
            fim = true;
        } catch (NumberFormatException e) {
            System.err.println("Erro na entrada de dados (número inválido): " + e.getMessage());
            System.out.println("Por favor, digite números válidos para os lados da pedra.");
            // Não seta fim = true, permite que o jogador tente novamente
        } catch (Exception e) {
            System.err.println("Um erro inesperado ocorreu durante o jogo: " + e.getMessage());
            e.printStackTrace();
            fim = true;
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
                if (servidorConexao != null && !servidorConexao.isClosed()) {
                    servidorConexao.close(); // Fecha a conexão anterior para o servidor lidar com a reconexão
                }
                conectar(); // Reconecta ao servidor para um novo jogo
                iniciar(); // Reinicia o estado local do jogo
                jogar(); // Inicia o novo jogo
            } catch (ConnectException e) {
                System.err.println("Não foi possível reconectar ao servidor para um novo jogo. " + e.getMessage());
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Erro de I/O ao tentar reiniciar o jogo: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            } catch (ClassNotFoundException e) {
                System.err.println("Erro de protocolo ao reiniciar o jogo: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                System.err.println("Ocorreu um erro inesperado ao reiniciar o jogo: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            try {
                if (servidorConexao != null && !servidorConexao.isClosed()) {
                    servidorConexao.close();
                }
            } catch (IOException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            } finally {
                if (console != null) {
                    console.close();
                }
                System.out.println("Obrigado por jogar!");
                System.exit(0);
            }
        }
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

