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
	private Thread ballMover = null;
	
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
		this.width = width;
		this.height = height;
		this.paddle = paddle;
	}
	
	/** Starts a new game engine thread. */
	public void start() {
		if (ballMover == null) { 
			ballMover = new Thread(this, "Game Engine");
			ballMover.start();
		}
	}
	
	/** Stops the engine and clears balls. */
	public void stop() {
		runThread = false;
		ballMover = null;
		balls.clear();
	}

	private void updateField() {
		updatePaddlePosition();
		updateBallPositions();
		for (EngineListener el : listeners) el.fieldUpdated(balls, paddle);
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
		Paddle p = paddle;
		for (Ball b : balls.toArray(new Ball[0])) {
			if (b.x <= 0 || b.x + b.D >= width) {
				// Ball hit a side.
				b.dx *= -1;
				if (b.x <= 0) b.x = 0;
				if (b.x + b.D >= width) b.x = width - b.D;
			}
			if (b.y - b.D >= height) {
				// Ball fell off bottom of screen.
				for (EngineListener el : listeners)
					el.ballDropped(b);
				balls.remove(b);
			} else if (b.y < -b.D) {
				// Ball went off the top of the screen.
				for (EngineListener el : listeners)
					el.sendBall(b);
				balls.remove(b);
			} else if (b.x + b.D >= p.x && b.x <= p.x + p.w
					&& b.y + b.D >= p.y && b.y + b.D < p.y + p.h/2)
			{
				// Ball hit top of paddle.
				b.dx += (b.x - (p.x + p.w/2)) / 4 / width;
				b.y = p.y - b.D;
				b.dy *= -1;
			} else if (b.x + b.D >= p.x && b.x + b.D < p.x + p.w/2
					&& b.y + b.D >= p.y && b.y <= p.y + p.h)
			{
				// Ball hit left paddle edge.
				b.x = p.x - b.D;
				if (b.dx > 0) b.dx *= -1;
			} else if (b.x <= p.x + p.w && b.x > p.x + p.w/2
					&& b.y + b.D >= p.y && b.y <= p.y + p.h)
			{
				// Ball hit right paddle edge.
				b.x = p.x + p.w;
				if (b.dx < 0) b.dx *= -1;
			}
			b.x += b.dx * width / UPDATES_PER_SEC;
			b.y += b.dy * height / UPDATES_PER_SEC;
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
		for (EngineListener el : listeners) el.ballAdded(b);
	}
	
	public Set<Ball> getBalls() {
		return balls;
	}
	
	public Paddle getPaddle() {
		return paddle;
	}
	
	/** Returns the width of the playing field */
	public int getWidth() { return width; }
	
	/** Returns the height of the playing field */
	public int getHeight() { return height; }
}
