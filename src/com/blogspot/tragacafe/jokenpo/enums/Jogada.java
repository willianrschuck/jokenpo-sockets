package com.blogspot.tragacafe.jokenpo.enums;

/**
 * Define as jogadas possíveis.
 * 
 * @author Eliel Alves da Silva, elielalves.cc@gmail.com
 * @author Willian Ricardo Schuck, willianrschuck@gmail.com
 * @version 0.1
 */
public enum Jogada {
	PEDRA, PAPEL, TESOURA;
	
	public Resultado compararCom(Jogada jogada) {
		if (this == jogada) {
			return Resultado.EMPATE;
		}
		if (jogada == perdePara()) {
			return Resultado.PERDEU;
		}
		return Resultado.GANHOU;
	}
	
	private Jogada perdePara() {
		switch (this) {
		case PEDRA: return PAPEL;
		case PAPEL: return TESOURA;
		default:    return PEDRA;
		}
	}
	
}
