package uiuc.mbr;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import uiuc.mbr.calendar.Calendar;
import uiuc.mbr.calendar.CalendarService;
import uiuc.mbr.calendar.Event;

/**
 * Created by Richard Shen on 2/19/2016.
 */
public class CalendarServiceTest extends AndroidTestCase {

    private Context ctxt;
    private CalendarService cs;
    private ContentResolver cr;
    private Cursor cur;

    @Override
    public void setUp() {
        ctxt = mock(Context.class);
        cs = new CalendarService(ctxt);
        cr = mock(ContentResolver.class);
        cur = mock(Cursor.class);
        when(ctxt.getContentResolver()).thenReturn(cr);
        when(cr.query(any(Uri.class), any((new String[]{}).getClass()), anyString(),
                any((new String[]{}).getClass()), anyString())).thenReturn(cur);
    }

    @Test
    public void testGetCalendars() {
        when(cur.moveToNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(cur.getLong(0)).thenReturn(0L).thenReturn(1L);
        when(cur.getString(1)).thenReturn("Calendar1").thenReturn("Calendar2");

        ArrayList<Calendar> cals = cs.getCalendars();
        assertNotNull(cals);
        assertEquals(2, cals.size());
        assertEquals(0L, cals.get(0).getId());
        assertEquals("Calendar1", cals.get(0).getName());
        assertEquals(1L, cals.get(1).getId());
        assertEquals("Calendar2", cals.get(1).getName());
    }

    @Test
    public void testGetCalendars_NoCalendars() {
        when(cur.moveToNext()).thenReturn(false);

        ArrayList<Calendar> cals = cs.getCalendars();
        assertNotNull(cals);
        assertEquals(0, cals.size());
    }

    @Test
    public void testGetEvents() {
        when(cur.moveToNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(cur.getLong(0)).thenReturn(0L).thenReturn(1L);
        when(cur.getLong(1)).thenReturn(0L).thenReturn(1L);
        when(cur.getString(2)).thenReturn("Event1").thenReturn("Event2");
        when(cur.getString(3)).thenReturn("Test1").thenReturn("Test2");
        when(cur.getString(4)).thenReturn("TestLand1").thenReturn("TestLand2");
        when(cur.getLong(5)).thenReturn(10000000L).thenReturn(20000000L);
        when(cur.getLong(6)).thenReturn(20000000L).thenReturn(30000000L);

        ArrayList<Event> events = cs.getEvents(0, 0);
        assertNotNull(events);
        assertEquals(2, events.size());

        assertEquals(0L, events.get(0).getCalendarId());
        assertEquals("Event1", events.get(0).getName());

        assertEquals(1L, events.get(1).getParentEventId());
        assertEquals("Event2", events.get(1).getName());
        assertEquals("Test2", events.get(1).getDescription());
        assertEquals("TestLand2", events.get(1).getLocation());
        assertEquals(new Date(20000000L), events.get(1).getStart());
        assertEquals(new Date(30000000L), events.get(1).getEnd());
    }

    @Test
    public void testGetEventsNext24Hours() {

    }
}
