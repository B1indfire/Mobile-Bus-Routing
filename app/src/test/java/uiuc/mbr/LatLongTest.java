package uiuc.mbr;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.test.AndroidTestCase;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uiuc.mbr.calendar.Event;
import uiuc.mbr.events.LatLong;


/**
 * Created by Scott on 3/2/2016.
 */
public class LatLongTest extends AndroidTestCase {

    private Event validEvent;
    private Event invalidEvent;

    @Override
    public void setUp() {
        long now = (new Date()).getTime();
        Date later1 = new Date(now + 1000);
        Date later2 = new Date(now + 2000);

        validEvent = new Event(0, 0, "test event 1", "desc 1", "505 E Healey Champaign il", later1, later2);
        invalidEvent = new Event(1, 1, "test event 4", "desc 4", "1517 thornwood dr", later1, later2);
    }

    /*
    @Test
    public void testGetEventLocationFromEvent_validEvent() {
        LatLong latLong = LatLong.getEventLocation(validEvent, getContext());
        assertNotNull(latLong);
        assertTrue(Math.abs(40.111159 - latLong.getLatitude()) < 0.1 );
        assertTrue(Math.abs(-88.231149 - latLong.getLongitude()) < 0.1);
    }

    @Test
    public void testGetEventLocationFromEvent_invalidEvent() {
        LatLong latLong = LatLong.getEventLocation(invalidEvent, getContext());
        assertNull(latLong);
    }
    */
}
