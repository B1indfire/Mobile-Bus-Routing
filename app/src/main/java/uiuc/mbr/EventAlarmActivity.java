package uiuc.mbr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import uiuc.mbr.calendar.CalendarService;
import uiuc.mbr.calendar.Event;
import uiuc.mbr.serv.AlarmService;

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
		AlarmService.removeAll();

		CalendarService service = new CalendarService(getApplicationContext());
		List<Event> events = service.getEventsNext24Hours();
		for(Event event : events)
		{
			AlarmService.addAlarm(event, getApplicationContext());
		}
	}
}
