package uiuc.mbr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.*;
import java.util.Calendar;

/**A time when the user should be notified, together with a name.*/
public class Alarm implements Comparable<Alarm>
{
	/**Sorted by when, earliest first.*/
	private static final PriorityQueue<Alarm> alarms = new PriorityQueue<>();
	private static final Map<Alarm, PendingIntent> pendingIntents = new HashMap<>();

	public static void add(Alarm alarm, Context context)
	{
		Intent intent = new Intent(context, OnAlarmActivity.class);
		PendingIntent action = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

		alarms.add(alarm);
		pendingIntents.put(alarm, action);
		AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mgr.setExact(AlarmManager.RTC_WAKEUP, alarm.when.getTimeInMillis(), action);
	}

	/**Returns a sorted list of all alarms, earliest first.*/
	public static Alarm[] allAlarms()
	{
		Alarm[] out = alarms.toArray(new Alarm[alarms.size()]);
		Arrays.sort(out);
		return out;
	}

	public static void remove(Alarm alarm, Context context)
	{
		alarms.remove(alarm);
		AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mgr.cancel(pendingIntents.get(alarm));
		pendingIntents.remove(alarm);
	}

	public static void removeAll(Context context)
	{
		AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		for(PendingIntent pending : pendingIntents.values())
			mgr.cancel(pending);
		alarms.clear();
		pendingIntents.clear();
	}



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
