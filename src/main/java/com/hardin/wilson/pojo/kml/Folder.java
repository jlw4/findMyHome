package com.hardin.wilson.pojo.kml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Folder {
	private String open;

	private String name;

	@JacksonXmlProperty(localName = "Placemark")
	@JacksonXmlElementWrapper(useWrapping=false)
	private Placemark[] placemarks;

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

	public Placemark[] getPlacemark() {
		return placemarks;
	}

	public void setPlacemark(Placemark[] Placemark) {
		this.placemarks = Placemark;
	}
}
