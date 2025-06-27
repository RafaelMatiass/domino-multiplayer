/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jogo;

/**
 *
 * @author rafaelmatias
 */
public class Pedra {
	
    private int ladoA;
    private int ladoB;

     public Pedra(int ladoA, int ladoB) {
        this.ladoA = Math.min(ladoA, ladoB); 
        this.ladoB = Math.max(ladoA, ladoB); 
    }

    public int getLadoA() {
            return ladoA;
    }

    public void setLadoA(int ladoA) {
            this.ladoA = ladoA;
    }

    public int getLadoB() {
            return ladoB;
    }

    public void setLadoB(int ladoB) {
            this.ladoB = ladoB;
    }

    public boolean ehCarrocao() {
        return ladoA == ladoB;
    }
    
    public void virar() {
        int temp = ladoA;
        ladoA = ladoB;
        ladoB = temp;
    }
    
    public boolean podeEncaixarEsquerda(int valorEsquerda) {
    return this.ladoA == valorEsquerda || this.ladoB == valorEsquerda || 
          (this.ehCarrocao() && this.ladoA == valorEsquerda);
}

    public boolean podeEncaixarDireita(int valorDireita) {
        return this.ladoA == valorDireita || this.ladoB == valorDireita || 
              (this.ladoA == this.ladoB && this.ladoA == valorDireita);
    }

    public void ajustarParaEncaixe(int valor, boolean esquerda) {
        if (esquerda) {
            if (this.ladoB != valor) {
                this.virar();
            }
        } else {
            if (this.ladoA != valor) {
                this.virar();
            }
        }
    }

    @Override
    public String toString() {
        return "[" + ladoA + "|" + ladoB + "]";
    }

}
