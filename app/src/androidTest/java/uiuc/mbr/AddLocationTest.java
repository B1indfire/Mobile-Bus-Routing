package uiuc.mbr;


import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uiuc.mbr.activities.AddressBookActivity;
import uiuc.mbr.event_selection.AddressBook;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class AddLocationTest {

    private String string1 = "test";
    private String address1 = "502 W green st";
    private String string2 = "test 2";
    private String address2 = "509 bash ct";

    @Rule
    public ActivityTestRule<AddressBookActivity> mActivityRule = new ActivityTestRule<AddressBookActivity>(AddressBookActivity.class);
    @Test
    public void testAddNewLocation() {

        onView(withId(R.id.addAddress)).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).perform(replaceText(string1));
        onView(withText("OK")).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).perform(replaceText(address1));
        onView(withText("OK")).perform(click());
        Assert.assertNotNull(AddressBook.getByName(string1, InstrumentationRegistry.getTargetContext()));
        onView(allOf(withText("Delete"))).perform(click());

    }

}

