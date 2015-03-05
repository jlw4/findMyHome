package com.hardin.wilson.pojo;

import java.io.File;
import java.util.Map;


public class NeighborhoodRatings {
	public static final File ratingsFile = new File("data/neighborhood_ratings.json");
	
    private String name;
    private Map<String, Integer> ratings;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, Integer> getRatings() {
		return ratings;
	}
	public void setRatings(Map<String, Integer> ratings) {
		this.ratings = ratings;
	}
}
