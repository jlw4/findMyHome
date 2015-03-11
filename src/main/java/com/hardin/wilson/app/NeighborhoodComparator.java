package com.hardin.wilson.app;

import java.util.Comparator;
import java.util.List;

import com.hardin.wilson.pojo.Neighborhood;

public class NeighborhoodComparator implements Comparator<Neighborhood> {
    
    private List<String> ratings;
    
    public NeighborhoodComparator(List<String> ratings) {
        this.ratings = ratings;
    }

    @Override
    public int compare(Neighborhood o1, Neighborhood o2) {
        // TODO Auto-generated method stub
        return 0;
    }

}
