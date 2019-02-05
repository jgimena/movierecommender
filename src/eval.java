import java.util.*;
import java.io.*;
import java.lang.*;

public class eval {

    static final int MIN_RATE_RECOM = 5;
    static final double MIN_SIM_USER_RATING = 0.00;
    static final double MIN_RATE_RECOM_VAL = 3.0;

    // Print out movie list
    public static void movieListPrint(ArrayList<Movie> movieIndex) {
        for (int count = 0 ; count < movieIndex.size() ; count++) {
            Movie movie = movieIndex.get(count);
            System.out.println(movie.toString());
        }
    }

    // Print out user list
    public static void userListPrint(ArrayList<User> userIndex) {
        for (int count = 0 ; count < userIndex.size() ; count++) {
            User user = userIndex.get(count);
            System.out.println(user.toString());
        }
    }

    // return Movie from movie list
    public static Movie getMovie(int num, ArrayList<Movie> movieIndex) {
        if (num >= 1 && num <= movieIndex.size()) {
            Movie movie = movieIndex.get(num - 1);
            return movie;
        }
        return null;
    }

    // Print out user rating list with movie names
    public static void printUserRatings(User user, ArrayList<Movie> movieIndex){
        ArrayList<Rating> RatingList = user.getRatingList();
        if (RatingList.size() == 0) {
            System.out.println("No ratings added!");
        }
        else {
            for (int count = 0 ; count < RatingList.size() ; count++) {
                Rating rate_temp = RatingList.get(count);
                Movie movie_temp = movieIndex.get(rate_temp.getID() - 1);
                System.out.println("You rated " + movie_temp.getTitle() + " a score of: " + rate_temp.getRating());
            }
        }
    }

    // Return average of ratings in a list
    public static double returnAverage(ArrayList<Rating> ratingList) {
        double avg = 0.00;
        for (int count = 0 ; count < ratingList.size() ; count++) {
            Rating rate_temp = ratingList.get(count);
            avg += rate_temp.getRating();
        }
        avg = avg / ratingList.size();
        return avg;
    }

    // Calculate numerator of similarity value
    public static double simNum(ArrayList<Rating> thisUserRatingList, double thisUserAvg, ArrayList<Rating> otherUserRatingList, double otherUserAvg) {
        double simNum = 0.00;
        for (int count = 0 ; count < thisUserRatingList.size() ; count++) {
            Rating thisUserRate = thisUserRatingList.get(count);
            Rating otherUserRate = otherUserRatingList.get(count);
            double first = thisUserRate.getRating() - thisUserAvg;
            double second = otherUserRate.getRating() - otherUserAvg;
            double comb = first * second;
            simNum += comb;
        }
        return simNum;
    }

    // Calculate denominator of similarity value
    public static double simDen(ArrayList<Rating> thisUserRatingList, double thisUserAvg, ArrayList<Rating> otherUserRatingList, double otherUserAvg) {
        double simDen = 0.00;
        double simDenFirst = 0.00;
        double simDenSecond = 0.00;

        // Calculate first
        for (int count = 0 ; count < thisUserRatingList.size() ; count++) {
            Rating thisUserRate = thisUserRatingList.get(count);
            double first = thisUserRate.getRating() - thisUserAvg;
            first = Math.pow(first, 2.00);
            simDenFirst += first;
        }

        // Calculate second
        for (int count = 0 ; count < thisUserRatingList.size() ; count++) {
            Rating otherUserRate = otherUserRatingList.get(count);
            double second = otherUserRate.getRating() - otherUserAvg;
            second = Math.pow(second, 2.00);
            simDenSecond += second;
        }

        // Multiply First and Second, then find square root
        simDen = Math.sqrt(simDenFirst * simDenSecond);

        return simDen;
    }

