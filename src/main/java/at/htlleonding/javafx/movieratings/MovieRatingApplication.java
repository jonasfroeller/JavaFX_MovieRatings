package at.htlleonding.javafx.movieratings;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MovieRatingApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MovieRatingApplication.class.getResource("movierating-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 350);
        stage.setTitle("Movie Rating");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}