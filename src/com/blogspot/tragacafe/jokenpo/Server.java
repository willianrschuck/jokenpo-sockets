package com.blogspot.tragacafe.jokenpo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.blogspot.tragacafe.jokenpo.enums.Status;
import com.blogspot.tragacafe.jokenpo.model.Jogador;

/**
 * Recebe a conexão dos clientes e cria a instância do jogo
 * repassando o trabalho de comunicação para a instância criada
 * 
 * @author Eliel Alves da Silva, elielalves.cc@gmail.com
 * @author Willian Ricardo Schuck, willianrschuck@gmail.com
 * @version 0.1
 */
public class Server {

	private static final String MOTD = "    __ _____ _____ _____ _____ _____ _____ \n" + 
									   " __|  |     |  |  |   __|   | |  _  |     |\n" + 
									   "|  |  |  |  |    -|   __| | | |   __|  |  |\n" + 
									   "|_____|_____|__|__|_____|_|___|__|  |_____|\n" +
									   "\n" +
									   "          Sistemas Operacionais 2\n" +
									   "\n" +
									   "           Eliel Alves da Silva\n" +
									   "          Willian Ricardo Schuck\n";
	private static Jogo jogoEmEspera;
	
	public static void main(String[] args) {
		
		try {

			@SuppressWarnings("resource")
			ServerSocket server = new ServerSocket(3500);
			System.out.println("Servidor iniciado");

			while (true) {

				Socket conexao = server.accept();
				BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
				PrintStream saida = new PrintStream(conexao.getOutputStream());
				Jogador jogador = new Jogador(entrada, saida, conexao);
				jogador.getSaida().println(MOTD);
				Jogo game = buscarJogo();
				if (game != null) {
					game.conectarJogador(jogador);
					continue;
				} 
				jogador.getSaida().print("Que pena! O servidor está cheio, tente mais tarde...");
				conexao.close();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static Jogo buscarJogo() {
		if (jogoEmEspera == null || jogoEmEspera.getStatus() == Status.FINALIZADO) {
			jogoEmEspera = new Jogo();
		}
		if (jogoEmEspera.getStatus() == Status.AGUARDANDO_JOGADORES) {
			return jogoEmEspera;
		}
		return null;
	}
	
}
