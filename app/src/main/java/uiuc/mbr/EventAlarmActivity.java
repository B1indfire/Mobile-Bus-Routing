package uiuc.mbr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Calendar;
import java.util.List;

import uiuc.mbr.calendar.CalendarService;
import uiuc.mbr.calendar.Event;

public class EventAlarmActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_alarm);
	}


	public void handleButton(View v)
	{
		Alarm.removeAll(getApplicationContext());

		CalendarService service = new CalendarService(getApplicationContext());
		List<Event> events = service.getEventsNext24Hours();
		for(Event event : events)
		{
			Calendar when = Calendar.getInstance();
			when.setTime(event.getStart());
			Alarm alarm = new Alarm(event.getName(), when);
			Alarm.add(alarm, getApplicationContext());
		}
	}
}