    // Print out movie recommendations
    public static void getRecommendations(User user1, ArrayList<User> userIndex, ArrayList<Movie> movieIndex){
        ArrayList<Rating> user_ratings = user1.getRatingList();
        ArrayList<Similarity> sim_scores = new ArrayList<>();

        // Only recommend movies when user has rated at least MIN_RATE_RECOM number of movies
        if (user_ratings.size() < MIN_RATE_RECOM) {
            System.out.println("Please rate more movies!");
        }
        else {
            // MAJOR CALCULATIONS AHEAD
            // We have to assign a similarity value to all other users
            for (int count = 0 ; count < userIndex.size() ; count++) {
                User user2 = userIndex.get(count);
                if (user1.getName().equalsIgnoreCase(user2.getName().toLowerCase()))
                {
                    //System.out.println("This is the same user (" + user1.getName() + ")!");
                }
                else {
                    //System.out.println("Comparing " + user1.getName() + "'s ratings with " + user2.getName() + "'s...");
                    // Create two movie lists matching all movies the two users have rated
                    ArrayList<Rating> user_rate_list_int1 = user1.getRatingListIntersect(user2);
                    ArrayList<Rating> user_rate_list_int2 = user2.getRatingListIntersect(user1);

                    // DEBUG: Print out matching ratings
                    /*for (int x1 = 0 ; x1 < user_rate_list_int1.size() ; x1++) {
                        Rating rate_temp1 = user_rate_list_int1.get(x1);
                        Rating rate_temp2 = user_rate_list_int2.get(x1);
                        Movie mov_temp = movieIndex.get(rate_temp1.getID());
                        System.out.println(user.getName() + " rated " + mov_temp.getTitle() + ": " + rate_temp1.getRating());
                        System.out.println(user_temp.getName() + " rated " + mov_temp.getTitle() + ": " + rate_temp2.getRating());
                    }*/

                    // Find the average of all the scores in the intersected lists
                    double user1_rate_avg = returnAverage(user_rate_list_int1);
                    double user2_rate_avg = returnAverage(user_rate_list_int2);
                    //System.out.println(user1.getName() + " AVG: " + user1_rate_avg);
                    //System.out.println(user2.getName() + " AVG: " + user2_rate_avg);

                    // Find the numerator of the similarity value
                    double simNum = simNum(user_rate_list_int1, user1_rate_avg, user_rate_list_int2, user2_rate_avg);
                    //System.out.println("sim(" + user1.getName() + ", " + user2.getName() + "): " + simNum);

                    // If the numerator is below MIN_SIM_USER_RATING, the two users are not similar. Only continue computation otherwise.
                    // For sim_scores, we are adding this user.
                    if (simNum > MIN_SIM_USER_RATING) {
                        //System.out.println("You and " + user2.getName() + " (ID: " + user2.getID() + ")  have similar ratings on movies! They may recommend something you haven't tried!");

                        double simDen = simDen(user_rate_list_int1, user1_rate_avg, user_rate_list_int2, user2_rate_avg);
                        double sim = simNum / simDen;

                        Similarity sim_temp = new Similarity(user2.getID(), sim);
                        sim_scores.add(sim_temp);

                        // ANOTHER TOUGH CALCULATION, HOLY CRAP
                        // Use this if the sim_score calculation fails.
                        // Get list of ratings the other users have that the other users have not rated on
                        /*ArrayList<Rating> recom = user1.getMissingRatingList(user2);
                        for (int count2 = 0 ; count2 < recom.size() ; count2++) {
                            Rating recom_rating = recom.get(count2);
                            Movie recom_mov = movieIndex.get(recom_rating.getID());
                            // DEBUG: Just recommend movies rated by this user that has more than 3 rating
                            if (recom_rating.getRating() >= 3) {
                                System.out.println(user2.getName() + " recommends: " + recom_mov.getTitle());
                            }
                        }*/
                    }
                }
            }


            // Get the list of movies that user1 has not tried yet
            ArrayList<Rating> missing_ratings = user1.getMissingRatingList(movieIndex);

            // Print out missing_ratings
            /*for (int count = 0 ; count < missing_ratings.size() ; count++) {
                Rating rate_temp = missing_ratings.get(count);
                Movie mov_temp = movieIndex.get(rate_temp.getID());
                System.out.println("You don't have a rating for: " + mov_temp.getTitle());
            }*/

            for (int count = 0 ; count < missing_ratings.size() ; count++) {
                Rating rate_temp = missing_ratings.get(count);
                Movie mov_temp = movieIndex.get(rate_temp.getID() - 1);

                double exp_rating;
                double exp_rating_1;
                double exp_rating_2 = 0.00;
                double sim_score_sum = 0.00;

                // Calculate exp_rating_1: 1 divided by sum of APPLICABLE sim_score (ignore users that have no rating on the movie)
                // Calculate exp_rating_2: sum of product of sim_score and the user's rating on movie
                for (int count1 = 0 ; count1 < sim_scores.size() ; count1++) {
                    Similarity sim_temp = sim_scores.get(count1);
                    User user_temp = userIndex.get(sim_temp.getID() - 1);
                    Rating user_temp_rate = user_temp.getRatingObj(mov_temp.getID());
                    if (user_temp_rate != null) {
                        sim_score_sum += sim_temp.getScore();
                        exp_rating_2 += sim_temp.getScore() * user_temp_rate.getRating();
                    }
                }

                exp_rating_1 = 1.00 / sim_score_sum;
                exp_rating = exp_rating_1 * exp_rating_2;

                // Recommend movie if exp_rating is higher than MIN_RATE_RECOM_VAL
                if (exp_rating > MIN_RATE_RECOM_VAL) {
                    System.out.print("We recommend: " + mov_temp.getTitle() + " (expected rating: ");
                    System.out.printf("%1.0f", exp_rating);
                    System.out.println(")");
                }
            }
        }

        System.out.println("");
    }

