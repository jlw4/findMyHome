package com.hardin.wilson.pojo.kml;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Folder {
	private String open;

	private String name;

	@JacksonXmlProperty(localName = "Placemark")
	private ArrayList<Placemark> placemarks;

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Placemark> getPlacemark() {
		return placemarks;
	}

	public void setPlacemark(ArrayList<Placemark> Placemark) {
		this.placemarks = Placemark;
	}
}
