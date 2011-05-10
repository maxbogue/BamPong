package bam.pong.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Start extends Activity{

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
	
	Intent myIntent = new Intent(v.getContext(), GUI.class);
    startActivity(myIntent);
	
	
	}
	});
	
	
	
	
	}	
	
}
