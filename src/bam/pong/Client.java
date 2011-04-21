package bam.pong;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles all client-server and client-client communications.
 */
public class Client {
	public static final String SERVER_ADDRESS = "127.0.0.1";
	public static final int    SERVER_PORT    = 1234;
	
	// Use NIO channels to avoid having a thread per socket.
	private ServerSocketChannel incoming;
	private SocketChannel server;
	private Map<SocketChannel, Client> peers; // We're using a concurrent map as a set.
	private Thread listener;
	private Selector selector;
	
	private class Listener implements Runnable {
		@Override
		public void run() {
			// loop while any socket is open
			// wait on selector
			// If peer socket, processPeerMessage
			// If server socket, processServerMessage
			// If incoming socket, add incoming connection as peer
		}
	}
	
	
	/**
	 * Initializes incoming socket and connects to server.
	 * 
	 * @throws IOException for any socket problems
	 */
	Client() throws IOException {
		peers = new ConcurrentHashMap<SocketChannel, Client>();

		// create incoming socket
		incoming = ServerSocketChannel.open();
		incoming.socket().bind(new InetSocketAddress(0));

		// connect to server
		server = SocketChannel.open(new InetSocketAddress(
				InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT));
		
		// create selector for thread
		selector = Selector.open();
		// register sockets
		incoming.register(selector, incoming.validOps());
		server.  register(selector, server  .validOps());
		
		// Start listener thread
		listener = new Thread(new Listener());
		listener.run();
	}
	
	void connectToClient(/* IP, port */) {
		// create socket, add to peers
		// setup callback to processPeerMessage
	}
	
	// Called whenever we get a message from the server
	void processServerMessage() {
		// If new client, connect to it
		// If client drop, disconnect from it
	}
	
	// Called whenever we get a message from a peer
	void processPeerMessage() {
		// Print message
	}
	
	void sendMessageToClients(String message) {
		// Send message to all clients
	}

	public static void main(String args[]) {
		try {
			Client c = new Client();
			c.sendMessageToClients("Hello World!");
		} catch (IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
}