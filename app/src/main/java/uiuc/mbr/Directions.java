package uiuc.mbr;

import java.util.ArrayList;
import java.util.List;

/**
 * Direction class that keeps track of the duration and list of directions that is 
 * parsed from JSON data.
 */
public class Directions {
	private int duration;
	private List<String> directions = new ArrayList<String>();
	
	public Directions(int duration) {
		this.duration = duration;
	}
	
	public void addDirections(String s) {
		directions.add(s);
	}
	
	public int getDuration() {
		return duration;
	}
	
	public List<String> getDirections() {
		return directions;
	}
}
