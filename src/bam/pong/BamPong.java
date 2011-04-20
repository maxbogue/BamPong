package bam.pong;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * The main window of BamPong.
 * 
 * @author Max Bogue
 */
public class BamPong extends JFrame {

	/** Used for serialization. */
	private static final long serialVersionUID = -7382341800985464596L;

	/** Diameter of the balls in pixels. */
	public static final int BALL_SIZE = 15;
	
	/** The game field component. */
	private GameField field;
	
	/** Constructs the main BamPong window. */
	public BamPong(GameField field) {

		this.field = field;

        setTitle( "BAM!Pong" );
		setLayout( new BorderLayout() );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add( field, BorderLayout.CENTER );
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {

		/*SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
			}
		});*/
		
		int w = 300;
		int h = 200;
		
		// Ball size must be subtracted here so the balls look like they hit the wall.
		GameEngine e = new GameEngine(w - BALL_SIZE, h - BALL_SIZE);
		GameField f = new GameField(e);
		f.setPreferredSize(new Dimension(w, h));
		e.update.register(f);
		
		BamPong bam = new BamPong(f);
		
		Ball bs[] = {
				new Ball(50, 25, 200, 40),
				new Ball(50, 75, -367, 100),
				new Ball(25, 25, 283, -200),
				new Ball(25, 25, 193, -20),
				new Ball(25, 25, 90, -40),
			};
		for( Ball b : bs ) e.addBall(b);
		e.start();
	}
	
}
