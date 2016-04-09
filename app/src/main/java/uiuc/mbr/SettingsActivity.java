package uiuc.mbr;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
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
import java.util.HashMap;

import uiuc.mbr.serv.AlarmService;


/**
 * Created by jimmy on 3/12/16.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String SETTINGS_FILE = "saved_settings";
    private NumberPicker maxWalkBar;
    private NumberPicker minArrBar;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        maxWalkBar = (NumberPicker) findViewById(R.id.maxDist);
        System.out.println(maxWalkBar);
        minArrBar = (NumberPicker) findViewById(R.id.minArrTime);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displaySettings();
    }

    private void displaySettings() {
        try {
            maxWalkBar.setValue(loadMaxWalkFromMemory(this.getApplicationContext()));
            minArrBar.setValue(loadMinArrFromMemory(this.getApplicationContext()));
        } catch(Exception e){
            maxWalkBar.setValue(0);
            minArrBar.setValue(0);
        }
    }
    public void saveSettings(View v){
        saveSettingsToFile(v, this.getApplicationContext());
        AlarmService.updateAllAlarmTimes(this.getApplicationContext());
    }

    private void saveSettingsToFile(View v, Context c) {
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
//        maxWalkBar = (SeekBar)findViewById(R.id.maxDist);
//        minArrBar = (SeekBar)findViewById(R.id.minArrTime);
        settings.put("maxWalk", maxWalkBar.getValue());
        settings.put("minArr", minArrBar.getValue());

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
        System.out.println("maxWalk: "+settings.get("maxWalk"));
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

        System.out.println("minarr: "+settings.get("minArr"));
        return settings.get("minArr");
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Settings Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://uiuc.mbr/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Settings Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://uiuc.mbr/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}