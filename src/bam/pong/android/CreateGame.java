package bam.pong.android;

import bam.pong.BamException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateGame extends Activity{
	
	
	private  void showError(String when, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
			.setTitle(when)
			.setCancelable(false)
			.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					CreateGame.this.finish();
				}
			})
			.show();
	}

	private void showError(String when, Exception why) {
		showError(when, why.getMessage());
	}

	
	
	public void onCreate(Bundle savedInstanceState)
	{
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main3);
	
	
	 final Button button3 = (Button) findViewById(R.id.Create);
     button3.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
        	 EditText et = (EditText) findViewById(R.id.Name);
        	 Editable n=et.getText();
        	 String game=n.toString();
        	 try {
     			GUI.client.createGame(game);
     			CreateGame.this.finish();
     			
     		} catch (BamException e) {
     			showError("Exception",e);
     		}
        	 
         }
        	 
             // Perform action on click
       
     });
     
	
	
	}

}
