package uiuc.mbr.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import uiuc.mbr.R;
import uiuc.mbr.alarm.AlarmService;

/**Activity that lets the user reach activities that let them navigate places.*/
public class NavigationMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
    }

    public void clickMapBtn(View v) {
        startActivity(new Intent(getApplicationContext(), MapActivity.class));
    }

    public void clickStpBtn(View v) {
        startActivity(new Intent(getApplicationContext(), GetStopsActivity.class));
    }

    public void clickQuickGotoVtn(View v)
    {
        startActivity(new Intent(getApplicationContext(), GotoActivity.class));
    }
}
