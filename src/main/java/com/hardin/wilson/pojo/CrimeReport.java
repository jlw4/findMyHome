package com.hardin.wilson.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CrimeReport {
	private String event_clearance_subgroup;
	private String event_clearance_date;
	private String cad_cdw_id;
	private String longitude;
	private String latitude;
	
	public String getEvent_clearance_subgroup() {
		return event_clearance_subgroup;
	}
	public void setEvent_clearance_subgroup(String event_clearance_subgroup) {
		this.event_clearance_subgroup = event_clearance_subgroup;
	}
	public String getEvent_clearance_date() {
		return event_clearance_date;
	}
	public void setEvent_clearance_date(String event_clearance_date) {
		this.event_clearance_date = event_clearance_date;
	}
	public String getCad_cdw_id() {
		return cad_cdw_id;
	}
	public void setCad_cdw_id(String cad_cdw_id) {
		this.cad_cdw_id = cad_cdw_id;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
}
