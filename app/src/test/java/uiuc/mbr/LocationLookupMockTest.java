package uiuc.mbr;

import android.location.Address;
import android.location.Geocoder;
import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.io.IOException;
import java.util.*;

import uiuc.mbr.events.LocationLookup;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocationLookupMockTest extends AndroidTestCase
{
	private static final double lllat = 39.47, lllon = -88.95, urlat = 40.49, urlon = -87.43;

	private Geocoder geocoder;

    @Override
    public void setUp() {
		geocoder = mock(Geocoder.class);
    }


	private static Address newAddress(double latitude, double longitude)
	{
		Address out = new Address(Locale.getDefault());
		out.setLatitude(latitude);
		out.setLongitude(longitude);
		return out;
	}

	/**Asserts equality between an Address and a LatLng.*/
	private void assertALEquals(Address expected, LatLng actual)
	{
		assertNotNull(actual);
		assertEquals(expected.getLatitude(), actual.latitude);
		assertEquals(expected.getLongitude(), actual.longitude);
	}


	@Test
	public void testArgNull()
	{
		assertNull(LocationLookup.lookupLocationInner(null, geocoder));
	}

	@Test
	public void testGotNull() throws IOException
	{
		when(geocoder.getFromLocationName("testGotNull", 1, lllat, lllon, urlat, urlon)).thenReturn(null);
		assertNull(LocationLookup.lookupLocationInner("testGotNull", geocoder));
	}

	@Test
	public void testGotEmpty() throws IOException
	{
		when(geocoder.getFromLocationName("testGotEmpty", 1, lllat, lllon, urlat, urlon)).thenReturn(new ArrayList<Address>());
		assertNull(LocationLookup.lookupLocationInner("testGotEmpty", geocoder));
	}

	@Test
	public void testGotOne() throws IOException
	{
		List<Address> list = new ArrayList<>();
		list.add(newAddress(12.34, -12.34));
		when(geocoder.getFromLocationName("testGotOne", 1, lllat, lllon, urlat, urlon)).thenReturn(list);
		assertALEquals(newAddress(12.34, -12.34), LocationLookup.lookupLocationInner("testGotOne", geocoder));
	}

	@Test
	public void testGotTwo() throws IOException
	{
		List<Address> list = new ArrayList<>();
		list.add(newAddress(11.11, -11.11));
		list.add(newAddress(22.22, -22.22));
		when(geocoder.getFromLocationName("testGotTwo", 1, lllat, lllon, urlat, urlon)).thenReturn(list);
		assertALEquals(newAddress(11.11, -11.11), LocationLookup.lookupLocationInner("testGotTwo", geocoder));
	}
}
