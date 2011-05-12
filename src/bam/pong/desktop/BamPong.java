package bam.pong.desktop;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import bam.pong.Client;
import bam.pong.PeerCommunication;
import bam.pong.ServerCommunication;

public class BamPong {

	/** Diameter of the balls in pixels. */
	public static final int D = 15;
	
	public static void main(String[] args) {
		
		int port = 1234;
		if( args.length == 3 ) {
			port = Integer.parseInt(args[2]);
		}
		
		InetAddress serverAddr = null;
		if (args.length > 1) {
			try {
				serverAddr = InetAddress.getByName(args[1]);
			} catch (UnknownHostException e1) {
				System.err.println("Couldn't find server " + args[1]);
				System.exit(1);
			}
		} else {
			String address = JOptionPane.showInputDialog(
					"Enter server address", "129.21.63.105");
			try {
				serverAddr = InetAddress.getByName(address);
			} catch (UnknownHostException e1) {
				JOptionPane.showMessageDialog(null,
						"Couldn't find server " + address);
				System.exit(1);
			}
		}
		
		String nick = null;
		if (args.length > 0) {
			nick = args[0];
		} else {
			nick = JOptionPane.showInputDialog("Enter Nickname:");
			if (nick == null) {
				System.exit(1);
			}
		}
		
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
		ConnectedFrame connFrame = new ConnectedFrame(c);
		connFrame.setVisible(true);
	}
	
}
