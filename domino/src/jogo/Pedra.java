package jogo;

import java.util.Objects;

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
            if (this.ladoB != valor && this.ladoA == valor) { 
                this.virar();
            }
        } else {
            if (this.ladoA != valor && this.ladoB == valor) { 
                this.virar();
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pedra other = (Pedra) obj;
        return (this.ladoA == other.ladoA && this.ladoB == other.ladoB) ||
               (this.ladoA == other.ladoB && this.ladoB == other.ladoA);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Math.min(ladoA, ladoB), Math.max(ladoA, ladoB));
    }

    @Override
    public String toString() {
        return "[" + ladoA + "|" + ladoB + "]";
    }

}