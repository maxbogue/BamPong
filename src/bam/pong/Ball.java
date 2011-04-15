package bam.pong;

/**
 * Represents a ball.
 * 
 * @author Max
 * @author Brian
 */
public class Ball {
	int x, y;
	int dx, dy;

	public Ball(int x, int y, int dx, int dy) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
	public Ball(int x, int y) {
		this(x, y, 0, 0);
	}
	
	public Ball() {
		this(0, 0, 0, 0);
	}
}
