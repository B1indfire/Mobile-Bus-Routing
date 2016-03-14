package uiuc.mbr;

import java.util.Calendar;

/**A time when the user should be notified, together with a name.*/
public class Alarm implements Comparable<Alarm>
{
	public final String name;
	public final Calendar when;

	public Alarm(String name, Calendar when)
	{
		this.name = name;
		this.when = when;
	}


	@Override
	public int compareTo(Alarm alarm)
	{
		return when.compareTo(alarm.when);
	}
}
