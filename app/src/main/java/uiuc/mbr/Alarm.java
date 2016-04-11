package uiuc.mbr;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import uiuc.mbr.calendar.Event;

/**A time when the user should be notified, together with a name.*/
public class Alarm implements Comparable<Alarm>, Serializable
{
	public final Event event;
	private Calendar alarmTime;

	public Alarm(Event event)
	{
		this.event = event;
	}

	/**
	 * Sets an appropriate time for the alarm to go off assuming the user is leaving from startLocation
	 * Takes app settings into account, such as maxWalkDistance and arrivalTimeOffset
	 */
	public void setAlarmTime(LatLng startLocation, Context c) {

		//Set arrival time based on min arrival setting
		Calendar arrivalTime = Calendar.getInstance();
		arrivalTime.setTime(event.getStart());
		Integer arrivalOffset = SettingsActivity.loadMinArrFromMemory(c);
		int offset = (arrivalOffset == null) ? 0 : arrivalOffset;
		arrivalTime.add(Calendar.MINUTE, -1 * offset);
		Date arrivalTimeAsDate = arrivalTime.getTime();

		alarmTime = getTimeFromApi(startLocation, arrivalTimeAsDate, c);
	}

	/**
	 * Determines the appropriate departure time to get to the events location at arrivalTime when leaving from startLocation
	 */
	private Calendar getTimeFromApi(LatLng startLocation, Date arrivalTime, Context c) {
		CumtdApi api = CumtdApi.create();

		Calendar departTime = Calendar.getInstance();
		departTime.setTime(arrivalTime);

		Integer tempW = SettingsActivity.loadMaxWalkFromMemory(c);
		int tempWInt = (tempW == null) ? 1 : tempW;
		double maxWalk = tempWInt*.1;

		try {
			//Get directions from CUMTD API
			Directions dir = api.getTripArriveBy("" + startLocation.latitude, "" + startLocation.longitude,
					"" + event.getLatLong().latitude, "" + event.getLatLong().longitude,
					"" + arrivalTime.getDate(), "" + arrivalTime.getTime(),
					"" + maxWalk, "arrive");

			int duration = (dir == null) ? 0 : dir.getDuration();

			//Determine appropriate leaving time from directions
			departTime.add(Calendar.MINUTE, -1 * duration);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return departTime;
	}

	public Calendar getAlarmTime()
	{
		return alarmTime;
	}

	@Override
	public int compareTo(Alarm alarm)
	{
		return event.getStart().compareTo(alarm.event.getStart());
	}

	@Override
	public boolean equals(Object other){
		if(!(other instanceof Alarm))
			return false;

		Alarm a = (Alarm) other;
		return this.event.equals(a.event);
	}
}
