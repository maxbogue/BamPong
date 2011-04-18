package bam.pong;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

// Basic outline of client networking
public class Client {
	public final String SERVER_ADDRESS = "127.0.0.1";
	public final int    SERVER_PORT    = 1234;
	
	ServerSocket incoming;
	Socket server;
	List<Socket> peers;
	
	Client() {
		// create incoming socket
		// Send incoming socket port to server
		// setup callback to processServerMessage
		// Get list of other clients
		// Connect to other clients
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
		Client c = new Client();
		c.sendMessageToClients("Hello World!");
	}
}