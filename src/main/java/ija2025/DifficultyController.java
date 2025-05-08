/*
 * DifficultyController.java
 *
 * Authors: Zhdanovich Iaroslav (xzhdan00)
 *          Malytskyi Denys     (xmalytd00)
 *
 * Description: Controller class for the difficulty selection screen that manages
 * UI elements, animations, and user interactions for different game difficulty levels
 * in the "lightbulb" project.
 */


package ija2025;

import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

// Controller for the difficulty selection screen
public class DifficultyController implements Initializable {
    // Text label for the difficulty menu
    @FXML
    private Text labelDifficultyMenu;

    // Buttons for different difficulty levels
    @FXML
    private Button easyButton;

    @FXML
    private Button mediumButton;

    @FXML
    private Button hardButton;

    // Button to return to the previous screen
    @FXML
    private Button backButton;

    // Initialize the controller after FXML has been loaded
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonTransitions();
        setupButtonActions();
    }

    // Configure action handlers for all buttons
    private void setupButtonActions() {
        // Return to main menu when back button is clicked
        backButton.setOnAction(event -> {
            SceneTransitionManager.switchScene(backButton, "/main-view.fxml");
        });

        // Handle easy difficulty selection
        easyButton.setOnAction(event -> {
            System.out.println("Easy difficulty selected");
            loadGameView("Easy");
        });

        // Handle medium difficulty selection
        mediumButton.setOnAction(event -> {
            System.out.println("Medium difficulty selected");
            loadGameView("Medium");
        });

        // Handle hard difficulty selection
        hardButton.setOnAction(event -> {
            System.out.println("Hard difficulty selected");
            loadGameView("Hard");
        });
    }

    // Start the game with the selected difficulty level
    private void loadGameView(String difficulty) {
        System.out.println("Starting game with " + difficulty + " difficulty");

        // Convert string difficulty to enum
        GameManager.Difficulty difficultyEnum;
        switch (difficulty) {
            case "Easy":
                difficultyEnum = GameManager.Difficulty.EASY;
                break;
            case "Medium":
                difficultyEnum = GameManager.Difficulty.MEDIUM;
                break;
            case "Hard":
                difficultyEnum = GameManager.Difficulty.HARD;
                break;
            default:
                difficultyEnum = GameManager.Difficulty.EASY;
        }

        // Store the selected difficulty to be accessed by GameController
        GameController.setSelectedDifficulty(difficultyEnum);

        // Switch to game screen
        SceneTransitionManager.switchScene(easyButton, "/game-view.fxml");
    }

    // Setup visual transitions for all UI elements
    private void setupButtonTransitions() {
        setupButtonTransition(easyButton);
        setupButtonTransition(mediumButton);
        setupButtonTransition(hardButton);
        setupButtonTransition(backButton);
        setupTextTransition(labelDifficultyMenu);
    }

    // Configure visual effects and animations for a button
    private void setupButtonTransition(Button button) {
        if (button != null) {
            // Define colors for different button states
            Color defaultBgColor = Color.rgb(43, 45, 48);
            Color hoverBgColor = Color.rgb(30, 31, 34);
            Color defaultTextColor = Color.rgb(205, 205, 205);
            Color hoverTextColor = Color.rgb(255, 255, 255);
            Color pressedBgColor = Color.rgb(80, 82, 85);
            Color pressedTextColor = Color.rgb(230, 230, 230);
            Color pressedBorderColor = Color.rgb(100, 103, 105);

            // Set default button style
            button.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                           "-fx-text-fill: rgb(205, 205, 205); " +
                           "-fx-border-color: rgb(30, 31, 34); " +
                           "-fx-border-width: 1px; " +
                           "-fx-border-radius: 5px; " +
                           "-fx-background-radius: 5px;");

            // Mouse enter effect - enlarge and brighten
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

            // Mouse press effect - shrink and darken
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

            // Mouse release effect - return to hover or default state
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

            // Mouse exit effect - return to normal size and color
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

    // Configure visual effects and animations for text elements
    private void setupTextTransition(Text text) {
        if (text != null) {
            // Define colors for different text states
            Color defaultTextColor = Color.rgb(205, 205, 205);
            Color hoverTextColor = Color.WHITE;
            Color pressedTextColor = Color.rgb(240, 240, 240);

            // Mouse enter effect - brighten and add glow
            text.setOnMouseEntered(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(300), text);
                fillTransition.setToValue(hoverTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                Glow glow = new Glow();
                glow.setLevel(0.5);
                text.setEffect(glow);

                fillTransition.play();
            });

            // Mouse exit effect - return to default color and remove glow
            text.setOnMouseExited(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(300), text);
                fillTransition.setToValue(defaultTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                text.setEffect(null);

                fillTransition.play();
            });

            // Mouse press effect - change color and increase glow
            text.setOnMousePressed(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(150), text);
                fillTransition.setToValue(pressedTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                Glow glow = new Glow();
                glow.setLevel(1);
                text.setEffect(glow);

                fillTransition.play();
            });

            // Mouse release effect - return to hover or default state
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