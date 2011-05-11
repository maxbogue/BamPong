package bam.pong.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import bam.pong.BamException;
import bam.pong.Client;
import bam.pong.ServerListener;

@SuppressWarnings("serial")
public class ConnectedFrame extends JFrame implements ServerListener {
	private Client client;
	private JButton create = new JButton("Create a game");
	private JButton join   = new JButton("Join a game");
	private JButton cancel = new JButton("Cancel the game");
	private JButton start  = new JButton("Start the game");
	private BamPongView bpv;
	
	public ConnectedFrame(Client c) {
		super("BAM! Pong");
		
		client = c;
		client.serverComm.addListener(this);
		
		GameField gf = new GameField();
		client.engine.addListener(gf);
		bpv = new BamPongView(gf);
		
		// Setup UI
		JPanel content = new JPanel();
		setContentPane(content);
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		content.add(create);
		content.add(join);
		content.add(cancel);
		cancel.setEnabled(false);
		content.add(start);
		start.setEnabled(false);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Setup callbacks
		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createGame();
			}
		});
		join.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				joinGame();
			}
		});
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelGame();
			}
		});
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});
	}
	
	private void showError(BamException e) {
		JOptionPane.showMessageDialog(this, e.getMessage());
	}
	
	private void createGame() {
		String name = JOptionPane.showInputDialog(this,
				"Enter name for game");
		
		try {
			client.createGame(name);
			create.setEnabled(false);
			join.setEnabled(false);
			start.setEnabled(true);
			cancel.setEnabled(true);
		} catch (BamException e) {
			showError(e);
		}
	}

	private void joinGame() {
		List<String> games = null;
		
		try {
			games = client.listGames();
		} catch (BamException e) {
			showError(e);
		}
		
		if (games.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"No games available.  Try creating one.");
			return;
		}
		
		String game = (String) JOptionPane.showInputDialog(this,
				"Choose a game to join", "Join Game",
				JOptionPane.QUESTION_MESSAGE, null,
				games.toArray(), games.get(0));
		
		if (game == null)
			return; // User canceled
		
		try {
			client.joinGame(game);
			create.setEnabled(false);
			join.setEnabled(false);
		} catch (BamException e) {
			showError(e);
		}
	}

	private void cancelGame() {
		try {
			client.cancelGame();
		} catch (BamException e) {
			showError(e);
		}
	}

	private void startGame() {
		try {
			client.startGame();
			join.setEnabled(false);
			start.setEnabled(false);
		} catch (BamException e) {
			showError(e);
		}
	}

	@Override
	public void gameStarted() {
		client.serverComm.removeListener(this);
		bpv.setVisible(true);
		this.setVisible(false);
	}

	@Override
	public void gameCanceled() {
		JOptionPane.showMessageDialog(this, "Game cancelled");
		join.setEnabled(true);
		create.setEnabled(true);
		cancel.setEnabled(false);
		start.setEnabled(false);
	}
}
