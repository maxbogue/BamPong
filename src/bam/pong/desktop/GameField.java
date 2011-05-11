package bam.pong.desktop;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
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
public class GameField extends JComponent implements EngineListener, KeyListener {

	private Set<Ball> balls = new HashSet<Ball>();
	private Paddle paddle = null;
	
	/** Constructor. It constructs. */
	public GameField() {}

	/** Draw the ball. */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        RenderingHints.VALUE_ANTIALIAS_ON);
		for ( Ball b : balls ) {
			g2.fillOval((int)b.x, (int)b.y, b.D, b.D);
		}
		if (paddle != null)
			g2.fillRect(paddle.x, paddle.y, paddle.w, paddle.h);
	}

	@Override
	public void fieldUpdated(Set<Ball> balls, Paddle paddle) {
		this.balls = balls;
		this.paddle = paddle;
		repaint();
	}

	@Override
	public void sendBall(Ball b) {}

	@Override
	public void ballDropped(Ball b) {}

	@Override
	public void ballAdded(Ball b) {}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			paddle.setMovement(Paddle.Movement.LEFT);
			break;
		case KeyEvent.VK_RIGHT:
			paddle.setMovement(Paddle.Movement.RIGHT);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT: 
			paddle.setMovement(Paddle.Movement.NONE);
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
