package bam.pong;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Handles the ball movement physics.
 * 
 * @author Max
 */
public class Engine implements Runnable {
	
	/** Number of times the game updates per second. */
	private final int UPDATES_PER_SEC = 30;
	
	/** Stops the engine thread. */
	private boolean runThread = true;
	
	/** The thread that moves balls. */
	private Thread ballMover;
	
	/** All balls in the game. */
	private Set<Ball> balls = new HashSet<Ball>();

	/** Width of the playing field. */
	private int width;
	
	/** Height of the playing field. */
	private int height;
	
	/** Everything listening to the engine. */
	private List<EngineListener> listeners = new LinkedList<EngineListener>();
	
	/** Makes an engine using the given width and height. */
	public Engine(int width, int height) {
		ballMover = new Thread(this);
		this.width = width;
		this.height = height;
	}
	
	/** Starts the game engine. */
	public void start() {
		ballMover.start();
	}
	
	public void setPaddle() {
		// Do stuff here!
	}
	
	/** Updates the position of each ball and triggers a view update. */
	private void updateBallPositions() {
		for (Ball b : new HashSet<Ball>(balls)) {
			b.x += b.dx / UPDATES_PER_SEC;
			b.y += b.dy / UPDATES_PER_SEC;
			if (b.x <= 0 || b.x >= width) b.dx *= -1;
			if (b.y <= 0) {
				b.dy *= -1;
				for (EngineListener el : listeners) el.ballDropped(b);
//				balls.remove(b);
			}
			if (b.y >= height) {
				b.dy *= -1;
				for (EngineListener el : listeners) el.sendBall(b);
//				balls.remove(b);
			}
		}
		for (EngineListener el : listeners) el.ballsUpdated(balls);
	}
	
	/** Run method for the ball mover thread. */
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
	
	public void addListener(EngineListener el) {
		listeners.add(el);
	}
	
	public boolean removeListener(EngineListener el) {
		return listeners.remove(el);
	}
	
	/** Adds a ball to the game. */
	public void addBall(Ball b) {
		balls.add(b);
	}
	
}
