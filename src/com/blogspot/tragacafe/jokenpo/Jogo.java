package com.blogspot.tragacafe.jokenpo;
import com.blogspot.tragacafe.jokenpo.actions.JogoActions;
import com.blogspot.tragacafe.jokenpo.enums.Jogada;
import com.blogspot.tragacafe.jokenpo.enums.Resultado;
import com.blogspot.tragacafe.jokenpo.enums.Status;
import com.blogspot.tragacafe.jokenpo.model.Jogador;

/**
 * Classe que representa uma instância do jogo,
 * mantém o seu status e implementa as ações de
 * {@link JogoActions}
 * 
 * @author Eliel Alves da Silva, elielalves.cc@gmail.com
 * @author Willian Ricardo Schuck, willianrschuck@gmail.com
 * @version 0.1
 */
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
	
	/**
	 * Adiciona um jogador a partida 
	 * @param jogador
	 */
	public void conectarJogador(Jogador jogador) {
		
		broadcastToPlayers("Jogador conectou-se");
		jogador.getSaida().println("Bem vindo ao jogo!\nDigite o seu nome: ");
		criarThreadJogador(jogador);
		
		if (jogadorUm == null) {
			jogadorUm = jogador;
			return; // Necessário aguardar o segundo jogador
		}
		jogadorDois = jogador;
		
		status = Status.AGUARDANDO_INICIO;
		iniciarJogo(); // Como o jogo neste momento possui dois jogadores conectados é dado inicio a partida
	
	}
	
	/**
	 * Cria uma thread para cada um dos jogadores.
	 * As threads administram a entrada de dados e podem chamar
	 * ações predefinidas dentro desta classe Jogo 
	 */
	public void iniciarJogo() {
		status = Status.INICIADO;
	}

	/** {@inheritDoc} */
	@Override
	public void sair(Jogador jogador) {
		broadcastToPlayers("O jogador " + jogador.getNome() + " saiu da partida!");
		finalizarPartida();
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
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
	
	/* Lógica para a verificação da jogada */
	private void processarJogada() {
		
		Jogada jogadaJogadorUm   = jogadorUm.getJogada();
		Jogada jogadaJogadorDois = jogadorDois.getJogada();
		
		if (jogadaJogadorUm == null || jogadaJogadorDois == null) {
			return; // Se um dos jogadores ainda não jogou, retorna para aguardar a sua jogada
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
			
		};
		
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

	/* Verifica se o jogo já encontra-se ganho por um dos jogadores */
	private boolean ehImpossivelVirarJogo() {
		
		int pontuacaoLimite = (TOTAL_JOGADAS - numeroEmpates) / 2;
		
		return (jogadorUm.getPontuacao() > pontuacaoLimite) || 
			   (jogadorDois.getPontuacao() > pontuacaoLimite);
		
	}

	/* Realiza a pontuação do jogador */
	private void pontuarJogadores(Jogador vencedor, Jogador perdedor) {
		
		vencedor.adicionarPonto();
		vencedor.addToHistorico(vencedor.getJogada(), Resultado.GANHOU);
		perdedor.addToHistorico(perdedor.getJogada(), Resultado.PERDEU);
		
		broadcastToPlayers(vencedor.getNome() + " ganhou!\n");
		
	}
	
	/* Adiciona o empate ao histórico dos jogadores e incrementa o número de empates */
	private void contabilizarEmpate(Jogador jogadorA, Jogador jogadorB) {
		
		jogadorA.addToHistorico(jogadorA.getJogada(), Resultado.EMPATE);
		jogadorB.addToHistorico(jogadorB.getJogada(), Resultado.EMPATE);
		
		numeroEmpates++;
		
		broadcastToPlayers("Empate!\n");
		
	}

	/* Envia as estatísticas da partida para os jogadores e encerra as conexões */
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
	
	/**
	 * Cria uma nova thread para receber a entrada de dados do jogador
	 * @param jogador
	 */
	private void criarThreadJogador(Jogador jogador) {
		new PlayerInputThread(jogador, this).start();
	}
	
	/**
	 * Envia a string recebida a todos os jogadores conectados
	 * @param msg
	 */
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
