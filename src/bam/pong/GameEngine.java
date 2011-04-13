package bam.pong;

import java.util.HashSet;
import java.util.Set;

public class GameEngine implements Runnable {
	
	private final int UPDATES_PER_SEC = 60;
	
	/** Stops the engine thread. */
	private boolean runThread = true;
	
	/** The thread that moves balls. */
	private Thread ballMover;
	
	/** All balls in the game. */
	private Set<Ball> balls = new HashSet<Ball>();
	
	public final Event update = new Event();
	
	public GameEngine() {
		ballMover = new Thread(this);
	}
	
	public void start() {
		ballMover.start();
	}
	
	public void updatePaddleLoc() {
		// Do stuff here!
	}
	
	private void updateBallPosition(Ball ball) {
		// Do stuff here!
	}
	
	@Override
	public void run() {
		while (runThread) {
			for (Ball b : balls) updateBallPosition(b);
			update.trigger();
			try {
				Thread.sleep(1000 / UPDATES_PER_SEC);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
