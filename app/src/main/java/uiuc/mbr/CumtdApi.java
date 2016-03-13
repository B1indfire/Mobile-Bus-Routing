package uiuc.mbr;

import java.io.IOException;
import java.net.*;
import java.util.*;
import org.json.*;

public class CumtdApi {

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

    /**
     * Get nearest stops from the current latitude and longitude.
     * @param x
     * @param y
     * @return
     * @throws JSONException
     * @throws MalformedURLException
     * @throws IOException
     */
    public List<String> getNearestStops(String x, String y) throws JSONException, MalformedURLException, IOException {
        List<String> list = new ArrayList<String>();
        JSONArray array = getStopsByLatLon(x, y).getJSONArray("stops");
        for (int i = 0; i < 5; i++) {
            String stop_id = (String) array.getJSONObject(i).get("stop_id");
            String stop_name = (String) array.getJSONObject(i).get("stop_name");
            list.add(stop_id + ":" + stop_name);
        }
        return list;
    }

    /**
     * Get future departures from current stop id.
     * @param stop_id
     * @return
     * @throws JSONException
     * @throws MalformedURLException
     * @throws IOException
     */
    public List<String> getDepartures(String stop_id) throws JSONException, MalformedURLException, IOException {
        List<String> list = new ArrayList<String>();
        JSONArray array = getDeparturesByStop(stop_id).getJSONArray("departures");
        for (int i = 0; i < array.length(); i++) {
            String headsign = (String) array.getJSONObject(i).get("headsign");
            String expected = (String) array.getJSONObject(i).get("expected");
            list.add(expected + ":" + headsign);
        }
        return list;
    }

    /**
     * This method parse the trip data from json to Directions object.
     * This methods return null if there is no way to get to the destination.
     * @param object
     * @return
     */
    public Directions parseTripData(JSONObject object) throws JSONException {
        JSONArray itineraries = object.getJSONArray("itineraries");
        if (itineraries.length() <= 0) {
            return null;
        }
        JSONObject itinerary = itineraries.getJSONObject(0);
        int duration = itinerary.getInt("travel_time");
        Directions d = new Directions(duration);
        JSONArray legs = itinerary.getJSONArray("legs");
        for (int i = 0; i < legs.length(); i++) {
            JSONObject current = legs.getJSONObject(i);
            String type = current.getString("type");
            if (type.equals("Walk")) {;
                JSONObject walk = current.getJSONObject("walk");
                String direction = walk.getString("direction");
                String distance = walk.get("distance").toString();
                JSONObject end = walk.getJSONObject("end");
                String target = end.getString("name");
                d.addDirections("Head " + direction + " for " + distance + " miles to " + target + ".");
            } 
            if (type.equals("Service")) {
                JSONArray services = current.getJSONArray("services");
                for (int j = 0; j < services.length(); j++) {
                    JSONObject service = services.getJSONObject(j);
                    JSONObject begin = service.getJSONObject("begin");
                    JSONObject end = service.getJSONObject("end");
                    JSONObject route = service.getJSONObject("route");
                    String start = begin.getString("name");
                    String finish = end.getString("name");
                    String bus = route.getString("route_id");
                    d.addDirections("Take the " + bus + " bus from " + start + " to " + finish + ".");
                }
            }
        }
        return d;
    }
    
    /**
     * Retrieves directions JSON data to be parsed.
     * @param origin_lat
     * @param origin_lon
     * @param destination_lat
     * @param destination_lon
     * @param date
     * @param time
     * @param max_walk
     * @param arrive_depart
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public Directions getTripArriveBy(String origin_lat, String origin_lon, String destination_lat, String destination_lon, String date, String time, String max_walk, String arrive_depart) throws MalformedURLException, IOException, JSONException {
        String url = this.url + "/GetPlannedTripsByLatLon?key=" + key + "&origin_lat=" + origin_lat + "&origin_lon=" + origin_lon + "&destination_lat=" + destination_lat+ "&destination_lon=" + destination_lon + "&date=" + date + "&time=" + time + "&max_walk=" + max_walk + "&arrive_depart=" + arrive_depart;
        return parseTripData(jsonFromString(readFromUrl(url)));
        
    }
}
