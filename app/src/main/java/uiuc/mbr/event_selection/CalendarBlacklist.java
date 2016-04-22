package uiuc.mbr.event_selection;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Stores a list of calendars whose events should not be displayed in the schedule.
 */
public class CalendarBlacklist {

	private static final String BLACKLIST_FILE = "blacklist";

	/**
	 * Checks if the calendar blacklist file exists on the device and creates a blank file if not
	 */
	private static void createFileIfNotExists(Context c) {
		try(FileOutputStream fos = c.openFileOutput(BLACKLIST_FILE, Context.MODE_APPEND)) {
			fos.write(("").getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Adds a given calendar's id to the list if not present.
	 */
	public static void add(long calId, Context c) {
		if (contains(calId, c))
			return;

		try(FileOutputStream fos = c.openFileOutput(BLACKLIST_FILE, Context.MODE_APPEND)) {
			try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
				writer.write("" + calId + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Removes a given calendar's id from the list if present.
	 */
	public static void remove(long calId, Context c) {
		if (!contains(calId, c))
			return;

		ArrayList<Long> calIds = new ArrayList<>();
		try(FileInputStream fis = c.openFileInput(BLACKLIST_FILE)) {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(fis)))
			{
				String line = reader.readLine();
				while(line != null)
				{
					calIds.add(Long.parseLong(line));
					line = reader.readLine();
				}
			}
		} catch(IOException e){
			throw new RuntimeException(e);
		}

		calIds.remove(calId);

		try(FileOutputStream fos = c.openFileOutput(BLACKLIST_FILE, Context.MODE_PRIVATE)) {
			try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
				for(Long i : calIds)
					writer.write("" + i.toString() + "\n");
			}
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Determines if a given calendar id is in the list.
	 */
	public static boolean contains(long calId, Context c) {
		createFileIfNotExists(c);

		try(FileInputStream fis = c.openFileInput(BLACKLIST_FILE)) {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(fis)))
			{
				String line = reader.readLine();
				while(line != null)
				{
					if(Long.parseLong(line) == calId)
					{
						Log.d("Blacklist", line + " == " + calId);
						return true;
					}
					Log.d("Blacklist", line + " != " + calId);
					line = reader.readLine();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return false;
	}

}
