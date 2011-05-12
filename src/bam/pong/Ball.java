package bam.pong;

/**
 * Represents a ball.
 * 
 * @author Max Bogue
 * @author Brian Gernhardt
 */
public class Ball {
	/** ID # of the ball. */
	public final int id;

	/** Current location of the ball. */
	public double x, y;

	/** Movement vector in % of screen/second. */
	public double dx, dy;

	/** Ball diameter in pixels. */
	public final int D;

	public Ball(int id, double x, double y, double dx, double dy, int D) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.D = D;
	}

}
