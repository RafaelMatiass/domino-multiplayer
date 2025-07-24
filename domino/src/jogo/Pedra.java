package jogo;

import java.util.Objects;

public class Pedra {
    public static final int VALOR_MINIMO = 0;
    public static final int VALOR_MAXIMO = 6;
    
    private int ladoA;
    private int ladoB;

    public Pedra(int ladoA, int ladoB) {
        if (ladoA < VALOR_MINIMO || ladoA > VALOR_MAXIMO || 
            ladoB < VALOR_MINIMO || ladoB > VALOR_MAXIMO) {
            throw new IllegalArgumentException("Valores da pedra devem estar entre " + 
                VALOR_MINIMO + " e " + VALOR_MAXIMO);
        }
        
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
        return this.ladoA == valorEsquerda || this.ladoB == valorEsquerda;
    }

   public boolean podeEncaixarDireita(int valorDireita) {
    return this.ladoA == valorDireita || this.ladoB == valorDireita;
}

    public void ajustarParaEncaixe(int valor, boolean esquerda) {
        if (esquerda) {
            // Para encaixe na esquerda, queremos que o ladoB coincida
            if (this.ladoA == valor && this.ladoB != valor) {
                this.virar();
            }
        } else {
            // Para encaixe na direita, queremos que o ladoA coincida
            if (this.ladoB == valor && this.ladoA != valor) {
                this.virar();
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pedra)) return false;
        Pedra other = (Pedra) obj;
        return (ladoA == other.ladoA && ladoB == other.ladoB) ||
               (ladoA == other.ladoB && ladoB == other.ladoA);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Math.min(ladoA, ladoB), Math.max(ladoA, ladoB));
    }

    @Override
    public String toString() {
        return String.format("[%d|%d]", ladoA, ladoB);
    }
    
    public Pedra clone() {
        return new Pedra(this.ladoA, this.ladoB);
    }
}