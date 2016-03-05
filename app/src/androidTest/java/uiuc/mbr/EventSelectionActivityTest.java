package uiuc.mbr;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import uiuc.mbr.events.EventSelectionActivity;

/**
 * Created by jimmy on 3/3/16.
 */
public class EventSelectionActivityTest extends ActivityInstrumentationTestCase2<EventSelectionActivity>{

    public Context ctxt;

    public EventSelectionActivityTest() {
        super(EventSelectionActivity.class);
    }


    @Test
    public void testActivityExists() {
        EventSelectionActivity activity = getActivity();
        assertNotNull(activity);
    }


}
