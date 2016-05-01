package uiuc.mbr;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import uiuc.mbr.activities.StartActivity;

/**
 * Tests the Settings class.
 * Warning: if assertions fail or exceptions occur, your settings may be irreparably changed.
 */
public class SettingsTest extends ActivityInstrumentationTestCase2<StartActivity> {

    final int TEST_VALUE = -1;
    int maxWalk = 3;
    int arrivalDiff = 0;

    public SettingsTest() {
        super(StartActivity.class);
    }

    /**
     * Sets maxWalk, then saves it for later use by testSave().
     * Keep tests in this order.
     */
    public void testMaxWalk() {
        Activity act = getActivity();
        maxWalk = Settings.getMaxWalkTenthsMiles(act);
        Settings.setMaxWalkTenthsMilesTemporarily(TEST_VALUE, act);
        assertEquals(TEST_VALUE, Settings.getMaxWalkTenthsMiles(act));
        Settings.saveSettings(act);
    }

    /**
     * Sets arrivalDiff, then saves it for later use by testSave().
     * Keep tests in this order.
     */
    public void testArrivalDiff() {
        Activity act = getActivity();
        arrivalDiff = Settings.getArrivalDiffMinutes(act);
        Settings.setArrivalDiffMinutesTemporarily(TEST_VALUE, act);
        assertEquals(TEST_VALUE, Settings.getArrivalDiffMinutes(act));
        Settings.saveSettings(act);
    }

    /**
     * Checks if the saveSettings() calls in testMaxWalk and testArrivalDiff worked correctly,
     * then restores the original values of maxWalk and arrivalDiff to Settings. Keep in this order.
     */
    public void testSave() {
        Activity act = getActivity();
        assertEquals(TEST_VALUE, Settings.getMaxWalkTenthsMiles(act));
        assertEquals(TEST_VALUE, Settings.getArrivalDiffMinutes(act));
        Settings.setMaxWalkTenthsMilesTemporarily(maxWalk, act);
        Settings.setArrivalDiffMinutesTemporarily(arrivalDiff, act);
        Settings.saveSettings(act);
    }

}
