module at.htlleonding.javafx.movieratings {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;


    opens at.htlleonding.javafx.movieratings to javafx.fxml;
    opens at.htlleonding.javafx.movieratings.model to javafx.base;
    exports at.htlleonding.javafx.movieratings;
}