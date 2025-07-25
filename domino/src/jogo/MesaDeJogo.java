package jogo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects; 
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

    public boolean isPoteVazio() {
        return pote.isEmpty();
    }

    public boolean aplicarJogada(String jogadorId, int pedraA, int pedraB, String ladoMesa) throws JogadaInvalidaException {
        if (!jogadorId.equals(this.jogadorAtual)) {
            throw new JogadaInvalidaException("Não é a sua vez de jogar!");
        }

        List<Pedra> maoDoJogador = maosDosJogadores.get(jogadorId);

        Pedra pedraParaRemover = null;
        for (Pedra p : maoDoJogador) {
            if ((p.getLadoA() == pedraA && p.getLadoB() == pedraB) ||
                (p.getLadoA() == pedraB && p.getLadoB() == pedraA)) {
                pedraParaRemover = p;
                break;
            }
        }

        if (pedraParaRemover == null) {
            throw new JogadaInvalidaException("Você não possui a pedra [" + pedraA + "|" + pedraB + "] na sua mão.");
        }

        Pedra pedraJogada = new Pedra(pedraA, pedraB);

        if (mesa.isEmpty()) {
            mesa.add(pedraJogada);
        } else {
            int pontaEsquerda = mesa.get(0).getLadoA();
            int pontaDireita = mesa.get(mesa.size() - 1).getLadoB();

            boolean jogadaValida = false;

            if (ladoMesa.equalsIgnoreCase("l")) {
                if (pedraJogada.podeEncaixarEsquerda(pontaEsquerda) || pedraJogada.podeEncaixarDireita(pontaEsquerda)) {
                    pedraJogada.ajustarParaEncaixe(pontaEsquerda, true);
                    mesa.add(0, pedraJogada);
                    jogadaValida = true;
                }
            } else if (ladoMesa.equalsIgnoreCase("r")) {
                if (pedraJogada.podeEncaixarEsquerda(pontaDireita) || pedraJogada.podeEncaixarDireita(pontaDireita)) {
                    pedraJogada.ajustarParaEncaixe(pontaDireita, false);
                    mesa.add(pedraJogada);
                    jogadaValida = true;
                }
            }

            if (!jogadaValida) {
                throw new JogadaInvalidaException("Pedra [" + pedraA + "|" + pedraB + "] não encaixa no lado " + ladoMesa + " da mesa.");
            }
        }

        removerPedraDaMao(jogadorId, pedraParaRemover);
        return true;
    }

    public boolean podePassar(String jogadorId) {
        List<Pedra> maoDoJogador = maosDosJogadores.get(jogadorId);
        if (maoDoJogador == null || maoDoJogador.isEmpty()) {
            return false;
        }

        if (mesa.isEmpty()) {
            // Se a mesa está vazia, o jogador só pode jogar uma pedra, não passar
            return false;
        }

        int pontaEsquerda = mesa.get(0).getLadoA();
        int pontaDireita = mesa.get(mesa.size() - 1).getLadoB();

        for (Pedra p : maoDoJogador) {
            if (p.getLadoA() == pontaEsquerda || p.getLadoB() == pontaEsquerda ||
                p.getLadoA() == pontaDireita || p.getLadoB() == pontaDireita) {
                return false; // Encontrou uma pedra que pode ser jogada, então não pode passar
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
                return entry.getKey(); // aqui o jogador venceu pois nào há pedras
            }
        }

        // Verificar se o jogo "trancou" (ninguém pode jogar e o pote está vazio)
        boolean jogador1PodeJogar = false;
        if (maosDosJogadores.containsKey("J1")) {
            for (Pedra p : maosDosJogadores.get("J1")) {
                if (podeJogarPedra(p, mesa)) {
                    jogador1PodeJogar = true;
                    break;
                }
            }
        }

        boolean jogador2PodeJogar = false;
        if (maosDosJogadores.containsKey("J2")) {
            for (Pedra p : maosDosJogadores.get("J2")) {
                if (podeJogarPedra(p, mesa)) {
                    jogador2PodeJogar = true;
                    break;
                }
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
    
    private void adicionarPedraNaMao(String jogadorId, Pedra pedra) {
        maosDosJogadores.computeIfAbsent(jogadorId, k -> new ArrayList<>()).add(pedra);
    }

    private void removerPedraDaMao(String jogadorId, Pedra pedraParaRemover) throws JogadaInvalidaException {
        List<Pedra> maoDoJogador = maosDosJogadores.get(jogadorId);
        if (maoDoJogador == null || !maoDoJogador.remove(pedraParaRemover)) {
            throw new JogadaInvalidaException("Erro interno: pedra não encontrada na mão para remoção.");
        }
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
        List<Pedra> mao = maosDosJogadores.get(jogadorId);
        if (mao != null) {
            for (Pedra p : mao) {
                pontos += p.getLadoA() + p.getLadoB();
            }
        }
        return pontos;
    }

    public String getStringEstadoMesa() {
        StringBuilder sb = new StringBuilder("MESA");
        for (Pedra p : mesa) {
            sb.append(";").append(p.getLadoA()).append("-").append(p.getLadoB());
        }
        return sb.toString();
    }
    
    private void processarJogadaNaMesa(Pedra pedraJogada, String ladoMesa) throws JogadaInvalidaException {
    boolean jogandoNaEsquerda = ladoMesa.equalsIgnoreCase("l");
    int valorEncaixe = jogandoNaEsquerda ? 
        mesa.get(0).getLadoA() : mesa.get(mesa.size()-1).getLadoB();

    // Verifica se a pedra pode encaixar
    if (!pedraJogada.podeEncaixarEsquerda(valorEncaixe) && !pedraJogada.podeEncaixarDireita(valorEncaixe)) {
        throw new JogadaInvalidaException("Pedra " + pedraJogada + " não encaixa no lado " + ladoMesa + " da mesa.");
    }

    // Ajusta a orientação da pedra
    pedraJogada.ajustarParaEncaixe(valorEncaixe, jogandoNaEsquerda);
    
    // Adiciona na mesa
    if (jogandoNaEsquerda) {
        mesa.add(0, pedraJogada);
    } else {
        mesa.add(pedraJogada);
    }
}
}