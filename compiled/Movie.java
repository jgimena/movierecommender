public class Movie {

    int I;      // ID
    String T;   // Title

    public Movie(int ID, String Title){
        setID(ID);
        setTitle(Title);
    }

    public int getID() {
        return I;
    }

    public void setID(int ID) {
        I = ID;
    }

    public String getTitle() {
        return T;
    }

    public void setTitle(String Title) {
        T = Title;
    }

    public String toString(){
        String temp;
        temp = I + ", " + T;
        return temp;
    }
}
