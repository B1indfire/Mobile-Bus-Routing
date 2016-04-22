package uiuc.mbr.activities;

import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import uiuc.mbr.alarm.Alarm;
import uiuc.mbr.R;
import uiuc.mbr.alarm.AlarmService;
import uiuc.mbr.calendar.Event;
import uiuc.mbr.events.AddressBook;
import uiuc.mbr.events.UserLocation;


/**Activity that runs when an alarm is triggered.*/
public class OnAlarmActivity extends AppCompatActivity
{
	private TextView currentAlarmName;
	private View alarmsEmpty, noCurrent, yesCurrent;

	private List<Alarm> alarms;
	@Nullable
	private Alarm triggered;
	private final Adapter adapter = new Adapter();
	private MediaPlayer soundPlayer = null;

	/**Non-null when we're trying to get the user's location.*/
	private GoogleApiClient client;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_on_alarm);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		currentAlarmName = (TextView) findViewById(R.id.a_onalarm_current_name);
		alarmsEmpty = findViewById(R.id.a_onalarm_empty);
		noCurrent = findViewById(R.id.a_onalarm_nocurrent);
		yesCurrent = findViewById(R.id.a_onalarm_yescurrent);
		ListView listView = (ListView) findViewById(R.id.a_onalarm_list);
		listView.setAdapter(adapter);
	}


	private void refresh()
	{
		alarms = AlarmService.getUntriggeredAlarms();
		Collections.sort(alarms);
		adapter.notifyDataSetChanged();

		String text;
		triggered = AlarmService.getTriggeredAlarm();
		if(triggered == null)
			text = null;
		else
		{
			Calendar now = Calendar.getInstance();
			if(triggered.getAlarmTime().compareTo(now) <= 0)
				text = triggered.event.getName();
			else
				text = null;
		}

		if(text == null)
		{
			yesCurrent.setVisibility(View.GONE);
			noCurrent.setVisibility(View.VISIBLE);
		}
		else
		{
			yesCurrent.setVisibility(View.VISIBLE);
			noCurrent.setVisibility(View.GONE);
			currentAlarmName.setText(text);

			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			if(uri == null)
				uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			soundPlayer = MediaPlayer.create(getApplicationContext(), uri);
			soundPlayer.setLooping(true);
			soundPlayer.start();
		}
	}


	@Override
	protected void onStart()
	{
		super.onStart();
		refresh();
	}


	@Override
	protected void onStop()
	{
		stopSoundIfPlaying();
		super.onStop();
	}


	private void stopSoundIfPlaying()
	{
		if(soundPlayer != null)
		{
			soundPlayer.stop();
			soundPlayer.release();
			soundPlayer = null;
		}
	}


	public void clickAlarmOffBtn(View v)
	{
		stopSoundIfPlaying();
		AlarmService.clearTriggeredAlarm(getApplicationContext());
		refresh();
	}

	public void clickOffAndMapBtn(View v)
	{
		Toast.makeText(this, "Finding your location...", Toast.LENGTH_LONG).show();
		client = new GoogleApiClient.Builder(getApplicationContext())
				.addApi(LocationServices.API)
				.addConnectionCallbacks(new GoogleCallbacks(triggered.event))
				.build();
		client.connect();
		clickAlarmOffBtn(v);
	}



	private class Adapter extends BaseAdapter
	{
		@Override
		public int getCount(){return alarms == null ? 0 : alarms.size();}

		@Override
		public Alarm getItem(int i){return alarms.get(i);}

		@Override
		public long getItemId(int i){return getItem(i).event.getParentEventId();}

		@Override
		public View getView(int i, View view, ViewGroup parent)
		{
			View v = view != null ? view : LayoutInflater.from(getApplicationContext()).inflate(R.layout.sub_onalarm_alarm, parent, false);
			TextView name = (TextView)v.findViewById(R.id.sub_onalarm_name);
			TextView time = (TextView)v.findViewById(R.id.sub_onalarm_when);
			Alarm alarm = getItem(i);

			name.setText(alarm.event.getName());
			time.setText(alarm.getAlarmTime().getTime().toString());
			return v;
		}


		@Override
		public void notifyDataSetChanged()
		{
			updateEmpty();
			super.notifyDataSetChanged();
		}


		/**Updates the display of whether the list of alarms is empty.*/
		private void updateEmpty(){alarmsEmpty.setVisibility(alarms.isEmpty() ? View.VISIBLE : View.INVISIBLE);}
	}



	/**Requests a location from the location APIs when we connect.*/
	private class GoogleCallbacks implements GoogleApiClient.ConnectionCallbacks
	{
		private final Event event;
		public GoogleCallbacks(Event event){this.event = event;}

		@Override
		public void onConnected(Bundle bundle) throws SecurityException
		{
			LocationRequest request = new LocationRequest();
			request.setInterval(1000)
					.setFastestInterval(100)
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
					.setNumUpdates(1);
			LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new LocationHandler(event));
		}

		@Override
		public void onConnectionSuspended(int i){}
	}


	/**Receives a location update and uses it.*/
	public class LocationHandler implements LocationListener
	{
		private final Event event;
		public LocationHandler(Event event){this.event = event;}

		@Override
		public void onLocationChanged(Location location)
		{
			finish();//don't show this activity again when the user finishes with the map

			Intent intent = new Intent(OnAlarmActivity.this, MapActivity.class);
			String eventLoc = event.getLocation();
			UserLocation loc = AddressBook.getByName(eventLoc, getApplicationContext());
			MapActivity.setupIntent(location.getLatitude(), location.getLongitude(), loc.latitude, loc.longitude, intent);
			startActivity(intent);
		}
	}
}
