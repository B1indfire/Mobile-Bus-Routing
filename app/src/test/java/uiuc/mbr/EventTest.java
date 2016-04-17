package uiuc.mbr;

import android.os.Bundle;
import android.test.AndroidTestCase;

import org.junit.Test;

import java.sql.Date;

import uiuc.mbr.calendar.Event;

import static org.mockito.Mockito.*;

public class EventTest extends AndroidTestCase
{
	private Bundle bundle;

	@Override
	public void setUp() {
		bundle = mock(Bundle.class);
	}


	@Test
	public void testNoPrefix()
	{
		Event a = new Event(123L, 456L, "lunch", "great", "everywhere", Date.valueOf("2000-01-01"), Date.valueOf("2000-01-02"));
		a.export("", bundle);

		verify(bundle, times(1)).putLong("calendarId", 123L);
		verify(bundle, times(1)).putLong("parentEventId", 456L);
		verify(bundle, times(1)).putString("name", "lunch");
		verify(bundle, times(1)).putString("description", "great");
		verify(bundle, times(1)).putString("location", "everywhere");
		verify(bundle, times(1)).putLong("start", a.getStart().getTime());
		verify(bundle, times(1)).putLong("end", a.getEnd().getTime());

		when(bundle.getLong("calendarId")).thenReturn(123L);
		when(bundle.getLong("parentEventId")).thenReturn(456L);
		when(bundle.getString("name")).thenReturn("lunch");
		when(bundle.getString("description")).thenReturn("great");
		when(bundle.getString("location")).thenReturn("everywhere");
		when(bundle.getLong("start")).thenReturn(a.getStart().getTime());
		when(bundle.getLong("end")).thenReturn(a.getEnd().getTime());

		Event b = Event.importFrom("", bundle);
		assertTrue(a.fullEquals(b));
	}

	@Test
	public void testWithPrefix()
	{
		Event a = new Event(222L, 555L, "dinner", "mediocre", "ur place m8", Date.valueOf("2001-01-01"), Date.valueOf("2001-01-02"));
		a.export("hello", bundle);

		verify(bundle, times(1)).putLong("hellocalendarId", 222L);
		verify(bundle, times(1)).putLong("helloparentEventId", 555L);
		verify(bundle, times(1)).putString("helloname", "dinner");
		verify(bundle, times(1)).putString("hellodescription", "mediocre");
		verify(bundle, times(1)).putString("hellolocation", "ur place m8");
		verify(bundle, times(1)).putLong("hellostart", a.getStart().getTime());
		verify(bundle, times(1)).putLong("helloend", a.getEnd().getTime());

		when(bundle.getLong("hellocalendarId")).thenReturn(222L);
		when(bundle.getLong("helloparentEventId")).thenReturn(555L);
		when(bundle.getString("helloname")).thenReturn("dinner");
		when(bundle.getString("hellodescription")).thenReturn("mediocre");
		when(bundle.getString("hellolocation")).thenReturn("ur place m8");
		when(bundle.getLong("hellostart")).thenReturn(a.getStart().getTime());
		when(bundle.getLong("helloend")).thenReturn(a.getEnd().getTime());

		Event b = Event.importFrom("hello", bundle);
		assertTrue(a.fullEquals(b));
	}

	@Test
	public void testNullNoPrefix()
	{
		Event a = new Event(555L, 999L, null, null, null, Date.valueOf("2000-01-01"), Date.valueOf("2009-11-05"));
		a.export("", bundle);

		verify(bundle, times(1)).putLong("calendarId", 555L);
		verify(bundle, times(1)).putLong("parentEventId", 999L);
		verify(bundle, times(1)).putString("name", null);
		verify(bundle, times(1)).putString("description", null);
		verify(bundle, times(1)).putString("location", null);
		verify(bundle, times(1)).putLong("start", a.getStart().getTime());
		verify(bundle, times(1)).putLong("end", a.getEnd().getTime());

		when(bundle.getLong("calendarId")).thenReturn(555L);
		when(bundle.getLong("parentEventId")).thenReturn(999L);
		when(bundle.getString("name")).thenReturn(null);
		when(bundle.getString("description")).thenReturn(null);
		when(bundle.getString("location")).thenReturn(null);
		when(bundle.getLong("start")).thenReturn(a.getStart().getTime());
		when(bundle.getLong("end")).thenReturn(a.getEnd().getTime());

		Event b = Event.importFrom("", bundle);
		assertTrue(a.fullEquals(b));
	}

	@Test
	public void testNullWithPrefix()
	{
		Event a = new Event(987L, 654L, null, null, null, Date.valueOf("2012-12-12"), Date.valueOf("2013-1-01"));
		a.export("try hardcoding this", bundle);

		verify(bundle, times(1)).putLong("try hardcoding thiscalendarId", 987L);
		verify(bundle, times(1)).putLong("try hardcoding thisparentEventId", 654L);
		verify(bundle, times(1)).putString("try hardcoding thisname", null);
		verify(bundle, times(1)).putString("try hardcoding thisdescription", null);
		verify(bundle, times(1)).putString("try hardcoding thislocation", null);
		verify(bundle, times(1)).putLong("try hardcoding thisstart", a.getStart().getTime());
		verify(bundle, times(1)).putLong("try hardcoding thisend", a.getEnd().getTime());

		when(bundle.getLong("try hardcoding thiscalendarId")).thenReturn(987L);
		when(bundle.getLong("try hardcoding thisparentEventId")).thenReturn(654L);
		when(bundle.getString("try hardcoding thisname")).thenReturn(null);
		when(bundle.getString("try hardcoding thisdescription")).thenReturn(null);
		when(bundle.getString("try hardcoding thislocation")).thenReturn(null);
		when(bundle.getLong("try hardcoding thisstart")).thenReturn(a.getStart().getTime());
		when(bundle.getLong("try hardcoding thisend")).thenReturn(a.getEnd().getTime());

		Event b = Event.importFrom("try hardcoding this", bundle);
		assertTrue(a.fullEquals(b));
	}
}
