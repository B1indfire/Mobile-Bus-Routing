package uiuc.mbr;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uiuc.mbr.activities.EventsMenuActivity;


import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class EventsScreenTest {

	@Rule
	public ActivityTestRule<EventsMenuActivity> mActivityRule = new ActivityTestRule<>(EventsMenuActivity.class);

	@Test
	public void testButton() {

		Espresso.onView(withId(R.id.blacklist_button)).check(matches(isDisplayed()));
		Espresso.onView(withId(R.id.blacklist_button)).check(matches(isClickable()));

		Espresso.onView(withId(R.id.events_button)).check(matches(isDisplayed()));
		Espresso.onView(withId(R.id.events_button)).check(matches(isClickable()));

		Espresso.onView(withId(R.id.edit_button)).check(matches(isDisplayed()));
		Espresso.onView(withId(R.id.edit_button)).check(matches(isClickable()));
	}
}
