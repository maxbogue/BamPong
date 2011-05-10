package bam.pong;

/**
 * Represents a ball.
 * 
 * @author Max Bogue
 * @author Brian Gernhardt
 */
public class Ball {

	/** Current location of the ball. */
	public double x, y;

	/** Movement vector in pixels/second. */
	public double dx, dy;

	/** Ball diameter in pixels. */
	public final int D;

	public Ball(double x, double y, double dx, double dy, int D) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.D = D;
	}

}
