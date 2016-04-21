package uiuc.mbr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import uiuc.mbr.events.AddressBookActivity;
import uiuc.mbr.events.CalendarSelectionActivity;
import uiuc.mbr.events.EventSelectionActivity;
import uiuc.mbr.serv.AlarmService;

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


	public void clickViewAlarmsBtn(View v)
	{
		startActivity(new Intent(getApplicationContext(), OnAlarmActivity.class));
	}

	public void clickQuickGotoVtn(View v)
	{
		startActivity(new Intent(getApplicationContext(), GotoActivity.class));
	}

	public void clickEventBtn (View v){

		startActivity(new Intent(getApplicationContext(), EventSelectionActivity.class));
	}

	public void clickSettingsBtn (View v)
	{
		startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
	}

	public void clickCalendarsBtn (View v)
	{
		startActivity(new Intent(getApplicationContext(), CalendarSelectionActivity.class));
	}

	public void clickAddressListBtn (View v)
	{
		startActivity(new Intent(getApplicationContext(), AddressBookActivity.class));
	}

	public void clickUpdateFirst(View view) {
		AlarmService.updateFirst(this);
	}
}
