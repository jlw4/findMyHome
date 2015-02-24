package com.hardin.wilson.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

public class Neighborhood {
    
    private String name;
    private double longitude;
    private double latitude;
    private List<School> schools;
    private double radius;
    private int schoolRating;

    public Neighborhood() {
        this(null, 0, 0);
    }
    
    public Neighborhood(String name, double longitude, double latitude) {
        radius = 0.011563; // distance from greenlake to maple leaf / 2
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        schools = new ArrayList<School>();
    }
    
    public void computeAverageSchoolRating() {
        int sum = 0;
        for (School s : schools) {
            sum += s.getGsRating() * 10;
        }
        schoolRating = sum / schools.size();
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

    public List<School> getSchools() {
        return schools;
    }

    public void setSchools(List<School> schools) {
        this.schools = schools;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getSchoolRating() {
        return schoolRating;
    }

    public void setSchoolRating(int schoolRating) {
        this.schoolRating = schoolRating;
    }

}
