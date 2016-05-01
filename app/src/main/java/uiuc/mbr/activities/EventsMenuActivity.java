package uiuc.mbr.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uiuc.mbr.R;

/**Activity that lets the user access activities related to events.*/
public class EventsMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
    }


    public void clickEventBtn (View v){

        startActivity(new Intent(getApplicationContext(), EventSelectionActivity.class));
    }

    public void clickCalendarsBtn (View v)
    {
        startActivity(new Intent(getApplicationContext(), CalendarSelectionActivity.class));
    }

    public void clickAddressListBtn (View v)
    {
        startActivity(new Intent(getApplicationContext(), AddressBookActivity.class));
    }
}
