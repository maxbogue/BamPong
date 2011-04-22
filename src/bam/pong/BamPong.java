package bam.pong;

import java.awt.Dimension;

public class BamPong {

	/** Diameter of the balls in pixels. */
	public static final int BALL_SIZE = 15;
	
	public static void main(String[] args) {

		/*SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
			}
		});*/
		
		int w = 300;
		int h = 200;
		
		// Ball size must be subtracted here so the balls look like they hit the wall.
		Engine e = new Engine(w - BALL_SIZE, h - BALL_SIZE);
		GameField f = new GameField();
		f.setPreferredSize(new Dimension(w, h));
		e.addListener(f);
		
		BamPongView bam = new BamPongView(f);
		
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
