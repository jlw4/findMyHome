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
    
    /**
     * Checks if a point is contained within the bounds of this neighborhood.
     * 
     * @param point The point to be tested.
     * @return True if the point is inside the bounds of this neighborhood's
     * polygon and false if it lies outside of the polygon.
     */
	public boolean isInsideBounds(Coordinate point) {
		if (boundary.size() < 3) {
			// Point cannot be inside if not at least three boundary
			// points (is not a polygon).
			return false;
		}
		
		int i, j, nvert = boundary.size();
		double px = point.getLongitude();
		double py = point.getLatitude();
		boolean c = false;

		// The algorithm is ray-casting to the right. Each iteration of the loop, the test point
		// is checked against one of the polygon's edges. The first line of the if-test succeeds
		// if the point's y-coord is within the edge's scope. The second line checks whether the
		// test point is to the left of the line. If that is true the ray-casted line drawn 
		// rightwards from the test point crosses that edge.

		// By repeatedly inverting the value of c, the algorithm counts how many times the rightward
		// line crosses the polygon. If it crosses an odd number of times, then the point is inside;
		// if an even number, the point is outside.
		for (i = 0, j = nvert - 1; i < nvert; j = i++) {
			double iy, ix, jy, jx;
			iy = boundary.get(i).getLatitude();
			ix = boundary.get(i).getLongitude();
			jy = boundary.get(j).getLatitude();
			jx = boundary.get(j).getLongitude();

			if (((iy >= py) != (jy >= py))
					&& (px <= (jx - ix) * (py - iy) / (jy - iy) + ix)) {
				c = !c;
			}
		}

		return c;
	}
    
    
    /* Getters and Setters */

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
