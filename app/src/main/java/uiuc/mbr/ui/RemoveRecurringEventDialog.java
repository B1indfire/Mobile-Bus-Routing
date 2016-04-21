package uiuc.mbr.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import uiuc.mbr.calendar.Event;
import uiuc.mbr.events.RecurringEventList;

/**Dialog triggered when the user tries to remove a recurring event from the schedule.
 * Asks the user whether they want to remove all instances or just one.*/
public class RemoveRecurringEventDialog extends DialogFragment
{
	public static void setup(Event event, Bundle args)
	{
		event.export("", args);
	}
	private void readArgs()
	{
		this.event = Event.importFrom("", getArguments());
	}

	private Event event;


	@Override
	public Dialog onCreateDialog(Bundle saved)
	{
		readArgs();

		return new AlertDialog.Builder(getActivity())
		.setMessage("This event is set to automatically be added to the schedule.  Would you like to disable this for future events?")

		// Set up the buttons
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RecurringEventList.remove(event, getActivity().getApplicationContext());
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RecurringEventList.addException(event, getActivity().getApplicationContext());
				dialog.cancel();
			}
		})
		.create();
	}

}
