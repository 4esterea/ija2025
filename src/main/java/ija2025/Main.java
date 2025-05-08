/*
 * Main.java
 *
 * Authors: Zhdanovich Iaroslav (xzhdan00)
 *          Malytskyi Denys     (xmalytd00)
 *
 * Description: Main application class that initializes the JavaFX interface
 * and configures the basic parameters of the application window for the
 * "lightbulb" project.
 */


package ija2025;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/main-view.fxml")));
        Scene scene = new Scene(root, 550, 650);
        stage.setScene(scene);
        stage.setTitle("lightbulb");
        Image icon = new Image(getClass().getResourceAsStream("/media/bulb_icon.png"));
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.show();
    }
}
