package uiuc.mbr.events;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.util.Locale;

import uiuc.mbr.R;
import uiuc.mbr.calendar.CalendarService;
import uiuc.mbr.calendar.Event;

/**
 * Created by varungove on 2/27/16.
 */
public class EventSelector extends AppCompatActivity {

    private static final String ADDRESS_FILE = "saved_addresses";

    private Geocoder geocoder;
    private CalendarService calService;
    private ArrayList<Event> eventlist;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        calService = new CalendarService(this.getApplicationContext());
        geocoder = new Geocoder(this, Locale.getDefault());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout my_layout = (LinearLayout) findViewById(R.id.events);
        my_layout.removeAllViews();

        eventlist = calService.getEventsNext24Hours();

        //TODO: Set check boxes as checked if already stored

        //From: http://stackoverflow.com/questions/13226353/android-checkbox-dynamically
        for (int i = 0; i < eventlist.size(); i++) {
            TableRow row = new TableRow(this);
            row.setId(i);
            row.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            CheckBox checkBox = new CheckBox(this);
            checkBox.setOnCheckedChangeListener(new EventCheckboxListener(this, eventlist.get(i), checkBox));
            checkBox.setId(i);
            checkBox.setText(eventlist.get(i).getName());
            row.addView(checkBox);
            my_layout.addView(row);
        }

    }


    public LatLong getEventLocation(Event event) {
        //TODO: Check invalid address to see if user has already supplied real address
        String location = event.getLocation();
        List<Address> address = new ArrayList<Address>() {
        };
        try {
            address = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address.size() == 0) {
            return loadLatLongFromMemory(location);
        }

        Address a = address.get(0);
        return new LatLong(a.getLatitude(), a.getLongitude());
    }

    public LatLong getEventLocation(String location) {
        //TODO: Check invalid address to see if user has already supplied real address
        List<Address> address = new ArrayList<Address>() {
        };
        try {
            address = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address.size() == 0) {
            return loadLatLongFromMemory(location);
        }


        Address a = address.get(0);
        return new LatLong(a.getLatitude(), a.getLongitude());
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "EventSelector Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://uiuc.mbr.events/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "EventSelector Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://uiuc.mbr.events/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
    }


    private class EventCheckboxListener implements CompoundButton.OnCheckedChangeListener {

        private Event event;
        private Activity parent;
        private String addressInput;
        private CheckBox self;

        public EventCheckboxListener(Activity parentActivity, Event e, CheckBox c) {
            parent = parentActivity;
            event = e;
            self = c;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            LatLong a = getEventLocation(event);
            if (isChecked) {
                if (a == null) { //Invalid Address
                    promptForValidAddress();
                } else { //Valid Address
                    event.setLatLong(a);
                    addEventToSchedule(event);
                }
            } else {
                //TODO: Remove from alarm list (if listed)
            }
        }

        private void promptForValidAddress() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
            builder.setTitle("Enter an Address");

            // Set up the input
            final EditText input = new EditText(parent);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            addressInput = "";

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addressInput = input.getText().toString();
                    LatLong a = getEventLocation(addressInput);
                    if (a != null) {
                        event.setLatLong(a);
                        addEventToSchedule(event);
                    } else {
                        Toast toast = Toast.makeText(parent, "Invalid Address", Toast.LENGTH_SHORT);
                        toast.show();
                        self.performClick();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            parent.runOnUiThread(new Runnable() {
                public void run() {
                    builder.show();
                }
            });
        }

        private void addEventToSchedule(Event e) {
            //if location string is invalid and not in memory
            //prompt to save location-address pair to memory

            if (getEventLocation(e) == null) //Invalid location string
                if (!locationInMemory(e.getLocation()))
                    promptForSavingAddress(e);

            Schedule.addEvent(e);
        }

        private void promptForSavingAddress(Event e) {
            final String loc = e.getLocation();
            final LatLong address = e.getLatLong();

            final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
            builder.setTitle("Save this address?");

            // Set up the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveAddress(loc, address);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            parent.runOnUiThread(new Runnable() {
                public void run() {
                    builder.show();
                }
            });
        }

        private void saveAddress(String loc, LatLong a) {
            //Create file if it doesn't exist
            FileOutputStream fos = null;
            try {
                fos = openFileOutput(ADDRESS_FILE, Context.MODE_APPEND);
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
                FileInputStream fis = openFileInput(ADDRESS_FILE);
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
                fos = openFileOutput(ADDRESS_FILE, Context.MODE_PRIVATE);
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
    }

    private boolean locationInMemory(String loc) {

        HashMap<String, LatLong> addresses = null;
        try {
            FileInputStream fis = openFileInput(ADDRESS_FILE);
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

    private LatLong loadLatLongFromMemory(String loc) {
        HashMap<String, LatLong> addresses = null;
        try {
            FileInputStream fis = openFileInput(ADDRESS_FILE);
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
