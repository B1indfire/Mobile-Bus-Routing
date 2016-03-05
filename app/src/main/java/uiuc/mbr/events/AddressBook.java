package uiuc.mbr.events;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;

/**
 * Created by Scott on 3/2/2016.
 * Maintains in memory a HashMap of string locations to LatLong objects
 */
public class AddressBook {

    private static final String ADDRESS_FILE = "saved_addresses";

    /**
     * Saves the given (String, LatLong) pair to memory
     * If the memory hasn't been initialized, this method will create a new file
     */
    public static void saveAddress(String loc, LatLong a, Context c) {
        //Create file if it doesn't exist
        FileOutputStream fos = null;
        try {
            fos = c.openFileOutput(ADDRESS_FILE, Context.MODE_APPEND);
            fos.write(("").getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Read in current mapping
        HashMap<String, LatLong> addresses = null;
        try {
            FileInputStream fis = c.openFileInput(ADDRESS_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            addresses = (HashMap<String, LatLong>) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (addresses == null)
            addresses = new HashMap<>();

        addresses.put(loc, a);

        //Write to address_file
        fos = null;
        try {
            fos = c.openFileOutput(ADDRESS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(addresses);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines if the given location string has an associated LatLong in memory
     */
    public static boolean locationInMemory(String loc, Context c) {

        HashMap<String, LatLong> addresses = null;
        try {
            FileInputStream fis = c.openFileInput(ADDRESS_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            addresses = (HashMap<String, LatLong>) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (addresses == null)
            return false;

        return addresses.containsKey(loc);
    }

    /**
     * Returns the LatLong associated with a given string location from memory
     * If not in memory, returns null
     */
    public static LatLong loadLatLongFromMemory(String loc, Context c) {
        HashMap<String, LatLong> addresses = null;
        try {
            FileInputStream fis = c.openFileInput(ADDRESS_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            addresses = (HashMap<String, LatLong>) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (addresses == null)
            return null;

        return addresses.get(loc);
    }
}
