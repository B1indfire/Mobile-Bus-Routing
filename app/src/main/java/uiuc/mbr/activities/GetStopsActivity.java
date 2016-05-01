package uiuc.mbr.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uiuc.mbr.directions.CumtdApi;
import uiuc.mbr.R;


/**Displays the names of the closest CUMTD stops to the device’s current location.
 * Selecting a stop will launch the StopListActivity for the chosen stop.*/
 public class GetStopsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_stops);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//TODO what does this code do and why do we have it?
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}

		// Allow network code on the main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Get location.
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // Network provider doesn't require line of sight to the sky
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();

		// Get nearest stops.
		CumtdApi api = CumtdApi.create();
		List<String> list;
		try {
			list = api.getNearestStops("" + latitude, "" + longitude);
		} catch (JSONException | IOException e) {
			throw new RuntimeException(e);
		}

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);
		for (String stop : list) {
			Button btn = new Button(this);
			btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			btn.setText(stop);
			btn.setBackground(getResources().getDrawable(R.drawable.back));
			btn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					ArrayList<String> list;
					CumtdApi api = CumtdApi.create();
					try {
						final Button button = (Button) v;
						list = (ArrayList<String>) api.getDepartures(button.getText().toString().split(":")[0]);
					} catch (IOException | JSONException e) {
						throw new RuntimeException(e);
					}
					Intent intent = new Intent(getBaseContext(), StopListActivity.class);
					intent.putExtra("departures", list);
					startActivity(intent);
				}
			});
			linearLayout.addView(btn);
		}
	}

}
