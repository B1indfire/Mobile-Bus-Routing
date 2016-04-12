package uiuc.mbr.calendar;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Event implements Serializable{
    private long calendarId;
    private long parentEventId;
    private String name;
    private String description;
    private String location;
    private Date start;
    private Date end;

    //private LatLng latLong;
    private double latitude;
    private double longitude;

    public Event(long calendarId, long parentEventId, String name, String description, String location, Date start, Date end) {
        this.calendarId = calendarId;
        this.parentEventId = parentEventId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
    }

    public long getCalendarId() {
        return calendarId;
    }

    public long getParentEventId() {
        return parentEventId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String toString(){
        return "EVENT{CAL_ID: "+calendarId+", PARENT_EVENT_ID: "+parentEventId+", NAME: "+name+", DESCRIPTION: "+description
                +", LOCATION: "+location+", START: "+start.toString()+", END: "+end.toString()+"}";
    }

    public LatLng getLatLong() {
        return new LatLng(latitude, longitude);
    }

    public void setLatLong(LatLng latLong) {
        this.latitude = latLong.latitude;
        this.longitude = latLong.longitude;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Event))
            return false;
        Event e2 = (Event) other;
        return this.name.equals(e2.getName()) && this.getStart().equals(e2.getStart());
    }


	/**Tests all members for equality.*/
	public boolean fullEquals(Event that)
	{
		return this.calendarId == that.calendarId &&
				this.parentEventId == that.parentEventId &&
				Objects.equals(this.name, that.name) &&
				Objects.equals(this.description, that.description) &&
				Objects.equals(this.location, that.location) &&
				Objects.equals(this.start, that.start) &&
				Objects.equals(this.end, that.end);
	}



    /**Puts values from an Event into a Bundle so it can be recreated later with importFrom().
	 * All keys used will start with the requested prefix; set a prefix that will avoid collisions with other code.*/
    public void export(String prefix, Bundle bundle)
    {
		bundle.putLong(prefix + "calendarId", calendarId);
		bundle.putLong(prefix + "parentEventId", parentEventId);
		bundle.putString(prefix + "name", name);
		bundle.putString(prefix + "description", description);
		bundle.putString(prefix + "location", location);
		bundle.putLong(prefix + "start", start.getTime());
		bundle.putLong(prefix + "end", end.getTime());
    }


	/**Recreates an Event using a prefix-bundle combination previously passed to export().*/
	public static Event importFrom(String prefix, Bundle bundle)
	{
		long calendarId = bundle.getLong(prefix + "calendarId");
		long parentEventId = bundle.getLong(prefix + "parentEventId");
		String name = bundle.getString(prefix + "name");
		String description = bundle.getString(prefix + "description");
		String location = bundle.getString(prefix + "location");
		Date start = new Date(bundle.getLong(prefix + "start"));
		Date end = new Date(bundle.getLong(prefix + "end"));

		return new Event(calendarId, parentEventId, name, description, location, start, end);
	}
}

