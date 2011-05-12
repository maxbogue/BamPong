package bam.pong.message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import bam.pong.Ball;
import bam.pong.ChannelHelper;
import bam.pong.PeerListener;

public class BallMessage extends Message {

	private Ball b;
	private double pos;

	public BallMessage(Ball b, double pos) {
		this.b = b;
		this.pos = pos;
	}

	public void sendMessage(SocketChannel sc) throws IOException {
		if (sc == null)
			throw new IllegalArgumentException("Not connected to peer");
		ByteBuffer msg = ByteBuffer.allocateDirect(33); // type(1), id(4), position(8), dx(8), dy(8)
		msg.put(M.BALL);
		msg.putInt(b.id);
		msg.putDouble(pos);
		msg.putDouble(b.dx);
		msg.putDouble(b.dy);
		msg.putInt(b.D);
		msg.flip();
		ChannelHelper.sendAll(sc, msg);
	}

	public static void receiveMessage(SocketChannel sc, PeerListener pl) throws IOException {
		if (pl == null) return;
		int id = ChannelHelper.getInt(sc);
		double position = ChannelHelper.getDouble(sc);
		double dx       = ChannelHelper.getDouble(sc);
		double dy       = ChannelHelper.getDouble(sc);
		int D			= ChannelHelper.getInt(sc);
		pl.receiveBall(id, position, dx, dy, D);
	}
}
