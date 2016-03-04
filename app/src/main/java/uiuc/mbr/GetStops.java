package uiuc.mbr;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uiuc.mbr.CumtdApi;
import uiuc.mbr.R;

public class GetStops extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_stops);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Allow network.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get location.
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        // Get nearest stops.
        CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
        List<String> list = new ArrayList<String>();
        try {
            list = api.getNearestStops("" + latitude, "" + longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sets up buttons for nearest stops and set trigger to display departures on button click.
        final Button button1 = (Button) findViewById(R.id.button1);
        button1.setText(list.get(0));
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                List<String> list = new ArrayList<String>();
                CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
                try {
                    final Button button = (Button) findViewById(R.id.button1);
                    list = api.getDepartures(button.getText().toString().split(":")[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(list.toString());
            }
        });
        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setText(list.get(1));
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                List<String> list = new ArrayList<String>();
                CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
                try {
                    final Button button = (Button) findViewById(R.id.button2);
                    list = api.getDepartures(button.getText().toString().split(":")[0]);
                    list = api.getDepartures(list.get(0).split(":")[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(list.toString());
            }
        });
        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setText(list.get(2));
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                List<String> list = new ArrayList<String>();
                CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
                try {
                    final Button button = (Button) findViewById(R.id.button3);
                    list = api.getDepartures(button.getText().toString().split(":")[0]);
                    list = api.getDepartures(list.get(0).split(":")[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(list.toString());
            }
        });
        final Button button4 = (Button) findViewById(R.id.button4);
        button4.setText(list.get(3));
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                List<String> list = new ArrayList<String>();
                CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
                try {
                    final Button button = (Button) findViewById(R.id.button4);
                    list = api.getDepartures(button.getText().toString().split(":")[0]);
                    list = api.getDepartures(list.get(0).split(":")[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(list.toString());
            }
        });
        final Button button5 = (Button) findViewById(R.id.button5);
        button5.setText(list.get(4));
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                List<String> list = new ArrayList<String>();
                CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
                try {
                    final Button button = (Button) findViewById(R.id.button5);
                    list = api.getDepartures(button.getText().toString().split(":")[0]);
                    list = api.getDepartures(list.get(0).split(":")[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(list.toString());
            }
        });
    }

}
