package uiuc.mbr.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import uiuc.mbr.R;


/**Lists upcoming departures at a stop.
 * Tightly coupled to GetStopsActivity.*/
public class StopListActivity extends AppCompatActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stop_list);

		Intent intent = getIntent();
		ArrayList<String> departures = intent.getStringArrayListExtra("departures");
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, departures);

		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(adapter);
	}
}
