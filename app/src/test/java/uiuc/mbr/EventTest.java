package uiuc.mbr;

import org.junit.Test;

import java.util.Date;

import uiuc.mbr.calendar.Event;

import static org.junit.Assert.*;

public class EventTest
{
	private static final Event test  = new Event(5, 8, "name", "description", "location", new Date(12345), new Date(9999));
	private static final Event nully = new Event(0, 0, null, null, null, null, null);


	private static void assertMatch(boolean match, Event a, Event b) {
		assertTrue(match == a.fullEquals(b));
	}


	@Test
	public void basic() {
		assertMatch(true, test, new Event(5, 8, "name", "description", "location", new Date(12345), new Date(9999)));
	}


	@Test
	public void mismatchCalendar() {
		assertMatch(false, test, new Event(3, 8, "name", "description", "location", new Date(12345), new Date(9999)));
	}
	@Test
	public void mismatchParent() {
		assertMatch(false, test, new Event(5, 9, "name", "description", "location", new Date(12345), new Date(9999)));
	}
	@Test
	public void mismatchLocation() {
		assertMatch(false, test, new Event(5, 8, "eman", "noitpircsed", "noitacol", new Date(12345), new Date(9999)));
	}
	@Test
	public void mismatchStart() {
		assertMatch(false, test, new Event(5, 8, "name", "description", "location", new Date(88888), new Date(9999)));
	}
	@Test
	public void mismatchEnd() {
		assertMatch(false, test, new Event(5, 8, "name", "description", "location", new Date(12345), new Date(0)));
	}


	@Test
	public void nully() {
		assertMatch(true, nully, new Event(0, 0, null, null, null, null, null));
	}
	@Test
	public void nullyMismatchN() {
		assertMatch(false, nully, new Event(0, 1, null, null, null, null, null));
	}
	@Test
	public void nullyMismatchS() {
		assertMatch(false, nully, new Event(0, 0, "abc", null, null, null, null));
	}
}