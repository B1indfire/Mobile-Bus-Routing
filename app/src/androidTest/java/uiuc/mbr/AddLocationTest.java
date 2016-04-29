package uiuc.mbr;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.hamcrest.BaseMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uiuc.mbr.activities.AddressBookActivity;
import uiuc.mbr.event_selection.AddressBook;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasToString;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class AddLocationTest {

    private String string1 = "test";
    private String address1 = "502 W green st";
    private String string2 = "test 2";
    private String address2 = "509 bash ct";

    @Rule
    public ActivityTestRule<AddressBookActivity> mActivityRule = new ActivityTestRule<AddressBookActivity>(AddressBookActivity.class);

    /**
     * Tests to see if an address can be added.
     */
    @Test
    public void testAddNewLocation() {
        //add new name/location pair
        onView(withId(R.id.addAddress)).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).perform(replaceText(string1));
        onView(withText("OK")).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).perform(replaceText(address1));
        onView(withText("OK")).perform(click());
        Assert.assertNotNull(AddressBook.getByName(string1, InstrumentationRegistry.getTargetContext()));

        //cleanup
        onView(allOf(withText("Delete"))).perform(click());

    }

    /**
     * Tests to see if it will not allow for a second saved address with the same name.
     * Adds an name/address pair then tries to add a new address with the same name.
     *
     */
    @Test
    public void testAddExistingLocation() {
        //Add first address
        onView(withId(R.id.addAddress)).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).perform(replaceText(string1));
        onView(withText("OK")).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).perform(replaceText(address1));
        onView(withText("OK")).perform(click());

        //try to add second address
        onView(withId(R.id.addAddress)).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).perform(replaceText(string1));
        onView(withText("OK")).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).check(doesNotExist());

        //cleanup
        onView(allOf(withText("Delete"))).perform(click());
    }

    /**
     * Test adding of two different name/address pairs.
     */
    /*@Test
    public void testAddTwoLocations(){
        //add first one
        onView(withId(R.id.addAddress)).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).perform(replaceText(string1));
        onView(withText("OK")).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).perform(replaceText(address1));
        onView(withText("OK")).perform(click());
        Assert.assertNotNull(AddressBook.getByName(string1, InstrumentationRegistry.getTargetContext()));

        onView(withId(R.id.addAddress)).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).perform(replaceText(string2));
        onView(withText("OK")).perform(click());
        onView(allOf(withClassName(endsWith("EditText")), withText(is("")))).perform(replaceText(address2));
        onView(withText("OK")).perform(click());
        Assert.assertNotNull(AddressBook.getByName(string2, InstrumentationRegistry.getTargetContext()));

        //cleanup
        //onData(anything()).inAdapterView(withId(R.id.address_book)).atPosition(3).perform(click());
        //onData(hasToString(startsWith("Delete"))).inAdapterView(withId(R.id.address_book)).atPosition(0).perform(click());

    }*/

}
