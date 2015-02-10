package com.hardin.wilson.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Even though this will be parsed from xml, we use the JsonIgnoreProperties
// annotation to ignore extra fields. A bit odd.
@JsonIgnoreProperties(ignoreUnknown = true)
public class School {
	private int gsId;
	private String name;
	private String type;
	private String gradeRange;
	private String city;
	private String state;
	private String address;
	private String phone;
	private String website;
	private String ncesId;
	private String districtId;
	private String overviewLink;
	private String ratingsLink;
	private String reviewsLink;
	private String schoolStatsLink;
	private String fax;
	private int enrollment;
	private int parentRating;
	private int gsRating;
	private double lat;
	private double lon;
	
	public School() { }

	public int getGsId() {
		return gsId;
	}

	public void setGsId(int gsId) {
		this.gsId = gsId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGradeRange() {
		return gradeRange;
	}

	public void setGradeRange(String gradeRange) {
		this.gradeRange = gradeRange;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getNcesId() {
		return ncesId;
	}

	public void setNcesId(String ncesId) {
		this.ncesId = ncesId;
	}

	public String getOverviewLink() {
		return overviewLink;
	}

	public void setOverviewLink(String overviewLink) {
		this.overviewLink = overviewLink;
	}

	public String getRatingsLink() {
		return ratingsLink;
	}

	public void setRatingsLink(String ratingsLink) {
		this.ratingsLink = ratingsLink;
	}

	public String getReviewsLink() {
		return reviewsLink;
	}

	public void setReviewsLink(String reviewsLink) {
		this.reviewsLink = reviewsLink;
	}

	public String getSchoolStatsLink() {
		return schoolStatsLink;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setSchoolStatsLink(String schoolStatsLink) {
		this.schoolStatsLink = schoolStatsLink;
	}

	public int getEnrollment() {
		return enrollment;
	}

	public void setEnrollment(int enrollment) {
		this.enrollment = enrollment;
	}

	public int getParentRating() {
		return parentRating;
	}

	public void setParentRating(int parentRating) {
		this.parentRating = parentRating;
	}

	public int getGsRating() {
		return gsRating;
	}

	public void setGsRating(int gsRating) {
		this.gsRating = gsRating;
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

	public String getDistrictId() {
		return districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
	
}
