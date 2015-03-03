package com.hardin.wilson.pojo;

public class Coordinate {
    
    private long id;
    private double longitude;
    private double latitude;
    
    public Coordinate() {
        // required
    }
    
    public Coordinate(double longitude, double latitude) {
        this(0, longitude, latitude);
    }
    
    public Coordinate(long id, double longitude, double latitude) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    public double distance(Coordinate other) {
        double d1 = latitude - other.latitude;
        double d2 = longitude - other.longitude;
        return Math.sqrt((d1 * d1) + (d2 * d2));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}
