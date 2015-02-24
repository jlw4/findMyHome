package com.hardin.wilson.pojo.kml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class MultiGeometry {
	
	@JacksonXmlProperty(localName = "Polygon")
	private Polygon[] polygons;

	public Polygon[] getPolygon() {
		return polygons;
	}

	public void setPolygon(Polygon[] Polygon) {
		this.polygons = Polygon;
	}
}
