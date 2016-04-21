package uiuc.mbr.directions;

import java.util.ArrayList;
import java.util.List;

/**
 * Direction class that keeps track of the duration and list of directions that is 
 * parsed from JSON data.
 */
public class Directions {
	public final int duration;
	private final List<String> directions = new ArrayList<>();
	private final List<String> coordinates = new ArrayList<>();
	
	public Directions(int duration) {
		this.duration = duration;
	}

	/**Adds a directions string and a coordinates string.*/
	public void add(String directions, String coordinates) {
		this.directions.add(directions);
		this.coordinates.add(coordinates);
	}

	/**Public for testing only.*/
	public List<String> getDirections() {
		return directions;
	}
	/**Public for testing only.*/
	public List<String> getCoordinates() {
		return coordinates;
	}
}
