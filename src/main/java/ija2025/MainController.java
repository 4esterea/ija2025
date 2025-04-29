package ija2025;

import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public Text labelMainMenu;

    @FXML
    private Label welcomeText;

    @FXML
    private Button playButton;

    @FXML
    private BorderPane mainWindow;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonTransitions();
        setupButtonActions();
    }

    private void setupButtonActions() {
        playButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("difficulty-view.fxml")));
                Scene scene = playButton.getScene();

                scene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupButtonTransitions() {
        if (playButton != null) {
            Color defaultBgColor = Color.rgb(43, 45, 48);
            Color hoverBgColor = Color.rgb(30, 31, 34);
            Color defaultTextColor = Color.rgb(205, 205, 205);
            Color hoverTextColor = Color.rgb(255, 255, 255);
            Color pressedBgColor = Color.rgb(80, 82, 85);
            Color pressedTextColor = Color.rgb(230, 230, 230);
            Color pressedBorderColor = Color.rgb(100, 103, 105);

            playButton.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                               "-fx-text-fill: rgb(205, 205, 205); " +
                               "-fx-border-color: rgb(30, 31, 34); " +
                               "-fx-border-width: 1px; " +
                               "-fx-border-radius: 5px; " +
                               "-fx-background-radius: 5px;");

            playButton.setOnMouseEntered(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), playButton);
                scaleTransition.setToX(1.05);
                scaleTransition.setToY(1.05);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                playButton.setStyle("-fx-background-color: rgb(30, 31, 34); " +
                                   "-fx-text-fill: rgb(255, 255, 255); " +
                                   "-fx-border-color: rgb(60, 63, 65); " +
                                   "-fx-border-width: 1px; " +
                                   "-fx-border-radius: 5px; " +
                                   "-fx-background-radius: 5px;");

                scaleTransition.play();
            });

            playButton.setOnMousePressed(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), playButton);
                scaleTransition.setToX(0.95);
                scaleTransition.setToY(0.95);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                playButton.setStyle("-fx-background-color: rgb(80, 82, 85); " + // Lighter background
                                   "-fx-text-fill: rgb(230, 230, 230); " + // Lighter text
                                   "-fx-border-color: rgb(100, 103, 105); " + // Lighter border
                                   "-fx-border-width: 1px; " +
                                   "-fx-border-radius: 5px; " +
                                   "-fx-background-radius: 5px;");

                scaleTransition.play();
            });

            playButton.setOnMouseReleased(e -> {
                if (playButton.isHover()) {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), playButton);
                    scaleTransition.setToX(1.05);
                    scaleTransition.setToY(1.05);
                    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                    playButton.setStyle("-fx-background-color: rgb(30, 31, 34); " +
                                       "-fx-text-fill: rgb(255, 255, 255); " +
                                       "-fx-border-color: rgb(60, 63, 65); " +
                                       "-fx-border-width: 1px; " +
                                       "-fx-border-radius: 5px; " +
                                       "-fx-background-radius: 5px;");

                    scaleTransition.play();
                } else {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), playButton);
                    scaleTransition.setToX(1.0);
                    scaleTransition.setToY(1.0);
                    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                    playButton.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                                       "-fx-text-fill: rgb(205, 205, 205); " +
                                       "-fx-border-color: rgb(30, 31, 34); " +
                                       "-fx-border-width: 1px; " +
                                       "-fx-border-radius: 5px; " +
                                       "-fx-background-radius: 5px;");

                    scaleTransition.play();
                }
            });

            playButton.setOnMouseExited(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), playButton);
                scaleTransition.setToX(1.0);
                scaleTransition.setToY(1.0);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                playButton.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                                   "-fx-text-fill: rgb(205, 205, 205); " +
                                   "-fx-border-color: rgb(30, 31, 34); " +
                                   "-fx-border-width: 1px; " +
                                   "-fx-border-radius: 5px; " +
                                   "-fx-background-radius: 5px;");

                scaleTransition.play();
            });
        }

        if (labelMainMenu != null) {
            Color defaultTextColor = Color.rgb(205, 205, 205);
            Color hoverTextColor = Color.WHITE;
            Color pressedTextColor = Color.rgb(240, 240, 240); // Lighter color for pressed state

            labelMainMenu.setOnMouseEntered(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(300), labelMainMenu);
                fillTransition.setToValue(hoverTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                Glow glow = new Glow();
                labelMainMenu.setEffect(glow);

                fillTransition.play();
            });

            labelMainMenu.setOnMouseExited(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(300), labelMainMenu);
                fillTransition.setToValue(defaultTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                labelMainMenu.setEffect(null);

                fillTransition.play();
            });

            labelMainMenu.setOnMousePressed(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(150), labelMainMenu);
                fillTransition.setToValue(pressedTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                Glow glow = new Glow();
                glow.setLevel(1);
                labelMainMenu.setEffect(glow);

                fillTransition.play();
            });

            labelMainMenu.setOnMouseReleased(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(150), labelMainMenu);

                if (labelMainMenu.isHover()) {
                    fillTransition.setToValue(hoverTextColor);


                    Glow glow = new Glow();
                    glow.setLevel(0.5);
                    labelMainMenu.setEffect(glow);
                } else {
                    fillTransition.setToValue(defaultTextColor);

                    labelMainMenu.setEffect(null);
                }

                fillTransition.setInterpolator(Interpolator.EASE_BOTH);
                fillTransition.play();
            });
        }
    }

}
