/*
 * SceneTransitionManager.java
 *
 * Authors: Zhdanovich Iaroslav (xzhdan00)
 *          Malytskyi Denys     (xmalytd00)
 *
 * Description: Utility class that handles scene transitions in the JavaFX application
 * with smooth fade effects. Provides a clean API for switching between different
 * FXML-based views while maintaining consistent visual styling and transition animations
 * throughout the "lightbulb" project.
 */


package ija2025;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class SceneTransitionManager {

    private static final String BACKGROUND_COLOR = "rgb(38, 38, 38)";

    public static void switchScene(Node sourceNode, String fxmlPath) {
        try {
            Scene scene = sourceNode.getScene();
            Parent currentRoot = scene.getRoot();

            Parent newRoot = FXMLLoader.load(Objects.requireNonNull(
                    SceneTransitionManager.class.getResource(fxmlPath)));

            StackPane stackPane = new StackPane();
            stackPane.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
            stackPane.getChildren().addAll(currentRoot, newRoot);

            newRoot.setOpacity(0);
            scene.setRoot(stackPane);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setInterpolator(Interpolator.EASE_BOTH);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setInterpolator(Interpolator.EASE_BOTH);

            ParallelTransition transition = new ParallelTransition(fadeOut, fadeIn);
            transition.setOnFinished(e -> {
                ((StackPane) scene.getRoot()).getChildren().clear();
                scene.setRoot(newRoot);
            });

            transition.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}