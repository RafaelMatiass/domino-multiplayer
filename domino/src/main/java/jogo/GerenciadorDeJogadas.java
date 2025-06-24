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
import java.util.List;
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
                String mensagem = (String) inMeuJogador.readObject();
                String[] info = mensagem.split(";");
                String remetenteId = info[0];
                String tipoMensagem = info[1];

                if (!remetenteId.equals(mesaDeJogo.getJogadorAtual())) {
                    outMeuJogador.writeObject("erro;Não é a sua vez de jogar!");
                    continue; // Espera a próxima mensagem
                }

                if (tipoMensagem.equalsIgnoreCase("passar")) {
                    try {
                        Pedra pedraComprada = mesaDeJogo.comprarPedra(remetenteId);
                        outMeuJogador.writeObject("comprar;" + pedraComprada.getLadoA() + "-" + pedraComprada.getLadoB());

                        if (mesaDeJogo.podePassar(remetenteId)) {
                             outOutroJogador.writeObject(remetenteId + ";passar"); 
                             mesaDeJogo.setProximoJogador(); 
                             outMeuJogador.writeObject("ok;Você passou a vez após comprar uma pedra.");
                        } else {
                            outOutroJogador.writeObject("comprar;" + remetenteId + ";" + pedraComprada.getLadoA() + "-" + pedraComprada.getLadoB()); 
                        }

                    } catch (PoteVazioException e) {
                        if (mesaDeJogo.podePassar(remetenteId)) {
                            outOutroJogador.writeObject(remetenteId + ";passar"); 
                            mesaDeJogo.setProximoJogador(); 
                            outMeuJogador.writeObject("ok;O pote está vazio e você passou a vez.");
                        } else {
                            outMeuJogador.writeObject("erro;Você tem jogadas válidas e o pote está vazio. Não pode passar a vez.");
                        }
                    }
                } else {
                    try {
                        int ladoA = Integer.parseInt(info[1].split("-")[0]);
                        int ladoB = Integer.parseInt(info[1].split("-")[1]);
                        String ladoMesa = info[2];

                        if (mesaDeJogo.aplicarJogada(remetenteId, ladoA, ladoB, ladoMesa)) {
                            outOutroJogador.writeObject(remetenteId + ";" + ladoA + "-" + ladoB);
                            mesaDeJogo.setProximoJogador();
                            outMeuJogador.writeObject("ok;Jogada realizada com sucesso.");
                        }
                    } catch (JogadaInvalidaException e) {
                        outMeuJogador.writeObject("erro;" + e.getMessage());
                    } catch (NumberFormatException e) {
                        outMeuJogador.writeObject("erro;Formato inválido para os lados da pedra. Use números.");
                        System.err.println("Erro de formato de pedra: " + e.getMessage());
                    }
                }
                
                String resultado = mesaDeJogo.verificarFimDeJogo();
                if (resultado != null) {
                    outMeuJogador.writeObject("fim;" + resultado);
                    outOutroJogador.writeObject("fim;" + resultado);
                    System.out.println("Jogo finalizado. Vencedor: " + resultado);
                    // Para um jogo real, você pode querer fechar os sockets ou reiniciar o jogo aqui
                    // Idealmente, um "game over" seria tratado de forma mais elegante para permitir novo jogo
                    break; // Sai do loop para finalizar a thread de comunicação
                }

            } 
        } catch (IOException e) {
            System.err.println("Erro de I/O no GerenciadorDeJogadas para " + meuId + ": " + e.getMessage());
            // Aqui você lidaria com a desconexão do cliente.
            // Ex: remover o jogador do jogo, notificar o outro jogador.
        } catch (ClassNotFoundException e) {
            // Erro de desserialização, geralmente indica uma incompatibilidade de classe.
            System.err.println("Erro de desserialização no GerenciadorDeJogadas para " + meuId + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Captura qualquer outra exceção não esperada (bugs, erros de lógica não tratados)
            System.err.println("Erro inesperado no GerenciadorDeJogadas para " + meuId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Lógica de limpeza, se necessário (ex: fechar streams, sockets)
            // Cuidado ao fechar streams aqui, pois podem ser compartilhadas.
        }
    }
}


