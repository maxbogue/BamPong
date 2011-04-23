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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles all client-server and client-client communications.
 */
public class Client {
	// Server information
	public static final String SERVER_ADDRESS = "127.0.0.1";
	public static final int    SERVER_PORT    = 1234;

	// Server message types
	public static final byte NEW_CLIENT = 1;
	public static final byte REGISTER_PORT = 2;
	
	// Use NIO channels to avoid having a thread per socket.
	private ServerSocketChannel incoming;         // other clients connect here
	private SocketChannel server;                 // our connection to the server
	private Map<SocketChannel, Peer> peers; 	  // connections to other clients
	private List<Peer> peerList = new LinkedList<Peer>();
	
	private Thread watcher;   // thread dealing with incoming data
	private Selector selector; // selector to wait on for data
	private Charset encoding;  // for string encoding
	
	private class Watcher implements Runnable {
		@Override
		public void run() {
			// loop while any socket is open
			while ( incoming.isOpen() || server.isOpen() || !peers.isEmpty() ) {
				try {
					// wait on selector
					selector.select(1000);
					
					// Handle all ready channels
					for (SelectionKey k : selector.selectedKeys()) {
						
						Channel c = k.channel();
						
						if ( peers.containsKey(c) ) {
							peers.get(c).processMessage();
						} else if ( c == server ) {
							processServerMessage();
						} else if ( c == incoming ) {
							SocketChannel peerChannel = incoming.accept();
							peerChannel.register(selector, SelectionKey.OP_READ );
							Peer peer = new Peer(peerChannel);
							peers.put(peerChannel, peer);
							peerList.add(peer);
						} else {
							System.err.println( "Tried to process unknown socket." );
						}
					}
					
					// Check for closed peers
					for (SocketChannel peer : peers.keySet()) {
						if (!peer.isOpen()) {
							peers.remove(peer);
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
	Client() throws IOException {
		peers = new ConcurrentHashMap<SocketChannel, Peer>();
		encoding = Charset.forName("UTF-8");

		// create incoming socket
		incoming = ServerSocketChannel.open();
		incoming.socket().bind(new InetSocketAddress(0));

		// connect to server
		server = SocketChannel.open(new InetSocketAddress(
				InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT));
		
		// Sent port information to server
		int port = incoming.socket().getLocalPort();
		byte port_hi = (byte) (port / 256);
		byte port_lo = (byte) (port % 256);
		server.write(ByteBuffer.wrap(new byte[] {REGISTER_PORT, port_hi, port_lo} ));
		
		// create selector for thread
		selector = Selector.open();
		// register sockets
		incoming.register(selector, SelectionKey.OP_READ );
		server	.register(selector, SelectionKey.OP_READ );
		
		// Start listener thread
		watcher = new Thread(new Watcher());
		watcher.run();
	}
	
//	private void connectToPeer(InetSocketAddress address) throws IOException {		
//		// Check for existing connection.
//		InetAddress host = address.getAddress();
//		if (peers.containsKey(host) && peers.get(host).isOpen())
//				return;  // Skip a peer we already have a connection to.
//		
//		// create socket, add to peers
//		SocketChannel peer = SocketChannel.open(address);
//		peers.put(address.getAddress(), peer);
//		peer.register(selector, SelectionKey.OP_READ );		
//	}
	
	// Called whenever we get a message from the server
	private void processServerMessage() throws IOException {
		ByteBuffer msg_buffer = ByteBuffer.allocate(100);
		server.read(msg_buffer);
		byte message[] = msg_buffer.array();
		
		switch(message[0]) {
		case NEW_CLIENT:
			// Message format { NEW_CLIENT, ip1, ip2, ip3, ip4, port_high, port_low }
//			byte address[] = Arrays.copyOfRange(message, 1, 5);
//			int port = message[5] * 256 + message[6];
//			InetSocketAddress peer = new InetSocketAddress(
//					InetAddress.getByAddress(address), port);
//			connectToPeer(peer);
			break;
		default:
			System.err.println("Unknown server message type: " + message[0]);
		}
	}
	
	void sendMessageToClients(String message) throws IOException {
		ByteBuffer encoded = encoding.encode(message);
		for(SocketChannel c : peers.keySet() )
			c.write(encoded);
	}
	
	// Send ball to the appropriate client.
	void sendBall(Ball b) {
		// Calculate the peer to send the ball to using b.x and send it.
	}
	
	public static void main(String args[]) {
		try {
			Client c = new Client();
			Thread.sleep(500);
			c.sendMessageToClients("Hello World!");
			Thread.sleep(10000);
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
}