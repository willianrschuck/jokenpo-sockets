package com.blogspot.tragacafe.jokenpo;
import java.io.IOException;

import com.blogspot.tragacafe.jokenpo.actions.JogarAction;
import com.blogspot.tragacafe.jokenpo.actions.QuandoProntoAction;
import com.blogspot.tragacafe.jokenpo.actions.SairAction;
import com.blogspot.tragacafe.jokenpo.enums.Jogada;
import com.blogspot.tragacafe.jokenpo.model.Jogador;


public class PlayerInputThread extends Thread {

	private static final String SAIR = "sair";
	
	private Jogador jogador;
	
	private JogarAction jogarAction;
	private QuandoProntoAction quandoProntoAction;
	private SairAction sairAction;

	public PlayerInputThread(Jogador jogador, JogarAction jogarAction, QuandoProntoAction onReady, SairAction sairAction) {
		this.jogador = jogador;
		this.jogarAction = jogarAction;
		this.quandoProntoAction = onReady;
		this.sairAction = sairAction;
	}

	@Override
	public void run() {
		try {
			String mensagemRecebida = jogador.getEntrada().readLine();
			jogador.setNome(mensagemRecebida);
			quandoProntoAction.verificarInicio();
			while (!(mensagemRecebida == null || mensagemRecebida.isEmpty())) {
				mensagemRecebida = jogador.getEntrada().readLine();
				
				if (mensagemRecebida.equalsIgnoreCase(SAIR)) {
					sairAction.sair(jogador);
					return;
				}
				
				try {
					Jogada jogada = Jogada.valueOf(mensagemRecebida.trim().toUpperCase());
					jogarAction.realizarJogada(jogador, jogada);
				} catch (Exception e) {
					jogador.getSaida().println("Entrada inválida. As opções são: pedra, papel ou tesoura.");
				}
			}
		} catch (IOException e) {
			// Nada a fazer
		}
	}
	
}
