public class Rating implements Comparable<Rating>{

    int I;      // Movie ID
    int R;      // Rating

    public Rating(int ID, int Rating) {
        I = ID;
        R = Rating;
    }

    // compareTo is mainly used to return higher ratings better
    @Override public int compareTo(Rating rating) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == rating) return EQUAL;

        // higher rating goes first
        if (this.R > rating.getRating()) {
            return BEFORE;
        }
        else if (this.R < rating.getRating()) {
            return AFTER;
        }
        else if (this.R > rating.getRating()){
            if (this.I < rating.getID()) {
                return BEFORE;
            }
            else {
                return AFTER;
            }
        }
        else return 0;
    }

    public int getID() {
        return I;
    }

    public void setID(int ID) {
        I = ID;
    }

    public int getRating() {
        return R;
    }

    public void setRating(int Rating) {
        R = Rating;
    }

    public String toString() {
        String temp;
        temp = "[" + I + ", " + R + "]";
        return temp;
    }

}
