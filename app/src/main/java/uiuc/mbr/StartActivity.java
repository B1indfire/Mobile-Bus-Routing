package uiuc.mbr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import uiuc.mbr.events.EventSelectionActivity;

public class StartActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if(id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void clickMapBtn(View v) {
		startActivity(new Intent(getApplicationContext(), MapActivity.class));
	}

	public void clickStpBtn(View v) {
		startActivity(new Intent(getApplicationContext(), GetStops.class));
	}

	public void clickAlarmBtn(View v) {
		startActivity(new Intent(getApplicationContext(), AlarmActivity.class));
	}

	public void clickViewAlarmsBtn(View v) {
		startActivity(new Intent(getApplicationContext(), OnAlarmActivity.class));
	}

	public void clickEventAlarmsBtn(View v){

		startActivity(new Intent(getApplicationContext(), EventAlarmActivity.class));
	}

	public void clickEventBtn (View v){

		startActivity(new Intent(getApplicationContext(), EventSelectionActivity.class));
	}

	public void clickSettingsBtn (View v) {
		startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
	}

	//tasks
	//TODO: Edit/remove stored addresses
}
