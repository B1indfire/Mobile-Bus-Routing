package uiuc.mbr.directions;

import java.io.IOException;
import java.net.*;
import java.util.*;
import org.json.*;

/**Gets data from the CUMTD API.
 * This class interacts with the offical API provided by CUMTD in order to retrieve data and process
 * it for MBR application.
 */
public class CumtdApi {
	private static final CumtdApi INSTANCE = new CumtdApi();

	private final String url = "https://developer.cumtd.com/api/v2.2/JSON";
	private final String key = "c4d5e4bb2baa48ba85772b857c9839c8";

	private CumtdApi(){}

	public static CumtdApi create() {
		return INSTANCE;
	}

	/**
	 * This method takes parameter name and parameter as a parameter and generates a URL to be
	 * passed on to the offical CUMTD API and retrieved a JSONObject from it.
	 */
	private JSONObject getFromApi(String name, String parameterName, String parameter) throws IOException, org.json.JSONException{
		String url = this.url + "/" + name + "?key=" + key + "&" + parameterName + "=" + parameter;
		return jsonFromString(readFromUrl(url));
	}

	/**
	 * This method takes parameter names and parameters as a parameter and generates a URL to be
	 * passed on to the offical CUMTD API and retrieved a JSONObject from it.
	 */
	private JSONObject getFromApi(String name, String parameterName1, String parameter1, String parameterName2, String parameter2) throws IOException, org.json.JSONException{
		String url = this.url + "/" + name + "?key=" + key + "&" + parameterName1 + "=" + parameter1 + "&" + parameterName2 + "=" + parameter2;
		return jsonFromString(readFromUrl(url));
	}

	/**
	 * This method takes parameter names and parameters as a parameter and generates a URL to be
	 * passed on to the offical CUMTD API and retrieved a JSONObject from it.
	 */
	private JSONObject getFromApi(String name, String parameterName1, String parameter1, String parameterName2, String parameter2, String parameterName3, String parameter3) throws IOException, org.json.JSONException{
		String url = this.url + "/" + name + "?key=" + key + "&" + parameterName1 + "=" + parameter1 + "&" + parameterName2 + "=" + parameter2 + "&" + parameterName3 + "=" + parameter3;
		return jsonFromString(readFromUrl(url));
	}

	/**
	 * This method takes the url as a parameter and reads the returned data and return it as a String.
	 */
	private String readFromUrl(String s) throws IOException {
		Scanner scanner = new Scanner(new URL(s.replace(" ", "%20")).openStream(), "UTF-8");
		scanner.useDelimiter("\\A");
		String out = scanner.next();
		scanner.close();
		return out;
	}

	/**
	 * This method takes a string as a parameter and returns a JSONObject from the string.
	 * This method will fail if the string is not in a JSON format.
	 */
	private JSONObject jsonFromString(String s) throws JSONException {
		return new JSONObject(s);
	}

	/**
	 * This method takes a stopId and return a JSONObject representing the list of upcoming
	 * departures that will happen at that specific stop.
	 */
	public JSONObject getDeparturesByStop(String stopId) throws IOException, org.json.JSONException{
		return getFromApi("GetDeparturesByStop", "stop_id", stopId);
	}

	/**
	 * This method takes the beginning and end stopId and the shapeId, and uses this to to return
	 * a JSONObject containing the part of the shape between the two stopIds. This is used to draw
	 * the appropriate path on the map.
	 */
	public JSONObject getShapeBetweenStops(String beginStopId, String endStopId, String shapeId) throws IOException, org.json.JSONException{
		return getFromApi("GetShapeBetweenStops", "begin_stop_id", beginStopId, "end_stop_id", endStopId, "shape_id", shapeId);
	}

	/**
	 * This method takes the latitude and longitude of the user as a parameter and returns a
	 * JSONObject representing all the nearby stops.
	 */
	public JSONObject getStopsByLatLon(String lat, String lon) throws IOException, org.json.JSONException{
		return getFromApi("GetStopsByLatLon", "lat", lat, "lon", lon);
	}

	/**
	 * This takes the beginning stopId, ending stopId, and the shapeId in order to return a list of
	 * the coordinates required to draw the path on the map. This method calls the
	 * getShapeBetweenStops method in order to do this.
	 */
	public List<String> getShapeCoordsByStop(String beginStop, String endStop, String shapeId) throws JSONException, IOException {
		List<String> list = new ArrayList<>();
		JSONArray array = getShapeBetweenStops(beginStop, endStop, shapeId).getJSONArray("shapes");
		System.out.println(array);
		for(int i = 0 ; i < array.length() ; i = i + 2){
			list.add(array.getJSONObject(i).get("shape_pt_lat").toString());
			list.add(array.getJSONObject(i).get("shape_pt_lon").toString());
		}
		return list;
	}

