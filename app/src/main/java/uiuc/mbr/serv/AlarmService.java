package uiuc.mbr.serv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.*;

import uiuc.mbr.Alarm;
import uiuc.mbr.OnAlarmActivity;
import uiuc.mbr.calendar.Event;

/**Keeps track of alarms and when they should trigger.
 * Launches OnAlarmActivity when an alarm is triggered.
 * Don't launch this service directly--stick to using the static methods.*/
public class AlarmService extends Service
{
	private static final String UNTRIGGERED_ALARMS_FILE = "untriggered_alarms";
	private static final String IDSMAP_FILE = "idsmap";

	private static Queue<Alarm> untriggeredAlarms = new PriorityQueue<>();
	@Nullable private static Alarm triggeredAlarm = null;
	private static Map<Long, Alarm> idsMap = new HashMap<>();

	/**
	 * Creates an alarm for the given Event
	 * The Alarm's time is set based on the traveling distance to the event
	 */
	public static void addAlarm(Event event, Context context)
	{
		Alarm alarm = new Alarm(event);

		//Add alarm to the queues
		if(idsMap.containsKey(alarm.event.getParentEventId()))
			return;
		untriggeredAlarms.add(alarm);
		idsMap.put(alarm.event.getParentEventId(), alarm);

		new AlarmAddTask(alarm, context).execute();
	}

	/**
	 * Updates the Alarm time for the next upcoming alarm using the phone's current GPS location
	 */
	public static void updateFirst(Context context) {
		new UpdateFirstTask(context).execute();
	}

	/**
	 * Removes any alarms associated with the given EventId
	 * Updates the subsequent alarm's time if necessary
	 */
	public static void remove(long eventId, Context context)
	{
		Alarm alarm = idsMap.get(eventId);

		new AlarmRemoveTask(alarm, context).execute();
	}

	/**
	 * Updates the start time of all Alarms
	 * Use this method when global changes have occured, such as changing Settings
	 */
	public static void updateAllAlarmTimes(Context context) {
		new UpdateAllAlarmsTask(context).execute();
	}

	private static int getIndex(Alarm[] alarms, Alarm alarm) {
		int index=-1;
		for (int i = 0; i < alarms.length; i++) {
			if(alarms[i].equals(alarm))
				index = i;
		}
		return index;
	}


	/**
	 * An asynchronous Task that handles adding new Alarms to the schedule
	 */
	private static class AlarmAddTask extends AsyncTask<Void, Void, Void> {

		private Context context;
		private Alarm alarm;

		public AlarmAddTask(Alarm a, Context c) {
			this.alarm = a;
			this.context = c;
		}

		@Override
		protected Void doInBackground(Void... params) {
			LatLng startingLoc = null;

			Alarm[] alarms = Arrays.copyOf(untriggeredAlarms.toArray(), untriggeredAlarms.size(), Alarm[].class);
			Arrays.sort(alarms);
			int index = getIndex(alarms, alarm);

			if(index>0){
				if(within2Hours(alarms[index-1], alarm)) {
					startingLoc = alarms[index-1].event.getLatLong();
				}
			}

			//Not within 2 hrs of previous event = Use current GPS location
			if (startingLoc == null) {
				startingLoc = getCurrentLocation(context);
			}

			//Set when the alarm needs to go off based on the starting location
			alarm.setAlarmTime(startingLoc, context);

			//Check if the next event is within two hours of this event's end time.
			if(index!=alarms.length-1){
				if(within2Hours(alarm, alarms[index+1])) {
					alarms[index+1].setAlarmTime(alarm.event.getLatLong(), context);
				}
			}

			return null;
		}



		@Override
		protected void onPostExecute(Void result) {
			Log.wtf("AddAlarm", "Completed AddAlarm for " + alarm.event.toString());

			saveAlarms(context);
			run(context);
		}
	}

	/**
	 * An asynchronous Task that handles updating the first Alarm in the queue
	 */
	private static class UpdateFirstTask extends AsyncTask<Void, Void, Void> {

		private Context context;
		private Alarm alarm;

		public UpdateFirstTask(Context c) {
			this.context = c;
		}

		@Override
		protected Void doInBackground(Void... params) {
			LatLng currentLoc = null;
			alarm = untriggeredAlarms.poll();

			if (alarm != null) {
				currentLoc = getCurrentLocation(context);
				alarm.setAlarmTime(currentLoc, context);
				untriggeredAlarms.add(alarm);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			saveAlarms(context);
			run(context);
		}
	}

	/**
	 * An asynchronous task that handles removing an Alarm
	 */
	private static class AlarmRemoveTask extends AsyncTask<Void, Void, Void> {

		private Alarm alarm;
		private Context context;

		public AlarmRemoveTask(Alarm a, Context c) {
			this.alarm = a;
			this.context = c;
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (untriggeredAlarms.size() == 0)
				return null;

			Alarm[] alarms = Arrays.copyOf(untriggeredAlarms.toArray(), untriggeredAlarms.size(), Alarm[].class);
			Arrays.sort(alarms);
			int index = getIndex(alarms, alarm);
			if (index == -1)
				return null;

			if (index != untriggeredAlarms.size()-1) {
				LatLng startLoc = null;

				if (index != 0)
					if (within2Hours(alarms[index-1], alarms[index+1]))
						startLoc = alarms[index-1].event.getLatLong();

				if (startLoc == null) {
					startLoc = getCurrentLocation(context);
				}

				alarms[index+1].setAlarmTime(startLoc, context);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			untriggeredAlarms.remove(alarm);
			if(triggeredAlarm == alarm)
				triggeredAlarm = null;
			idsMap.remove(alarm.event.getParentEventId());

			saveAlarms(context);
		}
	}

