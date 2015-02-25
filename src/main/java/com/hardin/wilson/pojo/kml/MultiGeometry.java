package com.hardin.wilson.pojo.kml;

import javax.xml.bind.annotation.XmlElement;

public class MultiGeometry {
	
	@XmlElement(name = "Polygon")
	public Polygon[] polygons;
}
