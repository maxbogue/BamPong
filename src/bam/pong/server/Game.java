package bam.pong.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import bam.pong.ChannelHelper;
import bam.pong.Constants;


public class Game {

	private static final int BALL_DIAMETER = 15;
	
	private String name;
	private List<Client> players = new LinkedList<Client>();
	private static Random rnd = new Random();
	private int nextBallID = 1;
	
	public Game(String name, Client owner) {
		this.name = name;
		addPlayer(owner);
	}
	
	public void startGame() {
		// Tell everyone to start
		ByteBuffer b = ByteBuffer.allocate(1);
		b.put(Constants.START_GAME);
		b.flip();
		
		for( Client client : players ) {
			SocketChannel c = client.getChannel();
			try {
				ChannelHelper.sendAll(c, b);
				b.rewind();
			} catch (IOException e) {
				// TODO
				e.printStackTrace();
			}
		}
		
		for( Client client : players ) {
			try {
				sendNewBall(client);
			} catch (IOException e) {
				// TODO
				e.printStackTrace();
			}
		}
	}
	
	public void sendNewBall() throws IOException {
		sendNewBall(players.get(rnd.nextInt(players.size())));
	}
	
	public void sendNewBall(Client player) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(33);
		
		b.put(Constants.NEW_BALL);
		b.putInt(nextBallID++);					// ID
		b.putDouble(rnd.nextDouble() * 0.9 + 0.05);	// Position.
		b.putDouble((rnd.nextDouble() * 0.8 + 0.2) * (rnd.nextBoolean() ? 1 : -1));	// dx.
		b.putDouble(rnd.nextDouble() * 0.8 + 0.2);	// dy.
		b.putInt(BALL_DIAMETER);
		b.flip();
		
		ChannelHelper.sendAll(player.getChannel(), b);
	}
	
	public boolean cancel() {
		if (!hasBegun()) {
			for (Client client : players) {
				SocketChannel sc = client.getChannel();
				try {
					ChannelHelper.putByte(sc, Constants.GAME_CANCELED);
				} catch (IOException e) {
					// TODO: bummer.
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void addPlayer(Client c) {
		players.add(c);
		c.setGame(this);
		c.resetScore();
	}
	
	/** Removes a player from the game
	 * 
	 * @return false if there are other players in the game
	 */
	public boolean removePlayer(Client c) {
		players.remove(c);
		return players.isEmpty();
	}
	
	public Client getOwner() {
		return players.get(0);
	}
	
	public List<Client> getPlayers() {
		return players;
	}
	
	public boolean hasBegun() {
		return false;
	}
	
	public String getName() {
		return name;
	}
	
}
