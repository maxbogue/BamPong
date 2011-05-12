package bam.pong.server;

import java.nio.channels.SocketChannel;

public class Client {

	
	private int id;
	private String name;
	private SocketChannel channel;
	private Game game = null;
	private int port;
	private int score = 0;
	
	public Client(int id, String name, int openPort, SocketChannel channel) {
		this.id = id;
		this.name = name;
		this.channel = channel;
		this.port = openPort;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public void upScore() {
		score++;
	}
	
	public void resetScore() {
		score = 0;
	}
	
	// Getters because Java is dumb.
	public int getId() { return id; }
	public String getName() { return name; }
	public SocketChannel getChannel() { return channel; }
	public Game getGame() { return game; }
	public int getPort() { return port; }
	public int getScore() { return score; }
}
