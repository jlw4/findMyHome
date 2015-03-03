package com.hardin.wilson.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Neighborhood descriptions
 */
public class Descriptions {
    
    private Map<String, String> descriptions; // neighborhood name -> description
    private Map<String, String> citations;
    
    public Descriptions() {
        descriptions = new HashMap<String, String>();
        citations = new HashMap<String, String>();
    }
    
    public String getCitation(String name) {
        return citations.get(name);
    }
    
    public String getDescription(String name) {
        return descriptions.get(name);
    }
    
    public void addDescription(String name, String description, String citation) {
        descriptions.put(name, description);
        citations.put(name, citation);
    }

    public Map<String, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<String, String> descriptions) {
        this.descriptions = descriptions;
    }

    public Map<String, String> getCitations() {
        return citations;
    }

    public void setCitations(Map<String, String> citations) {
        this.citations = citations;
    }

}
