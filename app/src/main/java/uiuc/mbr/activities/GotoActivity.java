package uiuc.mbr.activities;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;

import java.util.*;

import uiuc.mbr.R;
import uiuc.mbr.event_selection.*;

/**Lets the user quickly pick a destination to travel to.
 * XXX this activity won't behave well if the user rotates the screen*/
public class GotoActivity extends AppCompatActivity
{
	private List<UserLocation> locations;
	private final Adapter adapter = new Adapter();
	UserLocation chosenTo = null;

	private final LocationRequest locationRequest = new LocationRequest();
	private GoogleApiClient locationClient;

	private View progress;
	private ListView list;
	private View stepTo, stepFrom, stepRouting;
	private TextView to;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(uiuc.mbr.R.layout.activity_goto);

		progress = findViewById(R.id.a_goto_progress);
		list = (ListView)findViewById(R.id.a_goto_list);
		stepTo = findViewById(R.id.a_goto_step_to);
		stepFrom = findViewById(R.id.a_goto_step_from);
		stepRouting = findViewById(R.id.a_goto_step_routing);
		to = (TextView)findViewById(R.id.a_goto_to);

		AddressBook.initIfNecessary(getApplicationContext());//XXX db stuff on UI thread
		locations = AddressBook.getAll(getApplicationContext());//XXX db stuff on UI thread
		Collections.sort(locations);

		progress.setVisibility(View.GONE);
		setupStep(1, "Choose destination", stepTo);
		setupStep(2, "Finding your location", stepFrom);
		setupStep(3, "Generating a route", stepRouting);
		stepFrom.setVisibility(View.GONE);
		stepRouting.setVisibility(View.GONE);
		to.setVisibility(View.GONE);
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
	}



	/**Sets up the checkbox and name for an instance of sub_goto_step.xml.*/
	private static void setupStep(int n, String name, View view)
	{
		CheckBox check = (CheckBox)view.findViewById(R.id.s_gotostep_check);
		TextView vName = (TextView)view.findViewById(R.id.s_gotostep_name);
		check.setText(String.valueOf(n));
		vName.setText(name);
	}

	/**Marks the checkbox for an instance of sub_goto_step.xml as checked.*/
	private static void checkStep(View view)
	{
		((CheckBox)view.findViewById(R.id.s_gotostep_check)).setChecked(true);
	}




	/**Manages the list of choosable locations.*/
	private class Adapter extends BaseAdapter implements AdapterView.OnItemClickListener
	{
		@Override
		public int getCount(){return locations.size();}

		@Override
		public UserLocation getItem(int i){return locations.get(i);}

		@Override
		public long getItemId(int i){return getItem(i).name.hashCode();}

		@Override
		public View getView(int i, View convert, ViewGroup parent)
		{
			TextView v = convert != null ? (TextView)convert : new TextView(GotoActivity.this);
			v.setText(getItem(i).name);
			return v;
		}

		@Override
		public void onItemClick(AdapterView<?> view, View view1, int i, long l)
		{
			Log.wtf("chose item", i + " @ " + getItem(i).latitude + ", " + getItem(i).longitude);

			to.setText(getItem(i).name);
			to.setVisibility(View.VISIBLE);
			chosenTo = getItem(i);

			checkStep(stepTo);
			list.setVisibility(View.GONE);
			stepFrom.setVisibility(View.VISIBLE);
			progress.setVisibility(View.VISIBLE);

			//request location
			locationClient = new GoogleApiClient.Builder(getApplicationContext())
					.addApi(LocationServices.API)
					.addConnectionCallbacks(new GoogleCallbacks())
					.build();
			locationClient.connect();
		}
	}




	/**Requests a location from the location APIs when we connect.*/
	private class GoogleCallbacks implements GoogleApiClient.ConnectionCallbacks
	{
		@Override
		public void onConnected(Bundle bundle) throws SecurityException
		{
			locationRequest.setInterval(1000)
					.setFastestInterval(1000)
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
					.setNumUpdates(1);
			LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, new LocationHandler());
		}

		@Override
		public void onConnectionSuspended(int i){}
	}


	/**Receives a location update and uses it.*/
	public class LocationHandler implements LocationListener
	{
		@Override
		public void onLocationChanged(Location location)
		{
			checkStep(stepFrom);
			stepRouting.setVisibility(View.VISIBLE);

			finish();

			Log.wtf("GoToActivity", "Destination: " + Double.toString(chosenTo.latitude) + ", " + Double.toString(chosenTo.longitude));

			Intent intent = new Intent(getApplicationContext(), MapActivity.class);
			MapActivity.setupIntent(location.getLatitude(), location.getLongitude(), chosenTo.latitude, chosenTo.longitude, intent);
			startActivity(intent);
		}
	}
}
