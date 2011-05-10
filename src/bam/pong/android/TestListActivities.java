package bam.pong.android;



import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
 
public class TestListActivities extends ListActivity {
    String[] listItems = {"Game1", "Game2", 
                          "Game3", "Game4"};
  

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setListAdapter(new ArrayAdapter<String>(this, 
        R.layout.main2, listItems));
    }
}