package uiuc.mbr;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;

import uiuc.mbr.activities.EventSelectionActivity;

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
