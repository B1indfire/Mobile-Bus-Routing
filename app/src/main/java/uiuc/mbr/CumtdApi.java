package uiuc.mbr;

import java.io.IOException;
import java.net.*;
import java.util.*;
import org.json.*;

public class CumtdApi {

    // This class is used for connecting and retrieving data from CumtdApi. Further JSON parsing methods should be implemented here.

    String url;
    String key;

    public CumtdApi(String url, String key) {
        this.url = url;
        this.key = key;
    }

    private JSONObject getFromApi(String name) throws MalformedURLException, IOException, org.json.JSONException{
        return jsonFromString(readFromUrl(url + "/" + name + "?key=" + key));
    }

    private JSONObject getFromApi(String name, String parameterName, String parameter) throws MalformedURLException, IOException, org.json.JSONException{
        String url = this.url + "/" + name + "?key=" + key + "&" + parameterName + "=" + parameter;
        return jsonFromString(readFromUrl(url));
    }

    private JSONObject getFromApi(String name, String parameterName1, String parameter1, String parameterName2, String parameter2) throws MalformedURLException, IOException, org.json.JSONException{
        String url = this.url + "/" + name + "?key=" + key + "&" + parameterName1 + "=" + parameter1 + "&" + parameterName2 + "=" + parameter2;
        return jsonFromString(readFromUrl(url));
    }

    private JSONObject getFromApi(String name, String parameterName1, String parameter1, String parameterName2, String parameter2, String parameterName3, String parameter3) throws MalformedURLException, IOException, org.json.JSONException{
        String url = this.url + "/" + name + "?key=" + key + "&" + parameterName1 + "=" + parameter1 + "&" + parameterName2 + "=" + parameter2 + "&" + parameterName3 + "=" + parameter3;
        return jsonFromString(readFromUrl(url));
    }

    private JSONObject getFromApi(String name, String parameterName1, String parameter1, String parameterName2, String parameter2, String parameterName3, String parameter3, String parameterName4, String parameter4) throws MalformedURLException, IOException, org.json.JSONException{
        String url = this.url + "/" + name + "?key=" + key + "&" + parameterName1 + "=" + parameter1 + "&" + parameterName2 + "=" + parameter2 + "&" + parameterName3 + "=" + parameter3 + "&" + parameterName4 + "=" + parameter4;
        return jsonFromString(readFromUrl(url));
    }

    private String readFromUrl(String s) throws MalformedURLException, IOException {
        Scanner scanner = new Scanner(new URL(s.replace(" ", "%20")).openStream(), "UTF-8");
        scanner.useDelimiter("\\A");
        String out = scanner.next();
        scanner.close();
        return out;
    }

    private JSONObject jsonFromString(String s) throws org.json.JSONException {
        JSONObject json = new JSONObject(s);
        return json;
    }

    public JSONObject getCalendarDatesByDate(String date) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetCalendarDatesByDate", "date", date);
    }

    public JSONObject getCalendarDatesByService(String serviceId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetCalendarDatesByService", "service_id", serviceId);
    }

    public JSONObject getDeparturesByStop(String stopId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetDeparturesByStop", "stop_id", stopId);
    }

    public JSONObject getReroutes() throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetReroutes");
    }

    public JSONObject getReroutesByRoute(String routeId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetReroutesByRoute", "route_id", routeId);
    }

    public JSONObject getRoute(String routeId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetRoute", "route_id", routeId);
    }

    public JSONObject getRoutes() throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetRoutes");
    }

    public JSONObject getRoutesByStop(String stopId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetRoutesByStop", "stop_id", stopId);
    }

    public JSONObject getShape(String shapeId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetShape", "shape_id", shapeId);
    }

    public JSONObject getShapeBetweenStops(String beginStopId, String endStopId, String shapeId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetShapeBetweenStops", "begin_stop_id", beginStopId, "end_stop_id", endStopId, "shape_id", shapeId);
    }

    public JSONObject getStop(String stopId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetStop", "stop_id", stopId);
    }

    public JSONObject getStops(String in) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetStops");
    }

    public JSONObject getStopsByLatLon(String lat, String lon) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetStopsByLatLon", "lat", lat, "lon", lon);
    }

    public JSONObject getStopTimesByTrip(String tripId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetStopTimesByTrip", "trip_id", tripId);
    }

    public JSONObject getStopTimesByStop(String stopId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetStopTimesByStop", "stop_id", stopId);
    }

    public JSONObject getPlannedTripsByLatLon(String originLat, String originLon, String destinationLat, String destinationLon) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetPlannedTripsByLatLon", "origin_lat", originLat, "origin_lon", originLon, "destination_lat", destinationLat, "destination_lon", destinationLon);
    }

    public JSONObject getPlannedTripsByStops(String originStopId, String destinationStopId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetPlannedTripsByStop", "origin_stop_id", originStopId, "destination_stop_id", destinationStopId);
    }

    public JSONObject getTrip(String tripId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetTrip", "trip_id", tripId);
    }

    public JSONObject getTripsByBlock(String blockId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetTripsByBlock", "block_id", blockId);
    }

    public JSONObject getTripsByRoute(String routeId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetTripsByRoute", "route_id", routeId);
    }

    public JSONObject getVehicle(String vehicleId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetVehicle", "vehicle_id", vehicleId);
    }

    public JSONObject getVehicles(String in) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetVehicles");
    }

    public JSONObject getVehiclesByRoute(String routeId) throws MalformedURLException, IOException, org.json.JSONException{
        return getFromApi("GetVehiclesByRoute", "route_id", routeId);
    }

    public List<String> getShapeCoords(String shapeId) throws JSONException, MalformedURLException, IOException {
        List<String> list = new ArrayList<String>();
        JSONArray array = getShape(shapeId).getJSONArray("shapes");
        for(int i = 0 ; i < array.length() ; i = i + 2){
            list.add(array.getJSONObject(i).get("shape_pt_lat").toString());
            list.add(array.getJSONObject(i).get("shape_pt_lon").toString());
        }
        return list;
    }
}