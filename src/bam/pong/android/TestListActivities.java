package bam.pong.android;



import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import bam.pong.BamException;
import bam.pong.Client;
import bam.pong.Paddle.Movement;
import bam.pong.PeerCommunication;
import bam.pong.ServerCommunication;
import bam.pong.ServerListener;
import bam.pong.ViewListener;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
 
public class TestListActivities extends ListActivity implements ServerListener{
    
	int port=1234;
	Client c;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics display = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(display);

    	// Width and height of display in pixels.
    	int w = display.widthPixels;
    	int h = display.heightPixels;
    	
    	int pw = 80; // Paddle width.
    	int ph = 25; // Paddle height.
        Bundle extras = getIntent().getExtras();
    	String listItems[]=extras.getStringArray("games");
    	 String nick=extras.getString("nick");
    	 String serverAddr=extras.getString("serverAddr");
    	 InetAddress addr = null;
    		try {
    			addr = InetAddress.getByName(serverAddr);
    		} catch (UnknownHostException e1) {
    			System.err.println("Couldn't find server " + serverAddr);
    			System.exit(1);
    		}
    		
    		
    		
    		// Start peer communications
    		PeerCommunication peerComm = null;
    		try {
    			peerComm = new PeerCommunication(nick);
    		} catch (IOException e1) {
    			System.err.println("Error opening incoming port");
    			e1.printStackTrace();
    			System.exit(1);
    		}
    		
    		// Connect to server
    		ServerCommunication serverComm = null;
    		try {
    			serverComm = new ServerCommunication(nick, peerComm.getPort(), addr, port);
    		} catch (IOException e1) {
    			System.err.println("Error connecting to server");
    			e1.printStackTrace();
    			System.exit(1);
    		}
    		peerComm.setId(serverComm.getId());

    		  c = new Client(w, h, pw, ph, serverComm, peerComm);
    		c.serverComm.addListener(this);
    		
    	 
    	 
    	
        
        setListAdapter(new ArrayAdapter<String>(this, 
        R.layout.main2, listItems));
    }
    
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		Object o = this.getListAdapter().getItem(position);
		String game = o.toString();
		
		try {
			c.joinGame(game);
		} catch (BamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	public void newBall(int id, double pos, double dx, double dy, int d) {
		// TODO Auto-generated method stub
		
	}  
}