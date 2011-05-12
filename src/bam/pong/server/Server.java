package bam.pong.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bam.pong.ChannelHelper;
import bam.pong.Constants;

/**
 * Server class.  Handles all game setup for clients.
 * 
 * @author Max Bogue
 */
public class Server {

	/** For new incoming channels. */
	private ServerSocketChannel incoming;
	
	/** Existing open channels to clients. */
	private Map<SocketChannel, Client> clients = new HashMap<SocketChannel, Client>();
	
	/** Client IDs to client info */
	private Map<Integer, Client> ids2clients = new HashMap<Integer, Client>();
	
	/** Maximum ID handed out */
	private int maxID = 0;
	
	/** Existing games. */
	private Map<String, Game> games = new HashMap<String, Game>();
	
	private Set<Game> newGames = new HashSet<Game>();
	
	/** For string encoding. */
	private Charset utf8 = Charset.forName("UTF-8");
	
	private Selector selector;
	
	public Server() throws IOException {
		incoming = ServerSocketChannel.open();
		incoming.socket().bind(null);
	}
	
	public Server(int port) throws IOException {
		incoming = ServerSocketChannel.open();
		incoming.socket().bind(new InetSocketAddress(port));
	}
	
	public int getPort() {
		return incoming.socket().getLocalPort();
	}
	
	private void log(String msg) {
		System.out.println(msg);
	}
	
	public void run() {
		Set<SocketChannel> handShaken  = new HashSet<SocketChannel>();
		Set<SocketChannel> new_sockets = new HashSet<SocketChannel>();

		try {
			selector = Selector.open();
			incoming.configureBlocking(false);
			incoming.register( selector, SelectionKey.OP_ACCEPT );
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		log("Ready for connections on port "+incoming.socket().getLocalPort());
		while ( incoming.isOpen() || !clients.isEmpty() ) {
			try {
				// wait on selector
				selector.select(10000);
				
				// Handle all ready channels
				Set<SelectionKey> selected = selector.selectedKeys();
				for (SelectionKey k : selected) {
					selected.remove(k);
					Channel c = k.channel();
					
					if ( c == incoming ) {
						log("New connection.");
						SocketChannel sc = incoming.accept();
						new_sockets.add(sc);
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);
					} else if ( new_sockets.contains(c) ) {
						SocketChannel sc = (SocketChannel) c;
						new_sockets.remove(sc);
						ByteBuffer bb = ChannelHelper.readBytes(sc, 4);
						if (utf8.decode(bb).toString().equals("bam?")) {
							log("Handshaking...");
							sc.write(utf8.encode("BAM!"));
							handShaken.add(sc);
						} else {
							log("No hand to shake");
							sc.close();
						}
					} else if ( handShaken.contains(c) ) {
						SocketChannel sc = (SocketChannel) c;
						Client client = makeClient(sc);
						clients.put(sc, client);
						ids2clients.put(client.getId(), client);
						handShaken.remove(c);
					} else if ( clients.containsKey(c) ) {
						processClientMessage((SocketChannel) c);
					} else {
						System.err.println( "Tried to process unknown socket." );
						k.cancel();
						c.close();
					}
				}
			} catch (IOException e) {
				// TODO Handle this better.  In mean time, just keep going.
				e.printStackTrace();
			}

			// Check for closed clients
			List<String> dead_games = new ArrayList<String>();
			for (SocketChannel socket : clients.keySet().toArray(new SocketChannel[0])) {
				if (!socket.isOpen()) {
					Client client = clients.remove(socket);
					ids2clients.remove(client.getId());
					log("Player "+client.getName()+" dropped");
					for (Game game : games.values()) {
						if(client.equals(game.getOwner())) {
							log(" Cancelling their game");
							if(!game.removePlayer(client))
								game.cancel(); // Owner dropping = cancel game
							dead_games.add(game.getName());
						} else if(game.removePlayer(client)) {
							log(" Removing empty game");
							dead_games.add(game.getName());
						}
					}
				}
			}
			for( String name : dead_games ) {
				Game game = games.remove(name);
				newGames.remove(game);
			}
			
			// Check for other closed sockets
			for (SocketChannel socket : handShaken)
				if (!socket.isOpen())
					handShaken.remove(socket);
			for (SocketChannel socket : new_sockets)
				if (!socket.isOpen())
					handShaken.remove(socket);
		}
		
		// Close all new sockets
		new_sockets.addAll(handShaken);
		for (SocketChannel socket : new_sockets) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Close the selector
		try {
			selector.close();
		} catch (IOException e) {
			// Not much to do.
			System.err.println(e);
			e.printStackTrace();
		}
	}
	
