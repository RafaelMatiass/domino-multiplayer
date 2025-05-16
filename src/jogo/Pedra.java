package jogo;

public class Pedra {
	
	private int ladoA;
	private int ladoB;
	
	public Pedra(int ladoA, int ladoB) {
		this.ladoA = ladoA;
		this.ladoB = ladoB;
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
	
}
