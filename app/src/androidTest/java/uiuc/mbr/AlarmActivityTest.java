package uiuc.mbr;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

public class AlarmActivityTest {// extends ActivityInstrumentationTestCase2<AlarmActivity> {
//
//    AlarmActivity thisActivity;
//    Instrumentation instrumentation;
//    ActivityMonitor monitor;
//
//    public AlarmActivityTest() {
//        super(AlarmActivity.class);
//    }
//
//    @Before
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        thisActivity = getActivity();
//        instrumentation = getInstrumentation();
//        monitor = instrumentation.addMonitor(OnAlarmActivity.class.getName(), null, false);
//    }

    @Test
    public void testSetAlarmAndRun() {
		//TODO
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(calendar.getTimeInMillis() + 1000);
//        Alarm.add(new Alarm("Test", calendar), getActivity().getApplicationContext());
//        assertEquals(1, Alarm.allAlarms().length);
//
//        final Activity nextActivity = instrumentation.waitForMonitorWithTimeout(monitor, 10000);
//        assertNotNull(nextActivity);
//        assertEquals(OnAlarmActivity.class, nextActivity.getClass());
//
//        new Handler(thisActivity.getMainLooper()).post(new Runnable()
//            {
//                public void run() {
//                    try {
//                        Thread.sleep(1000); //need to wait for refresh() to get called in OnAlarm
//                    } catch (InterruptedException e) {
//
//                    }
//
//                    TextView currentAlarm = (TextView) nextActivity.findViewById(R.id.a_onalarm_current_name);
//                    assertEquals("Test", currentAlarm.getText());
//
//                    Button closeAlarm = (Button) nextActivity.findViewById(R.id.turn_off_alarm_btn);
//                    closeAlarm.performClick();
//                    assertEquals("No current alarm.", currentAlarm.getText());
//
//                    assertEquals(0, Alarm.allAlarms().length);
//
//                    nextActivity.finish();
//                }
//            }
//        );
    }

//    @Override
//    @After
//    protected void tearDown() throws Exception {
//        instrumentation.removeMonitor(monitor);
//        super.tearDown();
//    }
}
