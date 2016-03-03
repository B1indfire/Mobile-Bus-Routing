package uiuc.mbr;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.test.AndroidTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uiuc.mbr.calendar.Event;
import uiuc.mbr.events.LatLong;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
//import static org.powermock.api.mockito.PowerMockito.whenNew;

//import org.powermock.api.mockito.PowerMockito;

/**
 * Created by Scott on 3/2/2016.
 */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(Geocoder.class)
public class LatLongTest extends AndroidTestCase {

    private Event validEvent;
    private Event invalidEvent;
    private Context ctxt;
    private Geocoder geo;

    @Override
    public void setUp() {
        ctxt = mock(Context.class);
        geo = mock(Geocoder.class);

        long now = (new Date()).getTime();
        Date later1 = new Date(now + 1000);
        Date later2 = new Date(now + 2000);

        validEvent = new Event(0, 0, "test event 1", "desc 1", "505 E Healey Champaign il", later1, later2);
        invalidEvent = new Event(1, 1, "test event 4", "desc 4", "1517 thornwood dr", later1, later2);
    }


  /*  @Test
    public void testGetEventLocationFromEvent_validEvent() {
        try {
            whenNew(Geocoder.class).withArguments(any(Context.class), any(Locale.class)).thenReturn(geo);
            ArrayList<Address> al = new ArrayList<>();
            Address a = new Address(Locale.US);
            a.setLatitude(35);
            a.setLongitude(40);
            al.add(a);
            when(geo.getFromLocationName(anyString(), anyInt(), anyDouble(),anyDouble(),anyDouble(),anyDouble())).thenReturn(al);

            LatLong latLong = LatLong.getEventLocation(validEvent, ctxt);
            assertNotNull(latLong);
            assertTrue(Math.abs(35 - latLong.getLatitude()) < 0.1);
            assertTrue(Math.abs(40 - latLong.getLongitude()) < 0.1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*LatLong latLong = LatLong.getEventLocation(validEvent, getContext());
        assertNotNull(latLong);
        assertTrue(Math.abs(40.111159 - latLong.getLatitude()) < 0.1 );
        assertTrue(Math.abs(-88.231149 - latLong.getLongitude()) < 0.1);
    }

    @Test
    public void testGetEventLocationFromEvent_invalidEvent() {
        //LatLong latLong = LatLong.getEventLocation(invalidEvent, getContext());
        //assertNull(latLong);
    }*/

}
