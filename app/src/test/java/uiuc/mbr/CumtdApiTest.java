package uiuc.mbr;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.*;

import org.json.JSONObject;

import uiuc.mbr.directions.CumtdApi;
import uiuc.mbr.directions.Directions;

/**
 * Unit Tests for CumtdApi.java
 */
public class CumtdApiTest {

	private CumtdApi api;

	@Before
	public void setUp() {
		api = CumtdApi.create();
	}

	@After
	public void tearDown() {
		api = null;
	}

	/**
	 *   Test get directions method when there are no buses running.
	 */
	@Test
	public void testGetDirectionsNoBuses() throws JSONException, IOException {
		JSONObject json = new JSONObject("{\"new_changeset\":true,\"rqst\":{\"method\":\"GetPlannedTripsByLatLon\",\"params\":{\"date\":\"2016-12-25\",\"max_walk\":1,\"origin_lat\":40.11626,\"origin_lon\":-88.25783,\"destination_lat\":40.12233,\"time\":\"21:00\",\"destination_lon\":-88.29619,\"arrive_depart\":\"arrive\"}},\"itineraries\":[],\"time\":\"2016-03-12T21:08:52-06:00\",\"status\":{\"msg\":\"No service at origin at the date/time specified.\",\"code\":200}}");
		Directions d = api.parseTripData(json);
		assertNull(d);
	}

	/**
	 *   Test get directions method when the destination is too far.
	 */
	@Test
	public void testGetDirectionsBadLatLon() throws JSONException, IOException {
		JSONObject json = new JSONObject("{\"new_changeset\":true,\"rqst\":{\"method\":\"GetPlannedTripsByLatLon\",\"params\":{\"date\":\"2016-12-25\",\"max_walk\":1,\"origin_lat\":40.11626,\"origin_lon\":-88.25783,\"destination_lat\":45.12233,\"time\":\"21:00\",\"destination_lon\":-88.29619,\"arrive_depart\":\"arrive\"}},\"itineraries\":[],\"time\":\"2016-03-12T21:10:34-06:00\",\"status\":{\"msg\":\"No service at origin at the date/time specified.\",\"code\":200}}");
		Directions d = api.parseTripData(json);
		assertNull(d);
	}
}
