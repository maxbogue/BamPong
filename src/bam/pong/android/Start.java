package bam.pong.android;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import bam.pong.*;


public class Start extends Activity{
	int port=1234;

	public void onCreate(Bundle savedInstanceState)
	{
	super.onCreate(savedInstanceState);
	setContentView(R.layout.start);
	
	//Connect Button
	final Button button = (Button) findViewById(R.id.Connect);
	 button.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
            
	EditText et = (EditText) findViewById(R.id.textA);
	EditText et2 = (EditText) findViewById(R.id.TextB);
	Editable n=et.getText();
	Editable n2=et2.getText();
	String nick=n.toString();
	String serverAddr=n2.toString();
	
	/*InetAddress addr = null;
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

	//Client c = new Client(w, h, pw, ph, serverComm, peerComm);*/
	
	
	
	
	
	Intent myIntent = new Intent(v.getContext(), GUI.class);
    startActivity(myIntent);
	
	
	}
	});
	
	
	
	
	}	
	
}
