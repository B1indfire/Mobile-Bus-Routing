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

			Log.wtf("AddAlarm", "AddAlarm Started");

			Alarm[] temp = Arrays.copyOf(untriggeredAlarms.toArray(), untriggeredAlarms.size(), Alarm[].class);
			Arrays.sort(temp);
			int index=-1;
			for(int i =0; i<temp.length; i++){
				if(temp[i].equals(alarm))
					index=i;
			}
			Log.wtf("AddAlarm", "Index = " + index);

			if(index>0){
				if((alarm.event.getStart().getTime()-temp[index-1].event.getEnd().getTime())<7200000){ //2 hrs in millis
					startingLoc = temp[index-1].event.getLatLong();
				}
			}

			//Not within 2 hrs of previous event = Use current GPS location
			if (startingLoc == null) {
				//Get the current location
				LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
				Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // Network provider doesn't require line of sight to the sky
				startingLoc = new LatLng(location.getLatitude(), location.getLongitude());
			}

			//Set when the alarm needs to go off based on the starting location
			alarm.setAlarmTime(startingLoc, context);

			//Check if the next event is within two hours of this event's end time.
			if(index!=temp.length-1){//not end of array
				long diff = temp[index+1].event.getStart().getTime()-alarm.event.getEnd().getTime();
				Log.wtf("AddAlarm", Long.toString(diff));
				if(diff<7200000){ //2 hrs in millis
					Log.wtf("AddAlarm", "change next event start");
					temp[index+1].setAlarmTime(alarm.event.getLatLong(), context);
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			saveAlarms(context);
			run(context);
		}
	}


	public static void remove(long eventId, Context context)
	{
		Alarm alarm = idsMap.get(eventId);

		new AlarmRemoveTask(alarm, context).execute();
	}

	private static class AlarmRemoveTask extends AsyncTask<Void, Void, Void> {

		private Alarm alarm;
		private Context context;

		public AlarmRemoveTask(Alarm a, Context c) {
			this.alarm = a;
			this.context = c;
		}

		@Override
		protected Void doInBackground(Void... params) {

			Log.wtf("RemoveAlarm", "RemoveAlarm Started");

			Alarm[] temp = Arrays.copyOf(untriggeredAlarms.toArray(), untriggeredAlarms.size(), Alarm[].class);
			Arrays.sort(temp);
			int index=-1;
			for(int i =0; i<temp.length; i++){
				if(temp[i].equals(alarm))
					index=i;
			}
			Log.wtf("RemoveAlarm", "Index = " + index);

			if (index != untriggeredAlarms.size()-1) {
				LatLng startLoc = null;

				if (index != 0)
					if (temp[index+1].event.getStart().getTime()-temp[index-1].event.getEnd().getTime() < 7200000) //Less than 2 hrs
						startLoc = temp[index-1].event.getLatLong();

				if (startLoc == null) {
					//Get the current location
					LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
					Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // Network provider doesn't require line of sight to the sky
					startLoc = new LatLng(location.getLatitude(), location.getLongitude());
				}

				temp[index+1].setAlarmTime(startLoc, context);
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

	public static List<Alarm> getUntriggeredAlarms(){return new ArrayList<>(untriggeredAlarms);}

	@Nullable public static Alarm getTriggeredAlarm(){return triggeredAlarm;}
	public static void clearTriggeredAlarm(Context context)
	{
		if(triggeredAlarm == null)
			throw new IllegalStateException();
		triggeredAlarm = null;
		run(context);
	}

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
