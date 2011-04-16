package bam.pong;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * The main frame.
 * 
 * @author Max
 *
 */
public class BamPong extends JFrame {

	private static final long serialVersionUID = -7382341800985464596L;

	private static final int BALL_SIZE = 20;
	
	private GameField field;
	
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
		
		GameEngine e = new GameEngine(w - BALL_SIZE, h - BALL_SIZE);
		GameField f = new GameField(e);
		f.setPreferredSize(new Dimension(w, h));
		e.update.register(f);
		
		BamPong bam = new BamPong(f);
		
		
		Ball bs[] = {
				new Ball(50, 25, 3, 4),
				new Ball(50, 75, -3, 0),
				new Ball(25, 25, 5, -2),
			};
		for( Ball b : bs )
			e.addBall(b);
		e.start();
	}
	
}
