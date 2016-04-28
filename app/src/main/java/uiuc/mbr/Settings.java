package uiuc.mbr;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import uiuc.mbr.alarm.AlarmService;

/**Saves and loads settings.*/
public class Settings
{
	private static final String KEY_WALK = "maxWalk", KEY_ARRIVAL_DIFF = "minArr";
	private static final String SETTINGS_FILE = "saved_settings";

	private static int maxWalkMiles = 1, arrivalDifferenceMinutes = 0;
	private static boolean loaded = false;


	/**Returns how far the user is willing to walk, in tenths of miles.*/
	public static int getMaxWalkTenthsMiles(Context c) {
		loadIfNecessary(c);
		return maxWalkMiles;
	}

	/**Changes won't be saved until you call saveSettings().*/
	public static void setMaxWalkTenthsMilesTemporarily(int maxWalkMiles, Context c) {
		loadIfNecessary(c);
		Settings.maxWalkMiles = maxWalkMiles;
	}

	/**Get the number of minutes early the user wants to arrive.*/
	public static int getArrivalDiffMinutes(Context c) {
		loadIfNecessary(c);
		return arrivalDifferenceMinutes;
	}

	/**Changes won't be saved until you call saveSettings().*/
	public static void setArrivalDiffMinutesTemporarily(int arrivalDiffMinutes, Context c) {
		loadIfNecessary(c);
		Settings.arrivalDifferenceMinutes = arrivalDiffMinutes;
	}

	public static void saveSettings(Context context){
		saveSettingsToFile(context);
		AlarmService.updateAllAlarmTimes(context);
	}

	private static void saveSettingsToFile(Context c) {
		//create the file if it doesn't exist
		try(FileOutputStream fos = c.openFileOutput(SETTINGS_FILE, Context.MODE_APPEND)) {
			fos.write(("").getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		//put settings into a hashmap
		HashMap<String, Integer> settings = new HashMap<>(2);
		settings.put(KEY_WALK, maxWalkMiles);
		settings.put(KEY_ARRIVAL_DIFF, arrivalDifferenceMinutes);

		//save the hashmap to a file
		try(FileOutputStream fos = c.openFileOutput(SETTINGS_FILE, Context.MODE_PRIVATE)) {
			try(ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				oos.writeObject(settings);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**Loads settings if necessary.*/
	private static void loadIfNecessary(Context context) {
		if(loaded)
			return;
		loaded = true;

		HashMap<String, Integer> settings = null;
		try(FileInputStream fis = context.openFileInput(SETTINGS_FILE)) {
			try(ObjectInputStream ois = new ObjectInputStream(fis)) {
				settings = (HashMap<String, Integer>) ois.readObject();
			}
		} catch (IOException | ClassNotFoundException e) {
			//throw new RuntimeException(e);
		}

		if (settings == null) {
			maxWalkMiles = 1;
			arrivalDifferenceMinutes = 0;
		} else {
			maxWalkMiles = settings.get(KEY_WALK);
			arrivalDifferenceMinutes = settings.get(KEY_ARRIVAL_DIFF);
		}
	}
}
