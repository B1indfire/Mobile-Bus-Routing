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

    @Test
    public void testGetDirections() throws JSONException {
        JSONObject json = new JSONObject("{\"time\":\"2012-01-26T13:28:57-06:00\",\"new_changeset\":true,\"status\":{\"code\":200,\"msg\":\"ok\"},\"rqst\":{\"method\":\"GetPlannedTripsByLatLon\",\"params\":{\"destination_lat\":40.11626,\"destination_lon\":-88.25783,\"origin_lat\":40.12233,\"origin_lon\":-88.29619}},\"itineraries\":[{\"start_time\":\"2012-01-26T13:36:00-06:00\",\"end_time\":\"2012-01-26T14:11:00-06:00\",\"travel_time\":35,\"legs\":[{\"type\":\"Walk\",\"walk\":{\"begin\":{\"lat\":40.12233,\"lon\":-88.29619,\"name\":\"40.12233,-88.29619\",\"time\":\"2012-01-26T13:36:00-06:00\"},\"direction\":\"East\",\"distance\":0.05,\"end\":{\"lat\":40.122199,\"lon\":-88.295524,\"name\":\"Duncan&Clayton(SECorner)\",\"stop_id\":\"DNCNCLTN:2\",\"time\":\"2012-01-26T13:37:00-06:00\"}}},{\"services\":[{\"begin\":{\"lat\":40.122199,\"lon\":-88.295524,\"name\":\"Duncan&Clayton(SECorner)\",\"stop_id\":\"DNCNCLTN:2\",\"time\":\"2012-01-26T13:41:00-06:00\"},\"end\":{\"lat\":40.131976,\"lon\":-88.288742,\"name\":\"ParklandCollege\",\"stop_id\":\"PKLN:1\",\"time\":\"2012-01-26T13:45:00-06:00\"},\"route\":{\"route_color\":\"725700\",\"route_id\":\"9ABROWN\",\"route_long_name\":\"Brown\",\"route_short_name\":\"9A\",\"route_text_color\":\"ffffff\"},\"trip\":{\"trip_id\":\"9BA311__BB3\",\"trip_headsign\":\"COUNRYFAIR/PARKLAND\",\"route_id\":\"9ABROWN\",\"block_id\":\"BB3\",\"direction\":\"A\",\"service_id\":\"BB3\",\"shape_id\":\"9A\"}},{\"begin\":{\"lat\":40.131976,\"lon\":-88.288742,\"name\":\"ParklandCollege\",\"stop_id\":\"PKLN:1\",\"time\":\"2012-01-26T13:58:00-06:00\"},\"end\":{\"lat\":40.116209,\"lon\":-88.257355,\"name\":\"University&Prospect(SEFarSide)\",\"stop_id\":\"UNIPSPCT:6\",\"time\":\"2012-01-26T14:11:00-06:00\"},\"route\":{\"route_color\":\"666666\",\"route_id\":\"GREY\",\"route_long_name\":\"Grey\",\"route_short_name\":\"7\",\"route_text_color\":\"ffffff\"},\"trip\":{\"trip_id\":\"3GR763__BB3\",\"trip_headsign\":\"EAST-EDGEWOOD\",\"route_id\":\"GREY\",\"block_id\":\"BB3\",\"direction\":\"East\",\"service_id\":\"BB3\",\"shape_id\":\"7E\"}}],\"type\":\"Service\"}]}]}");  
        Directions d = api.parseTripData(json);
        assertEquals(d.getDuration(), 35);
        assertEquals(d.getDirections().toString(), "[Head East for 0.05 miles to Duncan&Clayton(SECorner)., Take the 9ABROWN bus from Duncan&Clayton(SECorner) to ParklandCollege., Take the GREY bus from ParklandCollege to University&Prospect(SEFarSide).]");
    }
}
