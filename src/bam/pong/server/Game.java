package bam.pong.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import bam.pong.ChannelHelper;
import bam.pong.Constants;


public class Game {
	
	private String name;
	private List<Client> players = new LinkedList<Client>();
	
	public Game(String name, Client owner) {
		this.name = name;
		players.add(owner);
	}
	
	public void startGame() {
		ByteBuffer b = ByteBuffer.allocate(1);
		b.put(Constants.START_GAME);
		b.flip();
		
		for( Client client : players ) {
			SocketChannel c = client.getChannel();
			try {
				ChannelHelper.sendAll(c, b);
			} catch (IOException e) {
				// TODO
				e.printStackTrace();
			}
		}
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
