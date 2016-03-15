package uiuc.mbr;

import java.util.Calendar;

import uiuc.mbr.calendar.Event;

/**A time when the user should be notified, together with a name.*/
public class Alarm implements Comparable<Alarm>
{
	public final Event event;

	public Alarm(Event event)
	{
		this.event = event;
	}


	public Calendar start()
	{
		Calendar out = Calendar.getInstance();
		out.setTime(event.getStart());
		return out;
	}

	@Override
	public int compareTo(Alarm alarm)
	{
		return event.getStart().compareTo(alarm.event.getStart());
	}
}
