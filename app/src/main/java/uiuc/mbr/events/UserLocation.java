package uiuc.mbr.events;

import android.support.annotation.Nullable;


/**Describes a user-supplied location (like "Joe's house"), along with data about the actual position of that location.*/
public class UserLocation implements Comparable<UserLocation>
{
	/**What the user entered in the "location" field for one or more events.*/
	public String name;
	/**If the name couldn't be looked up (e.x. "PAR"), then this is non-null
	 * and represents something the user entered that should be recognized by Google's geolocaiton services.*/
	@Nullable public String address;
	/**NaN if unknown (not looked up yet or lookup failed).*/
	public double latitude, longitude;


	/**Maximal constructor.*/
	public UserLocation(String name, @Nullable String address, double latitude, double longitude)
	{
		this.name = name;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**Constructor for a new UserLocation that only has a name.*/
	public UserLocation(String name)
	{
		this(name, null, Double.NaN, Double.NaN);
	}


	@Override
	public String toString(){return "LOCATION '" + name + "' -> '" + address + "' @ " + latitude + ", " + longitude;}

	/**Compares locations by name.*/
	@Override
	public int compareTo(UserLocation that){return this.name.compareTo(that.name);}
}
