import java.util.*;

public class User {
    int I;      // ID
    String N;   // Name
    ArrayList<Rating> RatingList = new ArrayList<>();

    public User(int ID) {
        I = ID;
        N = "";
        RatingList = new ArrayList<>();
    }

    public int getID() {
        return I;
    }

    public void setID(int ID) {
        I = ID;
    }

    public String getName() {
        return N;
    }

    public void setName(String Name) {
        N = Name;
    }

    public String toString() {
        String temp;
        temp = I + ", " + N;
        //temp = I + ", " + N + ", " + RatingList.toString();
        return temp;
    }

    // Adds a Rating object to the list of ratings
    public void addRating(Rating rating){
        Rating temp = getRatingObj(rating.getID());
        if (temp == null) {
            RatingList.add(rating);
        }
        else {
            temp.setRating(rating.getRating());
        }
    }

    // Returns the score of the movie
    public int getMovieScore(int movieID){
        Rating rate_temp = getRatingObj(movieID);
        if (rate_temp == null) {
            return 0;
        }
        else {
            return rate_temp.getRating();
        }
    }



    // Returns the Rating object
    public Rating getRatingObj(int movieID){
        for (int count = 0 ; count < RatingList.size() ; count++) {
            Rating rate_temp = RatingList.get(count);
            //System.out.println((count + 1) + " :: " + rate_temp.getID());
            if (rate_temp.getID() == movieID) {
                return rate_temp;
            }
        }
        return null;
    }

    // Returns a partial list of the RatingList that matches the other user's RatingList
    public ArrayList<Rating> getRatingListIntersect(User otherUser){
        ArrayList<Rating> newRatingList = new ArrayList<>();

        for (int count = 0 ; count < RatingList.size() ; count++) {
            Rating thisUserRate = RatingList.get(count);
            Rating otherUserRate = otherUser.getRatingObj(thisUserRate.getID());
            if (otherUserRate != null) {
                newRatingList.add(thisUserRate);
            }
        }

        return newRatingList;
    }

    // Returns a list of ratings that the other user have that the current user does not
    public ArrayList<Rating> getMissingRatingList(User otherUser){
        ArrayList<Rating> newRatingList = new ArrayList<>();
        ArrayList<Rating> otherRatingList = otherUser.getRatingList();

        for (int count = 0 ; count < otherRatingList.size() ; count++) {
            Rating otherUserRate = otherRatingList.get(count);
            Rating thisUserRate = getRatingObj(otherUserRate.getID());
            if (thisUserRate != null) {
                newRatingList.add(thisUserRate);
            }
        }
        return newRatingList;
    }

    // Returns a list of 'ratings' (populated by 0) signifying the user has not rated the movie
    public ArrayList<Rating> getMissingRatingList(ArrayList<Movie> movieIndex){
        ArrayList<Rating> newRatingList = new ArrayList<>();

        for (int count = 0 ; count < movieIndex.size() ; count++) {
            Rating userRate = getRatingObj(count + 1);
            if (userRate == null) {
                Rating temp_rate = new Rating(count + 1, 0);
                newRatingList.add(temp_rate);
            }
        }
        return newRatingList;
    }

    public ArrayList<Rating> getRatingList()
    {
        return RatingList;
    }

    public void setRatingList(ArrayList RatingList) {
        RatingList = new ArrayList<>();
        RatingList.addAll(RatingList);
    }
}
