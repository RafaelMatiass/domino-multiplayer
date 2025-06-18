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
