package uiuc.mbr;




import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uiuc.mbr.activities.NavigationMenuActivity;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class NavigationScreenTest {

    @Rule
    public ActivityTestRule<NavigationMenuActivity> mActivityRule = new ActivityTestRule<>(NavigationMenuActivity.class);

    @Test
    public void testButton() {

        Espresso.onView(withId(R.id.get_stop_button)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.get_stop_button)).check(matches(isClickable()));

        Espresso.onView(withId(R.id.go_to_button)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.go_to_button)).check(matches(isClickable()));

        Espresso.onView(withId(R.id.map_button)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.map_button)).check(matches(isClickable()));
    }
}
