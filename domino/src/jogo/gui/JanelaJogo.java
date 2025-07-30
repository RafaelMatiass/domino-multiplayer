package jogo.gui;

import jogo.Config;
import jogo.Pedra;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class JanelaJogo extends JFrame {
    // Componentes da interface
    private PainelMesa painelMesa;
    private PainelMao painelMao;
    private PainelControles painelControles;
    private PainelStatus painelStatus;

    // Conexão com servidor
    private Socket servidorConexao;
    private ObjectInputStream entrada;
    private ObjectOutputStream saida;

    // Estado do jogo
    private String jogadorId;
    private boolean minhaVez;
    private List<Pedra> mao;
    private List<Pedra> mesaAtual;

    public JanelaJogo() throws IOException, ClassNotFoundException {
        super("Jogo de Dominó");
        this.mao = new ArrayList<>();
        this.mesaAtual = new ArrayList<>();
        configurarJanela();
        conectarServidor();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSaida();
            }
        });
    }

    private void conectarServidor() throws IOException, ClassNotFoundException {
        try {
            servidorConexao = new Socket(InetAddress.getByName(Config.getIp()), Config.getPorta());
            saida = new ObjectOutputStream(servidorConexao.getOutputStream());
            saida.flush();
            entrada = new ObjectInputStream(servidorConexao.getInputStream());

            String mensagemInicial = (String) entrada.readObject();
            String[] infoInicial = mensagemInicial.split(";");

            jogadorId = infoInicial[0];
            minhaVez = infoInicial[1].equals("true");

            for (int i = 2; i < infoInicial.length; i++) {
                String[] lados = infoInicial[i].split("-");
                mao.add(new Pedra(Integer.parseInt(lados[0]), Integer.parseInt(lados[1])));
            }
            System.out.println("Cliente " + jogadorId + ": Conectado. Sua vez: " + minhaVez);

        } catch (IOException e) {
            System.err.println("Cliente " + jogadorId + ": Erro ao conectar ou receber dados iniciais: " + e.getMessage());
            throw e;
        }
    }

    private void inicializarComponentes() {
        painelMesa = new PainelMesa();
        painelMao = new PainelMao(mao);
        painelControles = new PainelControles(this);
        painelStatus = new PainelStatus(jogadorId, minhaVez);
        
        JScrollPane scrollMao = new JScrollPane(painelMao,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollMao.getHorizontalScrollBar().setUnitIncrement(20);

        add(scrollMao, BorderLayout.SOUTH);
        add(painelMesa, BorderLayout.CENTER);
        add(painelControles, BorderLayout.EAST);
        add(painelStatus, BorderLayout.NORTH);

        painelControles.habilitarControles(minhaVez);
        painelStatus.atualizarVez(minhaVez);

        new Thread(this::ouvirServidor).start();
        SoundPlayer.playSound("pecas_domino.wav", false);
    }

    private void ouvirServidor() {
        try {
            while (true) {
                String mensagem = (String) entrada.readObject();
                System.out.println("Cliente " + jogadorId + ": Recebeu do servidor: " + mensagem);
                SwingUtilities.invokeLater(() -> processarMensagem(mensagem));
            }
        } catch (SocketException e) {
            System.out.println("Cliente " + jogadorId + ": Conexão com o servidor foi fechada.");
        } catch (EOFException e) {
            System.out.println("Cliente " + jogadorId + ": Conexão terminada pelo servidor.");
        } catch (Exception e) {
            System.err.println("Cliente " + jogadorId + ": Erro inesperado ao ouvir servidor: " + e.getMessage());
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
        }
    }

    private void processarMensagem(String mensagem) {
        String[] partes = mensagem.split(";");

        switch (partes[0]) {
            case "jogada": 
                String jogadorQueJogou = partes[1];
                SoundPlayer.playSound("peca_recebida.wav", false); 
                painelStatus.atualizarMensagem("Jogador " + jogadorQueJogou + " jogou uma pedra.");
                break;

            case "ok": 
                if (partes[1].equals("jogada")) {
                    String[] infoJogadaConfirmada = partes[2].split("-");
                    int ladoA_confirmada = Integer.parseInt(infoJogadaConfirmada[0]);
                    int ladoB_confirmada = Integer.parseInt(infoJogadaConfirmada[1]);

                    Pedra pedraConfirmada = new Pedra(ladoA_confirmada, ladoB_confirmada);

                    mao.remove(pedraConfirmada);
                    painelMao.setMao(mao);
//                    SoundPlayer.playSound("peca_recebida.wav"); 
                    SoundPlayer.playSound("jogada_correta.wav", false); 

                    painelStatus.atualizarMensagem("Sua jogada: [" + ladoA_confirmada + "|" + ladoB_confirmada + "] realizada com sucesso.");

                } else if (partes[1].equals("passar")) {
                    painelStatus.atualizarMensagem("Você passou a vez.");
                }
                break;

            case "comprar": 
                String[] ladosComprados = partes[1].split("-");
                Pedra novaPedraComprada = new Pedra(Integer.parseInt(ladosComprados[0]), Integer.parseInt(ladosComprados[1]));
                mao.add(novaPedraComprada);
                painelMao.setMao(mao);
                SoundPlayer.playSound("passar.wav", false); 
                painelStatus.atualizarMensagem("Você comprou uma pedra: [" + novaPedraComprada.getLadoA() + "|" + novaPedraComprada.getLadoB() + "]");
                break;

            case "oponente_comprou":
                String jogadorQueComprou = partes[1];
                painelStatus.atualizarMensagem("Jogador " + jogadorQueComprou + " comprou uma pedra.");
                break;

            case "oponente_passou": 
                String jogadorQuePassou = partes[1];
                painelStatus.atualizarMensagem("Jogador " + jogadorQuePassou + " passou a vez.");
                break;

            case "MESA": 
                mesaAtual.clear(); 
                if (partes.length > 1) { 
                    for (int i = 1; i < partes.length; i++) {
                        String[] lados = partes[i].split("-");
                        mesaAtual.add(new Pedra(Integer.parseInt(lados[0]), Integer.parseInt(lados[1])));
                    }
                }
                System.out.println("Atualizando mesa com " + mesaAtual.size() + " pedras");
                painelMesa.setPedrasMesa(mesaAtual);
                break;

            case "TURNO": 
                String jogadorDaVez = partes[1];
                if (jogadorDaVez.equals(jogadorId)) {
                    minhaVez = true;
                    painelStatus.atualizarMensagem("É a sua vez!");
                } else {
                    minhaVez = false;
                    painelStatus.atualizarMensagem("É a vez do jogador " + jogadorDaVez);
                }
                painelStatus.atualizarVez(minhaVez);
                painelControles.habilitarControles(minhaVez);
                painelMao.limparSelecao();
                break;

            case "erro": 
                SoundPlayer.playSound("erro.wav", false);
                JOptionPane.showMessageDialog(this, partes[1], "Erro", JOptionPane.ERROR_MESSAGE);
                minhaVez = true;
                painelStatus.atualizarVez(minhaVez);
                painelControles.habilitarControles(minhaVez);
                break;

            case "fim": 
                finalizarJogo(partes[1]);
                break;

            default:
                System.out.println("Cliente " + jogadorId + ": Mensagem desconhecida do servidor: " + mensagem);
                break;
        }
    }

    public void jogarPedra(Pedra pedra, boolean esquerda) {
        try {
            String mensagemEnvio = jogadorId + ";" + pedra.getLadoA() + "-" + pedra.getLadoB() + ";" + (esquerda ? "l" : "r");
            System.out.println("Cliente " + jogadorId + ": Enviando jogada: " + mensagemEnvio);
            saida.writeObject(mensagemEnvio);

            painelControles.habilitarControles(false);
            painelStatus.atualizarMensagem("Aguardando confirmação do servidor...");

        } catch (IOException e) {
            System.err.println("Cliente " + jogadorId + ": Erro ao enviar jogada: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao enviar jogada: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            minhaVez = true;
            painelStatus.atualizarVez(minhaVez);
            painelControles.habilitarControles(minhaVez);
        }
    }

    public void passarVez() {
        try {
            String mensagemEnvio = jogadorId + ";passar";
            System.out.println("Cliente " + jogadorId + ": Enviando passar: " + mensagemEnvio);
            saida.writeObject(mensagemEnvio);

            painelControles.habilitarControles(false);
            painelStatus.atualizarMensagem("Aguardando confirmação do servidor para passar a vez...");

        } catch (IOException e) {
            System.err.println("Cliente " + jogadorId + ": Erro ao passar a vez: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao passar a vez: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            minhaVez = true;
            painelStatus.atualizarVez(minhaVez);
            painelControles.habilitarControles(minhaVez);
        }
    }

    private void finalizarJogo(String resultado) {
        String mensagem;
        if (resultado.equals("empate")) {
            mensagem = "Jogo terminou em empate!";
        } else if (resultado.equals(jogadorId)) {
            mensagem = "VOCÊ VENCEU! Parabéns! :)";
            SoundPlayer.playSound("vitoria.wav", false);
        } else {
            mensagem = "Você perdeu. Jogador " + resultado + " venceu! :(";
            SoundPlayer.playSound("game_over.wav", false);
        }

        JOptionPane.showMessageDialog(this, mensagem, "Fim do Jogo", JOptionPane.INFORMATION_MESSAGE);

        int opcao = JOptionPane.showConfirmDialog(this, "Deseja jogar novamente?", "Reiniciar",
            JOptionPane.YES_NO_OPTION);

        if (opcao == JOptionPane.YES_OPTION) {
            reiniciarJogo();
        } else {
            confirmarSaida();
        }
    }

    private void reiniciarJogo() {
        try {
            if (servidorConexao != null) servidorConexao.close();

            JanelaJogo novoJogo = new JanelaJogo();
            novoJogo.setVisible(true);
            dispose();
        } catch (Exception e) {
            System.err.println("Cliente " + jogadorId + ": Erro ao reiniciar o jogo: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao reiniciar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void confirmarSaida() {
        int opcao = JOptionPane.showConfirmDialog(this,
            "Deseja realmente sair do jogo?", "Sair",
            JOptionPane.YES_NO_OPTION);

        if (opcao == JOptionPane.YES_OPTION) {
            try {
                if (servidorConexao != null && !servidorConexao.isClosed()) {
                    servidorConexao.close();
                    System.out.println("Cliente " + jogadorId + ": Conexão fechada.");
                }
            } catch (IOException e) {
                System.err.println("Cliente " + jogadorId + ": Erro ao fechar conexão: " + e.getMessage());
                e.printStackTrace();
            } finally {
                System.out.println("Obrigado por jogar!");
                System.exit(0);
            }
        }
    }

    public PainelMao getPainelMao() {
        return painelMao;
    }

    public PainelMesa getPainelMesa() {
        return painelMesa;
    }

    public PainelStatus getPainelStatus() {
        return painelStatus;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new JanelaJogo().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao iniciar o jogo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}