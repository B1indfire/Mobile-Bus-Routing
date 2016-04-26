package uiuc.mbr.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import uiuc.mbr.R;
import uiuc.mbr.Settings;


/**Activity that lets the user edit settings.*/
public class SettingsActivity extends AppCompatActivity {
	private NumberPicker maxWalkBar;
	private NumberPicker minArrBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		maxWalkBar = (NumberPicker) findViewById(R.id.maxDist);
		System.out.println(maxWalkBar);
		minArrBar = (NumberPicker) findViewById(R.id.minArrTime);
	}

	@Override
	protected void onResume() {
		super.onResume();
		displaySettings();
	}

	private void displaySettings() {
		maxWalkBar.setValue(Settings.getMaxWalkTenthsMiles(this.getApplicationContext()));
		minArrBar.setValue(Settings.getArrivalDiffMinutes(this.getApplicationContext()));
	}


	public void saveSettings(View v){
		Settings.setMaxWalkTenthsMilesTemporarily(maxWalkBar.getValue(), getApplicationContext());
		Settings.setArrivalDiffMinutesTemporarily(minArrBar.getValue(), getApplicationContext());
		Settings.saveSettings(getApplicationContext());

		Toast.makeText(this, "Settings saved", Toast.LENGTH_LONG).show();
		finish();
	}
}