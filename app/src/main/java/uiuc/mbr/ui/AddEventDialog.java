package uiuc.mbr.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;//have to use this or else setView() requires a higher API level
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import uiuc.mbr.Alarm;
import uiuc.mbr.R;
import uiuc.mbr.calendar.Event;
import uiuc.mbr.events.AddressBook;
import uiuc.mbr.events.LocationLookup;
import uiuc.mbr.events.UserLocation;
import uiuc.mbr.serv.AlarmService;

/**Dialog that tries to add an alarm for an event, prompting the user for more information if needed.
 * Args must be setup().*/
public class AddEventDialog extends DialogFragment
{
	private Event event;
	/**Set to true if we manage to actually add this event to the schedule.*/
	private boolean accepted = false;
	/**We kept having weird problems where the activity was null late in the dialog's lifecycle.*/
	private Context context;

	/**Visible when we're working--don't allow user input when visible.*/
	private View loading;
	private TextView location, address, msg;


	/**Sets up the bundle used to set up this dialog.*/
	public static void setup(Event event, Bundle args)
	{
		event.export("", args);
	}

	private void readArgs()
	{
		this.event = Event.importFrom("", getArguments());
	}


	@Override
	public Dialog onCreateDialog(Bundle saved)
	{
		readArgs();
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.diag_add_event, (ViewGroup)getActivity().findViewById(android.R.id.content), false);

		AlertDialog out = new AlertDialog.Builder(getActivity())
				.setTitle(event.getName())
				.setView(view)
				.setPositiveButton("Continue", new ContinueHandler())
				.setNegativeButton("Cancel", new CancelHandler())
				.create();

		loading = view.findViewById(R.id.d_addevent_loading);
		location = (TextView)view.findViewById(R.id.d_addevent_location);
		address = (TextView)view.findViewById(R.id.d_addevent_address);
		msg = (TextView)view.findViewById(R.id.d_addevent_msg);

		location.setText(event.getLocation());
		context = getActivity().getApplicationContext();
		new Worker().execute();
		return out;
	}


	/*Prevents dialog from closing automatically when OK is clicked.*/
	@Override
	public void onResume()
	{
		((AlertDialog)getDialog()).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
		{
			private final ContinueHandler handler = new ContinueHandler();
			@Override
			public void onClick(View view){handler.onClick(null, -1);}
		});
		super.onResume();
	}


	@Override
	public void onDismiss(DialogInterface d)
	{
		if(getActivity() instanceof CloseListener)
			((CloseListener)getActivity()).onAddEventDialogClosed(accepted);
		super.onDismiss(d);
	}




	/**Tries to look up the event's location / address, and lets the user know the result.*/
	private class Worker extends AsyncTask<Void, Void, Void>
	{
		private UserLocation data = null;
		private String newAddress;

		@Override
		public void onPreExecute()
		{
			loading.setVisibility(View.VISIBLE);
			newAddress = address.getText().toString();
		}

		@Override
		protected Void doInBackground(Void[] args)
		{
			String locStr = event.getLocation();
			if(locStr != null && locStr.length() > 0)
			{
				AddressBook.initIfNecessary(context);
				data = AddressBook.getByName(event.getLocation(), context);
				if(data == null)
					AddressBook.create(data = new UserLocation(event.getLocation()), context);
				data.address = newAddress;

				String use = data.address != null && data.address.length() > 0 ? data.address : data.name;
				LatLng pos = LocationLookup.lookupLocation(use, context);
				data.latitude = pos == null ? Double.NaN : pos.latitude;
				data.longitude = pos == null ? Double.NaN : pos.longitude;
				AddressBook.update(data, context);
				Log.wtf("uiuc.mbr.dialog", "Looked up '" + use + "', got result " + pos);
			}

			return null;
		}


		@Override
		protected void onPostExecute(Void result)
		{
			loading.setVisibility(View.INVISIBLE);
			address.setText(data == null ? "" : data.address);

			if(data == null)
				msg.setText("This event has no location. This app needs a location for each event.");
			else if(Double.isNaN(data.latitude))
			{
				if(data.address == null || data.address.length() == 0)
				{
					Log.wtf("uiuc.mbr.dialog", "need address");
					msg.setText("Our location service couldn't find this event's location. Please fill out the address field for this location.");
				}
				else
				{
					Log.wtf("uiuc.mbr.dialog", "address could not be understood");
					msg.setText("Our location service couldn't understand this address.");
				}
			}
			else
			{
				AlarmService.addAlarm(new Alarm(event), context);
				//TODO intermittently throwing errors
				Snackbar.make(getActivity().findViewById(android.R.id.content), "Event added.", Snackbar.LENGTH_SHORT).show();
				accepted = true;
				dismiss();
			}
		}
	}



	/**Handles the positive button.*/
	private class ContinueHandler implements DialogInterface.OnClickListener
	{
		@Override
		public void onClick(DialogInterface anInterface, int i)
		{
			if(loading.getVisibility() == View.VISIBLE)
				return;
			new Worker().execute();
		}
	}


	/**Handles the "cancel" button.*/
	private class CancelHandler implements DialogInterface.OnClickListener
	{
		@Override
		public void onClick(DialogInterface anInterface, int i)
		{
			dismiss();
		}
	}



	/**If the parent activity wants to be notified when this dialog closes, implement this interface.*/
	public interface CloseListener
	{
		/**@param accepted if true, we set an alarm for this event*/
		void onAddEventDialogClosed(boolean accepted);
	}
}
