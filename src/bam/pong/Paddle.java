package bam.pong;

public class Paddle {

	public enum Movement { LEFT, RIGHT, NONE };

	/** Current location of the ball. */
	public int x;
	public final int y;

	public final int w, h;

	/** Max speed in pixels/sec. */
	public final int speed;

	public Movement movement = Movement.NONE;

	public Paddle(int width, int height, int speed, int x, int y) {
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
		this.speed = speed;
	}

	public void setMovement(Movement m) {
		movement = m;
	}

}
