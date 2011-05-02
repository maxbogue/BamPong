package bam.pong.server;

import java.util.LinkedList;
import java.util.List;

public class Game {
	
	public String name;
	List<Client> players = new LinkedList<Client>();
	
	public Game(String name, Client owner) {
		this.name = name;
		players.add(owner);
	}
	
	public Client getOwner() {
		return players.get(0);
	}
}
