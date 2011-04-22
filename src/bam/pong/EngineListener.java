package bam.pong;

import java.util.Collection;

/**
 * Interface for interacting with the engine.
 * 
 * @author Max Bogue
 */
public interface EngineListener {
	
	/**
	 * Ball positions have been updated.
	 */
	public void ballsUpdated(Collection<Ball> balls);
	
	/**
	 * The given ball went off the top of the screen.
	 */
	public void sendBall(Ball b);
	
	/**
	 * The given ball went off the bottom of the screen.
	 */
	public void ballDropped(Ball b);
	
}
