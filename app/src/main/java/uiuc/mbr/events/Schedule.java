package uiuc.mbr.events;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import uiuc.mbr.calendar.Event;

//TODO: Store upcoming events in memory or something

/**
 * Created by Scott on 2/28/2016.
 * Keeps track of Events chosen by the user for future routing
 * Consists of static data and methods for global use
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

        //Don't allow if already expired
        Date now = new Date();
        if (e.getStart().before(now))
            return;

        upcomingEvents.add(e);
        //TODO: Launch background stuff (alarms)
    }

    public static void removeEvent(Event e) {
        clearExpiredEvents();

        for (int i = 0; i < upcomingEvents.size(); i++) {
            if (upcomingEvents.get(i).equals(e)) {
                upcomingEvents.remove(i);
                break;
            }
        }

        //TODO: Remove from alarm list
    }

    public static void removeEventsWithAddress(String loc) {
        clearExpiredEvents();

        for (int i = 0; i < upcomingEvents.size(); i++) {
            if (upcomingEvents.get(i).getLocation().equals(loc)) {
                upcomingEvents.remove(i);
            }
        }

        //TODO: Remove from alarm list
    }

    /**
     * Checks if the given Event is in the list of upcoming events using the Event.equals() method
     */
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

    /**
     * Removes all Events from the list of upcoming events that begin before the current Date
     * This should be called at the beginning of any Schedule method
     */
    private static void clearExpiredEvents() {
        for (int i = upcomingEvents.size()-1; i >= 0; i--)
        {
            Event e = upcomingEvents.get(i);
            Date now = new Date();
            if (e.getStart().before(now))
                upcomingEvents.remove(e);
        }
    }


    public static void clearAllEvents() {
        upcomingEvents = new ArrayList<Event>();
    }
}
