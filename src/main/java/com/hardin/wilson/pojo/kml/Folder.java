package com.hardin.wilson.pojo.kml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

public class Folder {
	public String open;

	public String name;

	@XmlElement(name = "Placemark")
	public Placemark[] placemarks;
}