	/** Close ports so the server loop stops */
	public void shutdown() {
		try {
			incoming.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for( SocketChannel c : clients.keySet() ) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ( selector != null )
			selector.wakeup();
	}
	
	public void processClientMessage(SocketChannel c) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(1);
		int read = c.read(b);
		if( read < 0 ) {
			c.close(); // Socket was closed
			return;
		}
		if( read < 1 )
			return; // no data
		b.flip();
		byte k = b.get();
		String name;
		
		switch (k) {
		case Constants.LIST_GAMES:
			log("Listing "+newGames.size()+" game(s)");
			b = ByteBuffer.allocateDirect(1024);
			b.put(Constants.LIST_GAMES);
			b.putInt(newGames.size());
			for (Game g : newGames) {
				if (!ChannelHelper.putString(b,g.getName())) {
					// Filled buffer, send what we have.
					b.flip();
					ChannelHelper.sendAll(c, b);
					b.clear();

					// Check to see if the name is too big for the buffer
					ByteBuffer e = utf8.encode(g.getName());
					if( b.remaining() < e.limit() + 2)  // If so, reallocate
						b = ByteBuffer.allocateDirect(e.limit() + 2);
					
					// Put the string into the new buffer.  (must fit)
					ChannelHelper.putString(b, e);
				}
			}
			b.flip();
			ChannelHelper.sendAll(c, b);
			break;
		case Constants.CREATE_GAME:
			name = ChannelHelper.getString(c);
			if (games.containsKey(name)) {
				log("Refused duplicate game "+name);
				ChannelHelper.sendBoolean(c, k, false);
			} else {
				log("Creating game "+name);
				Game g = new Game(name, clients.get(c));
				games.put(name, g);
				newGames.add(g);
				ChannelHelper.sendBoolean(c, k, true);
			}
			break;
		case Constants.CANCEL_GAME:
			name = ChannelHelper.getString(c);
			if (games.containsKey(name) && !games.get(name).hasBegun()) {
				log("Cancelled game "+name);
				ChannelHelper.sendBoolean(c, k, true);
				newGames.remove(games.get(name));
				games.remove(name).cancel();
			} else {
				log("Refused to cancel game "+name);
				ChannelHelper.sendBoolean(c, k, false);
			}
			break;
		case Constants.JOIN_GAME:
			name = ChannelHelper.getString(c);
			if (games.containsKey(name)) {
				b = ByteBuffer.allocateDirect(1024);
				b.put(Constants.JOIN_GAME);
				b.put((byte) 1);
				
				Game game = games.get(name);
				List<Client> peers = game.getPlayers();
				
				b.putInt(peers.size());
				for( Client client : peers ) {
					// ID(4), IP(4), Port(4) = 12
					if (b.remaining() < 12) {
						b.flip();
						ChannelHelper.sendAll(c, b);
						b.clear();
					}
					b.putInt(client.getId());
					b.put(client.getChannel().socket().getInetAddress().getAddress());
					b.putInt(client.getPort());
					
					if( !ChannelHelper.putString(b, client.getName())) {
						b.flip();
						ChannelHelper.sendAll(c, b);
						b.clear();
						
						// Check to see if the name is too big for the buffer
						ByteBuffer e = utf8.encode(client.getName());
						if( b.remaining() < e.limit() + 2)  // If so, reallocate
							b = ByteBuffer.allocateDirect(e.limit() + 2);
						
						// Put the string into the new buffer.  (must fit)
						ChannelHelper.putString(b, e);
					}
				}
				b.flip();
				ChannelHelper.sendAll(c, b);

				log("Adding player to "+name);
				game.addPlayer(clients.get(c));
			} else {
				log("Coudln't find game "+name+" to join");
				ChannelHelper.sendBoolean(c, k, false);
			}
			break;
		case Constants.START_GAME:
			name = ChannelHelper.getString(c);
			if(games.containsKey(name)) {
				log("Starting game "+name);
				games.get(name).startGame();
			}
			break;
		case Constants.BALL_DROPPED:
			ChannelHelper.getInt(c); // Ball id that was dropped...
			Client client = clients.get(c);
			client.upScore();
			client.getGame().sendNewBall();
			break;
		default:
			System.err.println("Invalid key byte: " + k);
			break;
		}
	}
	
	public Client makeClient(SocketChannel c) throws IOException {
		String name = ChannelHelper.getString(c);
		int port = ChannelHelper.getInt(c);
		int id = ++maxID;
		ChannelHelper.putInt(c, id);
		log("New Client: "+name);
		return new Client(++maxID, name, port, c);
	}
	
	public static void main(String args[]) {
		int port = 0;
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("Port must be a number.");
				System.exit(1);
			}
		} else {
			port = 1234;
		}
		try {
			Server server = new Server(port);
			server.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
