package uiuc.mbr.calendar;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Event {
    private long calendarId;
    private long parentEventId;
    private String name;
    private String description;
    private String location;
    private Date start;
    private Date end;

    private LatLng latLong;

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

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public long getParentEventId() {
        return parentEventId;
    }

    public void setParentEventId(long parentEventId) {
        this.parentEventId = parentEventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String toString(){
        return "EVENT{CAL_ID: "+calendarId+", PARENT_EVENT_ID: "+parentEventId+", NAME: "+name+", DESCRIPTION: "+description
                +", LOCATION: "+location+", START: "+start.toString()+", END: "+end.toString()+"}";
    }

    public LatLng getLatLong() {
        return latLong;
    }

    public void setLatLong(LatLng latLong) {
        this.latLong = latLong;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Event))
            return false;
        Event e2 = (Event) other;
        return this.name.equals(e2.getName()) && this.getStart().equals(e2.getStart());
    }
}

