package bam.pong;

import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Displays a ball on a field via Swing
 * 
 * @author Brian
 */
public class SwingDisplay extends JComponent {
	/** For serialization (via JComponent) */
	private static final long serialVersionUID = -1198765485813951172L;

	private Set<Ball> balls;

	public SwingDisplay() {
		balls = new HashSet<Ball>();
	}

	public void addBall(Ball b) {
		balls.add(b);
		repaint();
	}

	public void removeBall(Ball b) {
		balls.remove(b);
		repaint();
	}

	/** Draw the ball */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        RenderingHints.VALUE_ANTIALIAS_ON);
		for( Ball b : balls ) {
			g2.fillOval(b.x, b.y, 20, 20);
		}
	}

	/**
	 * Test method to show the display.
	 * 
	 * @param args Command line arguments (ignored)
	 */
	public static void main(String[] args) {
		final SwingDisplay disp = new SwingDisplay();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame("BAM!Pong");
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(100,125);
				f.add(disp);
				f.setVisible(true);
			}
		});

		Thread animator = new Thread() {
			@Override
			public void run() {
				Ball bs[] = {new Ball(50, 25, 3, 0), new Ball(50, 75, -3, 0)};
				for( Ball b : bs )
					disp.addBall(b);
				for(;;) {
					for( Ball b : bs ) {
						b.x += b.dx;
						if( b.x <= 0 || b.x >= 100 )
							b.dx *= -1;
					}

					disp.repaint();
					
					try {
						Thread.sleep(1000/30);
					} catch (InterruptedException e) {
						// I don't care.
					}
				}
			}
		};

		animator.start();
	}

}
