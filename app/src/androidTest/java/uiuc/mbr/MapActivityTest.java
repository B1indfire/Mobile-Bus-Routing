package uiuc.mbr;

import android.location.Location;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import uiuc.mbr.activities.MapActivity;

public class MapActivityTest extends ActivityInstrumentationTestCase2<MapActivity>
{
	private static final LatLng testLocation = new LatLng(40.115009, -88.289264);//Clear Lake


	public MapActivityTest()
	{
		super(MapActivity.class);
	}


	@Test
	public void testActivityExists() {
		MapActivity activity = getActivity();
		assertNotNull(activity);
	}

	@Test
	public void testMarkerLocation() {
		final MapActivity activity = getActivity();
		new Handler(getActivity().getApplicationContext().getMainLooper()).post(new Runnable()
		{
			@Override
			public void run()
			{
				Location loc = new Location("fake provider");
				loc.setLatitude(testLocation.latitude);
				loc.setLongitude(testLocation.longitude);
				activity.locationHandler.onLocationChanged(loc);
				assertNotNull(activity.userLocationMarker);
				assertEquals(testLocation, activity.userLocationMarker.getPosition());
			}
		});
	}
}
