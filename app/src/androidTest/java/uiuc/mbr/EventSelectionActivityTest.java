package uiuc.mbr;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;

import uiuc.mbr.activities.EventSelectionActivity;

/**TODO expand on this; while the current test is extremely deep and complex, more would be nice*/
public class EventSelectionActivityTest extends ActivityInstrumentationTestCase2<EventSelectionActivity>{

	public Context ctxt;

	public EventSelectionActivityTest() {
		super(EventSelectionActivity.class);
	}


	@Test
	public void testActivityExists() {
		EventSelectionActivity activity = getActivity();
		assertNotNull(activity);
	}


}
