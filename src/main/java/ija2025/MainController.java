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
import javafx.scene.shape.SVGPath;
import javafx.scene.effect.DropShadow;
import javafx.scene.Group;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.InputStream;
import javafx.scene.image.ImageView;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Line;
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

    private void setupButtonActions() {
        playButton.setOnAction(event -> {
            SceneTransitionManager.switchScene(playButton, "difficulty-view.fxml");
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

    private void setupBulbIcon() {
        try {
            bulbIcon.setVisible(true);

            bulbIcon.getChildren().clear();

            InputStream input = getClass().getResourceAsStream("/ija2025/media/bulb_icon_mm.png");
            if (input == null) {
                System.err.println("Не найден файл bulb_icon.png в ресурсах!");
                return;
            }

            javafx.scene.image.Image image = new javafx.scene.image.Image(input);
            ImageView imageView = new ImageView(image);

            imageView.setFitWidth(300);
            imageView.setFitHeight(400);
            imageView.setPreserveRatio(true);

            Group bulbWithEffects = new Group();

            DropShadow glowEffect = new DropShadow();
            glowEffect.setColor(Color.YELLOW);
            glowEffect.setRadius(40);
            glowEffect.setSpread(0.7);

            imageView.setEffect(glowEffect);

            bulbWithEffects.getChildren().add(imageView);

            bulbIcon.getChildren().add(bulbWithEffects);

            FadeTransition backgroundPulse = new FadeTransition(Duration.seconds(3), bulbWithEffects);
            backgroundPulse.setFromValue(0.93);
            backgroundPulse.setToValue(1.0);
            backgroundPulse.setCycleCount(javafx.animation.Animation.INDEFINITE);
            backgroundPulse.setAutoReverse(true);
            backgroundPulse.play();

            FadeTransition blinkTransition = new FadeTransition(Duration.seconds(0.05), imageView);
            blinkTransition.setFromValue(1.0);
            blinkTransition.setToValue(0.05);
            blinkTransition.setAutoReverse(true);

            FadeTransition glowTransition = new FadeTransition(Duration.seconds(0.05), bulbWithEffects);
            glowTransition.setFromValue(1.0);
            glowTransition.setToValue(0.1);
            glowTransition.setAutoReverse(true);

            Thread flickerThread = new Thread(() -> {
                try {
                    java.util.Random random = new java.util.Random();
                    while (true) {
                        int pauseDuration = 800 + random.nextInt(3000);
                        Thread.sleep(pauseDuration);

                        int flickerCount = 1 + random.nextInt(3);

                        for (int i = 0; i < flickerCount; i++) {
                            final boolean isLastBlink = (i == flickerCount - 1);

                            javafx.application.Platform.runLater(() -> {
                                blinkTransition.stop();
                                glowTransition.stop();

                                int cycles;

                                if (isLastBlink) {
                                    cycles = (1 + random.nextInt(2)) * 2;
                                } else {
                                    cycles = 1 + random.nextInt(4);
                                }

                                blinkTransition.setCycleCount(cycles);
                                glowTransition.setCycleCount(cycles);

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