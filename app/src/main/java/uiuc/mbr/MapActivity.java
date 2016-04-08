package uiuc.mbr;

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

import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import uiuc.mbr.events.LocationLookup;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback
{

	private GoogleMap map;
	private final LocationRequest locationRequest = new LocationRequest();
	private final GoogleCallbacks googleCallbacks = new GoogleCallbacks();
	private GoogleApiClient googleClient;
	/**Public for testing only*/
	public final LocationHandler locationHandler = new LocationHandler();

	public Marker userLocationMarker;

	double o_latitude;
	double o_longitude;
	double d_latitude;
	double d_longitude;
	Date arrival;
	SupportMapFragment mapFragment;

	private static final long MILLISECONDS_IN_HOUR = 3600000;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		Intent intent = getIntent();
		o_latitude = intent.getDoubleExtra("ORIGIN_LATITUDE", -1000);
		o_longitude = intent.getDoubleExtra("ORIGIN_LONGITUDE", -1000);
		d_latitude = intent.getDoubleExtra("DEST_LATITUDE", -1000);
		d_longitude = intent.getDoubleExtra("DEST_LONGITUDE", -1000);
		arrival = new Date(intent.getLongExtra("ARRIVAL_TIME", System.currentTimeMillis() + MILLISECONDS_IN_HOUR));
		if(o_latitude == -1000 || o_longitude == -1000) {
			promptForNewAddress(false);
		} else if(d_latitude == -1000 || d_longitude == -1000) {
			promptForNewAddress(true);
		}
		else {
			mapFragment.getMapAsync(this);
		}
	}

	/**
	 * If dest == true, prompts for d_long/d_lat, otherwise prompts for o_long/o_lat
	 * @param dest
	 */
	private void promptForNewAddress(final boolean dest) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if(dest)
			builder.setTitle("Enter destination address");
		else
			builder.setTitle("Enter starting address");
		final MapActivity context = this;

		// Set up the input
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String addressInput = input.getText().toString();
				LatLng ll = LocationLookup.lookupLocation(addressInput, getApplicationContext());
				if (ll != null) { //Valid address
					if(dest) {
						d_latitude = ll.latitude;
						d_longitude = ll.longitude;
						mapFragment.getMapAsync(context);
					} else {
						o_latitude = ll.latitude;
						o_longitude = ll.longitude;
						if(d_latitude == -1000 || d_longitude == -1000)
							promptForNewAddress(true);
						else
							mapFragment.getMapAsync(context);
					}
				} else { //Invalid Address
					Toast toast = Toast.makeText(context, "Invalid Address", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				finish();
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});

		runOnUiThread(new Runnable() {
			public void run() {
				builder.show();
			}
		});
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
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		map = googleMap;

		int[] colors = {Color.RED, Color.GREEN, Color.BLUE};
		CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
		Directions d = new Directions(0);
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String[] dateTime = df.format(arrival).split(" ");
			d = api.getTripArriveBy(o_latitude, o_longitude, d_latitude, d_longitude, dateTime[0], dateTime[1], "1", "arrive");
		} catch (Exception e) {}
		if(d == null) {
			Toast toast = Toast.makeText(this, "No bus route found.", Toast.LENGTH_SHORT);
			toast.show();
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

		googleClient = new GoogleApiClient.Builder(getApplicationContext())
				.addApi(LocationServices.API)
				.addConnectionCallbacks(googleCallbacks)
				.build();
		googleClient.connect();
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
