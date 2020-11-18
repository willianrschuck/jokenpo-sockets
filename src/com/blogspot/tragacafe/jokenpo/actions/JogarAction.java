package com.blogspot.tragacafe.jokenpo.actions;
import com.blogspot.tragacafe.jokenpo.enums.Jogada;
import com.blogspot.tragacafe.jokenpo.model.Jogador;

public interface JogarAction {
	
	void realizarJogada(Jogador jogador, Jogada jogada);
	
}
