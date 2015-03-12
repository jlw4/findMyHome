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
        int score1 = 0;
        int score2 = 0;
        for (String rating : ratings) {
            score1 += o1.getRating(rating);
            score2 += o2.getRating(rating);
        }
        return score1 - score2;
    }

}
