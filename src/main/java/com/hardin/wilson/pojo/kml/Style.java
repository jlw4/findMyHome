package com.hardin.wilson.pojo.kml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Style {
	@JacksonXmlProperty(isAttribute = true)
	private String id;

	@JacksonXmlProperty(localName = "PolyStyle")
	private PolyStyle polyStyle;

	@JacksonXmlProperty(localName = "LineStyle")
	private LineStyle lineStyle;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PolyStyle getPolyStyle() {
		return polyStyle;
	}

	public void setPolyStyle(PolyStyle PolyStyle) {
		this.polyStyle = PolyStyle;
	}

	public LineStyle getLineStyle() {
		return lineStyle;
	}

	public void setLineStyle(LineStyle LineStyle) {
		this.lineStyle = LineStyle;
	}
}
