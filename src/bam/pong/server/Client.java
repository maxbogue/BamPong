package bam.pong.server;

import java.nio.channels.SocketChannel;

public class Client {

	
	private int id = 0;
	private String name;
	private int openPort;
	private SocketChannel channel;
	private Game game = null;
	
	public Client(String name, int openPort, SocketChannel channel) {
		this.name = name;
		this.openPort = openPort;
		this.channel = channel;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	// Getters because Java is dumb.
	public int getId() { return id; }
	public String getName() { return name; }
	public SocketChannel getChannel() { return channel; }
	public Game getGame() { return game; }
	
}
