package com.hardin.wilson.pojo.kml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OuterBoundaryIs {
	
	@JacksonXmlProperty(localName = "LinearRing")
	private LinearRing linearRing;

	public LinearRing getLinearRing() {
		return linearRing;
	}

	public void setLinearRing(LinearRing linearRing) {
		this.linearRing = linearRing;
	}
}
