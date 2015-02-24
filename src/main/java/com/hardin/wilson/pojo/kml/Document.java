package com.hardin.wilson.pojo.kml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Document {
	private String open;

	@JacksonXmlProperty(localName = "Folder")
	private Folder folder;

	private String name;

	@JacksonXmlProperty(localName = "Style")
	@JacksonXmlElementWrapper(useWrapping=false)
	private Style[] styles;

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder Folder) {
		this.folder = Folder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Style[] getStyle() {
		return styles;
	}

	public void setStyle(Style[] Style) {
		this.styles = Style;
	}
}
