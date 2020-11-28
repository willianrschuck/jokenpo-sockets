package com.blogspot.tragacafe.jokenpo.model;
import com.blogspot.tragacafe.jokenpo.enums.Jogada;
import com.blogspot.tragacafe.jokenpo.enums.Resultado;

/**
 * Estrutura de dados que representa uma entrada no histórico
 * de jogadas.
 * 
 * @author Eliel Alves da Silva, elielalves.cc@gmail.com
 * @author Willian Ricardo Schuck, willianrschuck@gmail.com 
 * @version 0.1
 */
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
