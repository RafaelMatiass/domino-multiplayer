package jogo;

import java.util.ArrayList;
import java.util.List;

public class ConjuntoPedras {
	
	public static List<Pedra> gerarPedras() {
		List<Pedra> pedras = new ArrayList<>();
		for (int x = 6; x >= 0; x--) {
			for (int y = 0; y <= x; y++) {
				pedras.add(new Pedra(x,y));
			}
		}
		return pedras;
	}
}
