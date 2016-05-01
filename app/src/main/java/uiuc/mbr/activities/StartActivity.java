package uiuc.mbr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import uiuc.mbr.R;
import uiuc.mbr.alarm.AlarmService;

/**Activity that runs when our app first launches.
 * Since our app has so many features, this activity merely lets the user navigate to a set of second-level "Menu" activities
 * that will allow them to reach the real, feature-related activities.
 * This activity does provide direct access to SettingsActivity via a menu item.*/
public class StartActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		AlarmService.loadAlarms(getApplicationContext());
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
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void clickNavigationBtn (View v) {
		startActivity(new Intent(getApplicationContext(), NavigationMenuActivity.class));
	}

	public void clickEventsBtn (View v) {
		startActivity(new Intent(getApplicationContext(), EventsMenuActivity.class));
	}

	public void clickAlarmsBtn (View v) {
		startActivity(new Intent(getApplicationContext(), AlarmsMenuActivity.class));
	}


}
