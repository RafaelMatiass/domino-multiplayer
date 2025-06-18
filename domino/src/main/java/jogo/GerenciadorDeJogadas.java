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

public class GerenciadorDeJogadas implements Runnable {
    
    private ObjectInputStream saidaJogador1;
    private ObjectOutputStream entradaJogador2;

    public GerenciadorDeJogadas(ObjectInputStream saidaJogador1, ObjectOutputStream entradaJogador2) {
        this.saidaJogador1 = saidaJogador1;
        this.entradaJogador2 = entradaJogador2;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                String mensagem = (String) saidaJogador1.readObject();
                entradaJogador2.writeObject(mensagem);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}


