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
	}
	
}
