package bam.pong;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Peer {
	private int id;
	private String name;
	private InetAddress address;
	private int port;
	
	public Peer(int id, String name) {
		this(id, name, null, 0);
	}
	
	public Peer(int id, String name, InetAddress address, int port) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.port = port;
	}
	
	/** This peer's game identifier. */
	public int getId() {
		return id;
	}
	
	/** This peer's user visible name. */
	public String getName() {
		return name;
	}
	
	/** Set address information.
	 * 
	 * Should only be initialized with information from the server.
	 */
	public void setAddress(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}
	
	/** Construct a InetSocketAddress to connect to the peer with.
	 *
	 * @return null if an address has never been set.
	 */
	public InetSocketAddress getSocketAddr() {
		if( address == null ) return null;
		return new InetSocketAddress(address, port);
	}

	/* Make it so that Peers with the same ID number are equal.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof Peer) ) return false;
		Peer that = (Peer) obj;
		return getId() == that.getId();
	}

	/* Hash based on ID.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getId();
	}
	
	
}
