package com.hardin.wilson.pojo;

import java.util.ArrayList;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "schools")
public class Schools {
	
    @JacksonXmlProperty(localName = "school")
    @JacksonXmlElementWrapper(useWrapping = false)
	private ArrayList<School> schools;

	public ArrayList<School> getSchools() {
		return schools;
	}

	public void setSchools(ArrayList<School> schools) {
		this.schools = schools;
	}
}
