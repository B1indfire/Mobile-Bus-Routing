package uiuc.mbr.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import uiuc.mbr.directions.CumtdApi;
import uiuc.mbr.directions.Directions;
import uiuc.mbr.R;
import uiuc.mbr.event_selection.LocationLookup;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback
{
	/**Sets up the intent used to start this activity with an origin and/or destination latitude+longitude.
	 * Calling this function is optional. Not calling it is equivalent to calling it with all NaN arguments.
	 * Anything you don't provide here (anything that's NaN) will be requested in a dialog.*/
	public static void setupIntent(double oLat, double oLon, double dLat, double dLon, Intent intent)
	{
		intent.putExtra("olat", oLat);
		intent.putExtra("olon", oLon);
		intent.putExtra("dlat", dLat);
		intent.putExtra("dlon", dLon);
	}

	/**Pulls data out of the intent used to start this activity.*/
	private void readIntent()
	{
		oLatitude = getIntent().getDoubleExtra("olat", Double.NaN);
		oLongitude = getIntent().getDoubleExtra("olon", Double.NaN);
		dLatitude = getIntent().getDoubleExtra("dlat", Double.NaN);
		dLongitude = getIntent().getDoubleExtra("dlon", Double.NaN);

		Log.wtf("MapActivity", "Destination: " + Double.toString(dLatitude) + ", " + Double.toString(dLongitude));
	}



	private GoogleMap map;
	private final LocationRequest locationRequest = new LocationRequest();
	private final GoogleCallbacks googleCallbacks = new GoogleCallbacks();
	private GoogleApiClient googleClient;

	/**Public for testing only*/
	public final LocationHandler locationHandler = new LocationHandler();

	public Marker userLocationMarker;

	private double oLatitude, oLongitude, dLatitude, dLongitude;
	private SupportMapFragment mapFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		readIntent();
	}


	/**Prompts the user for an origin or destination if one is needed;
	 * displays the route on the map otherwise.*/
	private void nextStep()
	{
		if(Double.isNaN(oLatitude) || Double.isNaN(oLongitude))
			promptForNewAddress(false);
		else if(Double.isNaN(dLatitude) || Double.isNaN(dLongitude))
			promptForNewAddress(true);
		else
			drawRouteOnMap();
	}


	/**Prompts the user for an origin/destination, depending on the argument.*/
	private void promptForNewAddress(final boolean dest) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if(dest)
			builder.setTitle("Enter destination");
		else
			builder.setTitle("Enter starting location");

		// Set up the input
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String addressInput = input.getText().toString();
				LatLng ll = LocationLookup.lookupLocation(addressInput, getApplicationContext());
				if(ll != null)
				{ //Valid address
					if(dest)
					{
						dLatitude = ll.latitude;
						dLongitude = ll.longitude;
					}
					else
					{
						oLatitude = ll.latitude;
						oLongitude = ll.longitude;
					}
				}
				else//Invalid Location
					Toast.makeText(MapActivity.this, "Invalid Location", Toast.LENGTH_SHORT).show();
				nextStep();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				finish();
			}
		})
		.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		})
		.show();
	}

	private void drawRouteOnMap() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		LatLng debugPos = new LatLng(0, 0);
		userLocationMarker = map.addMarker(new MarkerOptions().position(debugPos).title("You are here."));

		int[] colors = {Color.RED, Color.GREEN, Color.BLUE};
		CumtdApi api = CumtdApi.create();
		Directions d;
		try {
			int tempW = SettingsActivity.loadMaxWalkFromMemory(getApplicationContext());
			double maxWalk = tempW*.1;

			Log.wtf("MapActivity", "Destination: " + Double.toString(dLatitude) + ", " + Double.toString(dLongitude));

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
			String[] dateTime = df.format(new Date()).split(" ");

			d = api.getTripArriveBy(Double.toString(oLatitude),
					Double.toString(oLongitude),
					Double.toString(dLatitude),
					Double.toString(dLongitude),
					dateTime[0], dateTime[1],
					""+maxWalk, "depart");
		} catch (Exception e) {throw new RuntimeException(e);}
		if(d == null) {
			Toast.makeText(this, "No bus route found.", Toast.LENGTH_LONG).show();
			return;
		}

		List<String> list = d.getCoordinates();
		for (int i = 0; i < list.size(); i++) {
			String[] p1 = list.get(i).split(":");
			PolylineOptions line = new PolylineOptions();
			line.width(10);
			if (p1[0].equals("W")) {
				line.color(Color.BLACK);
			} else {
				line.color(colors[i%colors.length]);
			}
			String[] p2 = p1[1].split(",");
			for (int j = 0; j < p2.length; j+=2) {
				line.add(new LatLng(Double.parseDouble(p2[j]), Double.parseDouble(p2[j+1])));
			}
			map.addPolyline(line);
		}

		map.moveCamera(CameraUpdateFactory.zoomTo(14));
		map.moveCamera(CameraUpdateFactory.newLatLng(debugPos)); //TODO: Switch to actual LatLng

		googleClient = new GoogleApiClient.Builder(getApplicationContext())
				.addApi(LocationServices.API)
				.addConnectionCallbacks(googleCallbacks)
				.build();
		googleClient.connect();
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		map = googleMap;
		nextStep();
	}



	/**Handles connecting to the Google APIs when the activity starts.*/
	private class GoogleCallbacks implements GoogleApiClient.ConnectionCallbacks
	{
		@Override
		public void onConnected(Bundle bundle) throws SecurityException
		{
			//request a location update every 30 seconds
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//GPS, not network-based location
			locationRequest.setInterval(30 * 1000);
			LocationServices.FusedLocationApi.requestLocationUpdates(googleClient, locationRequest, locationHandler);
		}

		@Override
		public void onConnectionSuspended(int i){}
	}


	/**Handles location updates.*/
	public class LocationHandler implements LocationListener
	{
		@Override
		public void onLocationChanged(Location location)
		{
			LatLng here = new LatLng(location.getLatitude(), location.getLongitude());
			if(userLocationMarker == null)
				userLocationMarker = map.addMarker(new MarkerOptions().position(here).title("You are here."));
			else
				userLocationMarker.setPosition(here);
			map.moveCamera(CameraUpdateFactory.newLatLng(here));
		}
	}
}