	/**
	 * This method calls the getStopsByLatLon with the appropriate parameters and parses the
	 * JSONObject into a List<String> instead.
	 */
	public List<String> getNearestStops(String x, String y) throws JSONException, IOException {
		List<String> list = new ArrayList<>();
		JSONArray array = getStopsByLatLon(x, y).getJSONArray("stops");
		for (int i = 0; i < 5; i++) {
			String stop_id = (String) array.getJSONObject(i).get("stop_id");
			String stop_name = (String) array.getJSONObject(i).get("stop_name");
			list.add(stop_id + ":" + stop_name);
		}
		return list;
	}

	/**
	 * This method takes the stopId as a parameter and parses the JSONobject as a List<String> instead.
	 */
	public List<String> getDepartures(String stopId) throws JSONException, IOException {
		List<String> list = new ArrayList<>();
		JSONArray array = getDeparturesByStop(stopId).getJSONArray("departures");
		for (int i = 0; i < array.length(); i++) {
			String headsign = (String) array.getJSONObject(i).get("headsign");
			String expected = (String) array.getJSONObject(i).get("expected");
			list.add(expected + ":" + headsign);
		}
		return list;
	}

	/**
	 * This method parse the trip data from json to Directions object. This methods return null if
	 * there is no way to get to the destination.
	 */
	public Directions parseTripData(JSONObject object) throws JSONException, IOException {
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
			if (type.equals("Walk")) {
				JSONObject walk = current.getJSONObject("walk");
				String direction = walk.getString("direction");
				String distance = walk.get("distance").toString();
				JSONObject begin = walk.getJSONObject("begin");
				JSONObject end = walk.getJSONObject("end");
				double beginLat = begin.getDouble("lat");
				double beginLon = begin.getDouble("lon");
				double endLat = end.getDouble("lat");
				double endLon = end.getDouble("lon");
				String target = end.getString("name");
				String sd = "Head " + direction + " for " + distance + " miles to " + target + ".";
				String sc = "W:" + beginLat + "," + beginLon + "," + endLat + "," + endLon;
				d.add(sd, sc);
			}

			if (type.equals("Service")) {
				JSONArray services = current.getJSONArray("services");
				for (int j = 0; j < services.length(); j++)
				{
					JSONObject service = services.getJSONObject(j);
					JSONObject begin = service.getJSONObject("begin");
					JSONObject end = service.getJSONObject("end");
					JSONObject route = service.getJSONObject("route");
					JSONObject trip = service.getJSONObject("trip");
					String shape = trip.getString("shape_id");
					String start = begin.getString("name");
					String finish = end.getString("name");
					String bus = route.getString("route_id");
					String beginStopId = begin.getString("stop_id");
					String endStopId = end.getString("stop_id");
					List<String> coords = getShapeCoordsByStop(beginStopId, endStopId, shape);
					String shapeCoords = "";
					for(int k = 0; k < coords.size(); k = k + 2)
					{
						shapeCoords += coords.get(k) + "," + coords.get(k + 1) + ",";
					}
					String sd = "Take the " + bus + " bus from " + start + " to " + finish + ".";
					String sc = "S:" + shapeCoords.substring(0, shapeCoords.length() - 1);
					d.add(sd, sc);
				}
			}
		}
		return d;
	}

	/**
	 * This method takes the following parameters in order to generate a directions object,
	 * containing a list of coordinates to follow, as well as the list of string directions, as
	 * well as the expected duration of the travel.
	 */
	public Directions getTripArriveBy(String originLat, String originLon, String destinationLat,
									  String destinationLon, String date, String time,
									  String maxWalk, String arriveDepart)
			throws IOException, JSONException {
		String url = this.url + "/GetPlannedTripsByLatLon?key=" + key + "&origin_lat=" + originLat +
				"&origin_lon=" + originLon + "&destination_lat=" + destinationLat+
				"&destination_lon=" + destinationLon + "&date=" + date + "&time=" + time +
				"&max_walk=" + maxWalk + "&arrive_depart=" + arriveDepart;
		return parseTripData(jsonFromString(readFromUrl(url)));
	}
}
