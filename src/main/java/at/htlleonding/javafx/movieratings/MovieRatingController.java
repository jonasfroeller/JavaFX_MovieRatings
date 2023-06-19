package at.htlleonding.javafx.movieratings;

import at.htlleonding.javafx.movieratings.model.MovieRating;
import at.htlleonding.javafx.movieratings.model.MovieRepository;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.function.Predicate;

public class MovieRatingController {

    @FXML
    private Slider sldYearFilter;

    @FXML
    private TableColumn<MovieRating, Double> tbcRating;

    @FXML
    private TableColumn<MovieRating, String> tbcTitle;

    @FXML
    private TableColumn<MovieRating, Integer> tbcYear;

    @FXML
    private TableView<MovieRating> tbvMovieRatings;

    @FXML
    private Label txtAverageRating;

    @FXML
    private Label txtMovieCount;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtRating;

    @FXML
    private TextField txtYear;

    private FilteredList<MovieRating> movieRatingsFiltered;

    @FXML
    void initialize() {
        // table view config
        this.tbcTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        this.tbcYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        this.tbcRating.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // init bind list, filter list
        movieRatingsFiltered = new FilteredList<>(MovieRepository.getInstance().getMovieRatings());
        SortedList<MovieRating> movieRatingsSorted = new SortedList<>(movieRatingsFiltered);
        this.tbvMovieRatings.setItems(movieRatingsSorted);

        movieRatingsSorted.comparatorProperty().bind(this.tbvMovieRatings.comparatorProperty());

        // bind statistics
        this.txtMovieCount.textProperty().bind(Bindings.format("Movies: %d", MovieRepository.getInstance().getMovieCount()));
        this.txtAverageRating.textProperty().bind(Bindings.format("Average Rating: %.2f", MovieRepository.getInstance().getAverageRating()));

        // init slider, on change of slider filter value, filter
        this.sldYearFilter.minProperty().bind(MovieRepository.getInstance().getMinYear());
        this.sldYearFilter.maxProperty().bind(MovieRepository.getInstance().getMaxYear());
        this.sldYearFilter.valueProperty().addListener((observableValue, number, t1) -> this.refreshFilters());
        MovieRepository.getInstance().getMovieRatings().addListener((ListChangeListener<MovieRating>) change -> refreshFilters());
    }

    @FXML
    void addRating(ActionEvent event) {
        try {
            String title = txtName.getText();
            int year = Integer.parseInt(txtYear.getText());
            double rating = Double.parseDouble(txtRating.getText());

            if (MovieRepository.getInstance().addMovieRating(title, year, rating)) {
                txtName.setText("");
                txtYear.setText("");
                txtRating.setText("");
            }
        } catch (Exception ex) {
            error(ex);
        }
    }

    @FXML
    void removeSelected(ActionEvent event) {
        MovieRating selectedMovieRating = tbvMovieRatings.getSelectionModel().getSelectedItem();

        if (selectedMovieRating == null) {
            info("No movie rating selected!");
            return;
        }

        try {
            if (MovieRepository.getInstance().removeMovieRating(selectedMovieRating)) {
                tbvMovieRatings.getSelectionModel().clearSelection();
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private void error(Exception exception) {
        error(exception.getClass().getSimpleName() + ": " + exception.getMessage());
    }

    private void info(String message) { // Info Modal
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Info:");
        info.setContentText(message);
        info.show();
    }

    private void error(String message) { // Alert Modal
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error!");
        alert.setContentText(message);
        alert.show();
    }

    private void refreshFilters() {
        // This is needed to avoid concurrency issues:
        Platform.runLater(() -> {
            Predicate<? super MovieRating> filterPredicate = rating -> rating.getYear() >= sldYearFilter.getValue();
            this.movieRatingsFiltered.setPredicate(filterPredicate);
        });
    }
}
