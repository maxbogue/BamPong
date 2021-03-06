package bam.pong;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The controller class for the client side of BamPong.
 * 
 * @author Max Bogue
 */
public class Client implements EngineListener, ViewListener, PeerListener, ServerListener {

	// Width and height of gamefield and paddle.
	public final int w, h, pw, ph;
	
	public Engine engine;
	public ServerCommunication serverComm;

	private PeerCommunication peerComm;
	private Paddle paddle;
	private Game game;
	
	public Client(int w, int h, int pw, int ph, ServerCommunication serverComm, PeerCommunication peerComm) {
		this.w = w;
		this.h = h;
		this.pw = pw;
		this.ph = ph;
		this.serverComm = serverComm;
		this.peerComm = peerComm;
		
		this.serverComm.addListener(this);
		this.peerComm.setListener(this);
		
		paddle = new Paddle(pw, ph, 300, w/2 - pw/2, h - ph - 5);
		engine = new Engine(w, h, paddle);
		engine.addListener(this);
	}

	//////////////////
	// ViewListener //
	//////////////////

	public List<String> listGames() throws BamException {
		return Arrays.asList(serverComm.getGames());
	}

	public void createGame(String gameName) throws BamException {
		try {
			if (!serverComm.createGame(gameName))
				throw new BamException("Couldn't create game");
			game = new Game();
			game.name = gameName;
		} catch (IOException e) {
			throw new BamException("Error talking to server");
		}
	}

	public void joinGame(String gameName) throws BamException {
		try {
			game = serverComm.joinGame(gameName);
			if (game == null)
				throw new BamException("Couldn't join game.");
		} catch (IOException e) {
			throw new BamException("Error talking to server");
		}
		
		try {
			for (Peer peer : game.peers.values() )
				peerComm.connectToPeer(peer);
		} catch (IOException e) {
			throw new BamException("Error connecting to peers");
		}
	}

	public void cancelGame() throws BamException {
		if (game == null)
			throw new BamException("Not in a game!");
		
		try {
			if (!serverComm.cancelGame(game.name))
				throw new BamException("Couldn't cancel game.");
		} catch (IOException e) {
			throw new BamException("Error talking to server");
		}
	}
	
	public void startGame() throws BamException {
		if (game == null)
			throw new BamException("Not in a game!");
		
		try {
			serverComm.startGame(game.name);
		} catch (IOException e) {
			throw new BamException("Error talking to server");
		}
	}

	public void movePaddleTo(int x) {
		paddle.x = x;
	}

	public void movePaddleIn(Paddle.Movement dir) {
		paddle.setMovement(dir);
	}

	////////////////////
	// EngineListener //
	////////////////////

	public void sendBall(Ball b) {
		if (game.peers.isEmpty())
			return; // Nobody to send it to.
		
		int div = w / game.peers.size();
		int peerNum = (int) b.x / div;
		
		Iterator<Peer> it = game.peers.values().iterator();
		while (peerNum-- > 0)
			it.next();
		Peer peer = it.next();
		try {
			peerComm.sendBall(b, ((double) b.x) / w, peer);
		} catch (IOException e) {
			// TODO: Ball gets forgotten
			e.printStackTrace();
		}
	}

	public void ballDropped(Ball b) {
		try {
			serverComm.ballDropped(b);
		} catch (IOException e) {
			// TODO: Ball gets forgotten
			e.printStackTrace();
		}
	}

	public void fieldUpdated(Set<Ball> balls, Paddle paddle) {}
	public void ballAdded(Ball b) {}
	
	//////////////////
	// PeerListener //
	//////////////////
	
	public void receiveBall(int id, double position, double dx, double dy, int d) {
		if (dy < 0) dy *= -1;  // Make sure it's going down.
		engine.addBall(new Ball(id, position * w, -d, dx, dy, d));
	}
	
	@Override
	public void addPeer(Peer p) {
		game.peers.put(p.getId(), p);
	}

	@Override
	public void dropPeer(Peer p) {
		game.peers.remove(p.getId());
		if (game.peers.isEmpty())
			engine.stop();
		// TODO reconnection or something?
	}

	////////////////////
	// ServerListener //
	////////////////////
	
	public void gameStarted() {
		engine.start();
	}
	
	public void gameCanceled() {
		engine.stop();
		game = null;
	}

	public void newBall(int id, double pos, double dx, double dy, int d) {
		engine.addBall(new Ball(id, pos * w, -d, dx, dy, d));
	}
}
