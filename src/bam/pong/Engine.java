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
	
	/** The paddle. */
	private Paddle paddle;
	
	/** Width of the playing field. */
	private int width;
	
	/** Height of the playing field. */
	private int height;
	
	/** Everything listening to the engine. */
	private List<EngineListener> listeners = new LinkedList<EngineListener>();
	
	/** Makes an engine using the given width and height. */
	public Engine(int width, int height, Paddle paddle) {
		ballMover = new Thread(this);
		this.width = width;
		this.height = height;
		this.paddle = paddle;
	}
	
	/** Starts the game engine. */
	public void start() {
		ballMover.start();
	}
	
	private void updateField() {
		updatePaddlePosition();
		updateBallPositions();
		for (EngineListener el : listeners) el.fieldUpdated();
	}

	private void updatePaddlePosition() {
		Paddle p = paddle;
		if (p.movement == Paddle.Movement.LEFT) {
			p.x -= Math.min(p.x, p.speed / UPDATES_PER_SEC);
		} else if (p.movement == Paddle.Movement.RIGHT && p.y + p.w < width) {
			p.x += Math.min(width - p.x - p.w, p.speed / UPDATES_PER_SEC);
		}
	}
	
	/** Updates the position of each ball and triggers a view update. */
	private void updateBallPositions() {
		for (Ball b : new HashSet<Ball>(balls)) {
			// Detect paddle collision.
			if (b.x + b.D > paddle.x && b.x < paddle.x + paddle.w && b.y > paddle.y) {
				b.dx += (b.x - (paddle.x + paddle.w/2)) / 5;
				b.dy = -1 * Math.abs(b.dy);
			}
			// Bounce off sides.
			if (b.x <= 0 || b.x + b.D >= width) b.dx *= -1;
			// Detect if ball fell off bottom of screen.
			if (b.y - b.D >= height) {
				b.dy *= -1;
				for (EngineListener el : listeners) el.ballDropped(b);
//				balls.remove(b);
			}
			// Detect if ball went off the top of the screen.
			if (b.y <= 0) {
				b.dy *= -1;
				for (EngineListener el : listeners) el.sendBall(b);
//				balls.remove(b);
			}
			b.x += b.dx / UPDATES_PER_SEC;
			b.y += b.dy / UPDATES_PER_SEC;
		}
	}
	
	/** Run method for the ball mover thread. */
	@Override
	public void run() {
		while (runThread) {
			updateField();
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
	
	public Set<Ball> getBalls() {
		return balls;
	}
	
}
