package com.hardin.wilson.pojo.kml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "kml")
public class GoogleKmlRoot {
    
    public GoogleKmlRoot(Document doc) {
        document = doc;
    }
    
    public GoogleKmlRoot() {
        
    }
	
	@XmlElement(name = "Document")
	public Document document;
}
