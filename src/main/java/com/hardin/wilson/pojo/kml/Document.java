package com.hardin.wilson.pojo.kml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;


public class Document {
	public String open;

	@XmlElement(name = "Folder")
	public Folder folder;

	public String name;

	@XmlElement(name = "Style")
	public Style[] styles;
}
