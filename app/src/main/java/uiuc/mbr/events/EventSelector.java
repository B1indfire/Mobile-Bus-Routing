package uiuc.mbr.events;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uiuc.mbr.R;
import uiuc.mbr.calendar.CalendarService;
import uiuc.mbr.calendar.Event;

/**
 * Created by varungove on 2/27/16.
 */
public class EventSelector extends AppCompatActivity {

    private Geocoder geocoder;
    private CalendarService calService;
    private ArrayList<Event> eventlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        calService = new CalendarService(this.getApplicationContext());
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    @Override
    protected void onResume(){
        super.onResume();
        LinearLayout my_layout = (LinearLayout)findViewById(R.id.events);
        my_layout.removeAllViews();

        eventlist = calService.getEventsNext24Hours();

        //TODO: Set check boxes as checked if already stored

        //From: http://stackoverflow.com/questions/13226353/android-checkbox-dynamically
        for (int i = 0; i < eventlist.size(); i++)
        {
            TableRow row =new TableRow(this);
            row.setId(i);
            row.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            CheckBox checkBox = new CheckBox(this);
            checkBox.setOnCheckedChangeListener(new EventCheckboxListener(this, eventlist.get(i)));
            checkBox.setId(i);
            checkBox.setText(eventlist.get(i).getName());
            row.addView(checkBox);
            my_layout.addView(row);
        }

    }


    public Address getEventLocation(Event event) {
        String location = event.getLocation();
        List<Address> address = new ArrayList<Address>() {};
        try {
            address = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address.size() == 0)
            return null;
        else
            return address.get(0);
    }

    public Address getEventLocation(String location) {
        List<Address> address = new ArrayList<Address>() {};
        try {
            address = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address.size() == 0)
            return null;
        else
            return address.get(0);
    }


    private class EventCheckboxListener implements CompoundButton.OnCheckedChangeListener {

        private Event event;
        private Activity parent;
        private String addressInput;

        public EventCheckboxListener(Activity parentActivity, Event e) {
            parent = parentActivity;
            event = e;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Address a = getEventLocation(event);
            if (isChecked) {
                if (a == null) {
                    //TODO: Check invalid address to see if user has already supplied real address

                    a = getValidAddress();

                    //User cancels
                    if (a == null) {
                        //TODO: Uncheck box
                        return;
                    }

                }
                //TODO: Store to alarm
            } else {
                //TODO: Remove from alarm
            }
        }

        private Address getValidAddress() {
            Address a = null;
            while (a == null) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                builder.setTitle("Enter an Address");

                // Set up the input
                final EditText input = new EditText(parent);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                addressInput = "";

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addressInput = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                Log.d("ADDRESS", addressInput);

                parent.runOnUiThread(new Runnable() {
                    public void run() {
                        builder.show();
                    }
                });

                Log.d("ADDRESS", addressInput);

                //Cancel out
                if (addressInput.equals(""))
                    break;

                a = getEventLocation(addressInput);
            }

            return a;
        }
    }

}
