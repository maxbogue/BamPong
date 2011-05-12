package bam.pong.message;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public abstract class Message {

	private static int nextID = 1;

	public final int id;

	public Message() {
		this.id = nextID++;
	}

	public abstract void sendMessage(SocketChannel sc) throws IOException;

	public boolean equals(Object other) {
		if (other instanceof Message) {
			Message that = (Message) other;
			return this.id == that.id;
		} else {
			return false;
		}
	}
}
