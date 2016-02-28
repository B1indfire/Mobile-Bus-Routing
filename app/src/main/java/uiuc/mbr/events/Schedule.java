package uiuc.mbr.events;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import uiuc.mbr.calendar.Event;

/**
 * Created by Scott on 2/28/2016.
 */
public class Schedule {

    private static ArrayList<Event> upcomingEvents = new ArrayList<>();


    public static ArrayList<Event> getUpcomingEvents() {
        clearExpiredEvents();
        return upcomingEvents;
    }

    public static void setUpcomingEvents(ArrayList<Event> events) {
        upcomingEvents = events;
    }

    public static void addEvent(Event e) {
        clearExpiredEvents();
        if (contains(e))
            return;

        upcomingEvents.add(e);
        Log.d("Schedule", "Event Added: " + e);
        for (Event ev : upcomingEvents)
            Log.d("Schedule", ev.toString());
        //TODO: Launch background stuff (alarms)
    }

    public static void removeEvent(Event e) {
        clearExpiredEvents();
        upcomingEvents.remove(e);

        //TODO: Remove from alarm list
    }

    public static boolean contains(Event e) {
        for (Event ev : upcomingEvents)
            if (e.equals(ev))
                return true;
        return false;
    }

    public static Event getNextEvent() {
        clearExpiredEvents();
        if (upcomingEvents.size() == 0)
            return null;
        Event first = upcomingEvents.get(0);
        for (Event e : upcomingEvents) {
            if (e.getStart().before(first.getStart()))
                first = e;
        }
        return first;
    }

    private static void clearExpiredEvents() {
        for (int i = upcomingEvents.size()-1; i >= 0; i--)
        {
            Event e = upcomingEvents.get(i);
            Date now = new Date();
            if (e.getStart().before(now))
                upcomingEvents.remove(e);
        }
    }

}
