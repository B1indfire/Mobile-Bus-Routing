package uiuc.mbr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import uiuc.mbr.R;
import uiuc.mbr.calendar.Calendar;
import uiuc.mbr.calendar.CalendarService;
import uiuc.mbr.events.CalendarBlacklist;

/**
 * An activity which displays a list of calendars on the device and allows to user to toggle
 * whether their events should be displayed for scheduling
 * Created by Scott on 3/10/2016.
 */
public class CalendarSelectionActivity extends AppCompatActivity {

	//Provides access to the device calendar
	private CalendarService calService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendars);

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
		displayCalendarList();
	}

	/**
	 * Loads and displays a scrollable list of Calendars
	 * Events are listed with CheckBoxes indicating if they are not blacklisted
	 */
	private void displayCalendarList() {
		LinearLayout my_layout = (LinearLayout) findViewById(R.id.calendars);
		my_layout.removeAllViews();

		ArrayList<Calendar> calendarList = calService.getCalendars();

		//From: http://stackoverflow.com/questions/13226353/android-checkbox-dynamically
		for (int i = 0; i < calendarList.size(); i++) {
			Calendar cal = calendarList.get(i);
			TableRow row = new TableRow(this);
			row.setId(i);
			row.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
			CheckBox checkBox = new CheckBox(this);

			checkBox.setId(i);
			checkBox.setText(cal.getName());

			//Set checked state based on current blacklist
			checkBox.setChecked(true);
			if (CalendarBlacklist.contains(cal.getId(), this))
				checkBox.setChecked(false);

			//Assign a Listener to the CheckBox
			checkBox.setOnCheckedChangeListener(new CalendarCheckboxListener(this, cal));

			row.addView(checkBox);
			my_layout.addView(row);
		}
	}

	/**
	 * OnCheckChangeListener implementation for the Calendar list checkboxes
	 */
	private class CalendarCheckboxListener implements CompoundButton.OnCheckedChangeListener {

		//The Calendar associated with the checkbox
		private Calendar calendar;

		private Activity parent;

		public CalendarCheckboxListener(Activity parentActivity, Calendar c) {
			parent = parentActivity;
			calendar = c;
		}

		/**
		 * Triggered when the user clicks a checkbox
		 * Toggles the associated calendar's state in the CalendarBlacklist
		 */
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				CalendarBlacklist.remove(calendar.getId(), parent);
			} else {
				CalendarBlacklist.add(calendar.getId(), parent);
			}
		}
	}
}
