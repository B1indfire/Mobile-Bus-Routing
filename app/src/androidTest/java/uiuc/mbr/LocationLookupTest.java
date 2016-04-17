package uiuc.mbr;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import uiuc.mbr.events.LocationLookup;

public class LocationLookupTest extends ActivityInstrumentationTestCase2<StartActivity>
{
	private static final double TOLERANCE = 0.001;


	StartActivity thisActivity;
	Instrumentation instrumentation;

	public LocationLookupTest(){super(StartActivity.class);}


	@Before
	protected void setUp() throws Exception {
		super.setUp();

		thisActivity = getActivity();
		instrumentation = getInstrumentation();
	}

	@Test
	public void testLookupIUB() {
		LatLng result = LocationLookup.lookupLocation("Illini Union Bookstore", getActivity().getApplicationContext());
		assertNotNull(result);
		assertEquals(40.108249, result.latitude, TOLERANCE);
		assertEquals(-88.229138, result.longitude, TOLERANCE);
	}

	@Test
	public void testLookupOneSouth() {
		LatLng result = LocationLookup.lookupLocation("State Farm Center", getActivity().getApplicationContext());
		assertNotNull(result);
		assertEquals(40.096247, result.latitude, TOLERANCE);
		assertEquals(-88.235964, result.longitude, TOLERANCE);
	}
}
