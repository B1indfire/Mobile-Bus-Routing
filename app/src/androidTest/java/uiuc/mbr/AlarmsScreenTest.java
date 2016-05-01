package uiuc.mbr;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uiuc.mbr.activities.AlarmsMenuActivity;


import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AlarmsScreenTest {
	@Rule
	public ActivityTestRule<AlarmsMenuActivity> mActivityRule = new ActivityTestRule<>(AlarmsMenuActivity.class);

	@Test
	public void testButton() {

		Espresso.onView(withId(R.id.view_alarms_button)).check(matches(isDisplayed()));
		Espresso.onView(withId(R.id.view_alarms_button)).check(matches(isClickable()));

		Espresso.onView(withId(R.id.update_button)).check(matches(isDisplayed()));
		Espresso.onView(withId(R.id.update_button)).check(matches(isClickable()));


	}
}
