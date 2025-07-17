package jogo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public JogoDomino() { 
        console = new Scanner(System.in);
        try {
            conectar();
            iniciar();
            jogar();
        }  catch (IOException e) {
            System.err.println("Erro de comunicação com o servidor ao iniciar o jogo: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }  catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado ao iniciar o jogo: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (console != null) {
                console.close();
            }
        }
    }

    private void conectar() throws IOException, ClassNotFoundException { 
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
        } catch (IOException e) {
            throw new IOException("Erro de I/O ao conectar com o servidor: " + e.getMessage(), e);
        }
    }

    private void iniciar() {
        mesa = new ArrayList<>();
        fim = false;
    }

    private void jogar() { 
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
                            String vencedorIdOuEmpate = infoRecebida[1];
                            exibirResultadoFinal(vencedorIdOuEmpate);

                            break;
                        case "comprar":
                            if (infoRecebida.length == 3 &&
                                (infoRecebida[1].equals("J1") || infoRecebida[1].equals("J2"))) {

                                String jogadorQueComprou = infoRecebida[1];
                                String ladosPedraCompradaStr = infoRecebida[2]; 

                                System.out.println("O jogador " + jogadorQueComprou + " comprou uma pedra: [" + ladosPedraCompradaStr + "] e ainda é a vez dele.");

                            } else {
                                String[] ladosPedraComprada = infoRecebida[1].split("-");
                                Pedra pedraComprada = new Pedra(Integer.parseInt(ladosPedraComprada[0]), Integer.parseInt(ladosPedraComprada[1]));
                                mao.add(pedraComprada);
                                System.out.println("Você comprou uma pedra: [" + pedraComprada.getLadoA() + "|" + pedraComprada.getLadoB() + "]");

                                if (infoRecebida.length > 2 && infoRecebida[2].equals("passar")) {
                                    System.out.println("O outro jogador passou a vez.");
                                    suaVez = true; 
                                } else {
                                    // Se você comprou uma pedra e ainda tem jogadas válidas, sua vez continua.
                                    continue; 
                                }
                            }
                            break;
                        case "J1":
                        case "J2":
                            if (infoRecebida[1].equals("passar")) {
                                System.out.println("O outro jogador (" + infoRecebida[0] + ") passou a vez.");
                            } else {
                                String[] lados = infoRecebida[1].split("-");
                                if (lados.length == 2) {
                                    int ladoA = Integer.parseInt(lados[0]);
                                    int ladoB = Integer.parseInt(lados[1]);
                                    String ladoMesa = infoRecebida.length > 2 ? infoRecebida[2] : "r";

                                    if (adicionarNaMesa(ladoA, ladoB, ladoMesa)) {
                                        System.out.println("Jogador " + infoRecebida[0] + " jogou: " + 
                                                         mesa.get(ladoMesa.equals("l") ? 0 : mesa.size()-1));
                                    } else {
                                        System.out.println("Jogada inválida de " + infoRecebida[0] + 
                                                         ". Pedra [" + ladoA + "|" + ladoB + "] não encaixa.");
                                    }
                                }
                            }
                            suaVez = true;
                            break;
                        case "ok":
                            System.out.println(infoRecebida[1]);
                            break;
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
                        System.out.print("Deseja jogar ou passar? (j/p): ");
                        acao = console.nextLine();

                        if (acao.equalsIgnoreCase("p")) {
                            servidorSaida.writeObject(jogadorId + ";passar");
                            jogadaValidaCliente = true; 
                            
                        } else if (acao.equalsIgnoreCase("j")) {
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
                                    if (pedraParaRemoverLocal != null) {
                                        mao.remove(pedraParaRemoverLocal);
                                    }
                                    adicionarNaMesa(ladoA, ladoB, ladoMesa);

                                    jogadaValidaCliente = true;
                                } else if (respostaServidor.startsWith("erro")) {
                                    System.out.println("Erro na sua jogada: " + respostaServidor.split(";")[1]);
                                    
                                } else if (respostaServidor.startsWith("comprar")) {
                                    String[] ladosComprada = respostaServidor.split(";")[1].split("-");
                                    mao.add(new Pedra(Integer.parseInt(ladosComprada[0]), Integer.parseInt(ladosComprada[1])));
                                    System.out.println("Você comprou uma pedra: [" + ladosComprada[0] + "|" + ladosComprada[1] + "]");
                                    
                                }
                            } else {
                                System.out.println("\nVocê não possui esta pedra na mão. Tente novamente.\n");
                            }
                        } else {
                            System.out.println("Opção inválida. Digite 'j' ou 'p'.");
                        }
                    }
                    suaVez = false; 
                }
            }
        } catch (SocketException e) {
            System.err.println("Conexão com o servidor perdida: " + e.getMessage());
            fim = true; 
        } catch (IOException e) {
            System.err.println("Erro de comunicação durante o jogo: " + e.getMessage());
            e.printStackTrace();
            fim = true;
        }  catch (Exception e) {
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
                    servidorConexao.close(); 
                }
                conectar(); 
                iniciar(); 
                jogar(); 
            } catch (IOException e) {
                System.err.println("Erro de I/O ao tentar reiniciar o jogo: " + e.getMessage());
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
    
    private boolean adicionarNaMesa(int ladoA, int ladoB, String ladoMesa) {
        try {
            Pedra nova = new Pedra(ladoA, ladoB);

            if (mesa.isEmpty()) {
                mesa.add(nova);
                return true;
            }

            boolean jogandoNaEsquerda = ladoMesa.equalsIgnoreCase("l");
            int valorEncaixe = jogandoNaEsquerda ? mesa.get(0).getLadoA() : mesa.get(mesa.size()-1).getLadoB();

            if (nova.podeEncaixarEsquerda(valorEncaixe) || nova.podeEncaixarDireita(valorEncaixe)) {
                nova.ajustarParaEncaixe(valorEncaixe, jogandoNaEsquerda);

                if (jogandoNaEsquerda) {
                    mesa.add(0, nova);
                } else {
                    mesa.add(nova);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Erro ao adicionar pedra: " + e.getMessage());
            return false;
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nMESA:\n");

        if (mesa.isEmpty()) {
            sb.append("[---- MESA VAZIA ----]");
        } else {
            for (int i = 0; i < mesa.size(); i++) {
                sb.append(mesa.get(i).toString());
                if (i < mesa.size() - 1) {
                    sb.append("-");
                }
            }
        }
        sb.append("\n");
        return sb.toString();
    }
}

