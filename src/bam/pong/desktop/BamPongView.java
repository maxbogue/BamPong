package bam.pong.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import bam.pong.Ball;
import bam.pong.Engine;
import bam.pong.Paddle;


/**
 * The main window of BamPong.
 * 
 * @author Max Bogue
 */
@SuppressWarnings("serial")
public class BamPongView extends JFrame {

	/** The game field component. */
	private GameField field;
	
	/** Constructs the main BamPong window. */
	public BamPongView(GameField field) {

		this.field = field;
		addKeyListener( field );
		
        setTitle( "BAM!Pong" );
		setLayout( new BorderLayout() );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add( this.field, BorderLayout.CENTER );
		pack();
		setVisible(true);
	}
	
	static public void main(String args[]) {
		int w = 300;
		int h = 200;

		int pw = 80; // Paddle width.
		int ph = 15; // Paddle height.
		
		Paddle paddle = new Paddle(pw, ph, 300, w/2 - pw/2, h - ph - 5);
		
		// Ball size must be subtracted here so the balls look like they hit the wall.
		Engine e = new Engine(w, h, paddle);
		GameField f = new GameField(e.getBalls(), paddle);
		f.setPreferredSize(new Dimension(w, h));
		e.addListener(f);
		
		new BamPongView(f);
		
		int D = 15;
		Ball bs[] = {
				new Ball(1, 50, 25, 200, 40, D),
				new Ball(2, 50, 75, -367, 100, D),
				new Ball(3, 25, 25, 283, -200, D),
				new Ball(4, 25, 25, 193, -20, D),
				new Ball(5, 25, 25, 90, -40, D),
			};
		for( Ball b : bs ) e.addBall(b);
		e.start();
	}
}
