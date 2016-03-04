package uiuc.mbr;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import uiuc.mbr.Alarm;
import uiuc.mbr.OnAlarmActivity;
import uiuc.mbr.R;

public class AlarmActivity extends AppCompatActivity
{
	private TimePicker time;
	private TextView name;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		time = (TimePicker)findViewById(R.id.a_alarm_time);
		name = (TextView)findViewById(R.id.a_alarm_name);
	}



	public void clickAlarmBtn(View v)
	{
		Calendar when = Calendar.getInstance();
		when.set(Calendar.HOUR_OF_DAY, time.getCurrentHour());//the non-deprecated methods require API 23 (Android 6)
		when.set(Calendar.MINUTE, time.getCurrentMinute());
		when.set(Calendar.SECOND, 0);

		Calendar now = Calendar.getInstance();
		if(when.compareTo(now) <= 0)
		{
			Toast.makeText(getApplicationContext(), "Cannot create an alarm in the past.", Toast.LENGTH_LONG).show();
			return;
		}

		Alarm alarm = new Alarm(name.getText().toString(), when);
		Alarm.add(alarm, getApplicationContext());

		name.setText("");
		Toast.makeText(getApplicationContext(), "Alarm created.", Toast.LENGTH_SHORT).show();
	}
}
