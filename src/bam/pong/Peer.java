package bam.pong;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Peer {
	
	private SocketChannel channel;
	
	public Peer(SocketChannel channel) {
		this.channel = channel;
	}
	
	public void processMessage() throws IOException {
		ByteBuffer message = ByteBuffer.allocate(100);
		channel.read(message);
//		System.out.println(encoding.decode(message));
	}
	
}
