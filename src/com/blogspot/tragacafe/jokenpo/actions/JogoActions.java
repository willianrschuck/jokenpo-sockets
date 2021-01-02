package com.blogspot.tragacafe.jokenpo.actions;

import com.blogspot.tragacafe.jokenpo.enums.Jogada;
import com.blogspot.tragacafe.jokenpo.model.Jogador;

/**
 * Define as ações a serem implementadas pelo jogo,
 * possibilita o encapsulamento da lógica
 * 
 * @author Willian Ricardo Schuck, willianrschuck@gmail.com
 * @author Eliel Alves da Silva, elielalves.cc@gmail.com
 * @version 0.1
 */
public interface JogoActions {

	void verificarInicio();

	void realizarJogada(Jogador jogador, Jogada jogada);

	void sair(Jogador jogador);
	
}
