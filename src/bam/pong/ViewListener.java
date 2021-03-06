package bam.pong;

import java.util.List;

public interface ViewListener {

	public List<String> listGames() throws BamException;

	public void createGame(String gameName) throws BamException;

	public void joinGame(String gameName) throws BamException;

	public void cancelGame() throws BamException;

	public void startGame() throws BamException;

	public void movePaddleTo(int x);

	public void movePaddleIn(Paddle.Movement dir);

}
