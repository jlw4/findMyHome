package com.hardin.wilson.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WalkScore {
	private int walkScore;

	public int getWalkscore() {
		return walkScore;
	}

	public void setWalkscore(int walkscore) {
		this.walkScore = walkscore;
	}
	
}
