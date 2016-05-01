package uiuc.mbr;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

import org.json.JSONObject;

import uiuc.mbr.directions.CumtdApi;
import uiuc.mbr.directions.Directions;

/**
 * Unit tests for various methods in the CumtdApi class. These tests will fail if there is no
 * internet connection or Android Studios is not allowed internet access.
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
	 * Test getShapeBetweenStops method to make sure that the path of the shape between the two
	 * stops is not null.
     */
	@Test
	public void testGetShapeBetweenStops() throws JSONException, IOException {
		JSONObject jo = api.getShapeBetweenStops("IT:1", "WRTHLY:4", "4W");
		JSONArray shapes = jo.getJSONArray("shapes");
		assertNotNull(shapes);
	}

	/**
	 * Test to make sure that get nearest stops with the following valid coordinates does not return
	 * an empty list.
     */
	@Test
	public void testGetNearestStops() throws JSONException, IOException {
		List<String> list = api.getNearestStops("40.1133", "-88.226");
		assertFalse(list.isEmpty());
	}

	/**
	 * Test to make sure that getTripArriveBy returns a not null Directions object when the
	 * parameters are valid.
     */
	@Test
	public void testGetTripArriveBy() throws JSONException, IOException {
		Directions d = api.getTripArriveBy("40.113031", "-88.234714", "40.123031", "-88.214714", "2016-05-01", "13:00", "1", "arrive");
		assertNotNull(d);
	}

	/**
	 * Test to make sure that the getTripArriveBy returns a null Directions object when the
	 * parameters are invalid.
     */
	@Test
	public void testBadGetTripArriveBy() throws JSONException, IOException {
		Directions d = api.getTripArriveBy("40.113031", "88.234714", "40.123031", "-88.214714", "2016-05-01", "13:00", "1", "arrive");
		assertNull(d);
	}

}
