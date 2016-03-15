package uiuc.mbr.events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

import uiuc.mbr.Alarm;
import uiuc.mbr.R;
import uiuc.mbr.calendar.CalendarService;
import uiuc.mbr.calendar.Event;
import uiuc.mbr.serv.AlarmService;

/**
 * Activity where the User can select from a list of upcoming Events and choose which to add to the Schedule
 * If an Event has an invalid address, they will be prompted for a valid address
 * If they supply a valid address, the address will be saved to the device memory
 * XXX db stuff in UI thread
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
    protected void onResume() {
        super.onResume();
        displayEventList();
    }

    /**
     * Loads and displays a scrollable list of upcoming Events
     * Events are listed with CheckBoxes indicating if they are in the User's schedule
     */
    private void displayEventList() {
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.events);
        myLayout.removeAllViews();

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
            checkBox.setOnCheckedChangeListener(new EventCheckboxListener(eventlist.get(i), checkBox));
            checkBox.setId(i);
            checkBox.setText(eventlist.get(i).getName());

            //Have the box be checked if it's already part of the user's schedule
			if(AlarmService.getForEvent(eventlist.get(i).getParentEventId()) != null)
                checkBox.setChecked(true);

            row.addView(checkBox);
            myLayout.addView(row);
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

        private CheckBox self;


        public EventCheckboxListener(Event e, CheckBox c) {
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
            LatLng a = LocationLookup.lookupLocation(event.getLocation(), getApplicationContext());

            if (isChecked) { //Event Selected
                if (a == null) { //Invalid Address
                    promptForValidAddress();
                } else { //Valid Address
                    event.setLatLong(a);
                    addEventToSchedule(event);
                }
            } else { //Event Deselected
                //TODO
            }
        }

        /**
         * Creates and launches a dialog for the user to input a valid address
         * Calls addEventToSchedule() when the user chooses 'OK'
         * Deselects the event if the user cancels the dialog
         */
        private void promptForValidAddress() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("Enter an Address");

            // Set up the input
            final EditText input = new EditText(getApplicationContext());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            addressInput = "";

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addressInput = input.getText().toString();
                    LatLng a = LocationLookup.lookupLocation(addressInput, getApplicationContext());
                    if (a != null) {
                        event.setLatLong(a);
                        addEventToSchedule(event);
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Invalid Address", Toast.LENGTH_SHORT);
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

            runOnUiThread(new Runnable()
            {
                public void run()
                {
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
            if (LocationLookup.lookupLocation(e.getLocation(), getApplicationContext()) == null) //Invalid
                if (AddressBook.getByName(e.getLocation(), getApplicationContext()) == null)
                    promptForSavingAddress(e);

			Calendar start = Calendar.getInstance();
			start.setTime(event.getStart());
			AlarmService.addAlarm(new Alarm(event), getApplicationContext());
        }

        /**
         * Prompts to save the given Event's address to memory
         */
        private void promptForSavingAddress(Event e) {
            final String loc = e.getLocation();
            final LatLng pos = e.getLatLong();

            final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("Save this address?");

            // Set up the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AddressBook.create(new UserLocation(loc, null/*TODO*/, pos.longitude, pos.longitude), getApplicationContext());
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    builder.show();
                }
            });
        }
    }
}
