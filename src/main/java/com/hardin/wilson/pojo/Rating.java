package com.hardin.wilson.pojo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Rating {
    
    public static final String SCHOOL = "School";
    public static final String CRIME = "Crime";
    public static final String WALK_SCORE = "WalkScore";
    public static final String TRANSIT_SCORE = "TransitScore";
    public static final String HOME_PRICES = "Home Prices";
    public static final String RENT_PRICES = "Rent Prices";
    
    // adding a constant? Make sure to put it here too!
    private static List<String> values = Collections.unmodifiableList(Arrays.asList(SCHOOL, CRIME, WALK_SCORE, TRANSIT_SCORE, HOME_PRICES, RENT_PRICES));
    
    public static List<String> getValues() {
        return values;
    }
}
