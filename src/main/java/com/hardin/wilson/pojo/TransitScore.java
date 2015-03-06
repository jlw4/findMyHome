package com.hardin.wilson.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransitScore {
	private int transit_score;

	public int getTransit_score() {
		return transit_score;
	}

	public void setTransit_score(int transit_score) {
		this.transit_score = transit_score;
	}

}
