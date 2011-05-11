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
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import bam.pong.server.Server;

public class ServerCommunication {
	private SocketChannel server;     // socket to the server
//	private Peer me;                  // Our local peer information
	private Selector selector;        // wait on this for server messages
	private Queue<ByteBuffer> outbox; // messages to send
	private int id;                   // ID from server
	
	// Things to notify on async game events
	private List<ServerListener> listeners = new ArrayList<ServerListener>();
	
	private static final Charset utf8 = Charset.forName("UTF-8");
	
	/** Connect to a server at a given address */
	public ServerCommunication(String nickname, int incomingPort, InetSocketAddress host)
	throws IOException {

		server = SocketChannel.open(host);
		
		// Do handshaking
		server.write(utf8.encode("bam?"));
		ByteBuffer recognition = ChannelHelper.readBytes(server, 4);
		if(!utf8.decode(recognition).toString().equals("BAM!")) {
			server.close();
			throw new IllegalArgumentException("given host wasn't a BAMPong server");
		}
		
		// Send my information to server
		ByteBuffer name = utf8.encode(nickname);
		ByteBuffer msg  = ByteBuffer.allocateDirect(name.limit() + 6); // string + size(2) + port(4)
		ChannelHelper.putString(msg, name);
		msg.putInt(incomingPort);
		msg.flip();
		ChannelHelper.sendAll(server, msg);
		
		// Read peer ID
		id = ChannelHelper.getInt(server);
		
		// Prepare background communications
		outbox = new ConcurrentLinkedQueue<ByteBuffer>();
		selector = Selector.open();
		server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_READ);
		
		// Start communications thread.
		watcher.start();
	}
	
	public void addListener(ServerListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ServerListener listener) {
		listeners.remove(listener);
	}
	
	/** Returns the ID the server gave us. */
	public int getId() {
		return id;
	}
	
	/** Connect to a server at a given host and port */
	public ServerCommunication(String nickname, int incomingPort, InetAddress host, int port) throws IOException {
		this(nickname, incomingPort, new InetSocketAddress(host, port));
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
	
	// Queue a string message
	private void sendMessage(byte type, String contents) {
		ByteBuffer e = utf8.encode(contents);
		ByteBuffer b = ByteBuffer.allocate(e.limit()+3);
		b.put(type);
		ChannelHelper.putString(b, e);
		b.flip();
		sendMessage(b);
	}
	
	private Thread watcher = new Thread("Server Communication") {
		public void run() {
			while (server.isOpen()) {
				try {
					selector.select(10000);
					
					Set<SelectionKey> selected = selector.selectedKeys();
					for ( SelectionKey k : selected ) {
						selected.remove(k);
						
						if(k.isReadable())
							handleMessage();
					}

					while(!outbox.isEmpty()) {
						ByteBuffer b = outbox.remove();
						ChannelHelper.sendAll(server, b);
					}
				} catch (IOException e) {
					// TODO Something better?
					e.printStackTrace();
				}
			}
		}
	};
	
	private static List<String> games = new ArrayList<String>(); // LIST_GAMES
	private static byte create[] = new byte[1];                  // CREATE_GAME
	private static Game join[]   = new Game[1];                  // JOIN_GAME
	
	private void wakeUp(Object o) {
		synchronized (o) {
			o.notify();
		}
	}
	
	private void handleMessage() throws IOException {
		byte type = ChannelHelper.getByte(server);
//		ByteBuffer b;
		
		switch(type) {
		case Constants.LIST_GAMES:
			int len = ChannelHelper.getInt(server);
			games.clear();

			for(; len > 0; len--) {
				String name = ChannelHelper.getString(server);
				games.add(name);
			}
			wakeUp(games); // Wake-up whoever asked for the games
			break;
		case Constants.CREATE_GAME:
			create[0] = ChannelHelper.getByte(server);
			wakeUp(create);
			break;
		case Constants.JOIN_GAME:
			boolean success = ChannelHelper.getByte(server) != 0;
			
			if ( success ) {
				Game game = new Game();
				int num = ChannelHelper.getInt(server);
				while(num-- > 0) {
					int id = ChannelHelper.getInt(server);
					ByteBuffer ip = ChannelHelper.readBytes(server, 4);
					int port = ChannelHelper.getInt(server);
					String name = ChannelHelper.getString(server);
					
					InetAddress addr = InetAddress.getByAddress(ip.array());
					Peer peer = new Peer(id, name, addr, port);
					game.peers.put(id, peer);
				}
				join[0] = game;
			} else {
				join[0] = null;
			}

			wakeUp(join);
			break;
		case Constants.START_GAME:
			for ( ServerListener listener : listeners )
				listener.gameStarted();
			break;
		case Constants.GAME_CANCELED:
			for ( ServerListener listener : listeners )
				listener.gameCanceled();
		default:
			System.err.println("Unkown message type "+type);
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
		sendMessage(Constants.LIST_GAMES);
		waitOn(games);
		return games.toArray(new String[games.size()]);
	}
	
	/** Ask the server to create a game */
	public boolean createGame(String name) throws IOException {
		sendMessage(Constants.CREATE_GAME, name);
		
		// Wait for result
		waitOn(create);
		
		return create[0] != 0;
	}
	
	/** Ask server to cancel a game
	 *
	 * Asynchronous.  Notifies listener if current game is cancelled.
	 */
	public void cancelGame(String name) throws IOException {
		sendMessage(Constants.CANCEL_GAME, name);
	}
	
	/** Ask server to join a game */
	public Game joinGame(String name) throws IOException {
		sendMessage(Constants.JOIN_GAME, name);
		
		waitOn(join);
		
		join[0].name = name;
		return join[0];
	}
	
	/** Ask server to start a game.
	 * 
	 * Asynchronous.  Notifies listener when game starts.
	 */
	public void startGame(String name) throws IOException {
		sendMessage(Constants.START_GAME, name);
	}
	
	public static void main(String args[]) {
		final Server server;
		try {
			server = new Server();
			// Get me a server.
			Thread thread = new Thread() {
				public void run() {
					server.run();
				}
			};
			thread.start();
			System.out.println("Server started...");

			ServerCommunication sc = new ServerCommunication("p1", 1,
					InetAddress.getLocalHost(), server.getPort());
			System.out.println("Communicator started...");
			
			sc.createGame("test");
			for (String s : sc.getGames()) {
				System.out.println(s);
			}
			
			server.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
