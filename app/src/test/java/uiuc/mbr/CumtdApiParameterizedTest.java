package uiuc.mbr;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.junit.runner.RunWith;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;
import static org.junit.Assert.*;

@RunWith(Theories.class)
public class CumtdApiParameterizedTest {

	CumtdApi api = CumtdApi.create();
	double startLat = 40.113031;
	double startLon = -88.234714;

	@Theory
	public void testValidDuration(
			@TestedOn(ints = {0, 1, 2}) int latMultiplier,
			@TestedOn(ints = {0, 1, 2}) int lonMultiplier
	) throws IOException, JSONException {
		Directions d = api.getTripArriveBy(Double.toString(startLat + 0.01 *latMultiplier),
		                                   Double.toString(startLon + 0.01 *lonMultiplier),
		                                   Double.toString(startLat + 0.01 *latMultiplier),
		                                   Double.toString(startLon + 0.01 *lonMultiplier),
		                                   "2016-03-12", "1:00", "1", "arrive");
		if (d!=null) {
			assertNotNull(d.getDuration());
			assert(d.getDuration()>0);
		}
	}

	@Theory
	public void testNonEmptyDirections(
			@TestedOn(ints = {0, 1, 2}) int latMultiplier,
			@TestedOn(ints = {0, 1, 2}) int lonMultiplier
	) throws IOException, JSONException {
		Directions d = api.getTripArriveBy(Double.toString(startLat + 0.01 *latMultiplier), Double.toString(startLon + 0.01 *lonMultiplier), Double.toString(startLat + 0.01 *latMultiplier), Double.toString(startLon + 0.01 *lonMultiplier), "2016-03-12", "1:00", "1", "arrive");
		if (d!=null) {
			assertNotNull(d.getDirections());
			assert(!d.getDirections().isEmpty());
		}
	}

	@Theory
	public void testNonEmptyCoordinates(
			@TestedOn(ints = {0, 1, 2}) int latMultiplier,
			@TestedOn(ints = {0, 1, 2}) int lonMultiplier
	) throws IOException, JSONException {
		Directions d = api.getTripArriveBy(Double.toString(startLat + 0.01 *latMultiplier), Double.toString(startLon + 0.01 * lonMultiplier), Double.toString(startLat + 0.01 *latMultiplier), Double.toString(startLon + 0.01 *lonMultiplier), "2016-03-12", "1:00", "1", "arrive");
		if (d!=null) {
			assertNotNull(d.getCoordinates());
			assert(!d.getCoordinates().isEmpty());
		}
	}
}
