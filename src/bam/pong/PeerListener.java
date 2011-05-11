package bam.pong;

public interface PeerListener {
	public void receiveBall(int id, double position, double dx, double dy, int d);
	public void addPeer(Peer p);
	public void dropPeer(Peer p);
}
