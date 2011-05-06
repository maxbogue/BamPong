package bam.pong;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import bam.pong.server.Server;

public class ServerCommunication {
	private SocketChannel server;     // socket to the server
	private Peer me;                  // Our local peer information
	private Selector selector;        // wait on this for server messages
	private Queue<ByteBuffer> outbox; // messages to send
	
	private static final Charset utf8 = Charset.forName("UTF-8");
	
	/** Connect to a server at a given address */
	public ServerCommunication(Peer myself, InetSocketAddress host)
	throws IOException {
		me = myself;
		if (me.getSocketAddr() == null)
			throw new IllegalArgumentException("Need a local peer port to connect to server");

		server = SocketChannel.open(host);
		
		// Do handshaking
		server.write(utf8.encode("bam?"));
		ByteBuffer recognition = ChannelHelper.readBytes(server, 4);
		if(!utf8.decode(recognition).toString().equals("BAM!")) {
			server.close();
			throw new IllegalArgumentException("given host wasn't a BAMPong server");
		}
		
		// Send my information to server
		ByteBuffer name = utf8.encode(me.getName());
		ByteBuffer msg  = ByteBuffer.allocateDirect(name.limit() + 6); // string + size(2) + port(4)
		ChannelHelper.putString(msg, name);
		msg.putInt(me.getSocketAddr().getPort());
		msg.flip();
		ChannelHelper.sendAll(server, msg);
		
		// Prepare background communications
		outbox = new ConcurrentLinkedQueue<ByteBuffer>();
		selector = Selector.open();
		server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_READ);
		
		// Start communications thread.
		watcher.start();
	}
	
	/** Connect to a server at a given host and port */
	public ServerCommunication(Peer me, InetAddress host, int port) throws IOException {
		this(me, new InetSocketAddress(host, port));
	}

	
	// Queue a message to be sent by the communication thread
	private void sendMessage(ByteBuffer message) {
		outbox.add(message);
		selector.wakeup();
	}
	
	// Queue a one byte message
	private void sendMessage(byte message_type) {
		ByteBuffer b = ByteBuffer.allocate(1);
		b.put(message_type);
		b.flip();
		sendMessage(b);
	}
	
	private Thread watcher = new Thread() {
		public void run() {
			while (server.isOpen()) {
				try {
					int ready = selector.select(10000);
					
					if ( ready > 0 ) {
						selector.selectedKeys().clear(); // Handling message
						handleMessage();
					} else {
						// Got woke up for reading, so send all messages
						while(!outbox.isEmpty()) {
							ByteBuffer b = outbox.remove();
							ChannelHelper.sendAll(server, b);
						}
					}
				} catch (IOException e) {
					// TODO Something better?
					e.printStackTrace();
				}
			}
		}
	};
	
	private static final byte LIST_GAMES = 1;
	private static final byte CREATE_GAME = 2;
	private static final byte CANCEL_GAME = 3;
	private static final byte JOIN_GAME = 4;
	private static final byte START_GAME = 5;
	
	private static List<String> games = new ArrayList<String>(); // For LIST_GAMES
	private static byte create[] = new byte[1]; // for CREATE_GAME
	
	private void wakeUp(Object o) {
		synchronized (o) {
			o.notify();
		}
	}
	
	private void handleMessage() throws IOException {
		byte type = ChannelHelper.getByte(server);
//		ByteBuffer b;
		
		switch(type) {
		case LIST_GAMES:
			int len = ChannelHelper.getInt(server);
			games.clear();

			for(; len > 0; len--) {
				String name = ChannelHelper.getString(server);
				games.add(name);
			}
			wakeUp(games); // Wake-up whoever asked for the games
			break;
		case CREATE_GAME:
			create[0] = ChannelHelper.getByte(server);
			wakeUp(create);
			break;
		case CANCEL_GAME:
		case JOIN_GAME:
		case START_GAME:
		}
	}
	
	private void waitOn(Object o) {
		synchronized (o) {
			try {
				o.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Ask the server for the game list */
	public String[] getGames() {
		sendMessage(LIST_GAMES);
		waitOn(games);
		return games.toArray(new String[games.size()]);
	}
	
	/** Ask the server to create a game */
	public boolean createGame(String name) {
		// Construct message
		ByteBuffer e = utf8.encode(name);
		ByteBuffer b = ByteBuffer.allocateDirect(e.limit() + 3); // type(1) + size(2) + string
		b.put(CREATE_GAME);
		ChannelHelper.putString(b, e);
		b.flip();
		sendMessage(b);
		
		// Wait for result
		waitOn(create);
		
		return create[0] != 0;
	}
	
	public static void main(String args[]) {
		final Server server;
		try {
			server = new Server();
			// Get me a server.
			Thread thread = new Thread() {
				public void run() {
					try {
						server.run();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			thread.start();
			System.out.println("Server started...");

			Peer me = new Peer(1, "p1", InetAddress.getLocalHost(), 1);
			ServerCommunication sc = new ServerCommunication(me,
					InetAddress.getLocalHost(), server.getPort());
			System.out.println("Communicator started...");
			
			sc.createGame("test");
			for (String s : sc.getGames()) {
				System.out.println(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
