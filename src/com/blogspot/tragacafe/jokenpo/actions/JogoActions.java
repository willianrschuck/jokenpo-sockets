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

	/**
	 * Permite verificar se o jogo pode começar
	 */
	public void verificarInicio();

	/**
	 * Defina a ação a ser chamada quando o jogador efetuar uma jogada
	 * @param jogador
	 * @param jogada
	 */
	void realizarJogada(Jogador jogador, Jogada jogada);

	/**
	 * Define a ação a ser chamada quando o jogador desejar sair da partida
	 * @param jogador
	 */
	public void sair(Jogador jogador);
	
}
