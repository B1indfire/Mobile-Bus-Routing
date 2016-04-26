package uiuc.mbr.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import uiuc.mbr.R;
import uiuc.mbr.alarm.AlarmService;

public class AlarmsMenuActivity extends AppCompatActivity {

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
		Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show();
	}
}
