package bam.pong;

public interface PeerListener {
	public void receiveBall(int id);
	public void addPeer(Peer p);
	public void dropPeer(Peer p);
}
