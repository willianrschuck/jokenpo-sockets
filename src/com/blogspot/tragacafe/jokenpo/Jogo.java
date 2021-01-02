package com.blogspot.tragacafe.jokenpo;
import com.blogspot.tragacafe.jokenpo.actions.JogoActions;
import com.blogspot.tragacafe.jokenpo.enums.Jogada;
import com.blogspot.tragacafe.jokenpo.enums.Resultado;
import com.blogspot.tragacafe.jokenpo.enums.Status;
import com.blogspot.tragacafe.jokenpo.model.Jogador;

public class Jogo implements JogoActions {
	
	private static int TOTAL_JOGADAS = 10;
	
	private Status status;
	private Jogador jogadorUm;
	private Jogador jogadorDois;
	
	private int rodadasJogadas = 0;
	private int numeroEmpates = 0;
	
	public Jogo() {
		status = Status.AGUARDANDO_JOGADORES;
	}

	public void conectarJogador(Jogador jogador) {
		
		broadcastToPlayers("Jogador conectou-se");
		jogador.getSaida().println("Bem vindo ao jogo!\nDigite o seu nome: ");
		criarThreadJogador(jogador);
		
		if (jogadorUm == null) {
			jogadorUm = jogador;
			return;
		}
		jogadorDois = jogador;
		
		status = Status.AGUARDANDO_INICIO;
		iniciarJogo();
	
	}

	public void iniciarJogo() {
		status = Status.INICIADO;
	}

	@Override
	public void sair(Jogador jogador) {
		broadcastToPlayers("O jogador " + jogador.getNome() + " saiu da partida!");
		finalizarPartida();
	}

	@Override
	public void verificarInicio() {
		if ((jogadorUm == null || jogadorUm.getNome() == null) ||
			(jogadorDois == null || jogadorDois.getNome() == null)) {
			return;
		}
		status = Status.INICIADO;
		broadcastToPlayers("Partida iniciada!");
		broadcastToPlayers("Jogue!");
	}

	@Override
	public void realizarJogada(Jogador jogador, Jogada jogada) {
		
		if (status != Status.INICIADO) {
			jogador.getSaida().println("O jogo ainda não iniciou! Aguarde o outro jogador...");
			return;
		}
		
		if (jogador.getJogada() == null) {
			broadcastToPlayers(jogador.getNome() + " está pronto!");
		}
		jogador.setJogada(jogada);
		processarJogada();
		
	}

	private void processarJogada() {
		
		Jogada jogadaJogadorUm   = jogadorUm.getJogada();
		Jogada jogadaJogadorDois = jogadorDois.getJogada();
		
		if (jogadaJogadorUm == null || jogadaJogadorDois == null) {
			return;
		}
		
		broadcastToPlayers(jogadorUm.getNome() + " (" + jogadaJogadorUm + ") x (" + jogadaJogadorDois + ") " + jogadorDois.getNome());
		
		switch (jogadaJogadorUm.compararCom(jogadaJogadorDois)) {
		case EMPATE:
			contabilizarEmpate(jogadorUm, jogadorDois);
			break;
			
		case GANHOU:
			pontuarJogadores(jogadorUm /* vencedor */, jogadorDois /* perdedor */);
			break;
			
		case PERDEU:
			pontuarJogadores(jogadorDois /* vencedor */, jogadorUm /* perdedor */);
			break;
			
		}
		
		jogadorUm.limparJogada();
		jogadorDois.limparJogada();
		
		if (rodadasJogadas++ < TOTAL_JOGADAS && !ehImpossivelVirarJogo()) {
			broadcastToPlayers((TOTAL_JOGADAS - rodadasJogadas) + " rodadas restantes! ");
		} else {
			if (jogadorUm.getPontuacao() != jogadorDois.getPontuacao()) {
				finalizarPartida();
				return;
			}
			broadcastToPlayers("-- RODADA DE DESEMPATE --");
		}
		broadcastToPlayers("Jogue!");
		
	}

	private boolean ehImpossivelVirarJogo() {
		
		int pontuacaoLimite = (TOTAL_JOGADAS - numeroEmpates) / 2;
		
		return (jogadorUm.getPontuacao() > pontuacaoLimite) || 
			   (jogadorDois.getPontuacao() > pontuacaoLimite);
		
	}

	private void pontuarJogadores(Jogador vencedor, Jogador perdedor) {
		
		vencedor.adicionarPonto();
		vencedor.addToHistorico(vencedor.getJogada(), Resultado.GANHOU);
		perdedor.addToHistorico(perdedor.getJogada(), Resultado.PERDEU);
		
		broadcastToPlayers(vencedor.getNome() + " ganhou!\n");
		
	}

	private void contabilizarEmpate(Jogador jogadorA, Jogador jogadorB) {
		
		jogadorA.addToHistorico(jogadorA.getJogada(), Resultado.EMPATE);
		jogadorB.addToHistorico(jogadorB.getJogada(), Resultado.EMPATE);
		
		numeroEmpates++;
		
		broadcastToPlayers("Empate!\n");
		
	}

	private void finalizarPartida() {
		
		broadcastToPlayers("\n\n");
		broadcastToPlayers("Partida encerrada!\n");
		
		if (status == Status.INICIADO) {
			
			broadcastToPlayers(jogadorUm.getNome() +  ": " + jogadorUm.getPontuacao() + " pontos");
			broadcastToPlayers(jogadorDois.getNome() +  ": " + jogadorDois.getPontuacao() + " pontos");
			
			if (jogadorUm.getPontuacao() == jogadorDois.getPontuacao()) {
				broadcastToPlayers("O jogo terminou em empate!");
			} else if (jogadorUm.getPontuacao() > jogadorDois.getPontuacao()) {
				broadcastToPlayers("O jogador " + jogadorUm.getNome() + " venceu o jogo com " + jogadorUm.getPontuacao() + " pontos!");
			} else {
				broadcastToPlayers("O jogador " + jogadorDois.getNome() + " venceu o jogo com " + jogadorDois.getPontuacao() + " pontos!");
			}
			
			jogadorDois.desconectar();
			jogadorUm.desconectar();
			
		}
		
		this.status = Status.FINALIZADO;
		
	}

	private void criarThreadJogador(Jogador jogador) {
		new PlayerInputThread(jogador, this).start();
	}

	public void broadcastToPlayers(String msg) {
		if (jogadorUm != null) {
			jogadorUm.getSaida().println(msg);
		}
		if (jogadorDois != null) {
			jogadorDois.getSaida().println(msg);
		}
	}
	
	public Status getStatus() {
		return status;
	}
	
}
