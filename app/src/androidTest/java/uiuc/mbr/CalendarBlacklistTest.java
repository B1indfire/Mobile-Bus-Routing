package uiuc.mbr;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;

import uiuc.mbr.activities.CalendarSelectionActivity;
import uiuc.mbr.event_selection.CalendarBlacklist;

public class CalendarBlacklistTest extends ActivityInstrumentationTestCase2<CalendarSelectionActivity> {

    public CalendarBlacklistTest() {
        super(CalendarSelectionActivity.class);
    }

    @Test
    public void testActivityExists() {
        Activity act = getActivity();
        assertNotNull(act);
    }

    /**
     *  Android instrumentation tests have poor parameterized testing support
     */
    public void testAddRemove() {
        Activity act = getActivity();
        for(int id1 = -1; id1 < 2; id1++) {
            for(int id2 = -1; id2 < 2; id2++) {
                CalendarBlacklist.add(id1, act);
                assertTrue(CalendarBlacklist.contains(id1, act));

                CalendarBlacklist.add(id2, act);
                assertTrue(CalendarBlacklist.contains(id1, act));
                assertTrue(CalendarBlacklist.contains(id2, act));

                CalendarBlacklist.remove(id1, act);
                assertFalse(CalendarBlacklist.contains(id1, act));
                if (id1 != id2)
                    assertTrue(CalendarBlacklist.contains(id2, act));

                CalendarBlacklist.remove(id2, act);
                assertFalse(CalendarBlacklist.contains(id2, act));
            }
        }
    }
}
