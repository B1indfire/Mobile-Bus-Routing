package uiuc.mbr;

import org.json.*;
import org.json.JSONException;import org.junit.Test;

import java.io.IOException;
import java.lang.String;import java.util.List;
import uiuc.mbr.CumtdApi;import static org.junit.Assert.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Unit Tests for CumtdApi.java
 */
public class CumtdApiTest {
    /**
     * Test whether getNearestStops function is working correctly by finding the nearest stop to
     * Seibel Center.
     * @throws IOException
     * @throws JSONException
     */
    @Test
    public void testGetNearestStops() throws IOException, JSONException {
        CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
        List<String> list = api.getNearestStops("40.113860", "-88.224916");
        assertEquals(list.get(0), "GWNMN:Goodwin and Main");
    }

    /**
    *   Test get directions method in CumtdApi.
    */
    @Test
    public void testGetDirections() throws JSONException, MalformedURLException, IOException {
		CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
		Directions d = api.getTripArriveBy("40.11626", "-88.25783", "40.12233", "-88.29619", "2016-03-12", "21:00", "1", "arrive");
		assertEquals(d.getDirections().toString(), "[Head South for 0.32 miles to Springfield & VanDoren (NE Corner)., Take the GREENHOPPER EVENING WEEKEND bus from Springfield & VanDoren (NE Corner) to Country Fair (South Side)., Take the LIME SATURDAY bus from Country Fair (North Side) to Clayton & Creve Coeur (NE Corner).]");
	}


    /**
    *   Test get directions method when there are no buses running.
    */
    @Test
    public void testGetDirectionsNoBuses() throws JSONException, IOException {
        CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
        JSONObject json = new JSONObject("{\"new_changeset\":true,\"rqst\":{\"method\":\"GetPlannedTripsByLatLon\",\"params\":{\"date\":\"2016-12-25\",\"max_walk\":1,\"origin_lat\":40.11626,\"origin_lon\":-88.25783,\"destination_lat\":40.12233,\"time\":\"21:00\",\"destination_lon\":-88.29619,\"arrive_depart\":\"arrive\"}},\"itineraries\":[],\"time\":\"2016-03-12T21:08:52-06:00\",\"status\":{\"msg\":\"No service at origin at the date/time specified.\",\"code\":200}}");
        Directions d = api.parseTripData(json);
        assertNull(d);
    }

    /**
    *   Test get directions method when the destination is too far.
    */
    @Test
    public void testGetDirectionsBadLatLon() throws JSONException, IOException {
        CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
        JSONObject json = new JSONObject("{\"new_changeset\":true,\"rqst\":{\"method\":\"GetPlannedTripsByLatLon\",\"params\":{\"date\":\"2016-12-25\",\"max_walk\":1,\"origin_lat\":40.11626,\"origin_lon\":-88.25783,\"destination_lat\":45.12233,\"time\":\"21:00\",\"destination_lon\":-88.29619,\"arrive_depart\":\"arrive\"}},\"itineraries\":[],\"time\":\"2016-03-12T21:10:34-06:00\",\"status\":{\"msg\":\"No service at origin at the date/time specified.\",\"code\":200}}");
        Directions d = api.parseTripData(json);
        assertNull(d);
    }
    
    /**
     * Test getting coordinates for map plotting and seeing if they are correct.
     * @throws JSONException
     * @throws MalformedURLException
     * @throws IOException
     */
    @Test
    public void testGetCoordinates() throws JSONException, MalformedURLException, IOException {
		CumtdApi api = new CumtdApi("https://developer.cumtd.com/api/v2.2/JSON", "c4d5e4bb2baa48ba85772b857c9839c8");
		Directions d = api.getTripArriveBy("40.11626", "-88.25783", "40.12233", "-88.29619", "2016-03-12", "21:00", "1", "arrive");
		assertEquals(d.getCoordinates().toString(), "[W:40.11626,-88.25783,40.112685,-88.256487, S:40.112638,-88.256485,40.112677,-88.261345,40.112703,-88.263625,40.112732,-88.266383,40.112749,-88.268074,40.112773,-88.270047,40.112794,-88.272241,40.112865,-88.278373,40.112893,-88.282858,40.114322,-88.282072,40.114066,-88.281378,40.113551,-88.280575, S:40.113531,-88.279252,40.112866,-88.278746,40.11292,-88.285869,40.112984,-88.292159,40.113035,-88.295446,40.11739,-88.295506,40.1223,-88.296228]");
    }
}
