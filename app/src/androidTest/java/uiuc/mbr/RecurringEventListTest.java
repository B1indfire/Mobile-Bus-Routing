package uiuc.mbr;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;

import java.util.Date;

import uiuc.mbr.activities.EventSelectionActivity;
import uiuc.mbr.calendar.Event;
import uiuc.mbr.event_selection.RecurringEventList;

/**
 *  Android instrumentation tests have poor parameterized testing support
 */
public class RecurringEventListTest extends ActivityInstrumentationTestCase2<EventSelectionActivity> {

    public RecurringEventListTest() {
        super(EventSelectionActivity.class);
    }

    @Test
    public void testActivityExists() {
        Activity act = getActivity();
        assertNotNull(act);
    }

    public void testAddRemove() {
        Activity act = getActivity();
        for(int id1 = -1; id1 < 2; id1++) {
            for(int id2 = -1; id2 < 2; id2++) {
                Event e1 = new Event(0, id1, null, null, null, null, null);
                Event e2 = new Event(0, id2, null, null, null, null, null);
                RecurringEventList.add(e1, act);
                assertTrue(RecurringEventList.contains(e1, act));

                RecurringEventList.add(e2, act);
                assertTrue(RecurringEventList.contains(e1, act));
                assertTrue(RecurringEventList.contains(e2, act));

                RecurringEventList.remove(e1, act);
                assertFalse(RecurringEventList.contains(e1, act));
                if (!e1.fullEquals(e2))
                    assertTrue(RecurringEventList.contains(e2, act));

                RecurringEventList.remove(e2, act);
                assertFalse(RecurringEventList.contains(e2, act));
            }
        }
    }

    public void testAddRemoveExceptions() {
        Activity act = getActivity();
        for(int id1 = -1; id1 < 2; id1++) {
            for(int id2 = -1; id2 < 2; id2++) {
                Date time1 = new Date(System.currentTimeMillis()+60000*id1);
                Date time2 = new Date(System.currentTimeMillis()+60000*id2);
                Event e1 = new Event(0, id1, null, null, null, time1, null);
                Event e2 = new Event(0, id2, null, null, null, time2, null);
                RecurringEventList.addException(e1, act);
                assertTrue(RecurringEventList.containsException(e1, act));

                RecurringEventList.addException(e2, act);
                assertTrue(RecurringEventList.containsException(e1, act));
                assertTrue(RecurringEventList.containsException(e2, act));

                RecurringEventList.removeException(e1, act);
                assertFalse(RecurringEventList.containsException(e1, act));
                if (!e1.fullEquals(e2) && time2.getTime() > System.currentTimeMillis()) {
                    assertTrue(RecurringEventList.containsException(e2, act));

                    RecurringEventList.removeException(e2, act);
                    assertFalse(RecurringEventList.containsException(e2, act));
                }
                else {
                    assertFalse(RecurringEventList.containsException(e2, act));
                }
            }
        }
    }

    public void testContainsNonExempt() {
        Activity act = getActivity();
        Event e1 = new Event(0, 0, null, null, null, new Date(0), null);

        assertFalse(RecurringEventList.containsNonExempt(e1, act));

        RecurringEventList.add(e1, act);
        assertTrue(RecurringEventList.containsNonExempt(e1, act));

        RecurringEventList.addException(e1, act);
        assertFalse(RecurringEventList.containsNonExempt(e1, act));

        RecurringEventList.remove(e1, act);
        assertFalse(RecurringEventList.containsNonExempt(e1, act));

        RecurringEventList.removeException(e1, act);
        assertFalse(RecurringEventList.containsNonExempt(e1, act));
    }
}
