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

	/**
	 * Overrides super.
	 * Sets the two number picker objects to the two views from the xml.
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		maxWalkBar = (NumberPicker) findViewById(R.id.maxDist);
		minArrBar = (NumberPicker) findViewById(R.id.minArrTime);
	}

	/**
	 * Calls the super onResume() and also calls the displaySettings
	 */
	@Override
	protected void onResume() {
		super.onResume();
		displaySettings();
	}

	/**
	 * Gets the saved value by calling a Settings function, then sets the number picker to the
	 * correct value.
	 */
	private void displaySettings() {
		maxWalkBar.setValue(Settings.getMaxWalkTenthsMiles(this.getApplicationContext()));
		minArrBar.setValue(Settings.getArrivalDiffMinutes(this.getApplicationContext()));
	}


	/**
	 * Saves the values currently in the number picker by calling the saveSettings
	 * function from Settings.
	 * @param v
	 */
	public void saveSettings(View v){
		Settings.setMaxWalkTenthsMilesTemporarily(maxWalkBar.getValue(), getApplicationContext());
		Settings.setArrivalDiffMinutesTemporarily(minArrBar.getValue(), getApplicationContext());
		Settings.saveSettings(getApplicationContext());

		Toast.makeText(this, "Settings saved", Toast.LENGTH_LONG).show();
		finish();
	}
}