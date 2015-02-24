package com.hardin.wilson.pojo.kml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Polygon {
	private OuterBoundaryIs outerBoundaryIs;
	
	@JacksonXmlProperty(localName = "LinearRing")
	private LinearRing linearRing;

	public OuterBoundaryIs getOuterBoundaryIs() {
		return outerBoundaryIs;
	}

	public void setOuterBoundaryIs(OuterBoundaryIs outerBoundaryIs) {
		this.outerBoundaryIs = outerBoundaryIs;
	}

	public LinearRing getLinearRing() {
		return linearRing;
	}

	public void setLinearRing(LinearRing linearRing) {
		this.linearRing = linearRing;
	}
}
