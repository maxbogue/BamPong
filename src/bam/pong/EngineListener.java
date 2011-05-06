package bam.pong;

/**
 * Interface for interacting with the engine.
 * 
 * @author Max Bogue
 */
public interface EngineListener {
	
	/**
	 * Ball and paddle positions have been updated.
	 */
	public void fieldUpdated();
	
	/**
	 * The given ball went off the top of the field.
	 */
	public void sendBall(Ball b);
	
	/**
	 * The given ball went off the bottom of the field.
	 */
	public void ballDropped(Ball b);
	
	/**
	 * A new ball was added to the field.
	 */
	public void ballAdded(Ball b);
	
}
