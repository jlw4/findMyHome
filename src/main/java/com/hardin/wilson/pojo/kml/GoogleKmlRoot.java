package com.hardin.wilson.pojo.kml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@XmlRootElement(name = "kml")
public class GoogleKmlRoot {
	
	@XmlElement(name = "Document")
	public Document document;
}
