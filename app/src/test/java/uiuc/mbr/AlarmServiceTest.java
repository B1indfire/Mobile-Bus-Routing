package uiuc.mbr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import uiuc.mbr.calendar.Event;
import uiuc.mbr.serv.AlarmService;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AlarmServiceTest {

	private Alarm alarm1;
	private Alarm alarm2;
	private Boolean expectedResult;

	public AlarmServiceTest(Alarm a1, Alarm a2, Boolean expected) {
		this.alarm1 = a1;
		this.alarm2 = a2;
		this.expectedResult = expected;
	}


	@Parameterized.Parameters
	public static Collection alarms() {
		return Arrays.asList(new Object[][]{
				{new Alarm(new Event(0,0,"","","",new Date(0), new Date(3600000))), //Within 2 hours
						new Alarm(new Event(0,0,"","","",new Date(7200000), new Date(54623789))),
						true},
				{new Alarm(new Event(0,0,"","","",new Date(10000000), new Date(1000500))), //Well beyond 2 hours
						new Alarm(new Event(0,0,"","","",new Date(330000000), new Date(230000050))),
						false},
				{new Alarm(new Event(0,0,"","","",new Date(10000000), new Date(10000500))), //Exactly 2 hours before
						new Alarm(new Event(0,0,"","","",new Date(10000000-7200000), new Date(9000000))),
						false},
				{new Alarm(new Event(0,0,"","","",new Date(1000000), new Date(10450000))), //Exactly 2 hours after
						new Alarm(new Event(0,0,"","","",new Date(10450000+7200000), new Date(10459000+7200000))),
						true},
				{new Alarm(new Event(0,0,"","","",new Date(1000000), new Date(1000600))), //Same time
						new Alarm(new Event(0,0,"","","",new Date(1000600), new Date(1009000))),
						true},
				{new Alarm(new Event(0,0,"","","",new Date(1000000), new Date(1000600))), //Starts after second event ends
						new Alarm(new Event(0,0,"","","",new Date(800000), new Date(890000))),
						false}
		});
	}

	@Test
	public void testPrimeNumberChecker() {
		assertEquals(expectedResult.booleanValue(),
				AlarmService.within2Hours(alarm1, alarm2));
	}

}
