package com.hardin.wilson.pojo.kml;

import javax.xml.bind.annotation.XmlElement;

public class Placemark {
	
	@XmlElement(name = "Polygon")
	public Polygon polygon;

	public String name;

	public String styleUrl;
	
	@XmlElement(name = "MultiGeometry")
	public MultiGeometry multiGeometry;
}
