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

public class Alarms  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);
    }

    public void clickViewAlarmsBtn(View v)
    {
        startActivity(new Intent(getApplicationContext(), OnAlarmActivity.class));
    }

    public void clickUpdateFirst(View view) {
        AlarmService.updateFirst(this);
    }
}
