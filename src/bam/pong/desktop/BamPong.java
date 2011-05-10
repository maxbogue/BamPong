package bam.pong.desktop;

import java.awt.Dimension;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import bam.pong.Ball;
import bam.pong.Client;
import bam.pong.Engine;
import bam.pong.Paddle;
import bam.pong.PeerCommunication;
import bam.pong.ServerCommunication;

public class BamPong {

	/** Diameter of the balls in pixels. */
	public static final int D = 15;
	
	public static void main(String[] args) {
		
		// Process command line arguments
		if(args.length < 2 || args.length > 3) {
			System.out.println("java bam.pong.desktop.BamPong <nickname> <server IP> [port]");
			System.exit(1);
		}
		
		int port = 1234;
		if( args.length == 3 ) {
			port = Integer.parseInt(args[2]);
		}
		
		InetAddress serverAddr = null;
		try {
			serverAddr = InetAddress.getByName(args[1]);
		} catch (UnknownHostException e1) {
			System.err.println("Couldn't find server " + args[1]);
			System.exit(1);
		}
		
		String nick = args[0];
		
		// Start peer communications
		PeerCommunication peerComm = null;
		try {
			peerComm = new PeerCommunication(nick);
		} catch (IOException e1) {
			System.err.println("Error opening incoming port");
			e1.printStackTrace();
			System.exit(1);
		}
		
		// Connect to server
		ServerCommunication serverComm = null;
		try {
			serverComm = new ServerCommunication(nick, peerComm.getPort(), serverAddr, port);
		} catch (IOException e1) {
			System.err.println("Error connecting to server");
			e1.printStackTrace();
			System.exit(1);
		}
		peerComm.setId(serverComm.getId());

		Client c = new Client(300, 200, 80, 15, serverComm, peerComm);
		
		// TODO
	}
	
}
