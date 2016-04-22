package uiuc.mbr.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;

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

		// User feedback.
		AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this)
				.setTitle("Alert")
				.setMessage("Settings saved!")
				.create();
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}
}