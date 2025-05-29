/*
 * MainController.java
 *
 * Authors: Zhdanovich Iaroslav (xzhdan00)
 *          Malytskyi Denys     (xmalytd00)
 *
 * Description: Controller class for the main menu screen that manages UI elements,
 * animations, and user interactions for the "lightbulb" project.
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
import javafx.scene.effect.DropShadow;
import javafx.scene.Group;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.InputStream;
import javafx.scene.image.ImageView;
import javafx.animation.FadeTransition;

// Controller for the main menu screen
public class MainController implements Initializable {
    public Text labelMainMenu;

    @FXML
    private Button playButton;

    @FXML
    private Group bulbIcon;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonTransitions();
        setupButtonActions();
        setupBulbIcon();
    }

    // Configures button action handlers
    private void setupButtonActions() {
        playButton.setOnAction(event -> {
            SceneTransitionManager.switchScene(playButton, "/difficulty-view.fxml");
        });
    }

    // Sets up visual transitions for UI elements
    private void setupButtonTransitions() {
        if (playButton != null) {
            Color defaultBgColor = Color.rgb(43, 45, 48);
            Color hoverBgColor = Color.rgb(30, 31, 34);
            Color defaultTextColor = Color.rgb(205, 205, 205);
            Color hoverTextColor = Color.rgb(255, 255, 255);
            Color pressedBgColor = Color.rgb(80, 82, 85);
            Color pressedTextColor = Color.rgb(230, 230, 230);
            Color pressedBorderColor = Color.rgb(100, 103, 105);

            // Set default button style
            playButton.setStyle("-fx-background-color: rgb(43, 45, 48); " +
                    "-fx-text-fill: rgb(205, 205, 205); " +
                    "-fx-border-color: rgb(30, 31, 34); " +
                    "-fx-border-width: 1px; " +
                    "-fx-border-radius: 5px; " +
                    "-fx-background-radius: 5px;");

            // Mouse enter event - scale up and change style
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

            // Mouse press event - scale down and change style
            playButton.setOnMousePressed(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), playButton);
                scaleTransition.setToX(0.95);
                scaleTransition.setToY(0.95);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

                playButton.setStyle("-fx-background-color: rgb(80, 82, 85); " +
                        "-fx-text-fill: rgb(230, 230, 230); " +
                        "-fx-border-color: rgb(100, 103, 105); " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;");

                scaleTransition.play();
            });

            // Mouse release event - restore hover state or default state
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

            // Mouse exit event - restore default state
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

        // Configure main menu label transitions if available
        if (labelMainMenu != null) {
            Color defaultTextColor = Color.rgb(205, 205, 205);
            Color hoverTextColor = Color.WHITE;
            Color pressedTextColor = Color.rgb(240, 240, 240);

            // Mouse enter event - add glow effect and change color
            labelMainMenu.setOnMouseEntered(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(300), labelMainMenu);
                fillTransition.setToValue(hoverTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                Glow glow = new Glow();
                labelMainMenu.setEffect(glow);

                fillTransition.play();
            });

            // Mouse exit event - remove effects and restore color
            labelMainMenu.setOnMouseExited(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(300), labelMainMenu);
                fillTransition.setToValue(defaultTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                labelMainMenu.setEffect(null);

                fillTransition.play();
            });

            // Mouse press event - increase glow and change color
            labelMainMenu.setOnMousePressed(e -> {
                FillTransition fillTransition = new FillTransition(Duration.millis(150), labelMainMenu);
                fillTransition.setToValue(pressedTextColor);
                fillTransition.setInterpolator(Interpolator.EASE_BOTH);

                Glow glow = new Glow();
                glow.setLevel(1);
                labelMainMenu.setEffect(glow);

                fillTransition.play();
            });

            // Mouse release event - restore appropriate state
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

    // Initializes the animated light bulb icon
    private void setupBulbIcon() {
        try {
            bulbIcon.setVisible(true);
            bulbIcon.getChildren().clear();

            // Load bulb image from resources
            InputStream input = getClass().getResourceAsStream("/media/bulb_icon_mm.png");
            if (input == null) {
                return;
            }

            // Create and configure image view
            javafx.scene.image.Image image = new javafx.scene.image.Image(input);
            ImageView imageView = new ImageView(image);

            imageView.setFitWidth(300);
            imageView.setFitHeight(400);
            imageView.setPreserveRatio(true);

            // Create container with effects
            Group bulbWithEffects = new Group();

            // Add glow effect to bulb
            DropShadow glowEffect = new DropShadow();
            glowEffect.setColor(Color.YELLOW);
            glowEffect.setRadius(40);
            glowEffect.setSpread(0.7);

            imageView.setEffect(glowEffect);
            bulbWithEffects.getChildren().add(imageView);
            bulbIcon.getChildren().add(bulbWithEffects);

            // Add slow pulsing animation
            FadeTransition backgroundPulse = new FadeTransition(Duration.seconds(3), bulbWithEffects);
            backgroundPulse.setFromValue(0.93);
            backgroundPulse.setToValue(1.0);
            backgroundPulse.setCycleCount(javafx.animation.Animation.INDEFINITE);
            backgroundPulse.setAutoReverse(true);
            backgroundPulse.play();

            // Create blink animations
            FadeTransition blinkTransition = new FadeTransition(Duration.seconds(0.05), imageView);
            blinkTransition.setFromValue(1.0);
            blinkTransition.setToValue(0.05);
            blinkTransition.setAutoReverse(true);

            FadeTransition glowTransition = new FadeTransition(Duration.seconds(0.05), bulbWithEffects);
            glowTransition.setFromValue(1.0);
            glowTransition.setToValue(0.1);
            glowTransition.setAutoReverse(true);

            // Start thread for random flicker effect
            Thread flickerThread = new Thread(() -> {
                try {
                    java.util.Random random = new java.util.Random();
                    while (true) {
                        // Random pause between flickers
                        int pauseDuration = 800 + random.nextInt(3000);
                        Thread.sleep(pauseDuration);

                        // Random number of flickers
                        int flickerCount = 1 + random.nextInt(3);

                        for (int i = 0; i < flickerCount; i++) {
                            final boolean isLastBlink = (i == flickerCount - 1);

                            javafx.application.Platform.runLater(() -> {
                                blinkTransition.stop();
                                glowTransition.stop();

                                int cycles;

                                // Last blink in series has more flickers
                                if (isLastBlink) {
                                    cycles = (1 + random.nextInt(2)) * 2;
                                } else {
                                    cycles = 1 + random.nextInt(4);
                                }

                                blinkTransition.setCycleCount(cycles);
                                glowTransition.setCycleCount(cycles);

                                // Reset opacity after last blink
                                if (isLastBlink) {
                                    blinkTransition.setOnFinished(event -> {
                                        imageView.setOpacity(1.0);
                                        bulbWithEffects.setOpacity(1.0);
                                    });
                                } else {
                                    blinkTransition.setOnFinished(null);
                                }

                                blinkTransition.play();
                                glowTransition.play();
                            });

                            // Pause between individual flickers in a series
                            Thread.sleep(100 + random.nextInt(200));
                        }
                    }
                } catch (InterruptedException e) {
                    System.err.println("Blink Animation interrupted: " + e.getMessage());
                }
            });

            flickerThread.setDaemon(true);
            flickerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}