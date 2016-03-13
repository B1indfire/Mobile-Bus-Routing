package uiuc.mbr;

import android.content.Context;
import android.location.Location;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.SupportMapFragment;

import uiuc.mbr.events.LatLong;

/**
 * Created by jimmy on 3/12/16.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String SETTINGS_FILE = "saved_settings";
    private SeekBar maxWalkBar = (SeekBar)findViewById(R.id.maxDist);
    private SeekBar minArrBar = (SeekBar)findViewById(R.id.minArrTime);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displaySettings();
    }

    private void displaySettings() {
        maxWalkBar.setProgress(loadMaxWalkFromMemory(this.getApplicationContext()));
        minArrBar.setProgress(loadMinArrFromMemory(this.getApplicationContext()));
    }

    public void saveSettings(View v, Context c) {
        FileOutputStream fos = null;
        try {
            fos = c.openFileOutput(SETTINGS_FILE, Context.MODE_APPEND);
            fos.write(("").getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, Integer> settings = null;
        try {
            FileInputStream fis = c.openFileInput(SETTINGS_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            settings = (HashMap<String, Integer>) ois.readObject();
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

        if (settings == null)
            settings = new HashMap<>();

        settings.put("maxWalk", maxWalkBar.getProgress());
        settings.put("minArr", minArrBar.getProgress());

        //Write to address_file
        fos = null;
        try {
            fos = c.openFileOutput(SETTINGS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(settings);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Integer loadMaxWalkFromMemory(Context c) {
        HashMap<String, Integer> settings = null;
        try {
            FileInputStream fis = c.openFileInput(SETTINGS_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            settings = (HashMap<String, Integer>) ois.readObject();
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

        if (settings == null)
            return null;

        return settings.get("maxWalk");
    }

    public static Integer loadMinArrFromMemory(Context c) {
        HashMap<String, Integer> settings = null;
        try {
            FileInputStream fis = c.openFileInput(SETTINGS_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            settings = (HashMap<String, Integer>) ois.readObject();
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

        if (settings == null)
            return null;

        return settings.get("minArr");
    }
}