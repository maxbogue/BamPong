package bam.pong;

/**
 * Represents a ball.
 * 
 * @author Max
 * @author Brian
 */
public class Ball {

	/** Current location of the ball. */
	double x, y;

	/** Movement vector in pixels/second. */
	double dx, dy;

	public Ball(double x, double y, double dx, double dy) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}

	public Ball(double x, double y) {
		this(x, y, 0, 0);
	}

	public Ball() {
		this(0, 0, 0, 0);
	}
}
