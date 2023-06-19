package at.htlleonding.javafx.movieratings.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class MovieRepository {
    private static final MovieRepository instance = new MovieRepository();
    private final ObservableList<MovieRating> movieRatings = FXCollections.observableList(new LinkedList<>());
    private final Connection connection;
    private final SimpleIntegerProperty movieCount = new SimpleIntegerProperty(0);
    private final SimpleDoubleProperty averageRating = new SimpleDoubleProperty(-1);
    private final SimpleIntegerProperty minYear = new SimpleIntegerProperty(1888);
    private final SimpleIntegerProperty maxYear = new SimpleIntegerProperty(LocalDate.now().getYear());

    private MovieRepository() {
        // connect to database
        try {
            this.connection = DriverManager.getConnection("jdbc:derby:db");
            this.connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // fetch the rows of the database table and write them into the movieRatings list
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT TITLE, RELEASE_YEAR, RATING FROM MOVIE");
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                movieRatings.add(new MovieRating(rs.getString(1), rs.getInt(2), rs.getDouble(3)));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // listen on object changes
        updateStatistics(movieRatings);
        this.movieRatings.addListener((ListChangeListener<MovieRating>) change -> {
            updateStatistics(new LinkedList<>(change.getList()));
        });
    }

    public boolean addMovieRating(String title, int year, double rating) throws SQLException {
        MovieRating movieRating = new MovieRating(title, year, rating);

        if(this.movieRatings.contains(movieRating)) {
            throw new IllegalArgumentException("Movie rating already exists!");
        }

        // add movie rating to database
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO MOVIE (TITLE, RELEASE_YEAR, RATING) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, movieRating.getTitle());
            preparedStatement.setInt(2, movieRating.getYear());
            preparedStatement.setDouble(3, movieRating.getRating());

            int count = preparedStatement.executeUpdate();

            if (count == 0) {
                throw new RuntimeException("Failed to insert the movie rating into the database!");
            } else {
                System.out.println("Inserted movie rating into the database!");

                // add movie rating to list
                movieRatings.add(movieRating);
                System.out.println("Added movie rating to the list!");
                return true;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public boolean removeMovieRating(MovieRating movieRating) throws SQLException {
        // remove movie rating from database
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM MOVIE WHERE TITLE LIKE ? AND RELEASE_YEAR = ? AND RATING = ?")) {
            preparedStatement.setString(1, movieRating.getTitle());
            preparedStatement.setInt(2, movieRating.getYear());
            preparedStatement.setDouble(3, movieRating.getRating());

            int count = preparedStatement.executeUpdate();

            if (count == 0) {
                throw new RuntimeException("Failed to delete movie rating from database!");
            } else {
                System.out.println("Deleted movie rating in the database!");

                // remove movie rating from list
                movieRatings.remove(movieRating);
                System.out.println("Removed movie rating from the list!");
                return true;
            }
        } catch (SQLException e) { // TODO
            throw new SQLException(e);
        }
    }

    public void updateStatistics(List<MovieRating> list) {
        movieCount.setValue(list.size());
        averageRating.setValue(list.stream().mapToDouble(MovieRating::getRating).average().orElse(0));
        minYear.setValue(list.stream().mapToInt(MovieRating::getYear).min().orElse(1888));
        maxYear.setValue(list.stream().mapToInt(MovieRating::getYear).max().orElse(LocalDate.now().getYear()));
    }

    public ObservableList<MovieRating> getMovieRatings() {
        return FXCollections.unmodifiableObservableList(movieRatings);
    }

    public static MovieRepository getInstance() {
        return instance;
    }

    public SimpleIntegerProperty getMovieCount() {
        return movieCount;
    }

    public SimpleDoubleProperty getAverageRating() {
        return averageRating;
    }

    public SimpleIntegerProperty getMinYear() {
        return minYear;
    }

    public SimpleIntegerProperty getMaxYear() {
        return maxYear;
    }
}
