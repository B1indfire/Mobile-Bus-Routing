package uiuc.mbr.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;

import java.io.*;
import java.util.HashMap;

import uiuc.mbr.R;
import uiuc.mbr.alarm.AlarmService;


/**TODO this file desperately needs JavaDoc comments. What are the units for these settings?*/
public class SettingsActivity extends AppCompatActivity {
	private static final String KEY_WALK = "maxWalk", KEY_ARRIVAL_TIME = "minArr";


	private static final String SETTINGS_FILE = "saved_settings";
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
		try {
			maxWalkBar.setValue(loadMaxWalkFromMemory(this.getApplicationContext()));
			minArrBar.setValue(loadMinArrFromMemory(this.getApplicationContext()));
		} catch(Exception e){
			maxWalkBar.setValue(0);
			minArrBar.setValue(0);
		}
	}
	public void saveSettings(View v){
		saveSettingsToFile(v, this.getApplicationContext());
		AlarmService.updateAllAlarmTimes(this.getApplicationContext());
	}

	private void saveSettingsToFile(View v, Context c) {
		try(FileOutputStream fos = c.openFileOutput(SETTINGS_FILE, Context.MODE_APPEND)) {
			fos.write(("").getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		HashMap<String, Integer> settings;
		try(FileInputStream fis = c.openFileInput(SETTINGS_FILE)) {
			try(ObjectInputStream ois = new ObjectInputStream(fis)) {
				settings = (HashMap<String, Integer>) ois.readObject();
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		if (settings == null)
			settings = new HashMap<>();
		settings.put(KEY_WALK, maxWalkBar.getValue());
		settings.put(KEY_ARRIVAL_TIME, minArrBar.getValue());

		//Write to address_file
		try(FileOutputStream fos = c.openFileOutput(SETTINGS_FILE, Context.MODE_PRIVATE)) {
			try(ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				oos.writeObject(settings);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static int loadMaxWalkFromMemory(Context c) {
		HashMap<String, Integer> settings = null;
		try(FileInputStream fis = c.openFileInput(SETTINGS_FILE)) {
			try(ObjectInputStream ois = new ObjectInputStream(fis)) {
				settings = (HashMap<String, Integer>) ois.readObject();
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		if (settings == null)
			return 1;
		return settings.get(KEY_WALK);
	}

	public static int loadMinArrFromMemory(Context c) {
		HashMap<String, Integer> settings = null;
		try(FileInputStream fis = c.openFileInput(SETTINGS_FILE)) {
			try(ObjectInputStream ois = new ObjectInputStream(fis)) {
				settings = (HashMap<String, Integer>) ois.readObject();
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		if (settings == null)
			return 0;

		return settings.get(KEY_ARRIVAL_TIME);
	}
}