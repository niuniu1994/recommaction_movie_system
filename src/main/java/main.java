import pojo.Movie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class main {
    public static void main(String[] args) throws IOException {
        FirstRating firstRating = new FirstRating();
        firstRating.testLoadRaters();
    }
}
