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

    // NOVO MÉTODO: Centraliza o envio do estado da mesa para ambos os jogadores
    private void enviarEstadoMesaParaTodos() throws IOException {
        String estadoMesa = mesaDeJogo.getStringEstadoMesa();
        outMeuJogador.writeObject(estadoMesa);
        outMeuJogador.flush();
        outOutroJogador.writeObject(estadoMesa);
        outOutroJogador.flush();
        System.out.println("Servidor (" + meuId + "): Enviou estado da mesa: " + estadoMesa);
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
                        System.out.println("Servidor (" + meuId + "): " + remetenteId + " comprou pedra: " + pedraComprada.toString());

                        outOutroJogador.writeObject("oponente_comprou;" + remetenteId + ";" + pedraComprada.getLadoA() + "-" + pedraComprada.getLadoB());
                        System.out.println("Servidor (" + meuId + "): Notificou outro jogador sobre compra de " + remetenteId);

                        // Após a compra, enviar o estado da mesa atualizado (caso a compra mude algo relevante para a exibição global)
                        enviarEstadoMesaParaTodos();

                    } catch (PoteVazioException e) {
                        if (mesaDeJogo.podePassar(remetenteId)) {
                            outMeuJogador.writeObject("ok;passar;poteVazio");
                            System.out.println("Servidor (" + meuId + "): " + remetenteId + " passou a vez (pote vazio).");

                            outOutroJogador.writeObject("oponente_passou;" + remetenteId);
                            System.out.println("Servidor (" + meuId + "): Notificou outro jogador sobre passagem de " + remetenteId);

                            mesaDeJogo.setProximoJogador();
                            enviarEstadoMesaParaTodos(); // Envia o estado da mesa após passagem
                        } else {
                            outMeuJogador.writeObject("erro;Você tem jogadas válidas e o pote está vazio. Não pode passar a vez.");
                            System.out.println("Servidor (" + meuId + "): Erro: " + remetenteId + " tentou passar sem poder.");
                            continue;
                        }
                    }
                } else { // Tipo de mensagem é uma jogada de pedra
                    try {
                        int ladoA = Integer.parseInt(info[1].split("-")[0]);
                        int ladoB = Integer.parseInt(info[1].split("-")[1]);
                        String ladoMesa = info[2];

                        mesaDeJogo.aplicarJogada(remetenteId, ladoA, ladoB, ladoMesa);
                        System.out.println("Servidor (" + meuId + "): Jogada aplicada por " + remetenteId + ": [" + ladoA + "|" + ladoB + "] no lado " + ladoMesa);

                        // Confirmação para o próprio jogador
                        outMeuJogador.writeObject("ok;jogada;" + ladoA + "-" + ladoB + ";" + ladoMesa);
                        System.out.println("Servidor (" + meuId + "): Confirmou jogada para " + remetenteId);

                        // Notificação para o outro jogador (a pedra jogada, a mesa completa será atualizada pelo broadcast MESA)
                        outOutroJogador.writeObject("jogada;" + remetenteId + ";" + ladoA + "-" + ladoB + ";" + ladoMesa);
                        System.out.println("Servidor (" + meuId + "): Notificou outro jogador sobre a jogada de " + remetenteId);

                        // **CHAMADA CRÍTICA:** Envia o estado COMPLETO da mesa para AMBOS os jogadores
                        enviarEstadoMesaParaTodos();

                        mesaDeJogo.setProximoJogador();

                    } catch (JogadaInvalidaException e) {
                        outMeuJogador.writeObject("erro;" + e.getMessage());
                        System.out.println("Servidor (" + meuId + "): Erro de jogada para " + remetenteId + ": " + e.getMessage());
                        continue;
                    } catch (NumberFormatException e) {
                        outMeuJogador.writeObject("erro;Formato inválido para os lados da pedra. Use números.");
                        System.err.println("Servidor (" + meuId + "): Erro de formato de pedra: " + e.getMessage());
                        continue;
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

                String proximoJogador = mesaDeJogo.getJogadorAtual();
                outMeuJogador.writeObject("TURNO;" + proximoJogador);
                outOutroJogador.writeObject("TURNO;" + proximoJogador);
                System.out.println("Servidor (" + meuId + "): Enviando TURNO para: " + proximoJogador);
            }
        } catch (IOException e) {
            System.err.println("Erro de I/O no GerenciadorDeJogadas para " + meuId + ": " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Erro de desserialização no GerenciadorDeJogadas para " + meuId + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro inesperado no GerenciadorDeJogadas para " + meuId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}