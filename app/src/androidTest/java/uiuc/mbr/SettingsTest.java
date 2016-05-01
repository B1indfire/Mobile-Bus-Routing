package uiuc.mbr;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import uiuc.mbr.activities.StartActivity;

public class SettingsTest extends ActivityInstrumentationTestCase2<StartActivity> {

    int maxWalk;
    int arrivalDiff;

    public SettingsTest() {
        super(StartActivity.class);
    }

    /**
     * Sets maxWalk, then saves it for later use by testSave(). Keep tests in this order.
     */
    public void testMaxWalk() {
        Activity act = getActivity();
        maxWalk = Settings.getMaxWalkTenthsMiles(act);
        Settings.setMaxWalkTenthsMilesTemporarily(5, act);
        assertEquals(5, Settings.getMaxWalkTenthsMiles(act));
        Settings.saveSettings(act);
    }

    /**
     * Sets arrivalDiff, then saves it for later use by testSave(). Keep tests in this order.
     */
    public void testArrivalDiff() {
        Activity act = getActivity();
        arrivalDiff = Settings.getArrivalDiffMinutes(act);
        Settings.setArrivalDiffMinutesTemporarily(5, act);
        assertEquals(5, Settings.getArrivalDiffMinutes(act));
        Settings.saveSettings(act);
    }

    /**
     * Checks if the saveSettings() calls in testMaxWalk and testArrivalDiff worked correctly,
     * then restores the original values of maxWalk and arrivalDiff to Settings. Keep in this order.
     */
    public void testSave() {
        Activity act = getActivity();
        assertEquals(5, Settings.getMaxWalkTenthsMiles(act));
        assertEquals(5, Settings.getArrivalDiffMinutes(act));
        Settings.setMaxWalkTenthsMilesTemporarily(maxWalk, act);
        Settings.setArrivalDiffMinutesTemporarily(arrivalDiff, act);
        Settings.saveSettings(act);
    }

}
