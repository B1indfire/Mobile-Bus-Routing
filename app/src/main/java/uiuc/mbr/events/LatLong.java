package uiuc.mbr.events;

import java.io.Serializable;

/**
 * Created by Scott on 2/28/2016.
 */
public class LatLong implements Serializable {
    public double latitude;
    public double longitude;

    public LatLong(double lat, double longi) {
        latitude = lat;
        longitude = longi;
    }
}
