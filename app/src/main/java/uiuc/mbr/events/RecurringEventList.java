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

/**
 * Created by Scott on 3/10/2016.
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

    public static void addException(long eventId, long startTime, Context c) {
        if (containsException(eventId, startTime, c))
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

    public static void removeException(long eventId, long startTime, Context c) {
        if (!containsException(eventId, startTime, c))
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
            FileOutputStream fos = c.openFileOutput(RECURRING_EVENT_FILE, Context.MODE_PRIVATE);
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

    public static boolean containsException(long eventId, long startTime, Context c) {
        createFileIfNotExists(c);

        try {
            FileInputStream fis = c.openFileInput(RECURRING_EVENT_FILE);
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
}
