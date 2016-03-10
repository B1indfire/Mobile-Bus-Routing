package uiuc.mbr.events;

import android.content.Context;

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
public class CalendarBlacklist {

    public static final String BLACKLIST_FILE = "blacklist";

    private static void createFileIfNotExists(Context c) {
        FileOutputStream fos = null;
        try {
            fos = c.openFileOutput(BLACKLIST_FILE, Context.MODE_APPEND);
            fos.write(("").getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void add(long calId, Context c) {
        if (contains(calId, c))
            return;

        try {
            FileOutputStream fos = c.openFileOutput(BLACKLIST_FILE, Context.MODE_APPEND);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.append("" + calId + "\n");
            writer.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean contains(long calId, Context c) {
        createFileIfNotExists(c);

        try {
            FileInputStream fis = c.openFileInput(BLACKLIST_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            while (line != null) {
                if (Long.parseLong(line) == calId) {
                    return true;
                }
                line = reader.readLine();
            }
            reader.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
