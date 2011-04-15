package bam.pong;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles the ball movement physics.
 * 
 * @author Max
 */
public class GameEngine implements Runnable {
	
	private final int UPDATES_PER_SEC = 30;
	
	/** Stops the engine thread. */
	private boolean runThread = true;
	
	/** The thread that moves balls. */
	private Thread ballMover;
	
	/** All balls in the game. */
	private Set<Ball> balls = new HashSet<Ball>();

	private int width;
	private int height;
	
	public final Event update = new Event();
	
	public GameEngine(int width, int height) {
		ballMover = new Thread(this);
		this.width = width;
		this.height = height;
	}
	
	public void start() {
		ballMover.start();
	}
	
	public void updatePaddleLoc() {
		// Do stuff here!
	}
	
	private void updateBallPositions() {
		for (Ball b : balls) {
			b.x += b.dx;
			b.y += b.dy;
			if (b.x <= 0 || b.x >= width) b.dx *= -1;
			if (b.y <= 0 || b.y >= height) b.dy *= -1;
		}
		update.trigger();
	}
	
	@Override
	public void run() {
		while (runThread) {
			updateBallPositions();
			try {
				Thread.sleep(1000 / UPDATES_PER_SEC);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addBall(Ball b) {
		balls.add(b);
	}
	
	public boolean removeBall(Ball b) {
		return balls.remove(b);
	}
	
	public Set<Ball> getBalls() {
		return balls;
	}
	
}
