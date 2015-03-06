package com.hardin.wilson.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neighborhood {
    
    private String name;
    private double longitude; // center long
    private double latitude;  // center lat
    private Coordinate center;
    private Map<String, Integer> ratings;
    private List<Coordinate> boundary;
    private String description;
    private String descriptionCitation;

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
        ratings = new HashMap<String, Integer>();
    }
    
    public void addDescription(String description, String citation) {
        this.description = description;
        this.descriptionCitation = citation;
    }
    
    public void addRating(String r, int score) {
        ratings.put(r, score);
    }
    
    public void addCoordinate(Coordinate c) {
        boundary.add(c);
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

    public Map<String, Integer> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Integer> ratings) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionCitation() {
        return descriptionCitation;
    }

    public void setDescriptionCitation(String descriptionCitation) {
        this.descriptionCitation = descriptionCitation;
    }
}
