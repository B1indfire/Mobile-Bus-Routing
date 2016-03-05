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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uiuc.mbr.R;
import uiuc.mbr.calendar.CalendarService;
import uiuc.mbr.calendar.Event;

/**
 * Created by varungove on 2/27/16.
 * Activity where the User can select from a list of upcoming Events and choose which to add to the Schedule
 * If an Event has an invalid address, they will be prompted for a valid address
 * If they supply a valid address, the address will be saved to the device memory
 */

//TODO: Refactor out address saving/loading
public class EventSelectionActivity extends AppCompatActivity {

    //Provides access to the device calendar
    private CalendarService calService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        calService = new CalendarService(this.getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "EventSelectionActivity Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
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
                Action.TYPE_VIEW,
                "EventSelectionActivity Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://uiuc.mbr.events/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayEventList();
    }

    /**
     * Loads and displays a scrollable list of upcoming Events
     * Events are listed with CheckBoxes indicating if they are in the User's schedule
     */
    private void displayEventList() {
        LinearLayout my_layout = (LinearLayout) findViewById(R.id.events);
        my_layout.removeAllViews();

        ArrayList<Event> eventlist = calService.getEventsNext24Hours();

        //TODO: Automatically call performClick() on events with shared parentId's (from previous instances)
        //TODO: Blacklist events by CalendarID

        //From: http://stackoverflow.com/questions/13226353/android-checkbox-dynamically
        for (int i = 0; i < eventlist.size(); i++) {
            TableRow row = new TableRow(this);
            row.setId(i);
            row.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            CheckBox checkBox = new CheckBox(this);

            //Assign a Listener to the CheckBox
            checkBox.setOnCheckedChangeListener(new EventCheckboxListener(this, eventlist.get(i), checkBox));
            checkBox.setId(i);
            checkBox.setText(eventlist.get(i).getName());

            //Have the box be checked if it's already part of the user's schedule
            if (Schedule.contains(eventlist.get(i)))
                checkBox.setChecked(true);

            row.addView(checkBox);
            my_layout.addView(row);
        }
    }

    /**
     * OnCheckChangeListener implementation for the Event list checkboxes
     */
    private class EventCheckboxListener implements CompoundButton.OnCheckedChangeListener {

        //The Event associated with the checkbox
        private Event event;

        //The String inputted into the AlertDialog when submitting a new address
        private String addressInput;

        private Activity parent;
        private CheckBox self;


        public EventCheckboxListener(Activity parentActivity, Event e, CheckBox c) {
            parent = parentActivity;
            event = e;
            self = c;
        }

        /**
         * Triggered when the user clicks a checkbox
         *
         * If a Event is selected, it's location is checked for validity
         * If invalid the user is prompted for a new, valid address
         * Once a valid address is obtained, the Event is added to the schedule
         * If the new address is blank or invalid, the Event is deselected
         *
         * If an Event is deselected, it is removed from the schedule
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            LatLong a = LatLong.getEventLocation(event, parent);

            if (isChecked) { //Event Selected
                if (a == null) { //Invalid Address
                    promptForValidAddress();
                } else { //Valid Address
                    event.setLatLong(a);
                    addEventToSchedule(event);
                }
            } else { //Event Deselected
                Schedule.removeEvent(event);
            }
        }

        /**
         * Creates and launches a dialog for the user to input a valid address
         * Calls addEventToSchedule() when the user chooses 'OK'
         * Deselects the event if the user cancels the dialog
         */
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
                    LatLong a = LatLong.getEventLocation(addressInput, parent);
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
                    self.setChecked(false);
                }
            });

            parent.runOnUiThread(new Runnable() {
                public void run() {
                    builder.show();
                }
            });
        }

        /**
         * Adds the given Event to the Schedule
         * Checks if Event needs to save location to memory and prompts if so
         */
        private void addEventToSchedule(Event e) {

            //if location string is invalid and not in memory
            if (LatLong.getEventLocation(e, parent) == null) //Invalid
                if (!AddressBook.locationInMemory(e.getLocation(), parent)) //Not in memory
                    promptForSavingAddress(e);

            Schedule.addEvent(e);
        }

        /**
         * Prompts to save the given Event's address to memory
         */
        private void promptForSavingAddress(Event e) {
            final String loc = e.getLocation();
            final LatLong address = e.getLatLong();

            final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
            builder.setTitle("Save this address?");

            // Set up the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AddressBook.saveAddress(loc, address, parent);
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
    }
}
