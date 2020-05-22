package com.up.larp.geo;


/**
 * DataClass containing geo coordinates
 */
public class SimpleLocation {
    public double lat;
    public double lon;

    /**
     * @param lat latitude
     * @param lon longitude
     */
    public SimpleLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
