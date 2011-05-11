package bam.pong;

public interface ServerListener {
	public void gameStarted();
	public void gameCanceled();
	public void newBall(int id, double pos, double dx, double dy, int d);
}
