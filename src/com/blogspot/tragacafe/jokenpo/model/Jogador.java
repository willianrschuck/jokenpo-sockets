package com.blogspot.tragacafe.jokenpo.model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.blogspot.tragacafe.jokenpo.enums.Jogada;
import com.blogspot.tragacafe.jokenpo.enums.Resultado;

public class Jogador {

	private String nome;
	private Jogada jogada;
	private int pontuacao;
	private List<HistoryEntry> historico;
	
	private PrintStream saida;
	private BufferedReader entrada;
	private Socket conexao;

	public Jogador(BufferedReader entrada, PrintStream saida, Socket conexao) {
		this.entrada = entrada;
		this.saida = saida;
		this.conexao = conexao;
		this.historico = new ArrayList<>();
	}

	public void addToHistorico(Jogada jogada, Resultado resultado) {
		historico.add(new HistoryEntry(jogada, resultado));
	}

	public void limparJogada() {
		jogada = null;
	}

	private void enviarHistorico() {
		int i = 0; 
		for (HistoryEntry historyEntry : historico) {
			
			saida.println("[" + (++i) + "]" + historyEntry.getJogada() + ": " + historyEntry.getResultado());
		}
	}

	public void desconectar() {
		enviarHistorico();
		try {
			conexao.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}
	
	public void setJogada(Jogada jogada) {
		this.jogada = jogada;
	}
	
	public Jogada getJogada() {
		return jogada;
	}
	
	public void adicionarPonto() {
		pontuacao++;
	}
	
	public int getPontuacao() {
		return pontuacao;
	}
	
	public PrintStream getSaida() {
		return saida;
	}
	
	public BufferedReader getEntrada() {
		return entrada;
	}
	
	public List<HistoryEntry> getHistorico() {
		return historico;
	}

}