	/**
	 * An asynchronous task that handles updating all alarm times after a global settings change
	 */
	private static class UpdateAllAlarmsTask extends AsyncTask<Void, Void, Void> {

		private Context context;

		public UpdateAllAlarmsTask(Context c) {
			this.context = c;
		}

		@Override
		protected Void doInBackground(Void... params) {

			Alarm[] alarms = Arrays.copyOf(untriggeredAlarms.toArray(), untriggeredAlarms.size(), Alarm[].class);

			for (int index = 0; index < alarms.length; index++) {

				LatLng startLoc = null;

				if (index != 0)
					if (within2Hours(alarms[index-1], alarms[index]))
						startLoc = alarms[index].event.getLatLong();

				if (startLoc == null) {
					startLoc = getCurrentLocation(context);
				}

				alarms[index].setAlarmTime(startLoc, context);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			saveAlarms(context);
		}
	}


	/**
	 * Determines if the event associated with alarm1 ends within 2 hours of the event from alarm2's start time
	 */
	private static boolean within2Hours(Alarm a1, Alarm a2) {
		return a2.event.getStart().getTime()-a1.event.getEnd().getTime() < 7200000;
	}

	/**
	 * Returns the current device GPS coordinates as a LatLng
	 */
	private static LatLng getCurrentLocation(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // Network provider doesn't require line of sight to the sky
		return new LatLng(location.getLatitude(), location.getLongitude());
	}


	public static List<Alarm> getUntriggeredAlarms(){return new ArrayList<>(untriggeredAlarms);}

	@Nullable public static Alarm getTriggeredAlarm(){return triggeredAlarm;}
	public static void clearTriggeredAlarm(Context context)
	{
		if(triggeredAlarm == null)
			throw new IllegalStateException();
		triggeredAlarm = null;
		run(context);
	}


	/**
	 * Removes all alarms from the schedule
	 */
	public static void removeAll(Context c)
	{
		untriggeredAlarms.clear();
		triggeredAlarm = null;
		idsMap.clear();
		saveAlarms(c);
	}

	/**Returns an alarm if we have one for that event.*/
	@Nullable public static Alarm getForEvent(long eventId)
	{
		return idsMap.get(eventId);
	}


	private static void run(Context context)
	{
		context.startService(new Intent(context, AlarmService.class));
	}

	@Nullable @Override
	public IBinder onBind(Intent intent){return null;}

	@Override
	public int onStartCommand(Intent i, int flags, int startId)
	{
		loadAlarms(this);

		System.out.println(untriggeredAlarms);
		Alarm next = untriggeredAlarms.peek();
		if(next != null)
		{
			long nextTime = next.getAlarmTime().getTime().getTime();
			long now = System.currentTimeMillis();
			if(nextTime <= now)
			{
				Intent start = new Intent(getApplicationContext(), OnAlarmActivity.class);
				start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(start);
				untriggeredAlarms.poll();//remove
				if(triggeredAlarm != null)
					throw new IllegalStateException();
				triggeredAlarm = next;
			}
			else
			{
				AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
				Intent innerAction = new Intent(this, AlarmService.class);
				PendingIntent action = PendingIntent.getService(this, 0, innerAction, 0);
				mgr.setExact(AlarmManager.RTC_WAKEUP, nextTime, action);
			}
		}

		saveAlarms(this);

		return super.onStartCommand(i, flags, startId);
	}


	/**
	 * Loads all Alarm data from device memory
	 */
	public static void loadAlarms(Context c){
		Queue<Alarm> untriggeredTemp = new PriorityQueue<>();
		try {
			FileInputStream fis = c.openFileInput(UNTRIGGERED_ALARMS_FILE);
			ObjectInputStream ois = new ObjectInputStream(fis);
			untriggeredAlarms = (Queue<Alarm>) ois.readObject();
			ois.close();
			fis.close();

			fis = c.openFileInput(IDSMAP_FILE);
			ois = new ObjectInputStream(fis);
			idsMap = (Map<Long, Alarm>) ois.readObject();
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (untriggeredAlarms == null)
			untriggeredAlarms = new PriorityQueue<>();
		if(idsMap == null)
			idsMap = new HashMap<>();
	}

	/**
	 * Saves all Alarm data to device memory
	 */
	public static void saveAlarms(Context c){
		FileOutputStream fos = null;
		try {
			fos = c.openFileOutput(UNTRIGGERED_ALARMS_FILE, Context.MODE_APPEND);
			fos.write(("").getBytes());
			fos.close();
			fos = null;
			fos = c.openFileOutput(IDSMAP_FILE, Context.MODE_APPEND);
			fos.write(("").getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Write to files
		fos = null;
		try {
			fos = c.openFileOutput(UNTRIGGERED_ALARMS_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(untriggeredAlarms);
			oos.close();
			fos.close();
			fos = null;
			oos = null;

			fos = c.openFileOutput(IDSMAP_FILE, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(idsMap);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
