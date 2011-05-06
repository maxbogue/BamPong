package bam.pong.server;

import java.io.IOException;
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

import bam.pong.ChannelHelper;

/**
 * Server class.  Handles all game setup for clients.
 * 
 * @author Max Bogue
 */
public class Server {

	private static final byte LIST_GAMES = 1;
	private static final byte CREATE_GAME = 2;
	private static final byte CANCEL_GAME = 3;
	private static final byte JOIN_GAME = 4;
	private static final byte START_GAME = 5;
	
	/** For new incoming channels. */
	private ServerSocketChannel incoming;
	
	/** Existing open channels to clients. */
	private Map<SocketChannel, Client> clients = new HashMap<SocketChannel, Client>();
	
	/** Existing games. */
	private Map<String, Game> games = new HashMap<String, Game>();
	
	/** For string encoding. */
	private Charset utf8 = Charset.forName("UTF-8");
	
	public Server() throws IOException {
		incoming = ServerSocketChannel.open();
		incoming.socket().bind(null);
	}
	
	public void run() throws IOException {
		Set<SocketChannel> handShaken = new HashSet<SocketChannel>();
		Selector selector = Selector.open();
		incoming.configureBlocking(false);
		incoming.register( selector, SelectionKey.OP_ACCEPT );
		while ( incoming.isOpen() || !clients.isEmpty() || !handShaken.isEmpty() ) {
			try {
				// wait on selector
				selector.select(10000);
				
				// Handle all ready channels
				Set<SelectionKey> selected = selector.selectedKeys();
				for (SelectionKey k : selected) {
					selected.remove(k);
					Channel c = k.channel();
					
					if ( c == incoming ) {
						SocketChannel sc = incoming.accept();
						ByteBuffer bb = ChannelHelper.readBytes(sc, 4);
						if (utf8.decode(bb).toString().equals("bam?")) {
							sc.write(utf8.encode("BAM!"));
							handShaken.add(sc);
						}
					} else if ( handShaken.contains(c) ) {
						SocketChannel sc = (SocketChannel) c;
						clients.put(sc, makeClient(sc));
					} else if ( clients.containsKey(c) ) {
						processClientMessage((SocketChannel) c);
					} else {
						System.err.println( "Tried to process unknown socket." );
					}
				}
				
				// Check for closed sockets
				for (SocketChannel socket : clients.keySet()) {
					if (!socket.isOpen()) {
						clients.remove(socket);
						// TODO: Reconnect, propose drop
					}
				}
				for (SocketChannel socket : handShaken) {
					if (!socket.isOpen()) {
						handShaken.remove(socket);
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

	public void processClientMessage(SocketChannel c) throws IOException {
		byte k = ChannelHelper.getByte(c);
		ByteBuffer b;
		switch (k) {
		case LIST_GAMES:
			b = ByteBuffer.allocateDirect(1024);
			b.put(LIST_GAMES);
			b.putInt(games.size());
			for (Game g : games.values()) {
				if (!ChannelHelper.putString(b,g.name)) {
					// Filled buffer, send what we have.
					b.flip();
					ChannelHelper.sendAll(c, b);
					b.clear();

					// Check to see if the name is too big for the buffer
					ByteBuffer e = utf8.encode(g.name);
					if( b.remaining() < e.limit() + 2)  // If so, reallocate
						b = ByteBuffer.allocateDirect(e.limit() + 2);
					
					// Put the string into the new buffer.  (must fit)
					ChannelHelper.putString(b, e);
				}
			}
			b.flip();
			ChannelHelper.sendAll(c, b);
			break;
		case CREATE_GAME:
			b = ByteBuffer.allocateDirect(2);
			b.put(CREATE_GAME);
			String name = ChannelHelper.getString(c);
			if (games.containsKey(name)) {
				b.put((byte) 0); // False = NAK
			} else {
				games.put(name, new Game(name, clients.get(c)));
				b.put((byte) 1); // OK
			}
			b.flip();
			ChannelHelper.sendAll(c, b);
			break;
		case CANCEL_GAME:
			break;
		case JOIN_GAME:
			break;
		case START_GAME:
			break;
		default:
		}
	}
	
	public Client makeClient(SocketChannel c) throws IOException {
		String name = ChannelHelper.getString(c);
		int port = ChannelHelper.getInt(c);
		return new Client(name, port, c);
	}
	
}
