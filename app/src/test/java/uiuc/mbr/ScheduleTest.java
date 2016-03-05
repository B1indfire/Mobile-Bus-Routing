package uiuc.mbr;

import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import uiuc.mbr.calendar.Event;
import uiuc.mbr.events.Schedule;

/**
 * Created by Scott on 3/2/2016.
 */
public class ScheduleTest extends AndroidTestCase {

    private Event validFutureEvent;
    private Event invalidPastEvent;
    private Event validPastEvent;
    private Event invalidFutureEvent;
    private Event validFutureEvent2;

    @Override
    public void setUp() {
        long now = (new Date()).getTime();
        Date later1 = new Date(now + 1000);
        Date later2 = new Date(now + 2000);
        Date later3 = new Date(now + 3000);
        Date before1 = new Date(now - 3000);
        Date before2 = new Date(now - 2000);

        validFutureEvent = new Event(0, 0, "test event 1", "desc 1", "505 E Healey Champaign il", later1, later2);
        invalidPastEvent = new Event(1, 1, "test event 2", "desc 2", "siebz", before1, before2);
        validPastEvent = new Event(0, 0, "test event 3", "desc 3", "505 E Healey Champaign il", before1, before2);
        invalidFutureEvent = new Event(1, 1, "test event 4", "desc 4", "siebz", later1, later2);
        validFutureEvent2 = new Event(0, 0, "test event 5", "desc 1", "505 E Healey Champaign il", later2, later3);

        Schedule.clearAllEvents();
    }

    @Test
    public void testAddEvent_expiredEvent() {
        Schedule.addEvent(validPastEvent);

        ArrayList<Event> e = Schedule.getUpcomingEvents();
        assertNotNull(e);
        assertEquals(0, e.size());
    }

    @Test
    public void testAddEvent_validEvent(){
        Schedule.addEvent(validFutureEvent);
        Schedule.addEvent(invalidFutureEvent);

        ArrayList<Event> e = Schedule.getUpcomingEvents();
        assertNotNull(e);
        assertEquals(2, e.size());
        assertTrue(e.get(0).equals(validFutureEvent));
        assertTrue(e.get(1).equals(invalidFutureEvent));
    }

    @Test
    public void testAddEvent_sameValidEvent(){
        Schedule.addEvent(validFutureEvent);
        Schedule.addEvent(validFutureEvent);

        ArrayList<Event> e = Schedule.getUpcomingEvents();
        assertNotNull(e);
        assertEquals(1, e.size());
        assertTrue(e.get(0).equals(validFutureEvent));
    }

    @Test
    public void testRemoveEvent_eventInList() {
        Schedule.addEvent(validFutureEvent);
        Schedule.addEvent(invalidFutureEvent);

        Schedule.removeEvent(validFutureEvent);

        ArrayList<Event> e = Schedule.getUpcomingEvents();
        assertNotNull(e);
        assertEquals(1, e.size());
        assertTrue(e.get(0).equals(invalidFutureEvent));
    }

    @Test
    public void testRemoveEvent_eventNotInList() {
        Schedule.addEvent(validFutureEvent);
        Schedule.addEvent(invalidFutureEvent);

        Schedule.removeEvent(validPastEvent);

        ArrayList<Event> e = Schedule.getUpcomingEvents();

        assertNotNull(e);
        assertEquals(2, e.size());
        assertTrue(e.get(0).equals(validFutureEvent));
        assertTrue(e.get(1).equals(invalidFutureEvent));
    }

    @Test
    public void testContains() {
        Schedule.addEvent(validFutureEvent);
        Schedule.addEvent(invalidFutureEvent);

        assertTrue(Schedule.contains(validFutureEvent));
        assertTrue(Schedule.contains(invalidFutureEvent));
        assertFalse(Schedule.contains(validPastEvent));
    }

    @Test
    public void testGetNextEvent() {
        Schedule.addEvent(validPastEvent);
        Schedule.addEvent(validFutureEvent2);
        Schedule.addEvent(validFutureEvent);

        assertTrue(Schedule.getNextEvent().equals(validFutureEvent));
    }

    @Test
    public void testClearAllEvents() {
        Schedule.addEvent(validPastEvent);
        Schedule.addEvent(validFutureEvent2);
        Schedule.addEvent(validFutureEvent);

        Schedule.clearAllEvents();

        assertNotNull(Schedule.getUpcomingEvents());
        assertEquals(0, Schedule.getUpcomingEvents().size());
    }
}

