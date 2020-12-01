import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import pojo.Movie;
import edu.duke.*;
import pojo.Rating;
import sun.security.pkcs11.wrapper.Functions;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FirstRating {

    public List<Movie> loadMovies(String fileName) throws FileNotFoundException {
        FileResource fileResource = new FileResource(fileName);
        CSVParser parser = fileResource.getCSVParser();
        ArrayList<Movie> list = new ArrayList<>();
        for (CSVRecord record : parser){
            list.add(new Movie(record.get("id"), record.get("title"), record.get("year"),
                    record.get("genre"), record.get("director"), record.get("country"), record.get("poster"), Integer.parseInt(record.get("minutes")) ));
        }
        return list;
    }

    public List<Rater> loadRaters(String fileName) throws IOException {
        FileResource fileResource = new FileResource(fileName);
        CSVParser parser = fileResource.getCSVParser();
        List<Rater> raterList = new ArrayList<>();

        Map<String,List<CSVRecord>> map = parser.getRecords().stream().collect(Collectors.groupingBy(record -> record.get("rater_id")));

        for (String key: map.keySet()){
            Rater rater = new Rater(key);
            List<CSVRecord> recordsList = map.get(key);
            for (CSVRecord record : recordsList){
                rater.addRating(record.get("movie_id"),Double.parseDouble(record.get("rating")));
            }
            raterList.add(rater);
        }
        return raterList;

    }


    public void testLoadMovies() throws FileNotFoundException {

        List<Movie> movies = loadMovies("/Users/kainingxin/IdeaProjects/recommendation_system/src/main/resources/data/ratedmoviesfull.csv");
        System.out.println(movies.size());

        List<Movie> moviesComedy = movies.stream().filter(movie -> "Comedy".equals(movie.getGenres())).collect(Collectors.toList());
        System.out.println(moviesComedy.size());

        //how many movies are greater than 150 minutes
        List<Movie> movies150 = movies.stream().filter(movie -> movie.getMinutes() > 150).collect(Collectors.toList());
        System.out.println(movies150.size());

        //max number of film directed by any director
         Map<String, Integer> directors = Arrays.stream(movies.stream().map(Movie::getDirector).collect(Collectors.joining(",")).split(",")).map(String::trim).collect(
                 ()->{return new HashMap<String, Integer>();},
                 (HashMap<String,Integer> map, String directer) ->{
                   if (!map.containsKey(directer)) {
                       map.put(directer, 1);
                   }else {
                       map.put(directer,map.get(directer) + 1);
                   }
                 },(HashMap<String, Integer> map1, HashMap<String,Integer> map2)->{
                     map2.forEach((key,value) -> map1.merge(key,value, Integer::sum));
                 }
         );

         //the maximum number of movies by any director
        int max = directors.values().stream().mapToInt(v->v).max().orElse(0);
        List<String> maxDirectors = directors.entrySet().stream().filter(a -> a.getValue() == max).map(Map.Entry::getKey).collect(Collectors.toList());
        System.out.println(maxDirectors);

    }

    public void testLoadRaters() throws IOException {
        List<Rater> raterList = loadRaters("/Users/kainingxin/IdeaProjects/recommendation_system/src/main/resources/data/ratings_short.csv");
        raterList.forEach(a -> System.out.println(a.getID() + ":" + a.getItemsRated()));

        //max number of rating by any rater
        List<Rater> list = raterList.stream().collect(Collectors.groupingBy(rater -> rater.getItemsRated().size())).entrySet().stream().max(Map.Entry.comparingByKey()).orElse(null).getValue();
        list.stream().map(Rater::getItemsRated).forEach(System.out::println);

        //find the number of rating a particular movie has
        System.out.println("1798709 is rated by "+findNumberRatingMovie("1798709", raterList)+ " persons");

        System.out.println(differentRatedMovies(raterList) + " different movies are rated by these raters");

    }

    //find the number of ratings a particular movie has
    private int findNumberRatingMovie(String mId, List<Rater> raters){
        return (int) raters.stream().map(Rater::getItemsRated).filter(a -> a.contains(mId)).count();
    }

    // determine how many different movies have been rated by all these
    //raters
    private int differentRatedMovies(List<Rater> raters){
        Set<String> moviesSet = new HashSet<>();
        raters.stream().map(Rater::getItemsRated).forEach(moviesSet::addAll);
        return moviesSet.size();
    }
}
