package com.blogspot.tragacafe.jokenpo.model;
import com.blogspot.tragacafe.jokenpo.enums.Jogada;
import com.blogspot.tragacafe.jokenpo.enums.Resultado;

public class HistoryEntry {
	
	private Jogada jogada;
	private Resultado resultado;
	
	public HistoryEntry(Jogada jogada, Resultado resultado) {
		super();
		this.jogada = jogada;
		this.resultado = resultado;
	}
	
	public Jogada getJogada() {
		return jogada;
	}
	
	public Resultado getResultado() {
		return resultado;
	}
	
}
