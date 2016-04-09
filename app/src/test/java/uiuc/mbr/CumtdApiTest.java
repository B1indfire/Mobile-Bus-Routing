package uiuc.mbr;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.String;
import java.net.MalformedURLException;
import java.util.List;

import static org.junit.Assert.*;

import org.json.JSONObject;

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
     * Test whether getNearestStops function is working correctly by finding the nearest stop to
     * Seibel Center.
     * @throws IOException
     * @throws JSONException
     */
    /*@Test
    public void testGetDirections() throws JSONException, IOException {
        Directions d = api.getTripArriveBy("40.11626", "-88.25783", "40.12233", "-88.29619", "2016-03-12", "21:00", "1", "arrive");
        assertEquals(d.getDirections().toString(), "[Head South for 0.32 miles to Springfield & VanDoren (NE Corner)., Take the GREENHOPPER EVENING WEEKEND bus from Springfield & VanDoren (NE Corner) to Country Fair (South Side)., Take the LIME SATURDAY bus from Country Fair (North Side) to Clayton & Creve Coeur (NE Corner).]");
    }*/

    /**
    *   Test get directions method in CumtdApi.
    */
    @Test
    public void testGetDirections() throws JSONException, MalformedURLException, IOException {
        JSONObject json = new JSONObject("{\"time\":\"2012-01-26T13:28:57-06:00\",\"new_changeset\":true,\"status\":{\"code\":200,\"msg\":\"ok\"},\"rqst\":{\"method\":\"GetPlannedTripsByLatLon\",\"params\":{\"destination_lat\":40.11626,\"destination_lon\":-88.25783,\"origin_lat\":40.12233,\"origin_lon\":-88.29619}},\"itineraries\":[{\"start_time\":\"2012-01-26T13:36:00-06:00\",\"end_time\":\"2012-01-26T14:11:00-06:00\",\"travel_time\":35,\"legs\":[{\"type\":\"Walk\",\"walk\":{\"begin\":{\"lat\":40.12233,\"lon\":-88.29619,\"name\":\"40.12233,-88.29619\",\"time\":\"2012-01-26T13:36:00-06:00\"},\"direction\":\"East\",\"distance\":0.05,\"end\":{\"lat\":40.122199,\"lon\":-88.295524,\"name\":\"Duncan&Clayton(SECorner)\",\"stop_id\":\"DNCNCLTN:2\",\"time\":\"2012-01-26T13:37:00-06:00\"}}},{\"services\":[{\"begin\":{\"lat\":40.122199,\"lon\":-88.295524,\"name\":\"Duncan&Clayton(SECorner)\",\"stop_id\":\"DNCNCLTN:2\",\"time\":\"2012-01-26T13:41:00-06:00\"},\"end\":{\"lat\":40.131976,\"lon\":-88.288742,\"name\":\"ParklandCollege\",\"stop_id\":\"PKLN:1\",\"time\":\"2012-01-26T13:45:00-06:00\"},\"route\":{\"route_color\":\"725700\",\"route_id\":\"9ABROWN\",\"route_long_name\":\"Brown\",\"route_short_name\":\"9A\",\"route_text_color\":\"ffffff\"},\"trip\":{\"trip_id\":\"9BA311__BB3\",\"trip_headsign\":\"COUNRYFAIR/PARKLAND\",\"route_id\":\"9ABROWN\",\"block_id\":\"BB3\",\"direction\":\"A\",\"service_id\":\"BB3\",\"shape_id\":\"9A\"}},{\"begin\":{\"lat\":40.131976,\"lon\":-88.288742,\"name\":\"ParklandCollege\",\"stop_id\":\"PKLN:1\",\"time\":\"2012-01-26T13:58:00-06:00\"},\"end\":{\"lat\":40.116209,\"lon\":-88.257355,\"name\":\"University&Prospect(SEFarSide)\",\"stop_id\":\"UNIPSPCT:6\",\"time\":\"2012-01-26T14:11:00-06:00\"},\"route\":{\"route_color\":\"666666\",\"route_id\":\"GREY\",\"route_long_name\":\"Grey\",\"route_short_name\":\"7\",\"route_text_color\":\"ffffff\"},\"trip\":{\"trip_id\":\"3GR763__BB3\",\"trip_headsign\":\"EAST-EDGEWOOD\",\"route_id\":\"GREY\",\"block_id\":\"BB3\",\"direction\":\"East\",\"service_id\":\"BB3\",\"shape_id\":\"7E\"}}],\"type\":\"Service\"}]}]}");  
        Directions d = api.parseTripData(json);
        assertEquals(d.getDuration(), 35);
        assertEquals(d.getDirections().toString(), "[Head East for 0.05 miles to Duncan&Clayton(SECorner)., Take the 9ABROWN bus from Duncan&Clayton(SECorner) to ParklandCollege., Take the GREY bus from ParklandCollege to University&Prospect(SEFarSide).]");
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
    
    /**
     * Test getting coordinates for map plotting and seeing if they are correct.
     * @throws JSONException
     * @throws MalformedURLException
     * @throws IOException
     */
    /*@Test
    public void testGetCoordinates() throws JSONException, MalformedURLException, IOException {
		Directions d = api.getTripArriveBy("40.11626", "-88.25783", "40.12233", "-88.29619", "2016-03-12", "21:00", "1", "arrive");
		assertEquals(d.getCoordinates().toString(), "[W:40.11626,-88.25783,40.112685,-88.256487, S:40.112638,-88.256485,40.112677,-88.261345,40.112703,-88.263625,40.112732,-88.266383,40.112749,-88.268074,40.112773,-88.270047,40.112794,-88.272241,40.112865,-88.278373,40.112893,-88.282858,40.114322,-88.282072,40.114066,-88.281378,40.113551,-88.280575, S:40.113531,-88.279252,40.112866,-88.278746,40.11292,-88.285869,40.112984,-88.292159,40.113035,-88.295446,40.11739,-88.295506,40.1223,-88.296228]");
    }*/
}
