package bam.pong;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ChannelHelper {
	public static final Charset utf8 = Charset.forName("UTF-8");

	public static ByteBuffer readBytes(SocketChannel c, int length) throws IOException {
		ByteBuffer b = ByteBuffer.allocateDirect(length);
		int total = 0;
		while( total < length ) {
			int read = c.read(b);
			if( read < 0 ) {
				if( !c.isOpen() )
					throw new IOException("Socket closed early.");
				read = 0;
			}
			total += read;
		}
		return b;
	}
	
	public static byte getByte(SocketChannel c) throws IOException {
		ByteBuffer b = readBytes(c, 1);
		return b.get();
	}

	public static short getShort(SocketChannel c) throws IOException {
		ByteBuffer b = readBytes(c, 2);
		return b.getShort();
	}

	public static int getInt(SocketChannel c) throws IOException {
		ByteBuffer b = readBytes(c, 4);
		return b.getInt();
	}
	
	public static String getString(SocketChannel c) throws IOException {
		short len = getShort(c);
		ByteBuffer b = readBytes(c, len);
		return utf8.decode(b).toString();
	}
}
