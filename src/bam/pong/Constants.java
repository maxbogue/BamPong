package bam.pong;

/** Constants needed by multple classes */
public class Constants {

	/** Client-Server: List Games */
	public static final byte LIST_GAMES = 1;
	/** Client-Server: Create Game */
	public static final byte CREATE_GAME = 2;
	/** Client-Server: Cancel Game */
	public static final byte CANCEL_GAME = 3;
	/** Client-Server: Join Game */
	public static final byte JOIN_GAME = 4;
	/** Client-Server: Start Game */
	public static final byte START_GAME = 5;
	/** Server to Client: Game Canceled */
	public static final byte GAME_CANCELED = 6;
}
