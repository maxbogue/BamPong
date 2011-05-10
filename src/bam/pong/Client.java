package bam.pong;

import java.util.List;

/**
 * The controller class for the client side of BamPong.
 * 
 * @author Max Bogue
 */
public class Client implements EngineListener, ViewListener {

	// Width and height of gamefield and paddle.
	public final int w, h, pw, ph;

	private ServerCommunication serverComm;
	private PeerCommunication peerComm = null;
	private Engine engine = null;
	private Paddle paddle = null;
	
	public Client(int w, int h, int pw, int ph, ServerCommunication serverComm) {
		this.w = w;
		this.h = h;
		this.pw = pw;
		this.ph = ph;
		this.serverComm = serverComm;
	}

	//////////////////
	// ViewListener //
	//////////////////

	public List<String> listGames() throws BamException {
		return null;
	}

	public void createGame(String gameName) throws BamException {
		
	}

	public void joinGame(String gameName) throws BamException {
		
	}

	public void cancelGame(String gameName) throws BamException {
		
	}

	public void movePaddleTo(int x) {
		
	}

	public void movePaddleIn(Paddle.Movement dir) {
		
	}

	////////////////////
	// EngineListener //
	////////////////////

	public void sendBall(Ball b) {
		
	}

	public void ballDropped(Ball b) {
		
	}

	public void fieldUpdated() {}
	public void ballAdded(Ball b) {}

	///////////////
	// Utilities //
	///////////////

	private Paddle makePaddle() {
		return new Paddle(pw, ph, 300, w/2 - pw/2, h - ph - 5);
	}

}
