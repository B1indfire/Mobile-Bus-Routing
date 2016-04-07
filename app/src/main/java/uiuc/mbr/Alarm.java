package uiuc.mbr;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import uiuc.mbr.calendar.Event;

/**A time when the user should be notified, together with a name.*/
public class Alarm implements Comparable<Alarm>
{
	public final Event event;
	private Calendar alarmTime;

	public Alarm(Event event)
	{
		this.event = event;
	}


	public void setAlarmTime(LatLng startLocation, Context c) {
		CumtdApi api = CumtdApi.create();

		//Set arrival time based on min arrival setting
		Calendar arrivalTime = Calendar.getInstance();
		arrivalTime.setTime(event.getStart());
		Integer arrivalOffset = SettingsActivity.loadMinArrFromMemory(c);
		int offset = (arrivalOffset == null) ? 0 : arrivalOffset;
		arrivalTime.add(Calendar.MINUTE, -1 * offset);
		Date arrivalTimeAsDate = arrivalTime.getTime();

		alarmTime = getTimeFromApi(startLocation, c, api, arrivalTime, arrivalTimeAsDate);
	}

	private Calendar getTimeFromApi(LatLng startLocation, Context c, CumtdApi api, Calendar arrivalTime, Date arrivalTimeAsDate) {
		Calendar time;
		try {
			//Get directions from CUMTD API
			Directions dir = api.getTripArriveBy("" + startLocation.latitude, "" + startLocation.longitude,
					                             "" + event.getLatLong().latitude, "" + event.getLatLong().longitude,
					                             "" + arrivalTimeAsDate.getDate(), "" + arrivalTimeAsDate.getTime(),
					                             "arrive", c);

			int duration = (dir == null) ? 0 : dir.getDuration();

			//Determine appropriate leaving time from directions
			Calendar departTime = Calendar.getInstance();
			departTime.setTime(arrivalTimeAsDate);
			departTime.add(Calendar.MINUTE, -1 * duration);
			time = departTime;

		} catch (IOException e) {
			e.printStackTrace();
			time = arrivalTime;
		} catch (JSONException e) {
			e.printStackTrace();
			time = arrivalTime;
		}
		return time;
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
}
