package com.blogspot.tragacafe.jokenpo;
import com.blogspot.tragacafe.jokenpo.actions.JogarAction;
import com.blogspot.tragacafe.jokenpo.actions.QuandoProntoAction;
import com.blogspot.tragacafe.jokenpo.actions.SairAction;
import com.blogspot.tragacafe.jokenpo.enums.Jogada;
import com.blogspot.tragacafe.jokenpo.enums.Resultado;
import com.blogspot.tragacafe.jokenpo.enums.Status;
import com.blogspot.tragacafe.jokenpo.model.Jogador;

public class Jogo {
	
	private Status status;
	private Jogador jogadorUm;
	private Jogador jogadorDois;
	
	private int rodadasRestantes = 4;
	
	private QuandoProntoAction quandoProntoAction = () -> { verificarInicio(); };
	private JogarAction jogarAction = (jogador, jogada) -> { realizarJogada(jogador, jogada); };
	private SairAction sairAction = (jogador) -> { desconectar(jogador); };
	
	public Jogo() {
		status = Status.AGUARDANDO_JOGADORES;
	}
	
	/* Cria uma thread para cada um dos jogadores.
	 * As threads administram a entrada de dados e podem chamar
	 * ações predefinidas dentro desta classe Jogo */
	public void iniciarJogo() {
		status = Status.INICIADO;
	}
	
	private void criarThreadJogador(Jogador jogador) {
		new PlayerInputThread(jogador, jogarAction, quandoProntoAction, sairAction).start();
	}
	
	/* Adiciona um jogador a partida */
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

	/* Permite que o jogador se desconecte */
	private void desconectar(Jogador jogador) {
		broadcastToPlayers("O jogador " + jogador.getNome() + " saiu da partida!");
		finalizarPartida();
	}

	/* Verifica se todos os jogadores estão prontos para iniciar a partida */
	private void verificarInicio() {
		if ((jogadorUm == null || jogadorUm.getNome() == null) ||
			(jogadorDois == null || jogadorDois.getNome() == null)) {
			return;
		}
		status = Status.INICIADO;
		broadcastToPlayers("Partida iniciada!");
		broadcastToPlayers("Jogue!");
	}

	/* Atualiza a jogada do objeto jogador e realiza o processamento da jogada */
	private void realizarJogada(Jogador jogador, Jogada jogada) {
		
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
		
		if (--rodadasRestantes > 0) {
			broadcastToPlayers(rodadasRestantes + " rodadas restantes! ");
		} else {
			if (jogadorUm.getPontuacao() != jogadorDois.getPontuacao()) {
				finalizarPartida();
				return;
			}
			broadcastToPlayers("-- RODADA DE DESEMPATE --");
		}
		broadcastToPlayers("Jogue!");
		
	}

	/* Realiza a pontuação do jogador */
	private void pontuarJogadores(Jogador vencedor, Jogador perdedor) {
		
		vencedor.adicionarPonto();
		vencedor.addToHistorico(vencedor.getJogada(), Resultado.GANHOU);
		perdedor.addToHistorico(perdedor.getJogada(), Resultado.PERDEU);
		
		broadcastToPlayers(vencedor.getNome() + " ganhou!\n");
		
	}
	
	private void contabilizarEmpate(Jogador jogadorA, Jogador jogadorB) {
		
		jogadorA.addToHistorico(jogadorA.getJogada(), Resultado.EMPATE);
		jogadorB.addToHistorico(jogadorB.getJogada(), Resultado.EMPATE);
		
		broadcastToPlayers("Empate!\n");
		
	}

	/* Envia as estatísticas da partida para os jogadores e encerra as conexões */
	private void finalizarPartida() {
		
		broadcastToPlayers("\n\n");
		broadcastToPlayers("Partida encerrada!\n");
		
		broadcastToPlayers(jogadorUm.getNome() +  ": " + jogadorUm.getPontuacao() + " pontos");
		broadcastToPlayers(jogadorDois.getNome() +  ": " + jogadorDois.getPontuacao() + " pontos");
		
		if (jogadorUm.getPontuacao() == jogadorDois.getPontuacao()) {
			broadcastToPlayers("O jogo terminou em empate!");
		} else if (jogadorUm.getPontuacao() > jogadorDois.getPontuacao()) {
			broadcastToPlayers("O jogador " + jogadorUm.getNome() + " venceu o jogo com " + jogadorUm.getPontuacao() + "!");
		} else {
			broadcastToPlayers("O jogador " + jogadorDois.getNome() + " venceu o jogo com " + jogadorDois.getPontuacao() + "!");
		}
		
		jogadorUm.desconectar();
		jogadorDois.desconectar();
		
		this.status = Status.FINALIZADO;
		
	}
	
	/* Transmite uma mensagem para ambos os jogadores */
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
