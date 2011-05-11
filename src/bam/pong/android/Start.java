package bam.pong.android;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
	Editable n=et.getText();
	Editable n2=et2.getText();
	String nick=n.toString();
	String serverAddr=n2.toString();
	
	
	
	
	
	
	
	Intent myIntent = new Intent(v.getContext(), GUI.class);
	myIntent.putExtra("nick",nick);
	myIntent.putExtra("serverAddr",serverAddr);
    startActivity(myIntent);
	
	
	}
	});
	
	
	
	
	}	
	
}
