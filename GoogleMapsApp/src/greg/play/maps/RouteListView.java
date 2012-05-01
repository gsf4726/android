package greg.play.maps;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RouteListView extends ListActivity {

	public RouteListView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  List<String> routeIds = null;	  
	  Bundle extras = getIntent().getExtras();
	  if (extras != null) 
	  {
		  routeIds = extras.getStringArrayList("routeIds");
	  }
	  
	  setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routeIds));

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		String item = (String) getListAdapter().getItem(position);
		Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
		
        Intent i = new Intent();
        i.putExtra("routeId", item);
        setResult(1, i);
        finish();
	}	
}
