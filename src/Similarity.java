public class Similarity implements Comparable<Similarity>{

    double S;                   // Score
    int I;                      // ID

    @Override public int compareTo(Similarity similarity) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == similarity) return EQUAL;

        // higher score goes first
        if (this.S > similarity.S) {
            return BEFORE;
        }
        else if (this.S < similarity.S) {
            return AFTER;
        }
        else if (this.S == similarity.S){
            if (this.I < similarity.I) {
                return BEFORE;
            }
            else {
                return AFTER;
            }
        }
        else return 0;
    }

    public Similarity(int ID, double Score) {
        setScore(Score);
        setID(ID);
    }

    public double getScore() { return S; }

    public void setScore(double Score) { S = Score; }

    public int getID() { return I; }

    public void setID(int ID) { I = ID; }

    public String toString() {
        String temp;
        temp = I + ", " + S;
        return temp;
    }

}