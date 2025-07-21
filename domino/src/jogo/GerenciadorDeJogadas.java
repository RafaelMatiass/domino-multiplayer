package jogo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import exceptions.JogadaInvalidaException;
import exceptions.PoteVazioException;

public class GerenciadorDeJogadas implements Runnable {

    private String meuId;
    private ObjectInputStream inMeuJogador;
    private ObjectOutputStream outOutroJogador;
    private ObjectOutputStream outMeuJogador;
    private MesaDeJogo mesaDeJogo;

    public GerenciadorDeJogadas(String meuId, ObjectInputStream inMeuJogador, ObjectOutputStream outOutroJogador, MesaDeJogo mesaDeJogo, ObjectOutputStream outMeuJogador) {
        this.meuId = meuId;
        this.inMeuJogador = inMeuJogador;
        this.outOutroJogador = outOutroJogador;
        this.mesaDeJogo = mesaDeJogo;
        this.outMeuJogador = outMeuJogador;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String mensagemRecebida = (String) inMeuJogador.readObject();
                System.out.println("Servidor (" + meuId + "): Recebeu: " + mensagemRecebida);
                String[] info = mensagemRecebida.split(";");
                String remetenteId = info[0];
                String tipoMensagem = info[1];

                if (!remetenteId.equals(mesaDeJogo.getJogadorAtual())) {
                    outMeuJogador.writeObject("erro;Não é a sua vez de jogar!");
                    System.out.println("Servidor (" + meuId + "): Erro: Não é a vez de " + remetenteId); 
                    continue;
                }

                if (tipoMensagem.equalsIgnoreCase("passar")) {
                    try {
                        Pedra pedraComprada = mesaDeJogo.comprarPedra(remetenteId);
                        outMeuJogador.writeObject("comprar;" + pedraComprada.getLadoA() + "-" + pedraComprada.getLadoB() + ";suaVez");
                        System.out.println("Servidor (" + meuId + "): " + remetenteId + " comprou pedra: " + pedraComprada.toString()); // Depuração

                        outOutroJogador.writeObject("oponente_comprou;" + remetenteId + ";" + pedraComprada.getLadoA() + "-" + pedraComprada.getLadoB());
                        System.out.println("Servidor (" + meuId + "): Notificou outro jogador sobre compra de " + remetenteId); 

                    } catch (PoteVazioException e) {
                        if (mesaDeJogo.podePassar(remetenteId)) {

                            outOutroJogador.writeObject("oponente_passou;" + remetenteId);
                            System.out.println("Servidor (" + meuId + "): " + remetenteId + " passou a vez (pote vazio)."); 
                            mesaDeJogo.setProximoJogador();
                            outMeuJogador.writeObject("ok;passar;poteVazio");
                        } else {
                            outMeuJogador.writeObject("erro;Você tem jogadas válidas e o pote está vazio. Não pode passar a vez.");
                            System.out.println("Servidor (" + meuId + "): Erro: " + remetenteId + " tentou passar sem poder.");
                        }
                    }
                } else { 
                    try {
                        int ladoA = Integer.parseInt(info[1].split("-")[0]);
                        int ladoB = Integer.parseInt(info[1].split("-")[1]);
                        String ladoMesa = info[2];

                        if (mesaDeJogo.aplicarJogada(remetenteId, ladoA, ladoB, ladoMesa)) {

                            Pedra pedraRealJogadaNaMesa;
                            if (mesaDeJogo.getMesa().size() == 1 || ladoMesa.equalsIgnoreCase("l")) {
                                pedraRealJogadaNaMesa = mesaDeJogo.getMesa().get(0); 
                            } else {
                                pedraRealJogadaNaMesa = mesaDeJogo.getMesa().get(mesaDeJogo.getMesa().size() - 1); 
                            }

                            String mensagemJogadaCompleta = remetenteId + ";" + pedraRealJogadaNaMesa.getLadoA() + "-" + pedraRealJogadaNaMesa.getLadoB() + ";" + ladoMesa;

                            outOutroJogador.writeObject("jogada;" + mensagemJogadaCompleta);
                            System.out.println("Servidor (" + meuId + "): Notificou outro jogador: " + mensagemJogadaCompleta); 

                            outMeuJogador.writeObject("ok;jogada;" + mensagemJogadaCompleta);
                            System.out.println("Servidor (" + meuId + "): Confirmou jogada para " + remetenteId + ": " + mensagemJogadaCompleta); 

                            mesaDeJogo.setProximoJogador(); 

                        }
                    } catch (JogadaInvalidaException e) {
                        outMeuJogador.writeObject("erro;" + e.getMessage());
                        System.out.println("Servidor (" + meuId + "): Erro de jogada para " + remetenteId + ": " + e.getMessage()); 
                    } catch (NumberFormatException e) {
                        outMeuJogador.writeObject("erro;Formato inválido para os lados da pedra. Use números.");
                        System.err.println("Servidor (" + meuId + "): Erro de formato de pedra: " + e.getMessage()); 
                    }
                }

                String resultado = mesaDeJogo.verificarFimDeJogo();
                if (resultado != null) {
                    outMeuJogador.writeObject("fim;" + resultado);
                    outMeuJogador.flush();
                    outOutroJogador.writeObject("fim;" + resultado);
                    outOutroJogador.flush();
                    System.out.println("Jogo finalizado. Vencedor: " + resultado); 
                    break;
                }

                if (!tipoMensagem.equalsIgnoreCase("passar") || (tipoMensagem.equalsIgnoreCase("passar") && mesaDeJogo.isPoteVazio())) { 
                    String proximoJogador = mesaDeJogo.getJogadorAtual();
                    outMeuJogador.writeObject("TURNO;" + proximoJogador);
                    outOutroJogador.writeObject("TURNO;" + proximoJogador);
                    System.out.println("Servidor (" + meuId + "): Enviando TURNO para: " + proximoJogador); 
                }
            }
        } catch (IOException e) {
            System.err.println("Erro de I/O no GerenciadorDeJogadas para " + meuId + ": " + e.getMessage());
            e.printStackTrace(); // Depuração completa
        } catch (ClassNotFoundException e) {
            System.err.println("Erro de desserialização no GerenciadorDeJogadas para " + meuId + ": " + e.getMessage());
            e.printStackTrace(); 
        } catch (Exception e) {
            System.err.println("Erro inesperado no GerenciadorDeJogadas para " + meuId + ": " + e.getMessage());
            e.printStackTrace(); 
        } finally {
            // Lógica de limpeza, se necessário (ex: fechar streams, sockets)
        }
    }
}