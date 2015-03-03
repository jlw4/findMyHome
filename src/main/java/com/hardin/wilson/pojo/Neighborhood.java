package com.hardin.wilson.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neighborhood {
    
    private String name;
    private double longitude;
    private double latitude;
    private Coordinate center;
    private List<School> schools;
    private Map<Rating, Integer> ratings;
    private List<Coordinate> boundary;

    public Neighborhood() {
        this(null, 0, 0);
    }
    
    public Neighborhood(String name, double longitude, double latitude) {
        // distance from greenlake to maple leaf / 2 0.011563
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        center = new Coordinate(longitude, latitude);
        boundary = new ArrayList<Coordinate>();
        schools = new ArrayList<School>();
        ratings = new HashMap<Rating, Integer>();
    }
    
    public void computeAverageSchoolRating() {
        int sum = 0;
        for (School s : schools) {
            sum += s.getGsRating() * 10;
        }
        ratings.put(Rating.SCHOOL, sum / schools.size());
    }
    
    public void addCoordinate(Coordinate c) {
        boundary.add(c);
    }
    
    public void addSchool(School school) {
        schools.add(school);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Map<Rating, Integer> getRatings() {
        return ratings;
    }

    public void setRatings(Map<Rating, Integer> ratings) {
        this.ratings = ratings;
    }

    public List<Coordinate> getBoundary() {
        return boundary;
    }

    public void setBoundary(List<Coordinate> boundary) {
        this.boundary = boundary;
    }

    public Coordinate getCenter() {
        return center;
    }

    public void setCenter(Coordinate center) {
        this.center = center;
    }

    public List<School> getSchools() {
        return schools;
    }

    public void setSchools(List<School> schools) {
        this.schools = schools;
    }
}
