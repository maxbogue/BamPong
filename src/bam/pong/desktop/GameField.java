package bam.pong.desktop;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Set;

import javax.swing.JComponent;

import bam.pong.Ball;
import bam.pong.EngineListener;
import bam.pong.Paddle;

/**
 * Represents the game field using Swing.
 * 
 * @author Brian
 * @author Max
 */
@SuppressWarnings("serial")
public class GameField extends JComponent implements EngineListener {

	private Set<Ball> balls;
	private Paddle paddle;
	
	/** Constructor. It constructs. */
	public GameField(Set<Ball> balls, Paddle paddle) {
		this.balls = balls;
		this.paddle = paddle;
	}

	/** Draw the ball. */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        RenderingHints.VALUE_ANTIALIAS_ON);
		for ( Ball b : balls ) {
			g2.fillOval((int)b.x, (int)b.y - b.D, b.D, b.D);
		}
		g2.fillRect(paddle.x, paddle.y, paddle.w, paddle.h);
	}

	@Override
	public void fieldUpdated() {
		repaint();
	}

	@Override
	public void sendBall(Ball b) {}

	@Override
	public void ballDropped(Ball b) {}

	@Override
	public void ballAdded(Ball b) {}

}