    // Needed to check if String is numeric
    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static boolean userExists(String name, ArrayList<User> userIndex){
        for (int count = 0 ; count < userIndex.size() ; count++) {
            User user = userIndex.get(count);
            if (name.equalsIgnoreCase(user.getName())) {
                return true;
            }
        }
        return false;
    }

    public static User getUser(String name, ArrayList<User> userIndex) {
        for (int count = 0 ; count < userIndex.size() ; count++) {
            User user = userIndex.get(count);
            if (name.equalsIgnoreCase(user.getName())) {
                return user;
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {

        ArrayList<Movie> movieIndex = new ArrayList();
        ArrayList<User> userIndex = new ArrayList();

        File movieFile = new File ("movie-data.txt");
        File userFile = new File ("user-data.txt");

        if (!(movieFile.exists())) {
            System.out.println("Missing file: movie-data.txt");
            System.exit(1);
        }
        else if (!(userFile.exists())) {
            System.out.println("Missing file: user-data.txt");
            System.exit(1);
        }
        else {
            // Main code
            System.out.println("For more information, consult 'report.doc'.");
            System.out.println();

            FileInputStream inM = new FileInputStream(movieFile);
            FileInputStream inU = new FileInputStream(userFile);

            try {

                ArrayList<String> movielist = new ArrayList<String>();

                BufferedReader readM = new BufferedReader(new InputStreamReader(inM));
                BufferedReader readU = new BufferedReader(new InputStreamReader(inU));

                String line;
                StringTokenizer tokenizer;

                int indexNumber = 0;

                boolean isName = false;
                boolean isRating = false;

                // Read movie-data.txt and populate movieIndex
                while ((line = readM.readLine()) != null) {
                    // Consider end of line
                    if (!line.isEmpty()) {
                        String[] split = line.split("\t", 2);

                        // Create temporary Movie object
                        int movienum = Integer.parseInt(split[0]);
                        Movie temp = new Movie(movienum, split[1]);

                        movieIndex.add(temp);
                    }
                }

                // Read movie-data.txt and populate userIndex
                while ((line = readU.readLine()) != null) {
                    String[] split = line.split("\t", 2);

                    if (isRating && isNumeric(split[0])) {
                        //System.out.println("Current indexNumber: " + indexNumber);
                        //System.out.println("WORKS! " + line);
                        int movieID = Integer.parseInt(split[0]);
                        int rating = Integer.parseInt(split[1]);
                        Rating movie_rating = new Rating(movieID, rating);
                        User user = userIndex.get(indexNumber - 1);
                        user.addRating(movie_rating);
                    }

                    // Add Name to User
                    if (isName) {
                        User user = userIndex.get(indexNumber - 1);
                        user.setName(line);
                        isName = false;
                    }

                    tokenizer = new StringTokenizer(line);
                    String word;
                    while (tokenizer.hasMoreTokens()) {
                        word = tokenizer.nextToken();

                        // Keyword
                        if (word.charAt(0) == '.' && word.length() > 1) {
                            switch (word.charAt(1)) {
                                case 'I':
                                    // The next word is the user ID
                                    word = tokenizer.nextToken();
                                    indexNumber = Integer.parseInt(word);
                                    User newUser = new User(indexNumber);
                                    userIndex.add(newUser);
                                    isName = false;
                                    isRating = false;
                                    break;
                                case 'N':
                                    // The next line is the name
                                    isName = true;
                                    isRating = false;
                                    break;
                                case 'R':
                                    // The next lines are ratings
                                    isRating = true;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }

                // Test to see if changing ratings work
                /*
                User test = (User)userIndex.get(1);
                Rating rTest = new Rating(2, 5);
                test.addRating(rTest);
                System.out.println(test.getMovieScore(2));
                */


                /*User test = (User)userIndex.get(1);
                int x = test.getMovieScore(3);
                System.out.println(x);*/

                // NOW THAT MOVIELIST AND USERLIST IS POPULATED, DO SOME USER INPUT OR WHATEVER

                boolean exit = false;
                String console_input = "";
                Scanner sc = new Scanner(System.in);

                System.out.println("Welcome to the Movie Recommender!");
                System.out.println("");

                while (!exit) {

                    boolean repeatQuestionMain = true;


                    String name = "";
                    User currentUser = null;

                    while (repeatQuestionMain) {
                        if (currentUser == null) {
                            System.out.println("Current User: null");
                        }
                        else {
                            System.out.println("Current User: " + currentUser.getName());
                        }
                        System.out.println("MAIN MENU:");
                        System.out.println("");
                        if (currentUser == null) {
                            System.out.println("1. Login/Create New User");
                        }
                        if (currentUser != null) {
                            System.out.println("2. Logout User");
                            System.out.println("3. Rate Movies");
                            System.out.println("4. Get Your Ratings");
                            System.out.println("5. Get Movie Recommendations");
                        }
                        System.out.println("8. Print Out Movie List");
                        System.out.println("9. Print Out User List");
                        System.out.println("0. Exit");
                        System.out.println("");
                        System.out.print("Select a number: ");
                        console_input = sc.nextLine();
                        System.out.println("");

                        switch (console_input.charAt(0)) {
                            case '1':
                                if (currentUser != null) {
                                    System.out.println("Incorrect Entry!");
                                    System.out.println("");
                                    break;
                                }
                                // Create or log in user
                                boolean repeatQuestion1 = true;
                                while (repeatQuestion1) {

                                    System.out.print("Enter a name: ");
                                    console_input = sc.nextLine();
                                    name = console_input;
                                    if (userExists(console_input, userIndex)) {
                                        boolean repeatQuestion11 = true;
                                        while (repeatQuestion11) {
                                            System.out.println("User (" + name + ") exists! Are you logging in?");
                                            System.out.println("1. Yes");
                                            System.out.println("2. No");
                                            console_input = sc.nextLine();

                                            switch (console_input.charAt(0)) {
                                                case '1':
                                                    currentUser = getUser(name, userIndex);
                                                    repeatQuestion11 = false;
                                                    System.out.println("User logged in! Welcome, " + name);
                                                    break;
                                                case '2':
                                                    repeatQuestion11 = false;
                                                    break;
                                                default:
                                                    System.out.println("Not a valid option!");
                                                    break;
                                            }
                                        }
                                        repeatQuestion1 = false;
                                    }
                                    else {
                                        if (name.length() < 1) {
                                            System.out.println("Incorrect Entry!");
                                            System.out.println("");
                                            break;
                                        }
                                        boolean repeatQuestion12 = true;
                                        while (repeatQuestion12) {
                                            System.out.println("User (" + name + ") does not exist! Are you creating a new user?");
                                            System.out.println("1. Yes");
                                            System.out.println("2. No");
                                            console_input = sc.nextLine();

                                            switch (console_input.charAt(0)) {
                                                case '1':
                                                    User user = new User(userIndex.size() + 1);
                                                    user.setName(name);
                                                    userIndex.add(user);
                                                    currentUser = user;
                                                    repeatQuestion12 = false;
                                                    System.out.println("User created! Welcome, " + name);
                                                    break;
                                                case '2':
                                                    System.out.println("User not created!");
                                                    repeatQuestion12 = false;
                                                    break;
                                                default:
                                                    System.out.println("Not a valid option!");
                                                    break;
                                            }
                                            repeatQuestion1 = false;
                                        }
                                    }
                                    System.out.println("");
                                }
                                break;
                            case '2':
                                if (currentUser == null) {
                                    System.out.println("Incorrect Entry!");
                                    System.out.println("");
                                    break;
                                }
                                // Log out user
                                currentUser = null;
                                break;
                            case '3':
                                if (currentUser == null) {
                                    System.out.println("Incorrect Entry!");
                                    System.out.println("");
                                    break;
                                }
                                // Rate movies
                                boolean repeatQuestion3 = true;
                                Scanner sc_int = new Scanner(System.in);
                                String mov_ent = "";
                                String rate_ent = "";
                                Movie currentMovie;
                                while (repeatQuestion3) {
                                    System.out.println("Select a number, or 0 to exit:");
                                    mov_ent = sc_int.nextLine();
                                    if (isNumeric(mov_ent)) {
                                        int mov_num = Integer.parseInt(mov_ent);
                                        if (mov_num == 0){
                                            break;
                                        }
                                        currentMovie = getMovie(mov_num, movieIndex);
                                        if (currentMovie != null) {
                                            System.out.println("How would you rate '" + currentMovie.getTitle() + "'?");
                                            System.out.println("Enter a rating (1-5)");
                                            rate_ent = sc_int.nextLine();
                                            if (isNumeric(rate_ent)) {
                                                int rate_num = Integer.parseInt(rate_ent);
                                                if (rate_num >=1 && rate_num <=5){
                                                    // Add movie rating to user list
                                                    Rating newRating = new Rating(mov_num, rate_num);
                                                    currentUser.addRating(newRating);
                                                    System.out.println("Rating added!");
                                                }
                                                else {
                                                    System.out.println("Invalid rating!");
                                                }
                                            }
                                        }
                                        else {
                                            System.out.println("Not in the list!");
                                        }
                                    }
                                    else {
                                        System.out.println("Invalid input!");
                                    }
                                }
                                break;
                            case '4':
                                if (currentUser == null) {
                                    System.out.println("Incorrect Entry!");
                                    System.out.println("");
                                    break;
                                }
                                // Print out user rating list
                                System.out.println("Your Ratings:");
                                System.out.println("");
                                printUserRatings(currentUser, movieIndex);
                                System.out.println("");
                                break;
                            case '5':
                                if (currentUser == null) {
                                    System.out.println("Incorrect Entry!");
                                    System.out.println("");
                                    break;
                                }
                                // Provide movie recommendations based on current user and users in the index
                                getRecommendations(currentUser, userIndex, movieIndex);
                                System.out.println("");
                                break;
                            case '8':
                                // Print out movie list
                                System.out.println("Movie List:");
                                System.out.println();
                                movieListPrint(movieIndex);
                                break;
                            case '9':
                                // Print out user list
                                System.out.println("User List:");
                                System.out.println();
                                userListPrint(userIndex);
                                break;
                            case '0':
                                exit = true;
                                break;
                            default:
                                System.out.println("Incorrect Entry!");
                                System.out.println("");
                        }
                        if (exit) {
                            break;
                        }
                        System.out.println("Press ENTER to continue:");
                        sc.nextLine();
                        System.out.println("");
                        System.out.println("------------------------------");
                        System.out.println("");
                    }
                }

                System.out.println("Thank you for running this program!");
                System.out.println("Author: Jonathan Gimena");
            }
            finally {
                inM.close();
                inU.close();
                System.exit(0);
            }



        }
    }
}
