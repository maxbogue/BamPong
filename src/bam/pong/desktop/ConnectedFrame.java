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

@SuppressWarnings("serial")
public class ConnectedFrame extends JFrame {
	private Client client;
	private JButton create = new JButton("Create a game");
	private JButton join   = new JButton("Join a game");
	private JButton cancel = new JButton("Cancel the game");
	private JButton start  = new JButton("Start the game");
	
	public ConnectedFrame(Client c) {
		super("BAM! Pong");
		
		client = c;
		
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
		
		// TODO: Display a list of games and ask the user to pick one
	}

	private void cancelGame() {
		try {
			client.cancelGame();
		} catch (BamException e) {
			showError(e);
		}
	}

	private void startGame() {
		// TODO: Start the game
	}
}
