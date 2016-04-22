package uiuc.mbr.event_selection;

import org.junit.Test;

import static org.junit.Assert.*;

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