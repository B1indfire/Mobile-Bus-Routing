package uiuc.mbr.events;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


import java.util.*;

import uiuc.mbr.R;
import uiuc.mbr.calendar.CalendarService;
import uiuc.mbr.calendar.Event;
import uiuc.mbr.serv.AlarmService;
import uiuc.mbr.ui.AddEventDialog;

/**
 * Activity where the User can select from a list of upcoming Events and choose which to add to the Schedule
 * If an Event has an invalid address, they will be prompted for a valid address
 * If they supply a valid address, the address will be saved to the device memory
 * XXX db stuff in UI thread
 */

public class EventSelectionActivity extends AppCompatActivity implements AddEventDialog.CloseListener
{
	@Nullable private List<Event> events;

	private final Adapter adapter = new Adapter();

    /**Provides access to the device calendar*/
    private CalendarService calService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        calService = new CalendarService(getApplicationContext());
		ListView list = (ListView)findViewById(R.id.a_events_list);
		list.setAdapter(adapter);
		new Loader().execute();
    }


	@Override
	public void onAddEventDialogClosed(boolean accepted)
	{
		new Loader().execute();
	}



    /**OnCheckChangeListener implementation for the Event list checkboxes*/
    private class EventCheckboxListener implements CompoundButton.OnCheckedChangeListener {

        //The Event associated with the checkbox
        private Event event;


        public EventCheckboxListener(Event e) {
            event = e;
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
            if (isChecked)//Event Selected
			{
				AddEventDialog dialog = new AddEventDialog();
				Bundle args = new Bundle();
				AddEventDialog.setup(event, args);
				dialog.setArguments(args);
				dialog.show(getFragmentManager(), null);
			}
			else//Event Deselected
				AlarmService.remove(event.getParentEventId(), getApplicationContext());
        }
    }



	/**Loads the list to display.*/
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
			checkBox.setOnCheckedChangeListener(new EventCheckboxListener(event));
			name.setText(event.getName());
			location.setText(event.getLocation());

			return v;
		}
	}
}
