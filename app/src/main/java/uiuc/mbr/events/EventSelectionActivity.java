package uiuc.mbr.events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.android.gms.maps.model.LatLng;

import java.util.*;

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
public class EventSelectionActivity extends AppCompatActivity
{
	@Nullable private List<Event> events;

	private final Adapter adapter = new Adapter();


    //Provides access to the device calendar
    private CalendarService calService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        calService = new CalendarService(this.getApplicationContext());
		ListView list = (ListView)findViewById(R.id.a_events_list);
		list.setAdapter(adapter);
		new Loader().execute();
    }



    /**OnCheckChangeListener implementation for the Event list checkboxes*/
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
            LatLng pos = LocationLookup.lookupLocation(event.getLocation(), getApplicationContext());

            if (isChecked) { //Event Selected
                if (pos == null) { //Invalid Address
                    promptForValidAddress();
                } else { //Valid Address
                    event.setLatLong(pos);
                    addEventToSchedule(event);
                }
            } else { //Event Deselected
				AlarmService.remove(event.getParentEventId(), getApplicationContext());
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

			builder.show();
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



	private class Loader extends AsyncTask<Void, Void, Void>
	{
		private List<Event> e;

		@Override
		protected Void doInBackground(Void[] args)
		{
			e = calService.getEventsNext24Hours();
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			events = e;
			adapter.notifyDataSetChanged();
		}
	}


	/**Displays the list of Events.*/
	private class Adapter extends BaseAdapter
	{

		@Override
		public int getCount(){return events == null ? 0 : events.size();}

		@Override
		public Event getItem(int i){return events.get(i);}

		@Override
		public long getItemId(int i){return getItem(i).getParentEventId();}

		@Override
		public View getView(int i, View convert, ViewGroup parent)
		{
			View v = convert != null ? convert : LayoutInflater.from(getApplicationContext()).inflate(R.layout.sub_event, parent, false);
			CheckBox checkBox = (CheckBox)v.findViewById(R.id.sub_event_checkbox);
			TextView name = (TextView)v.findViewById(R.id.sub_event_name);
			TextView location = (TextView)v.findViewById(R.id.sub_event_location);
			Event event = getItem(i);

			checkBox.setChecked(AlarmService.getForEvent(event.getParentEventId()) != null);
			checkBox.setOnCheckedChangeListener(new EventCheckboxListener(event, checkBox));
			name.setText(event.getName());
			location.setText(event.getLocation());

			return v;
		}
	}
}
