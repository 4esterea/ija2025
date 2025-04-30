package ija2025;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML
    private Text timerText;

    @FXML
    private Button pauseButton;

    @FXML
    private Button stepBackButton;

    @FXML
    private Button stepForwardButton;

    @FXML
    private Pane gameField;

    @FXML
    private BorderPane gameWindow;

    private Timeline timeline;
    private int seconds = 0;
    private int minutes = 0;
    private Stage pausePopup;
    private boolean isPaused = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonTransitions();
        setupButtonActions();
        setupGameField();
        startTimer();
    }

    private void setupButtonActions() {
        pauseButton.setOnAction(event -> {
            if (isPaused) {
                resumeGame();
            } else {
                pauseGame();
            }
        });

        stepForwardButton.setOnAction(event -> {
            stepForward();
        });

        stepBackButton.setOnAction(event -> {
            stepBack();
        });
    }

    private void setupButtonTransitions() {
        if (pauseButton != null) {
            Color defaultBgColor = Color.rgb(43, 45, 48);
            Color hoverBgColor = Color.rgb(30, 31, 34);
            Color defaultTextColor = Color.rgb(205, 205, 205);
            Color hoverTextColor = Color.rgb(255, 255, 255);
            Color pressedBgColor = Color.rgb(80, 82, 85); // Lighter background
            Color pressedTextColor = Color.rgb(230, 230, 230); // Lighter text
            Color pressedBorderColor = Color.rgb(100, 103, 105); // Lighter border

            pauseButton.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                               "-fx-text-fill: rgb(205, 205, 205); " +
                               "-fx-border-color: rgb(30, 31, 34); " +
                               "-fx-border-width: 1px; " +
                               "-fx-border-radius: 5px; " +
                               "-fx-background-radius: 5px;");

            pauseButton.setOnMouseEntered(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), pauseButton);
                scaleTransition.setToX(1.05);
                scaleTransition.setToY(1.05);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                pauseButton.setStyle("-fx-background-color: rgb(30, 31, 34); " +
                                   "-fx-text-fill: rgb(255, 255, 255); " +
                                   "-fx-border-color: rgb(60, 63, 65); " +
                                   "-fx-border-width: 1px; " +
                                   "-fx-border-radius: 5px; " +
                                   "-fx-background-radius: 5px;");

                scaleTransition.play();
            });

            pauseButton.setOnMousePressed(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), pauseButton);
                scaleTransition.setToX(0.95);
                scaleTransition.setToY(0.95);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                pauseButton.setStyle("-fx-background-color: rgb(80, 82, 85); " + // Lighter background
                                   "-fx-text-fill: rgb(230, 230, 230); " + // Lighter text
                                   "-fx-border-color: rgb(100, 103, 105); " + // Lighter border
                                   "-fx-border-width: 1px; " +
                                   "-fx-border-radius: 5px; " +
                                   "-fx-background-radius: 5px;");

                scaleTransition.play();
            });

            pauseButton.setOnMouseReleased(e -> {
                if (pauseButton.isHover()) {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), pauseButton);
                    scaleTransition.setToX(1.05);
                    scaleTransition.setToY(1.05);
                    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                    pauseButton.setStyle("-fx-background-color: rgb(30, 31, 34); " +
                                       "-fx-text-fill: rgb(255, 255, 255); " +
                                       "-fx-border-color: rgb(60, 63, 65); " +
                                       "-fx-border-width: 1px; " +
                                       "-fx-border-radius: 5px; " +
                                       "-fx-background-radius: 5px;");

                    scaleTransition.play();
                } else {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), pauseButton);
                    scaleTransition.setToX(1.0);
                    scaleTransition.setToY(1.0);
                    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                    pauseButton.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                                       "-fx-text-fill: rgb(205, 205, 205); " +
                                       "-fx-border-color: rgb(30, 31, 34); " +
                                       "-fx-border-width: 1px; " +
                                       "-fx-border-radius: 5px; " +
                                       "-fx-background-radius: 5px;");

                    scaleTransition.play();
                }
            });

            pauseButton.setOnMouseExited(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), pauseButton);
                scaleTransition.setToX(1.0);
                scaleTransition.setToY(1.0);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                pauseButton.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                                   "-fx-text-fill: rgb(205, 205, 205); " +
                                   "-fx-border-color: rgb(30, 31, 34); " +
                                   "-fx-border-width: 1px; " +
                                   "-fx-border-radius: 5px; " +
                                   "-fx-background-radius: 5px;");

                scaleTransition.play();
            });
        }

        if (stepBackButton != null) {
            stepBackButton.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                    "-fx-text-fill: rgb(205, 205, 205); " +
                    "-fx-border-color: rgb(30, 31, 34); " +
                    "-fx-border-width: 1px; " +
                    "-fx-border-radius: 5px; " +
                    "-fx-background-radius: 5px;");

            stepBackButton.setOnMouseEntered(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), stepBackButton);
                scaleTransition.setToX(1.05);
                scaleTransition.setToY(1.05);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                stepBackButton.setStyle("-fx-background-color: rgb(30, 31, 34); " +
                        "-fx-text-fill: rgb(255, 255, 255); " +
                        "-fx-border-color: rgb(60, 63, 65); " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;");

                scaleTransition.play();
            });

            stepBackButton.setOnMousePressed(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), stepBackButton);
                scaleTransition.setToX(0.95);
                scaleTransition.setToY(0.95);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                stepBackButton.setStyle("-fx-background-color: rgb(80, 82, 85); " +
                        "-fx-text-fill: rgb(230, 230, 230); " +
                        "-fx-border-color: rgb(100, 103, 105); " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;");

                scaleTransition.play();
            });

            stepBackButton.setOnMouseReleased(e -> {
                if (stepBackButton.isHover()) {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), stepBackButton);
                    scaleTransition.setToX(1.05);
                    scaleTransition.setToY(1.05);
                    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                    stepBackButton.setStyle("-fx-background-color: rgb(30, 31, 34); " +
                            "-fx-text-fill: rgb(255, 255, 255); " +
                            "-fx-border-color: rgb(60, 63, 65); " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 5px; " +
                            "-fx-background-radius: 5px;");

                    scaleTransition.play();
                } else {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), stepBackButton);
                    scaleTransition.setToX(1.0);
                    scaleTransition.setToY(1.0);
                    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                    stepBackButton.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                            "-fx-text-fill: rgb(205, 205, 205); " +
                            "-fx-border-color: rgb(30, 31, 34); " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 5px; " +
                            "-fx-background-radius: 5px;");

                    scaleTransition.play();
                }
            });
        }

        if (stepForwardButton != null) {
            stepForwardButton.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                    "-fx-text-fill: rgb(205, 205, 205); " +
                    "-fx-border-color: rgb(30, 31, 34); " +
                    "-fx-border-width: 1px; " +
                    "-fx-border-radius: 5px; " +
                    "-fx-background-radius: 5px;");

            stepForwardButton.setOnMouseEntered(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), stepForwardButton);
                scaleTransition.setToX(1.05);
                scaleTransition.setToY(1.05);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                stepForwardButton.setStyle("-fx-background-color: rgb(30, 31, 34); " +
                        "-fx-text-fill: rgb(255, 255, 255); " +
                        "-fx-border-color: rgb(60, 63, 65); " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;");

                scaleTransition.play();
            });

            stepForwardButton.setOnMousePressed(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), stepForwardButton);
                scaleTransition.setToX(0.95);
                scaleTransition.setToY(0.95);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                stepForwardButton.setStyle("-fx-background-color: rgb(80, 82, 85); " +
                        "-fx-text-fill: rgb(230, 230, 230); " +
                        "-fx-border-color: rgb(100, 103, 105); " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;");

                scaleTransition.play();
            });

            stepForwardButton.setOnMouseReleased(e -> {
                if (stepForwardButton.isHover()) {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), stepForwardButton);
                    scaleTransition.setToX(1.05);
                    scaleTransition.setToY(1.05);
                    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                    stepForwardButton.setStyle("-fx-background-color: rgb(30, 31, 34); " +
                            "-fx-text-fill: rgb(255, 255, 255); " +
                            "-fx-border-color: rgb(60, 63, 65); " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 5px; " +
                            "-fx-background-radius: 5px;");

                    scaleTransition.play();
                } else {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), stepForwardButton);
                    scaleTransition.setToX(1.0);
                    scaleTransition.setToY(1.0);
                    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                    stepForwardButton.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                            "-fx-text-fill: rgb(205, 205, 205); " +
                            "-fx-border-color: rgb(30, 31, 34); " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 5px; " +
                            "-fx-background-radius: 5px;");

                    scaleTransition.play();
                }
            });
        }
    }


    private void stepBack(){
        System.out.println("Step Back");
        // TODO: Implement step back logic
    }

    private void stepForward(){
        System.out.println("Step Forward");
        // TODO: Implement step forward logic
    }

    private void setupGameField() {
        gameField.setStyle("-fx-border-color: rgb(60, 63, 65); -fx-border-width: 2px;");
        // TODO: Add game field setup logic here
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds++;
            if (seconds == 60) {
                seconds = 0;
                minutes++;
            }
            updateTimerText();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateTimerText() {
        timerText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void pauseGame() {
        isPaused = true;
        timeline.pause();
        showPauseMenu();
    }

    private void resumeGame() {
        isPaused = false;
        timeline.play();
        if (pausePopup != null) {
            pausePopup.close();
        }
    }

    private void showPauseMenu() {
        pausePopup = new Stage();
        pausePopup.initModality(Modality.APPLICATION_MODAL);
        pausePopup.initStyle(StageStyle.UNDECORATED);
        pausePopup.initOwner(pauseButton.getScene().getWindow());
        VBox pauseLayout = new VBox(20);
        pauseLayout.setAlignment(Pos.CENTER);

        pauseLayout.getStyleClass().add("pauseModal");

        Text pauseTitle = new Text("Game Paused");
        pauseTitle.setStyle("-fx-fill: rgb(205, 205, 205);");
        pauseTitle.setFont(new Font("Papyrus", 24));

        Button continueButton = createStyledButton("Continue");
        Button mainMenuButton = createStyledButton("Main Menu");

        continueButton.setOnAction(e -> resumeGame());

        mainMenuButton.setOnAction(e -> {
            Stage primaryStage = (Stage) pausePopup.getOwner();
            SceneTransitionManager.switchScene(primaryStage.getScene().getRoot(), "main-view.fxml");
            pausePopup.close();
        });

        pauseLayout.getChildren().addAll(pauseTitle, continueButton, mainMenuButton);

        Scene pauseScene = new Scene(pauseLayout, 300, 250);
        pauseScene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());
        pausePopup.setScene(pauseScene);

        pausePopup.setX(pauseButton.getScene().getWindow().getX() +
                       (pauseButton.getScene().getWindow().getWidth() - 300) / 2);
        pausePopup.setY(pauseButton.getScene().getWindow().getY() + 
                       (pauseButton.getScene().getWindow().getHeight() - 200) / 2);

        pausePopup.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                       "-fx-text-fill: rgb(205, 205, 205); " +
                       "-fx-border-color: rgb(30, 31, 34); " +
                       "-fx-border-width: 1px; " +
                       "-fx-border-radius: 5px; " +
                       "-fx-background-radius: 5px; " +
                       "-fx-font-family: 'Papyrus'; " +
                       "-fx-font-size: 16px; " +
                       "-fx-padding: 10px 30px;");

        button.setOnMouseEntered(e ->
            button.setStyle("-fx-background-color: rgb(30, 31, 34); " +
                           "-fx-text-fill: rgb(255, 255, 255); " +
                           "-fx-border-color: rgb(60, 63, 65); " +
                           "-fx-border-width: 1px; " +
                           "-fx-border-radius: 5px; " +
                           "-fx-background-radius: 5px; " +
                           "-fx-font-family: 'Papyrus'; " +
                           "-fx-font-size: 16px; " +
                           "-fx-padding: 10px 30px;")
        );

        button.setOnMousePressed(e ->
            button.setStyle("-fx-background-color: rgb(80, 82, 85); " +
                           "-fx-text-fill: rgb(230, 230, 230); " +
                           "-fx-border-color: rgb(100, 103, 105); " +
                           "-fx-border-width: 1px; " +
                           "-fx-border-radius: 5px; " +
                           "-fx-background-radius: 5px; " +
                           "-fx-font-family: 'Papyrus'; " +
                           "-fx-font-size: 16px; " +
                           "-fx-padding: 10px 30px;")
        );

        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                           "-fx-text-fill: rgb(205, 205, 205); " +
                           "-fx-border-color: rgb(30, 31, 34); " +
                           "-fx-border-width: 1px; " +
                           "-fx-border-radius: 5px; " +
                           "-fx-background-radius: 5px; " +
                           "-fx-font-family: 'Papyrus'; " +
                           "-fx-font-size: 16px; " +
                           "-fx-padding: 10px 30px;")
        );

        return button;
    }
}
