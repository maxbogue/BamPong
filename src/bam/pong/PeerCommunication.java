package bam.pong;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Handles all peer-to-peer communications.
 */
public class PeerCommunication {
	// Use NIO channels to avoid having a thread per socket.
	private ServerSocketChannel incoming;       // other clients connect here
	private Map<Peer, SocketChannel> sockets;   // open socket -> peer
	private Map<SocketChannel, Peer> peers;     // connected peer -> socket
	private Set<SocketChannel> new_sockets;     // peers that have connected, but don't have ID for
	private Map<SocketChannel, Peer> new_peers; // peers we're connecting to
	
	private Thread watcher;   // thread dealing with incoming data
	private Selector selector; // selector to wait on for data
	private Charset utf8;  // for string encoding
	
	private Peer me;
		
	private class Watcher implements Runnable {
		@Override
		public void run() {
			// loop while any socket is open
			while ( incoming.isOpen() || !peers.isEmpty() ) {
				try {
					// wait on selector
					selector.select(1000);
					
					// Handle all ready channels
					for (SelectionKey k : selector.selectedKeys()) {
						Channel c = k.channel();
						
						if ( c == incoming ) {
							acceptIncomingPeer();
						} else if ( new_sockets.contains(c) ) {
							processNewSocket((SocketChannel) c);
						} else if ( new_peers.containsValue(c) ) {
							processNewPeer((SocketChannel) c);
						} else if ( peers.containsValue(c) ) {
							processPeerMessage((SocketChannel) c);
						} else {
							System.err.println( "Tried to process unknown socket." );
						}
					}
					
					// Check for closed sockets
					for (SocketChannel socket : peers.keySet()) {
						if (!socket.isOpen()) {
							Peer peer = peers.get(socket);
							peers.remove(socket);
							sockets.remove(peer);
							// TODO: Reconnect, propose drop
						}
					}
					for (SocketChannel socket : new_sockets) {
						if (!socket.isOpen()) {
							new_sockets.remove(socket);
						}
					}
					for (SocketChannel socket : new_peers.keySet()) {
						if (!socket.isOpen()) {
							new_peers.remove(socket);
							// TODO: Retry?
						}
					}
				} catch (IOException e) {
					// TODO Handle this better.  In mean time, just keep going.
					System.err.println(e);
					e.printStackTrace();
				}
			}
			try {
				selector.close();
			} catch (IOException e) {
				// Not much to do.
				System.err.println(e);
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Initializes incoming socket and connects to server.
	 * 
	 * @throws IOException for any socket problems
	 */
	PeerCommunication() throws IOException {
		sockets = new HashMap<Peer, SocketChannel>();
		peers   = new HashMap<SocketChannel, Peer>();
		new_sockets = new HashSet<SocketChannel>();
		new_peers   = new HashMap<SocketChannel, Peer>();
		
		utf8 = Charset.forName("UTF-8");

		// create incoming socket
		incoming = ServerSocketChannel.open();
		incoming.socket().bind(new InetSocketAddress(0));
		
		// create selector for thread
		selector = Selector.open();
		// register sockets
		incoming.register(selector, SelectionKey.OP_ACCEPT );
		
		// Start listener thread
		watcher = new Thread(new Watcher());
		watcher.run();
	}

	/** Connect to a new peer. */
	public void connectToPeer(Peer peer) throws IOException {
		InetSocketAddress address = peer.getSocketAddr();
		if (address == null)
			throw new IllegalArgumentException("peer must have address to connect to it");
		if (peers.containsKey(peer))
			throw new IllegalArgumentException("already connected to peer");

		// create socket, add to peers
		SocketChannel socket = SocketChannel.open(address);
		socket.register(selector, SelectionKey.OP_READ );
		new_peers.put(socket, peer);
	}

	// Called when a peer tries to connect to us
	private void acceptIncomingPeer() throws IOException {
		SocketChannel socket = incoming.accept();
		socket.write(utf8.encode("bam!")); // Send recognition signal
		new_sockets.add(socket);
	}

	// Called when a peer we're trying to connect to sends us a message
	private void processNewPeer(SocketChannel c) throws IOException {
		ByteBuffer message = ByteBuffer.allocateDirect(4);

		c.read(message);
		if(!message.equals(utf8.encode("bam!"))) {
			// Connected to something that wasn't a BAMPong client...
			c.close();
			new_peers.remove(c);
		}
		
		// Assemble response 
		message.clear();
		message.putInt(me.getId());
		ByteBuffer name = utf8.encode(me.getName());
		message.putShort((short) name.capacity());
		message.put(name);
		
		// Send message
		c.write(message);
		
		// Move socket to connected peers.
		Peer peer = new_peers.remove(c);
		peers.put(c, peer);
		sockets.put(peer, c);
	}

	// Called when someone who's just connected to us sends a messages
	private void processNewSocket(SocketChannel c) throws IOException {
		ByteBuffer message = ByteBuffer.allocateDirect(3);
		c.read(message);
		
		// Extract information
		int id = message.getInt();
		int len = message.getShort();
		message = ByteBuffer.allocateDirect(len);
		String name = utf8.decode(message).toString();
		
		// Move to connected peers lists
		Peer peer = new Peer(id, name);
		new_sockets.remove(c);
		peers.put(c, peer);
		sockets.put(peer, c);
	}
	
	// Send ball to the appropriate client.
	public void sendBall(Ball b, Peer p) {
	}
	
	public void sendDebug(String message) throws IOException {
		ByteBuffer msg  = utf8.encode(message);
		ByteBuffer buff = ByteBuffer.allocateDirect(msg.position() + 3);
		buff.put(MSG_DEBUG);
		buff.putShort((short) msg.capacity());
		buff.put(msg);
		for ( SocketChannel c : peers.keySet() ) {
			c.write(buff);
		}
	}
	
	private static final byte MSG_DEBUG = 0;
	
	// Called when an established peer sends us a message
	private void processPeerMessage(SocketChannel c) throws IOException {
		ByteBuffer b = ByteBuffer.allocateDirect(1);
		c.read(b);
		
		int type = b.get();
		
		switch(type) {
		case MSG_DEBUG:
			b = ByteBuffer.allocateDirect(2);
			c.read(b);
			short len = b.getShort();
			b = ByteBuffer.allocateDirect(len);
			String message = utf8.decode(b).toString();
			System.out.println(message);
			break;
//		Pass ball
//			Ball data; paxos confirmation that passer has ball to pass?
//		Dropped ball
//			Informative to all peers & server
//			(Maybe implement as passing ball to server?)
//			Server may respond with “you lose”
//		Drop player
//			Paxos confirmation that client is gone
//		Heartbeat?
//			Probably only sent in response to drop
//			Ping/pong messages
//		Server failure
//			All peers, send choice of backup	
		}
	}
}