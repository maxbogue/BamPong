package bam.pong;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 * The main window of BamPong.
 * 
 * @author Max Bogue
 */
public class BamPongView extends JFrame {

	/** Used for serialization. */
	private static final long serialVersionUID = -7382341800985464596L;
	
	/** The game field component. */
	private GameField field;
	
	/** Constructs the main BamPong window. */
	public BamPongView(GameField field) {

		this.field = field;

        setTitle( "BAM!Pong" );
		setLayout( new BorderLayout() );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add( this.field, BorderLayout.CENTER );
		pack();
		setVisible(true);
	}
	
}
