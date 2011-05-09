package bam.pong;

import java.io.IOException;
import java.net.InetAddress;
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
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.text.AbstractDocument.BranchElement;

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
					selector.select(10000);
					
					// Handle all ready channels
					Set<SelectionKey> selected = selector.selectedKeys();
					for (SelectionKey k : selected) {
						selected.remove(k); // We're handling it

						if(!k.isValid()) continue; // Invalid?

						Channel c = k.channel();
						if ( c.equals(incoming) ) {
							acceptIncomingPeer();
						} else if ( new_sockets.contains(c) ) {
							processNewSocket((SocketChannel) c);
						} else if ( new_peers.containsKey(c) ) {
							processNewPeer((SocketChannel) c);
						} else if ( peers.containsKey(c) ) {
							processPeerMessage((SocketChannel) c);
						} else {
							System.err.println( "Tried to process unknown socket." );
							k.cancel(); // If we don't know it now, we'll probably never know it.
							c.close();
						}
					}
					
					// Check for closed sockets
					for (SocketChannel socket : peers.keySet()) {
						if (!socket.isOpen()) {
							socket.keyFor(selector).cancel();
							Peer peer = peers.get(socket);
							peers.remove(socket);
							sockets.remove(peer);
							// TODO: Reconnect, propose drop
						}
					}
					for (SocketChannel socket : new_sockets) {
						if (!socket.isOpen()) {
							socket.keyFor(selector).cancel();
							new_sockets.remove(socket);
						}
					}
					for (SocketChannel socket : new_peers.keySet()) {
						if (!socket.isOpen()) {
							socket.keyFor(selector).cancel();
							new_peers.remove(socket);
							// TODO: Retry?
						} else if(!socket.isRegistered()) {
							// Check for new sockets.
							socket.register(selector, SelectionKey.OP_READ);
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
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Initializes incoming socket and connects to server.
	 * 
	 * @throws IOException for any socket problems
	 */
	public PeerCommunication(Peer me) throws IOException {
		this.me = me;
		
		sockets = new HashMap<Peer, SocketChannel>();
		peers   = new HashMap<SocketChannel, Peer>();
		new_sockets = new HashSet<SocketChannel>();
		new_peers   = new HashMap<SocketChannel, Peer>();
		
		utf8 = ChannelHelper.utf8;

		// create incoming socket
		incoming = ServerSocketChannel.open();
		incoming.socket().bind(new InetSocketAddress(0));
		
		// create selector for thread
		selector = Selector.open();
		// register sockets
		incoming.configureBlocking(false);
		incoming.register(selector, SelectionKey.OP_ACCEPT );
		
		// Start listener thread
		watcher = new Thread(new Watcher());
		watcher.start();
	}
	
	/** Returns the port this object is listening to for other peers. */
	public int getPort() {
		return incoming.socket().getLocalPort();
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
		socket.configureBlocking(false);
		new_peers.put(socket, peer); // watcher will register it
		selector.wakeup();
	}

	private void log(String message) {
		System.err.println(me.getName() + ": " + message);
	}
	
	// Called when a peer tries to connect to us
	private void acceptIncomingPeer() throws IOException {
		SocketChannel socket = incoming.accept();
		if( socket == null ) return; // Nothing to accept
		socket.write(utf8.encode("bam!")); // Send recognition signal
		new_sockets.add(socket);
		socket.configureBlocking(false);
		socket.register(selector, SelectionKey.OP_READ);
	}

	// Called when a peer we're trying to connect to sends us a message
	private void processNewPeer(SocketChannel c) throws IOException {
		ByteBuffer message = ChannelHelper.readBytes(c, 4);
		String recognize = utf8.decode(message).toString();
		if(!recognize.equals("bam!")) {
			// Connected to something that wasn't a BAMPong client...
			c.close();
			Peer p = new_peers.remove(c);
			log( "Closing attempt to " + p.getName() + " got " + recognize );
			return;
		}
		
		// Assemble response 
		ByteBuffer name = utf8.encode(me.getName());
		message = ByteBuffer.allocateDirect(name.limit() + 6);
		message.putInt(me.getId());
		ChannelHelper.putString(message, name);
		message.flip();
		
		// Send message
		c.write(message);
		
		// Move socket to connected peers.
		Peer peer = new_peers.remove(c);
		peers.put(c, peer);
		sockets.put(peer, c);
	}

	// Called when someone who's just connected to us sends a messages
	private void processNewSocket(SocketChannel c) throws IOException {
		int id      = ChannelHelper.getInt(c);
		String name = ChannelHelper.getString(c);
		
		// Move to connected peers lists
		Peer peer = new Peer(id, name);
		new_sockets.remove(c);
		peers.put(c, peer);
		sockets.put(peer, c);
	}
	
	
	// Paxos states
	private static final byte NACK    = 0;
	private static final byte PREPARE = 1;
	private static final byte PROMISE = 2;
	private static final byte REQUEST = 3;
	private static final byte ACCEPT  = 4;
	
	// Ball ID -> Paxos State
	//   State here shouldn't equal NACK. (Perhaps it means error?)
	//   PREPARE = sent PREPARE, waiting for PROMISEs
	//   PROMISE = sent PROMISE for current proposal
	//   REQUEST = sent REQUEST, waiting for REQ
	//   ACCEPT  = sent or received quorum of ACCEPT
	
	private class Paxos {
		int  ball;
		byte state;
		int  proposal;
		int  peer;
		int  ack  = 0; // Number of good replies
		int  nack = 0; // Number of bad  replies
		
		Paxos(int b) {
			this(b, ACCEPT, 0, me.getId());
		}
		Paxos(int b, byte s, int pro, int p) {
			ball = b;
			state = s;
			proposal = pro;
			peer = p;
		}
		
		void state(byte s) {
			state = s;
			ack  = 0;
			nack = 0;
		}
		
		boolean quorum() {
			int quorum = peers.size() / 2 + 1;
			return ack >= quorum || nack >= quorum;
		}
	}

	private Map<Integer, Paxos> ball_state     = new ConcurrentHashMap<Integer, Paxos>();

	/** Initiate Paxos for new ball location. */
	public void sendBall(Ball b, Peer p) throws IOException {
		// Initialize our state
		int ball = b.id;
		Paxos state = ball_state.get(ball);
		if(state == null) {
			state = new Paxos(ball, REQUEST, 1, p.getId());
		} else {
			state.state( REQUEST );
			state.proposal++;
			state.peer = p.getId();
		}
		
		// Send a message
		sendBallMessage(null, ball, REQUEST);
	}
	
	// Called to send a ball message
	private void sendBallMessage(SocketChannel c, int ball, byte type) {
		if( type == PROMISE && c == null ) // Other message types are broadcast
			throw new IllegalArgumentException("Need a peer to send a promise to");
		
		Paxos state = ball_state.get(ball);
		if(state == null)
			throw new IllegalArgumentException("Don't know about ball " + ball);

		// Max reply: type(1), ball(4), state(1), proposal(4), curr_prop(4), curr_peer(4)
		ByteBuffer msg = ByteBuffer.allocateDirect(18);
		msg.put(MSG_BALL);
		msg.putInt(ball);
		
		// TODO: finish
	}
	
	// Called to update Paxos state from a message
	private void processBallMessage(SocketChannel c) throws IOException {
		int  peer       = peers.get(c).getId();
		int  ball       = ChannelHelper.getInt(c);
		byte state      = ChannelHelper.getByte(c);
		int  proposal   = ChannelHelper.getInt(c);
		
		Paxos paxos = ball_state.get(ball);
		if(paxos == null) {
			// New ball
			paxos = new Paxos(ball);
			ball_state.put(ball, paxos);
		}
		
		// Paxos state machine
		byte reply = NACK;
		switch(state) {
		case PREPARE:
			if(proposal < paxos.proposal)
				break; // Refuse
			
			paxos.state( reply = PROMISE );
			paxos.peer  = peer;
			break;
		case PROMISE:
			if(paxos.state != PREPARE || paxos.proposal != proposal)
				return; // Ignore promises we're not expecting
			
			paxos.ack++; // Good response
			if(paxos.quorum()) {
				// We have a quorum!
				paxos.state( reply = REQUEST );
				break;
			}
			return; // Wait for more PROMISE/NACK
		case REQUEST:
			if(proposal < paxos.proposal || peer != paxos.peer)
				break; // Refuse

			paxos.state( reply = ACCEPT );
			paxos.peer  = peer;
			// TODO, I think
			break;
		case ACCEPT:
			// TODO
			break;
		case NACK:
			if( proposal != paxos.proposal )
				return; // Ignore nacks for non-current proposal
			// TODO
			break;
		default:
			System.err.println("Unknown Paxos State: " + state);
			return;
		}
		
		sendBallMessage(c, ball, reply);
	}
	
	// Send a message to all peers.
	private void broadcast(ByteBuffer buff) throws IOException {
		for ( SocketChannel c : peers.keySet() ) {
			buff.mark();
			ChannelHelper.sendAll(c, buff);
			buff.reset();
		}
	}
	
	/** Send a debug message to all connected peers. */
	public void sendDebug(String message) throws IOException {
		ByteBuffer msg  = utf8.encode(message);
		ByteBuffer buff = ByteBuffer.allocateDirect(msg.limit() + 3);
		buff.put(MSG_DEBUG);
		ChannelHelper.putString(buff, msg);
		buff.flip();
		
		broadcast(buff);
	}
	
	//////// List of message types
	private static final byte MSG_DEBUG = 0;
	private static final byte MSG_BALL  = 1;
	
	// Called when an established peer sends us a message
	private void processPeerMessage(SocketChannel c) throws IOException {
		Peer peer = peers.get(c);

		ByteBuffer b = ByteBuffer.allocateDirect(1);
		if (c.read(b) == 0)
			return; // No data?
		b.flip();
		byte type = b.get();
		
		switch(type) {
		case MSG_DEBUG:
			log(peer.getName()+": "+ChannelHelper.getString(c));
			break;
		case MSG_BALL:
			processBallMessage(c);
			break;
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
	
	// Test method.  Attempts to establish a connection and send a debug message.
	public static void main(String args[]) {
		try {
			Peer p1 = new Peer(1, "p1");
			PeerCommunication pc1 = new PeerCommunication(p1);
			p1.setAddress(InetAddress.getLocalHost(), pc1.getPort());

			Peer p2 = new Peer(2, "p2");
			PeerCommunication pc2 = new PeerCommunication(p2);
			p2.setAddress(InetAddress.getLocalHost(), pc2.getPort());
			
			pc2.connectToPeer(p1);
			Thread.sleep(500);
			pc1.sendDebug("Hello, peers!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}