package uiuc.mbr;

import org.junit.Test;

import uiuc.mbr.event_selection.UserLocation;

import static org.junit.Assert.*;

/**Tests the comparison function of UserLocations.*/
public class UserLocationTest
{
	@Test
	public void testCompare1()
	{
		UserLocation a = new UserLocation("first");
		UserLocation b = new UserLocation("second");
		assertTrue(a.compareTo(b) < 0);
	}

	@Test
	public void testCompare2()
	{
		UserLocation a = new UserLocation("");
		UserLocation b = new UserLocation("nonempty");
		assertTrue(a.compareTo(b) < 0);
	}
}