package bam.pong.android;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import bam.pong.Client;
import bam.pong.PeerCommunication;
import bam.pong.ServerCommunication;
import bam.pong.ServerListener;


public class GUI extends Activity implements ServerListener{

	private void showError(String when, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
			.setTitle(when)
			.setCancelable(false)
			.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					GUI.this.finish();
				}
			})
			.show();
	}

	private void showError(String when, Exception why) {
		showError(when, why.getMessage());
	}

	int port=1234;
	ServerCommunication serverComm;
	PeerCommunication peerComm;
	Client client;
	
	@Override
	public void onStop() {
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);

		// Width and height of display in pixels.
		int w = display.widthPixels;
		int h = display.heightPixels;

		int pw = 80; // Paddle width.
		int ph = 25; // Paddle height.

		Bundle extras = getIntent().getExtras();
		String nick=extras.getString("nick");
		String serverAddr=extras.getString("serverAddr");
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(serverAddr);
		} catch (UnknownHostException e1) {
			showError("Server Lookup Failed", e1);
			return;
		}

		// Start peer communications
		peerComm = null;
		try { 
			peerComm = new PeerCommunication(nick);
		} catch (IOException e1) {
			showError("Peer Communication Failed", e1);
			return;
		}

		// Connect to server
		serverComm = null;
		try {
			serverComm = new ServerCommunication(nick, peerComm.getPort(), addr, port);
		} catch (IOException e1) {
			showError("Server Connection Failed", e1);
			return;
		}
		peerComm.setId(serverComm.getId());

		client = new Client(w, h, pw, ph, serverComm, peerComm);
		client.serverComm.addListener(this);



		final Button button1 = (Button) findViewById(R.id.widget27);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
			}
		});



		final Button button3 = (Button) findViewById(R.id.widget29);
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
			}
		});

		//Join Game
		final Button button4 = (Button) findViewById(R.id.widget30);
		button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/*List<String> games = null;

     		try {
     			games = c.listGames();
     		} catch (BamException e) {
     			//showError(e);
     		}
     		String[] array = (String[])games.toArray();

        	 Intent myIntent = new Intent(v.getContext(), TestListActivities.class);
        	 myIntent.putExtra("games",array);
        	 myIntent.putExtra("nick",nick);
        	 myIntent.putExtra("serverAddr",serverAddr);
        	 startActivity(myIntent);  */
			}
		});


		//Start Game
		final Button button5 = (Button) findViewById(R.id.widget31);
		button5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), BamPong.class);
				startActivity(myIntent);

				// Perform action on click

			}
		});




	}

	@Override
	public void gameStarted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameCanceled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void newBall(int id) {
		// TODO Auto-generated method stub

	}
	
	
}
