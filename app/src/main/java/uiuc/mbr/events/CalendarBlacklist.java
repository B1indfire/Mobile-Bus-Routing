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
import java.util.HashMap;

import uiuc.mbr.calendar.Calendar;

/**
 * Created by Scott on 3/10/2016.
 * Stores a list of calendars whose events should not be displayed in the schedule.
 */
public class CalendarBlacklist {

    public static final String BLACKLIST_FILE = "blacklist";

    /**
     * Checks if the calendar blacklist file exists on the device and creates a blank file if not
     */
    private static void createFileIfNotExists(Context c) {
        FileOutputStream fos = null;
        try {
            fos = c.openFileOutput(BLACKLIST_FILE, Context.MODE_APPEND);
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
     * Adds a given calendar's id to the list if not present.
     */
    public static void add(long calId, Context c) {
        if (contains(calId, c))
            return;

        try {
            FileOutputStream fos = c.openFileOutput(BLACKLIST_FILE, Context.MODE_APPEND);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write("" + calId + "\n");
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
     * Removes a given calendar's id from the list if present.
     */
    public static void remove(long calId, Context c) {
        if (!contains(calId, c))
            return;

        ArrayList<Long> calIds = new ArrayList<>();
        try {
            FileInputStream fis = c.openFileInput(BLACKLIST_FILE);
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

        calIds.remove(new Long(calId));

        try {
            FileOutputStream fos = c.openFileOutput(BLACKLIST_FILE, Context.MODE_PRIVATE);
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
     * Determines if a given calendar id is in the list.
     */
    public static boolean contains(long calId, Context c) {
        createFileIfNotExists(c);

        try {
            FileInputStream fis = c.openFileInput(BLACKLIST_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            while (line != null) {
                if (Long.parseLong(line) == calId) {
                    Log.d("Blacklist", line + " == " + calId);
                    return true;
                }
                Log.d("Blacklist", line + " != " + calId);
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

}
