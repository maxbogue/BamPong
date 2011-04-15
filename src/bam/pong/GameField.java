package bam.pong;

import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Displays a ball on a field via Swing
 * 
 * @author Brian
 * @author Max
 */
public class GameField extends JComponent implements EventListener {
	
	/** For serialization (via JComponent) */
	private static final long serialVersionUID = -1198765485813951172L;
	
	private static final int BALL_SIZE = 20;
	
	private GameEngine engine;

	public GameField(GameEngine engine) {
		this.engine = engine;
	}

	/** Draw the ball */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        RenderingHints.VALUE_ANTIALIAS_ON);
		for ( Ball b : engine.getBalls() ) {
			g2.fillOval(b.x, b.y, BALL_SIZE, BALL_SIZE);
		}
	}

	@Override
	public void eventTriggered(Object o) {
		repaint();
	}
	
}
