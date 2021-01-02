package com.blogspot.tragacafe.jokenpo;
import java.io.IOException;

import com.blogspot.tragacafe.jokenpo.actions.JogoActions;
import com.blogspot.tragacafe.jokenpo.enums.Jogada;
import com.blogspot.tragacafe.jokenpo.model.Jogador;

public class PlayerInputThread extends Thread {

	private static final String SAIR = "sair";
	
	private Jogador jogador;
	
	private JogoActions jogoActions;

	public PlayerInputThread(Jogador jogador, JogoActions onReady) {
		this.jogador = jogador;
		this.jogoActions = onReady;
	}

	@Override
	public void run() {

		try {
			
			String mensagemRecebida = jogador.getEntrada().readLine();
			jogador.setNome(mensagemRecebida);
			jogoActions.verificarInicio();
			
			while (!(mensagemRecebida == null)) {
				mensagemRecebida = jogador.getEntrada().readLine();
				
				if (mensagemRecebida == null || mensagemRecebida.equalsIgnoreCase(SAIR)) {
					break;
				}
				
				try {
					Jogada jogada = Jogada.valueOf(mensagemRecebida.trim().toUpperCase());
					jogoActions.realizarJogada(jogador, jogada);
				} catch (Exception e) {
					jogador.getSaida().println("Entrada inválida. As opções são: pedra, papel ou tesoura.");
				}
			}
			
			jogoActions.sair(jogador);
			
		} catch (IOException e) {
			// Nada a fazer
		}
		
	}
	
}
