package com.hardin.wilson.pojo.kml;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Polygon {
	public OuterBoundaryIs outerBoundaryIs;
	
	@XmlElement(name = "LinearRing")
	public LinearRing linearRing;
}
