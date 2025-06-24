package jogo;

/**
 *
 * @author rafaelmatias
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import exceptions.JogadaInvalidaException; 
import exceptions.PoteVazioException; 

public class MesaDeJogo {

    private List<Pedra> mesa; 
    private Map<String, List<Pedra>> maosDosJogadores; 
    private List<Pedra> pote; // Pedras para compra
    private String jogadorAtual; 

    private final int NUM_PEDRAS_INICIAIS = 7; 

    public MesaDeJogo() {
        this.mesa = new ArrayList<>();
        this.maosDosJogadores = new HashMap<>();
        this.pote = new ArrayList<>();
        List<Pedra> todasAsPedras = ConjuntoPedras.gerarPedras(); // Recupera as Pedras geradas para distribuir aos jogadores
        Collections.shuffle(todasAsPedras, new Random()); 

        for (int i = 0; i < todasAsPedras.size(); i++) {
            if (i < NUM_PEDRAS_INICIAIS) {
                adicionarPedraNaMao("J1", todasAsPedras.get(i));
            } else if (i >= NUM_PEDRAS_INICIAIS && i < (NUM_PEDRAS_INICIAIS * 2)) {
                adicionarPedraNaMao("J2", todasAsPedras.get(i));
            } else {
                pote.add(todasAsPedras.get(i)); // Forma o pote para comprar
            }
        }
        
        this.jogadorAtual = "J1"; 
    }

    private void adicionarPedraNaMao(String jogadorId, Pedra pedra) {
        maosDosJogadores.computeIfAbsent(jogadorId, k -> new ArrayList<>()).add(pedra);
    }

    public List<Pedra> getMesa() {
        return mesa;
    }

    public List<Pedra> getMaoDoJogador(String jogadorId) {
        return maosDosJogadores.get(jogadorId);
    }

    public String getJogadorAtual() {
        return jogadorAtual;
    }

    public void setProximoJogador() {
        if (jogadorAtual.equals("J1")) {
            jogadorAtual = "J2";
        } else {
            jogadorAtual = "J1";
        }
    }

    public boolean aplicarJogada(String jogadorId, int pedraA, int pedraB, String ladoMesa) throws JogadaInvalidaException {
        if (!jogadorId.equals(this.jogadorAtual)) {
            throw new JogadaInvalidaException("Não é a sua vez de jogar!");
        }

        Pedra pedraJogada = new Pedra(pedraA, pedraB);
        List<Pedra> maoDoJogador = maosDosJogadores.get(jogadorId);

        boolean encontrouPedra = false;
        Pedra pedraParaRemover = null;
        for (Pedra p : maoDoJogador) {
            if ((p.getLadoA() == pedraA && p.getLadoB() == pedraB) ||
                (p.getLadoA() == pedraB && p.getLadoB() == pedraA)) {
                encontrouPedra = true;
                pedraParaRemover = p;
                break;
            }
        }

        if (!encontrouPedra) {
            throw new JogadaInvalidaException("Você não possui a pedra [" + pedraA + "|" + pedraB + "] na sua mão.");
        }
        
    
        if (mesa.isEmpty()) {
            mesa.add(pedraJogada);
            maoDoJogador.remove(pedraParaRemover);
            return true;
        } else {
            Pedra pontaEsquerda = mesa.get(0);
            Pedra pontaDireita = mesa.get(mesa.size() - 1);

            boolean jogadaValida = false;
            Pedra pedraParaMesa = pedraJogada; 

            if (ladoMesa.equalsIgnoreCase("l")) {
                if (pedraJogada.getLadoA() == pontaEsquerda.getLadoA()) {
                    pedraParaMesa = new Pedra(pedraJogada.getLadoB(), pedraJogada.getLadoA());
                    jogadaValida = true;
                } else if (pedraJogada.getLadoB() == pontaEsquerda.getLadoA()) {
                    pedraParaMesa = pedraJogada;
                    jogadaValida = true;
                }
            } else if (ladoMesa.equalsIgnoreCase("r")) {
                if (pedraJogada.getLadoA() == pontaDireita.getLadoB()) {
                    pedraParaMesa = pedraJogada;
                    jogadaValida = true;
                } else if (pedraJogada.getLadoB() == pontaDireita.getLadoB()) {
                    pedraParaMesa = new Pedra(pedraJogada.getLadoB(), pedraJogada.getLadoA());
                    jogadaValida = true;
                }
            }

            if (jogadaValida) {
                mesa.add(ladoMesa.equalsIgnoreCase("l") ? 0 : mesa.size(), pedraParaMesa); 
                maoDoJogador.remove(pedraParaRemover);
                return true;
            } else {
                throw new JogadaInvalidaException("Pedra [" + pedraA + "|" + pedraB + "] não encaixa no lado " + ladoMesa + " da mesa.");
            }
        }
    }
    
    /**
     * Verifica se um jogador pode "passar" (não tem jogadas válidas na mão).
     * Não implementa a lógica de compra de pedras do pote ainda.
     * @param jogadorId O ID do jogador.
     * @return true se o jogador não tiver jogadas válidas com as pedras da mão, false caso contrário.
     */
    public boolean podePassar(String jogadorId) {
        List<Pedra> maoDoJogador = maosDosJogadores.get(jogadorId);
        if (maoDoJogador == null || maoDoJogador.isEmpty()) {
            return false; 
        }

        if (mesa.isEmpty()) {
            return false; 
        }

        int pontaEsquerda = mesa.get(0).getLadoA();
        int pontaDireita = mesa.get(mesa.size() - 1).getLadoB();

        for (Pedra p : maoDoJogador) {
            if (p.getLadoA() == pontaEsquerda || p.getLadoB() == pontaEsquerda ||
                p.getLadoA() == pontaDireita || p.getLadoB() == pontaDireita) {
                return false; // Encontrou uma jogada válida
            }
        }
        return true; 
    }
    
    /**
     * Tenta comprar uma pedra do pote.
     * @param jogadorId O ID do jogador que está comprando.
     * @return A pedra comprada, ou null se o pote estiver vazio.
     */
    public Pedra comprarPedra(String jogadorId) throws PoteVazioException {
        if (pote.isEmpty()) {
            throw new PoteVazioException("O pote de compras está vazio. Não é possível comprar mais pedras.");
        }
        Pedra pedraComprada = pote.remove(0);
        maosDosJogadores.computeIfAbsent(jogadorId, k -> new ArrayList<>()).add(pedraComprada);
        return pedraComprada;
    }

    /**
     * Verifica as condições de término do jogo (alguém bateu ou trancou).
     * @return O ID do vencedor ("J1", "J2", "empate") ou null se o jogo não terminou.
     */
    public String verificarFimDeJogo() {
        for (Map.Entry<String, List<Pedra>> entry : maosDosJogadores.entrySet()) {
            if (entry.getValue().isEmpty()) {
                return entry.getKey();
            }
        }

        // Verificar se o jogo "trancou" (ninguém consegue jogar e pote vazio)
        // Esta lógica é mais complexa e precisaria verificar se ambos os jogadores não têm jogadas válidas
        // e se o pote está vazio. Por simplicidade inicial, vamos verificar apenas se o pote está vazio
        // e se nenhum jogador pode jogar.
        boolean jogador1PodeJogar = false;
        for (Pedra p : maosDosJogadores.get("J1")) {
            if (podeJogarPedra(p, mesa)) {
                jogador1PodeJogar = true;
                break;
            }
        }
        
        boolean jogador2PodeJogar = false;
        for (Pedra p : maosDosJogadores.get("J2")) {
            if (podeJogarPedra(p, mesa)) {
                jogador2PodeJogar = true;
                break;
            }
        }

        if (pote.isEmpty() && !jogador1PodeJogar && !jogador2PodeJogar) {
            int pontosJ1 = calcularPontos("J1");
            int pontosJ2 = calcularPontos("J2");

            if (pontosJ1 < pontosJ2) {
                return "J1";
            } else if (pontosJ2 < pontosJ1) {
                return "J2";
            } else {
                return "empate";
            }
        }

        return null; 
    }
    
    private boolean podeJogarPedra(Pedra pedra, List<Pedra> mesaAtual) {
        if (mesaAtual.isEmpty()) {
            return true;
        }
        int pontaEsquerda = mesaAtual.get(0).getLadoA();
        int pontaDireita = mesaAtual.get(mesaAtual.size() - 1).getLadoB();
        
        return (pedra.getLadoA() == pontaEsquerda || pedra.getLadoB() == pontaEsquerda ||
                pedra.getLadoA() == pontaDireita || pedra.getLadoB() == pontaDireita);
    }

    private int calcularPontos(String jogadorId) {
        int pontos = 0;
        for (Pedra p : maosDosJogadores.get(jogadorId)) {
            pontos += p.getLadoA() + p.getLadoB();
        }
        return pontos;
    }
}
