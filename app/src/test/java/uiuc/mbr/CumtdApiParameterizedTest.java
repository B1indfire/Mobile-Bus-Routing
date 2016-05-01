package uiuc.mbr;

import java.io.IOException;

import org.json.JSONException;
import org.junit.runner.RunWith;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;

import uiuc.mbr.directions.CumtdApi;
import uiuc.mbr.directions.Directions;

import static org.junit.Assert.*;

/**
 * This class is a multi parameter parameterized test that tests the getTripArriveBy method
 * using a bunch of generated beginning and ending lat lon coordinates in order to see if it
 * returns a valid Directions object. These tests will fail if there is no internet connection
 * or Android Studios is not allowed internet access.
 */
@RunWith(Theories.class)
public class CumtdApiParameterizedTest {

	CumtdApi api = CumtdApi.create();
	double startLat = 40.113031;
	double startLon = -88.234714;

	/**
	 * This method test to make sure that the duration field of the returned Directions object
	 * is not null and is greater than 0.
     */
	@Theory
	public void testValidDuration(
			@TestedOn(ints = {0, 1, 2}) int latMultiplier,
			@TestedOn(ints = {0, 1, 2}) int lonMultiplier
	) throws IOException, JSONException {
		Directions d = api.getTripArriveBy(Double.toString(startLat), Double.toString(startLon), Double.toString(startLat + 0.01 *latMultiplier), Double.toString(startLon + 0.01 *lonMultiplier), "2016-05-01", "13:00", "1", "arrive");

		if (d!=null) {
			assertNotNull(d.duration);
			assertTrue(d.duration > 0);
		}
	}

	/**
	 * This method test to make sure that the text directions of the returned Directions Object
	 * returns a non empty list and that the list is not empty.
     */
	@Theory
	public void testNonEmptyDirections(
			@TestedOn(ints = {0, 1, 2}) int latMultiplier,
			@TestedOn(ints = {0, 1, 2}) int lonMultiplier
	) throws IOException, JSONException {
		Directions d = api.getTripArriveBy(Double.toString(startLat), Double.toString(startLon), Double.toString(startLat + 0.01 *latMultiplier), Double.toString(startLon + 0.01 *lonMultiplier), "2016-05-01", "13:00", "1", "arrive");
		if (d!=null) {
			assertNotNull(d.getDirections());
			assertFalse(d.getDirections().isEmpty());
		}
	}

	/**
	 * This method test to make sure that the list of coordinates from the returned Directions
	 * Object returns a non empty list and that the list is not empty.
     */
	@Theory
	public void testNonEmptyCoordinates(
			@TestedOn(ints = {0, 1, 2}) int latMultiplier,
			@TestedOn(ints = {0, 1, 2}) int lonMultiplier
	) throws IOException, JSONException {
		Directions d = api.getTripArriveBy(Double.toString(startLat), Double.toString(startLon), Double.toString(startLat + 0.01 *latMultiplier), Double.toString(startLon + 0.01 *lonMultiplier), "2016-05-01", "13:00", "1", "arrive");
		if (d!=null) {
			assertNotNull(d.getCoordinates());
			assertFalse(d.getCoordinates().isEmpty());
		}
	}
}
