package com.hardin.wilson.pojo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Rating {
    
    public static final String SCHOOL = "School";
    public static final String SAFETY = "Safety";
    public static final String WALK_SCORE = "WalkScore";
    public static final String TRANSIT_SCORE = "TransitScore";
    
    // adding a constant? Make sure to put it here too!
    private static List<String> values = Collections.unmodifiableList(Arrays.asList(SCHOOL, SAFETY, WALK_SCORE, TRANSIT_SCORE));
    
    public static List<String> getValues() {
        return values;
    }
}
