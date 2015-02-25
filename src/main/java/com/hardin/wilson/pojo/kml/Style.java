package com.hardin.wilson.pojo.kml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Style {
	@XmlAttribute
	public String id;

	@XmlElement(name = "PolyStyle")
	public PolyStyle polyStyle;

	@XmlElement(name = "LineStyle")
	public LineStyle lineStyle;
}
