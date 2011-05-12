package bam.pong.android;




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
    
	
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
    	String listItems[]=extras.getStringArray("games");
    	 
    	 
     
        setListAdapter(new ArrayAdapter<String>(this, 
        R.layout.main2, listItems));
    }
    
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		Object o = this.getListAdapter().getItem(position);
		String game = o.toString();
		
		try {
			GUI.client.joinGame(game);
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