package bam.pong.android;





import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;


public class GUI extends Activity{

	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

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
     
     final Button button4 = (Button) findViewById(R.id.widget30);
     button4.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
             // Perform action on click
         }
     });
     
     final Button button5 = (Button) findViewById(R.id.widget31);
     button5.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
        	 Intent myIntent = new Intent(v.getContext(), BamPong.class);
             startActivity(myIntent);
        	 
             // Perform action on click
        	 
         }
     });
     

	
	
}
}
