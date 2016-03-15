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

/**
 * Created by Scott on 3/10/2016.
 */
public class RecurringEventList {

    public static final String RECURRING_EVENT_FILE = "recurring_list";

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
    }


    public static void add(long eventId, Context c) {
        if (contains(eventId, c))
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

    public static void remove(long eventId, Context c) {
        if (!contains(eventId, c))
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

    public static boolean contains(long eventId, Context c) {
        createFileIfNotExists(c);

        try {
            FileInputStream fis = c.openFileInput(RECURRING_EVENT_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            while (line != null) {
                if (Long.parseLong(line) == eventId) {
                    Log.d("Blacklist", line + " == " + eventId);
                    return true;
                }
                Log.d("Blacklist", line + " != " + eventId);
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
