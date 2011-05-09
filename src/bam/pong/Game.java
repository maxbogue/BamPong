package bam.pong;

import java.util.HashMap;
import java.util.Map;

public class Game {
	Map<Integer, Peer> peers = new HashMap<Integer, Peer>();
	Map<Integer, Ball> balls = new HashMap<Integer, Ball>();
}
