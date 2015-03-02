package com.hardin.wilson.pojo;

public enum Rating {
    
    SCHOOL ("School"),
    CRIME ("Crime"),
    WALK_SCORE ("WalkScore"),
    TRANSIT_SCORE ("TransitScore"),
    HOME_PRICES ("Home Prices"),
    RENT_PRICES ("Rent Prices");
    
    private String name;
    
    Rating(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String toString() {
        return name;
    }
}
