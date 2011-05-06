package bam.pong.desktop;

import java.awt.Dimension;

import bam.pong.Ball;
import bam.pong.Engine;
import bam.pong.Paddle;

public class BamPong {

	/** Diameter of the balls in pixels. */
	public static final int D = 15;
	
	public static void main(String[] args) {

		/*SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
			}
		});*/
		
		int w = 300;
		int h = 200;

		int pw = 80; // Paddle width.
		int ph = 15; // Paddle height.
		
		Paddle paddle = new Paddle(pw, ph, 30, w/2 - pw/2, h - ph - 5);
		
		// Ball size must be subtracted here so the balls look like they hit the wall.
		Engine e = new Engine(w, h, paddle);
		GameField f = new GameField(e.getBalls(), paddle);
		f.setPreferredSize(new Dimension(w, h));
		e.addListener(f);
		
		new BamPongView(f);
		
		Ball bs[] = {
				new Ball(50, 25, 200, 40, D),
				new Ball(50, 75, -367, 100, D),
				new Ball(25, 25, 283, -200, D),
				new Ball(25, 25, 193, -20, D),
				new Ball(25, 25, 90, -40, D),
			};
		for( Ball b : bs ) e.addBall(b);
		e.start();
	}
	
}
