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

		int w = 300;
		int h = 200;

		int pw = 80; // Paddle width.
		int ph = 15; // Paddle height.
		
		Paddle paddle = new Paddle(pw, ph, 300, w/2 - pw/2, h - ph - 5);
		
		// Ball size must be subtracted here so the balls look like they hit the wall.
		Engine e = new Engine(w, h, paddle);
		GameField f = new GameField(e.getBalls(), paddle);
		f.setPreferredSize(new Dimension(w, h));
		e.addListener(f);
		
		Client c = new Client(w, h, pw, ph, serverComm, peerComm);
		
		// TODO
	}
	
}
