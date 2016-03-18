package uiuc.mbr.events;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import uiuc.mbr.calendar.Event;

/**
 * Used by EventSelectionActivity to keep track of recurring events which the user wants automatically added to their schedule.
 * Also keeps track of instances of the recurring events which the user does not want in their schedule (called exceptions).
 */
public class RecurringEventList {

    public static final String RECURRING_EVENT_FILE = "recurring_list";
    public static final String EXCEPTIONS_FILE = "exceptions";

    private static void createFileIfNotExists(Context c) {
        FileOutputStream fos = null;
        try {
            fos = c.openFileOutput(RECURRING_EVENT_FILE, Context.MODE_APPEND);
            fos.write(("").getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        }

        try {
            fos = c.openFileOutput(EXCEPTIONS_FILE, Context.MODE_APPEND);
            fos.write(("").getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Adds an event's parent ID to the recurring event list if not already included.
     */
    public static void add(Event event, Context c) {
        long eventId = event.getParentEventId();
        if (contains(event, c))
            return;

        try {
            FileOutputStream fos = c.openFileOutput(RECURRING_EVENT_FILE, Context.MODE_APPEND);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write("" + eventId + "\n");
            writer.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
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

        try {
            FileOutputStream fos = c.openFileOutput(EXCEPTIONS_FILE, Context.MODE_APPEND);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write("" + eventId + "," + startTime + "\n");
            writer.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
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
        try {
            FileInputStream fis = c.openFileInput(RECURRING_EVENT_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            while (line != null) {
                calIds.add(Long.parseLong(line));
                line = reader.readLine();
            }
            reader.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        }

        calIds.remove(new Long(eventId));

        try {
            FileOutputStream fos = c.openFileOutput(RECURRING_EVENT_FILE, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            for (Long i : calIds) {
                writer.write("" + i.toString() + "\n");
            }
            writer.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
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
        try {
            FileInputStream fis = c.openFileInput(EXCEPTIONS_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            while (line != null) {
                int middle = line.indexOf(",");
                String id = line.substring(0, middle);
                String start = line.substring(middle+1);
                if((Long.parseLong(id) != eventId || Long.parseLong(start) != startTime) &&
                        Long.parseLong(start) >= Calendar.getInstance().getTimeInMillis())
                    lines.add(line);
                line = reader.readLine();
            }
            reader.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        }

        try {
            FileOutputStream fos = c.openFileOutput(EXCEPTIONS_FILE, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            for (String i : lines) {
                writer.write(i + "\n");
            }
            writer.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Determines if the recurring event list contains the given event's parent id.
     */
    public static boolean contains(Event event, Context c) {
        long eventId = event.getParentEventId();
        createFileIfNotExists(c);

        try {
            FileInputStream fis = c.openFileInput(RECURRING_EVENT_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            while (line != null) {
                if (Long.parseLong(line) == eventId) {
                    return true;
                }
                line = reader.readLine();
            }
            reader.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
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

        try {
            FileInputStream fis = c.openFileInput(EXCEPTIONS_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            while (line != null) {
                int middle = line.indexOf(",");
                String id = line.substring(0, middle);
                String start = line.substring(middle+1);
                if(Long.parseLong(id) == eventId && Long.parseLong(start) == startTime)
                    return true;

                line = reader.readLine();
            }
            reader.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ERROR", e.getMessage());
            e.printStackTrace();
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
