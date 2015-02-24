package com.hardin.wilson.pojo.kml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Placemark {
	
	@JacksonXmlProperty(localName = "Polygon")
	private Polygon polygon;

	private String description;

	private String name;

	private String styleUrl;

	@JacksonXmlProperty(localName = "ExtendedData")
	private String extendedData;
	
	@JacksonXmlProperty(localName = "MultiGeometry")
	private MultiGeometry multiGeometry;

	public Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(Polygon Polygon) {
		this.polygon = Polygon;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStyleUrl() {
		return styleUrl;
	}

	public void setStyleUrl(String styleUrl) {
		this.styleUrl = styleUrl;
	}

	public String getExtendedData() {
		return extendedData;
	}

	public void setExtendedData(String ExtendedData) {
		this.extendedData = ExtendedData;
	}

	public MultiGeometry getMultiGeometry() {
		return multiGeometry;
	}

	public void setMultiGeometry(MultiGeometry multiGeometry) {
		this.multiGeometry = multiGeometry;
	}
}
