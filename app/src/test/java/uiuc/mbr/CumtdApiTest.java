package uiuc.mbr;

import org.json.*;
import org.json.JSONException;import org.junit.Test;

import java.io.IOException;
import java.lang.String;import java.util.List;
import uiuc.mbr.CumtdApi;import static org.junit.Assert.*;

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
}
