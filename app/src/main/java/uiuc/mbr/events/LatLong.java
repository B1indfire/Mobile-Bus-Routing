package uiuc.mbr.events;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uiuc.mbr.calendar.Event;

/**
 * Created by Scott on 2/28/2016.
 * A serializable version of LatLng
 */
public class LatLong implements Serializable {
    private double latitude;
    private double longitude;
    private String streetAddress;

    public LatLong(double lat, double longi, String addr) {
        latitude = lat;
        longitude = longi;
        streetAddress = addr;
    }

    /**
     * Finds the LatLong of an Event's location using Google's Geocoding API
     * If the address is invalid, checks for address in AddressBook
     * @return A LatLong if the address' location is valid or in AddressBook - null otherwise
     */
    public static LatLong getEventLocation(Event event, Context c) {
        return getEventLocation(event.getLocation(), c);
    }

    /**
     * Finds the LatLong of a string address using Google's Geocoding API
     * If the address is invalid, checks for address in AddressBook
     * @return A LatLong if the address' location is valid or in AddressBook - null otherwise
     */
    public static LatLong getEventLocation(String location, Context c) {
        Geocoder geocoder = new Geocoder(c, Locale.getDefault());
        List<Address> address = new ArrayList<Address>() {};
        try {
            address = geocoder.getFromLocationName(location, 1, 39.47, -88.95, 40.49, -87.43); //Champaign area coords
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address == null || address.size() == 0) {
            return AddressBook.loadLatLongFromMemory(location, c);
        }


        Address a = address.get(0);
        return new LatLong(a.getLatitude(), a.getLongitude(), location);
    }




    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
}
