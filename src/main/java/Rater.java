/**
 * Write a description of class Rater here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

import pojo.Rating;

import java.util.*;
import java.util.stream.Collectors;

public class Rater {
    private String myID;
    private ArrayList<Rating> myRatings;

    public Rater(String id) {
        myID = id;
        myRatings = new ArrayList<Rating>();
    }

    public void addRating(String item, double rating) {
        myRatings.add(new Rating(item,rating));
    }

    public boolean hasRating(String item) {
        return myRatings.stream().anyMatch(rating -> rating.getItem().equals(item));
    }

    public String getID() {
        return myID;
    }

    public double getRating(String item) {
        Optional<Rating> res = myRatings.stream().filter(rating -> rating.getItem().equals(item)).findFirst();
        return res.map(Rating::getValue).orElse(-1.0);
    }

    public int numRatings() {
        return myRatings.size();
    }

    public List<String> getItemsRated() {
        return myRatings.stream().map(Rating::getItem).collect(Collectors.toList());
    }
}
