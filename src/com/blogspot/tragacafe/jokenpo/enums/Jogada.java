package com.blogspot.tragacafe.jokenpo.enums;

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
