package bam.pong;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/** A variety of functions to make reading from a channel easier. */
public class ChannelHelper {
	/** UTF-8 encoder/decoder */
	public static final Charset utf8 = Charset.forName("UTF-8");

	/** Reads a certain number of bytes from the stream.
	 * 
	 * Makes 10 attempts to fill the buffer before throwing an exception.
	 * 
	 * @param c Channel to read from
	 * @param length Number of bytes to read
	 * @return A full byte buffer of the given length
	 * @throws IOException if the socket closes early, can't get enough to read, or has another read issue
	 */
	public static ByteBuffer readBytes(SocketChannel c, int length) throws IOException {
		ByteBuffer b = ByteBuffer.allocateDirect(length);
		int retries = 10;
		while ( b.remaining() > 0 && retries > 0 ) {
			int read = c.read(b);
			if ( read < 0 ) {
				c.close();
				throw new IOException("Socket closed early.");
			}
			retries--;
		}
		if ( retries <= 0 )
			throw new IOException("Not enough data to read");
		b.flip();
		return b;
	}
	
	/** Reads a single byte from a channel */
	public static byte getByte(SocketChannel c) throws IOException {
		ByteBuffer b = readBytes(c, 1);
		return b.get();
	}

	/** Reads a single short from a channel. */
	public static short getShort(SocketChannel c) throws IOException {
		ByteBuffer b = readBytes(c, 2);
		return b.getShort();
	}

	/** Reads a single int from a channel */
	public static int getInt(SocketChannel c) throws IOException {
		ByteBuffer b = readBytes(c, 4);
		return b.getInt();
	}
	
	/** Reads a string from a channel.
	 * 
	 * Strings are read from the channel as a short indicating their length
	 * and then the string contents UTF-8 encoded.
	 * 
	 * @param c Channel to read from
	 * @return A string read from the channel.
	 * @throws IOException If the string could not be read
	 */
	public static String getString(SocketChannel c) throws IOException {
		short len = getShort(c);
		ByteBuffer b = readBytes(c, len);
		return utf8.decode(b).toString();
	}
	
	/** Sends the entirety of a buffer
	 * 
	 * Loops until the entire buffer was written.  Make sure that the buffer
	 * has contents to send.  You should use c.flip() before calling this function.
	 * 
	 * @param c Channel to send to
	 * @param b Buffer to send
	 * @throws IOException
	 */
	public static void sendAll(SocketChannel c, ByteBuffer b) throws IOException {
		while ( b.remaining() > 0 ) {
			int sent = c.write(b);
			if ( sent < 0 )
				throw new IOException("Socket closed early.");
		}
	}
	
	/** Adds a string to a buffer.
	 * 
	 * Encodes the string in the format that getString expects.
	 * 
	 * @return false if the string could not fit in the buffer.
	 */
	public static boolean putString(ByteBuffer b, String s) {
		ByteBuffer e = utf8.encode(s);
		if (b.remaining() < e.limit() + 2)
			return false;
		b.putShort((short) e.limit());
		b.put(e);
		return true;
	}
	
	/** Adds an encoded string to a buffer.
	 * 
	 * Encodes the string in the format that getString expects.
	 * This is suitable for sending buffers encoded by utf8.encode.
	 * 
	 * @return false if the string could not fit in the buffer.
	 */
	public static boolean putString(ByteBuffer b, ByteBuffer s) {
		if (b.remaining() < s.limit() + 2)
			return false;
		b.putShort((short) s.limit());
		b.put(s);
		return true;
	}
	
	/** Send a single byte to a channel. */
	public static void putByte(SocketChannel c, byte b) throws IOException {
		ByteBuffer msg = ByteBuffer.allocate(1);
		msg.put(b);
		msg.flip();
		sendAll(c, msg);
	}
	
	/** Send a byte and a boolean to a channel.
	 * 
	 * Generally used for a message type and a reply value.
	 */
	public static void sendBoolean(SocketChannel c, byte k, boolean r) throws IOException {
		ByteBuffer b = ByteBuffer.allocateDirect(2);
		b.put(k);
		b.put((byte) (r ? 1 : 0));
		b.flip();
		
		sendAll(c, b);
	}
}
