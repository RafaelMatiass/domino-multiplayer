package jogo;

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
        List<Pedra> todasAsPedras = ConjuntoPedras.gerarPedras();
        Collections.shuffle(todasAsPedras, new Random()); 

        for (int i = 0; i < todasAsPedras.size(); i++) {
            if (i < NUM_PEDRAS_INICIAIS) {
                adicionarPedraNaMao("J1", todasAsPedras.get(i));
            } else if (i >= NUM_PEDRAS_INICIAIS && i < (NUM_PEDRAS_INICIAIS * 2)) {
                adicionarPedraNaMao("J2", todasAsPedras.get(i));
            } else {
                pote.add(todasAsPedras.get(i)); 
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
        } else {

            int valorEncaixeEsquerda = mesa.get(0).getLadoA();
            int valorEncaixeDireita = mesa.get(mesa.size() - 1).getLadoB();

            boolean jogadaValida = false;

            if (ladoMesa.equalsIgnoreCase("l")) {
                if (pedraJogada.podeEncaixarEsquerda(valorEncaixeEsquerda) || pedraJogada.podeEncaixarDireita(valorEncaixeEsquerda)) {
                    
                    // Ajusta a orientação da pedra antes de adicioná-la à mesa
                    pedraJogada.ajustarParaEncaixe(valorEncaixeEsquerda, true);
                    mesa.add(0, pedraJogada); 
                    jogadaValida = true;
                }
            } else if (ladoMesa.equalsIgnoreCase("r")) {
                if (pedraJogada.podeEncaixarEsquerda(valorEncaixeDireita) || pedraJogada.podeEncaixarDireita(valorEncaixeDireita)) {

                    pedraJogada.ajustarParaEncaixe(valorEncaixeDireita, false);
                    mesa.add(pedraJogada); 
                    jogadaValida = true;
                }
            }

            if (!jogadaValida) {
                throw new JogadaInvalidaException("Pedra [" + pedraA + "|" + pedraB + "] não encaixa no lado " + ladoMesa + " da mesa.");
            }
        }

        maoDoJogador.remove(pedraParaRemover);
        return true;
    }
    
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
                return false; 
            }
        }
        return true; 
    }
    
    public Pedra comprarPedra(String jogadorId) throws PoteVazioException {
        if (pote.isEmpty()) {
            throw new PoteVazioException("O pote de compras está vazio. Não é possível comprar mais pedras.");
        }
        Pedra pedraComprada = pote.remove(0);
        maosDosJogadores.computeIfAbsent(jogadorId, k -> new ArrayList<>()).add(pedraComprada);
        return pedraComprada;
    }

    public String verificarFimDeJogo() {
        for (Map.Entry<String, List<Pedra>> entry : maosDosJogadores.entrySet()) {
            if (entry.getValue().isEmpty()) {
                return entry.getKey();
            }
        }

        // Verificar se o jogo "trancou" (Implementar)
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
