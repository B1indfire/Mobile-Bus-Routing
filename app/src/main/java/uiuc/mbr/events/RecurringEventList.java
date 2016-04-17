package uiuc.mbr.events;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.*;

import uiuc.mbr.calendar.Event;

/**
 * Used by EventSelectionActivity to keep track of recurring events which the user wants automatically added to their schedule.
 * Also keeps track of instances of the recurring events which the user does not want in their schedule (called exceptions).
 */
public class RecurringEventList {

	public static final String RECURRING_EVENT_FILE = "recurring_list";
	public static final String EXCEPTIONS_FILE = "exceptions";

	private static void createFileIfNotExists(Context c) {
		try(FileOutputStream fos = c.openFileOutput(RECURRING_EVENT_FILE, Context.MODE_APPEND)) {
			fos.write(("").getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try(FileOutputStream fos = c.openFileOutput(EXCEPTIONS_FILE, Context.MODE_APPEND)) {
			fos.write(("").getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Adds an event's parent ID to the recurring event list if not already included.
	 */
	public static void add(Event event, Context c) {
		long eventId = event.getParentEventId();
		if (contains(event, c))
			return;

		try(FileOutputStream fos = c.openFileOutput(RECURRING_EVENT_FILE, Context.MODE_APPEND)) {
			try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
				writer.write("" + eventId + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an exception to a recurring event to the exceptions list if not already included.
	 */
	public static void addException(Event event, Context c) {
		long eventId = event.getParentEventId();
		long startTime = event.getStart().getTime();
		if (containsException(event, c))
			return;

		try(FileOutputStream fos = c.openFileOutput(EXCEPTIONS_FILE, Context.MODE_APPEND)) {
			try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
				writer.write("" + eventId + "," + startTime + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Removes an event's parent ID to the recurring event list if included.
	 */
	public static void remove(Event event, Context c) {
		long eventId = event.getParentEventId();
		if (!contains(event, c))
			return;

		ArrayList<Long> calIds = new ArrayList<>();
		try(FileInputStream fis = c.openFileInput(RECURRING_EVENT_FILE)) {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
				String line = reader.readLine();
				while(line != null) {
					calIds.add(Long.parseLong(line));
					line = reader.readLine();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		calIds.remove(eventId);

		try(FileOutputStream fos = c.openFileOutput(RECURRING_EVENT_FILE, Context.MODE_PRIVATE)) {
			try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
				for(Long i : calIds)
					writer.write("" + i.toString() + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Removes an exception to a recurring event from the exceptions list if included.
	 */
	public static void removeException(Event event, Context c) {
		long eventId = event.getParentEventId();
		long startTime = event.getStart().getTime();
		if (!containsException(event, c))
			return;

		ArrayList<String> lines = new ArrayList<>();
		try(FileInputStream fis = c.openFileInput(EXCEPTIONS_FILE)) {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(fis)))
			{
				String line = reader.readLine();
				while(line != null) {
					int middle = line.indexOf(",");
					String id = line.substring(0, middle);
					String start = line.substring(middle + 1);
					if((Long.parseLong(id) != eventId || Long.parseLong(start) != startTime) &&
							Long.parseLong(start) >= Calendar.getInstance().getTimeInMillis())
						lines.add(line);
					line = reader.readLine();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try(FileOutputStream fos = c.openFileOutput(EXCEPTIONS_FILE, Context.MODE_PRIVATE)) {
			try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos)))
			{
				for(String i : lines) {
					writer.write(i + "\n");
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Determines if the recurring event list contains the given event's parent id.
	 */
	public static boolean contains(Event event, Context c) {
		long eventId = event.getParentEventId();
		createFileIfNotExists(c);

		try(FileInputStream fis = c.openFileInput(RECURRING_EVENT_FILE)) {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
				String line = reader.readLine();
				while(line != null) {
					if(Long.parseLong(line) == eventId)
						return true;
					line = reader.readLine();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return false;
	}

	/**
	 * Determines if the exceptions list contains the given event instance.
	 */
	public static boolean containsException(Event event, Context c) {
		long eventId = event.getParentEventId();
		long startTime = event.getStart().getTime();
		createFileIfNotExists(c);

		try(FileInputStream fis = c.openFileInput(EXCEPTIONS_FILE)) {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
				String line = reader.readLine();
				while(line != null) {
					int middle = line.indexOf(",");
					String id = line.substring(0, middle);
					String start = line.substring(middle + 1);
					if(Long.parseLong(id) == eventId && Long.parseLong(start) == startTime)
						return true;

					line = reader.readLine();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return false;
	}

	/**
	 * Determines if the given event is in the recurring events list but is not in the exceptions.
	 */
	public static boolean containsNonExempt(Event event, Context c) {
		return contains(event, c) && !containsException(event,c);
	}
}
