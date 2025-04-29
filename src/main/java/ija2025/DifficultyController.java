package ija2025;

import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class DifficultyController implements Initializable {
    @FXML
    private Text labelDifficultyMenu;

    @FXML
    private Button easyButton;

    @FXML
    private Button mediumButton;

    @FXML
    private Button hardButton;

    @FXML
    private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonTransitions();
        setupButtonActions();
    }

    private void setupButtonActions() {
        backButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-view.fxml")));
                Scene scene = backButton.getScene();

                scene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        easyButton.setOnAction(event -> {
            System.out.println("Easy difficulty selected");
            loadGameView("Easy");
        });

        mediumButton.setOnAction(event -> {
            System.out.println("Medium difficulty selected");
            loadGameView("Medium");
        });

        hardButton.setOnAction(event -> {
            System.out.println("Hard difficulty selected");
            loadGameView("Hard");
        });
    }

    private void setupButtonTransitions() {
        setupButtonTransition(easyButton);
        setupButtonTransition(mediumButton);
        setupButtonTransition(hardButton);
        setupButtonTransition(backButton);
        setupTextTransition(labelDifficultyMenu);
    }

    private void loadGameView(String difficulty) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("game-view.fxml")));
            Scene scene = easyButton.getScene();

            scene.setRoot(root);

            System.out.println("Starting game with " + difficulty + " difficulty");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupButtonTransition(Button button) {
        if (button != null) {
            Color defaultBgColor = Color.rgb(43, 45, 48);
            Color hoverBgColor = Color.rgb(30, 31, 34);
            Color defaultTextColor = Color.rgb(205, 205, 205);
            Color hoverTextColor = Color.rgb(255, 255, 255);
            Color pressedBgColor = Color.rgb(80, 82, 85);
            Color pressedTextColor = Color.rgb(230, 230, 230);
            Color pressedBorderColor = Color.rgb(100, 103, 105);

            button.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                           "-fx-text-fill: rgb(205, 205, 205); " +
                           "-fx-border-color: rgb(30, 31, 34); " +
                           "-fx-border-width: 1px; " +
                           "-fx-border-radius: 5px; " +
                           "-fx-background-radius: 5px;");

            button.setOnMouseEntered(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), button);
                scaleTransition.setToX(1.05);
                scaleTransition.setToY(1.05);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                button.setStyle("-fx-background-color: rgb(30, 31, 34); " +
                               "-fx-text-fill: rgb(255, 255, 255); " +
                               "-fx-border-color: rgb(60, 63, 65); " +
                               "-fx-border-width: 1px; " +
                               "-fx-border-radius: 5px; " +
                               "-fx-background-radius: 5px;");

                scaleTransition.play();
            });

            button.setOnMousePressed(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
                scaleTransition.setToX(0.95);
                scaleTransition.setToY(0.95);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                button.setStyle("-fx-background-color: rgb(80, 82, 85); " +
                               "-fx-text-fill: rgb(230, 230, 230); " +
                               "-fx-border-color: rgb(100, 103, 105); " +
                               "-fx-border-width: 1px; " +
                               "-fx-border-radius: 5px; " +
                               "-fx-background-radius: 5px;");

                scaleTransition.play();
            });

            button.setOnMouseReleased(e -> {
                if (button.isHover()) {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
                    scaleTransition.setToX(1.05);
                    scaleTransition.setToY(1.05);
                    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                    button.setStyle("-fx-background-color: rgb(30, 31, 34); " +
                                   "-fx-text-fill: rgb(255, 255, 255); " +
                                   "-fx-border-color: rgb(60, 63, 65); " +
                                   "-fx-border-width: 1px; " +
                                   "-fx-border-radius: 5px; " +
                                   "-fx-background-radius: 5px;");

                    scaleTransition.play();
                } else {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
                    scaleTransition.setToX(1.0);
                    scaleTransition.setToY(1.0);
                    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                    button.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                                   "-fx-text-fill: rgb(205, 205, 205); " +
                                   "-fx-border-color: rgb(30, 31, 34); " +
                                   "-fx-border-width: 1px; " +
                                   "-fx-border-radius: 5px; " +
                                   "-fx-background-radius: 5px;");

                    scaleTransition.play();
                }
            });

            button.setOnMouseExited(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), button);
                scaleTransition.setToX(1.0);
                scaleTransition.setToY(1.0);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                button.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                               "-fx-text-fill: rgb(205, 205, 205); " +
                               "-fx-border-color: rgb(30, 31, 34); " +
                               "-fx-border-width: 1px; " +
                               "-fx-border-radius: 5px; " +
                               "-fx-background-radius: 5px;");

                scaleTransition.play();
            });
        }
    }

    private void setupTextTransition(Text text) {
        if (text != null) {
            Color defaultTextColor = Color.rgb(205, 205, 205);
            Color hoverTextColor = Color.WHITE;
            Color pressedTextColor = Color.rgb(240, 240, 240);

            text.setOnMouseEntered(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(300), text);
                fillTransition.setToValue(hoverTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                Glow glow = new Glow();
                glow.setLevel(0.5);
                text.setEffect(glow);

                fillTransition.play();
            });

            text.setOnMouseExited(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(300), text);
                fillTransition.setToValue(defaultTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                text.setEffect(null);

                fillTransition.play();
            });

            text.setOnMousePressed(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(150), text);
                fillTransition.setToValue(pressedTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                Glow glow = new Glow();
                glow.setLevel(1);
                text.setEffect(glow);

                fillTransition.play();
            });

            text.setOnMouseReleased(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(150), text);

                if (text.isHover()) {
                    fillTransition.setToValue(hoverTextColor);

                    Glow glow = new Glow();
                    glow.setLevel(0.5);
                    text.setEffect(glow);
                } else {
                    fillTransition.setToValue(defaultTextColor);

                    text.setEffect(null);
                }

                fillTransition.setInterpolator(Interpolator.EASE_BOTH);
                fillTransition.play();
            });
        }
    }
}
