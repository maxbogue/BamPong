package bam.pong;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/**
 * Represents the game field using Swing.
 * 
 * @author Brian
 * @author Max
 */
public class GameField extends JComponent implements EventListener {

	/** For serialization (via JComponent) */
	private static final long serialVersionUID = -1198765485813951172L;

	/** The engine object.  Only currently needed for access to the ball locations. */
	private GameEngine engine;

	/** Constructor. It constructs. */
	public GameField(GameEngine engine) {
		this.engine = engine;
	}

	/** Draw the ball. */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        RenderingHints.VALUE_ANTIALIAS_ON);
		for ( Ball b : engine.getBalls() ) {
			g2.fillOval((int)b.x, (int)b.y, BamPong.BALL_SIZE, BamPong.BALL_SIZE);
		}
	}

	@Override
	public void eventTriggered(Object o) {
		repaint();
	}
	
}
