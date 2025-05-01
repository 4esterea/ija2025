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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
    private Button solutionButton;

    @FXML
    private Pane gameField;

    @FXML
    private BorderPane gameWindow;

    private Timeline timeline;
    private int seconds = 0;
    private int minutes = 0;
    private Stage pausePopup;
    private boolean isPaused = false;

    private GameManager gameManager;

    // Static variable to store the selected difficulty
    private static GameManager.Difficulty selectedDifficulty = GameManager.Difficulty.EASY;

    // Static method to set the selected difficulty
    public static void setSelectedDifficulty(GameManager.Difficulty difficulty) {
        selectedDifficulty = difficulty;
    }

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

        solutionButton.setOnAction(event -> {
           showSolution();
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

    private String getNodeInfo(GameNode node) {
        int row = node.getRow();
        int col = node.getCol();
        int rotationsNeeded = gameManager.getRotationsToOriginal(row, col);

        String nodeType = "";
        if (node instanceof PowerNode) {
            nodeType = "P";
        } else if (node instanceof LightBulbNode) {
            nodeType = "L";
        } else if (node instanceof WireNode) {
            nodeType = "W";
        }

        // Возвращаем тип узла и необходимое количество поворотов
        return nodeType + rotationsNeeded;
    }

    private void drawSolutionGrid(GraphicsContext gc, int gridSize, double cellSize) {
        // Очищаем холст
        gc.clearRect(0, 0, gridSize * cellSize, gridSize * cellSize);

        // Рисуем сетку
        gc.setStroke(Color.rgb(60, 63, 65));
        gc.setLineWidth(1);

        // Рисуем горизонтальные и вертикальные линии
        for (int i = 0; i <= gridSize; i++) {
            gc.strokeLine(0, i * cellSize, gridSize * cellSize, i * cellSize);
            gc.strokeLine(i * cellSize, 0, i * cellSize, gridSize * cellSize);
        }

        // Отображаем элементы решения с числами
        GameNode[][] grid = gameManager.getGrid();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != null) {
                    GameNode node = grid[row][col];
                    // Рисуем фон ячейки
                    gc.setFill(Color.rgb(30, 31, 34));
                    gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);

                    // Получаем информацию о узле
                    String nodeInfo = getNodeInfo(node);

                    // Подбираем цвет в зависимости от кол-ва требуемых поворотов
                    int rotationsNeeded = gameManager.getRotationsToOriginal(row, col);
                    if (rotationsNeeded == 0) {
                        gc.setFill(Color.rgb(100, 200, 100)); // зеленый - правильное положение
                    } else {
                        gc.setFill(Color.rgb(200, 200, 100)); // желтый - требуются повороты
                    }

                    // Рисуем текст
                    gc.setFont(new Font("Arial", cellSize / 3));
                    double textWidth = gc.getFont().getSize() * nodeInfo.length() * 0.6;
                    double textHeight = gc.getFont().getSize();
                    double textX = col * cellSize + (cellSize - textWidth) / 2;
                    double textY = row * cellSize + (cellSize + textHeight) / 2;

                    gc.fillText(nodeInfo, textX, textY);
                }
            }
        }
    }
    private void showSolution() {
        // Создаем новое окно
        Stage solutionStage = new Stage();
        solutionStage.initModality(Modality.APPLICATION_MODAL);
        solutionStage.initStyle(StageStyle.UNDECORATED);
        solutionStage.initOwner(solutionButton.getScene().getWindow());

        // Создаем контейнер для содержимого
        BorderPane solutionLayout = new BorderPane();
        solutionLayout.setStyle("-fx-background-color: rgb(43, 45, 48); -fx-padding: 20px;");

        // Заголовок окна
        Text solutionTitle = new Text("Решение");
        solutionTitle.setStyle("-fx-fill: rgb(205, 205, 205); -fx-font-size: 24px;");
        solutionTitle.setFont(new Font("Papyrus", 24));

        // Создаем холст для отображения сетки с решением
        int gridSize = gameManager.getGridSize();
        double cellSize = Math.min(500, 500) / gridSize;
        Canvas solutionCanvas = new Canvas(gridSize * cellSize, gridSize * cellSize);
        GraphicsContext gc = solutionCanvas.getGraphicsContext2D();

        // Рисуем сетку с цифрами
        drawSolutionGrid(gc, gridSize, cellSize);

        // Кнопка закрытия
        Button closeButton = createStyledButton("Закрыть");
        closeButton.setOnAction(e -> solutionStage.close());

        // Компоновка элементов окна
        VBox topBox = new VBox(10, solutionTitle);
        topBox.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(20, solutionCanvas);
        centerBox.setAlignment(Pos.CENTER);

        VBox bottomBox = new VBox(10, closeButton);
        bottomBox.setAlignment(Pos.CENTER);

        solutionLayout.setTop(topBox);
        solutionLayout.setCenter(centerBox);
        solutionLayout.setBottom(bottomBox);

        // Устанавливаем сцену и размер окна
        Scene solutionScene = new Scene(solutionLayout);
        solutionStage.setScene(solutionScene);

        // Позиционируем окно по центру относительно родителя
        solutionStage.setWidth(gridSize * cellSize + 100);
        solutionStage.setHeight(gridSize * cellSize + 200);
        solutionStage.setX(solutionButton.getScene().getWindow().getX() +
                (solutionButton.getScene().getWindow().getWidth() - solutionStage.getWidth()) / 2);
        solutionStage.setY(solutionButton.getScene().getWindow().getY() +
                (solutionButton.getScene().getWindow().getHeight() - solutionStage.getHeight()) / 2);

        // Показываем окно
        solutionStage.show();
    }



    private void setupGameField() {
        gameField.setStyle("-fx-border-color: rgb(60, 63, 65); -fx-border-width: 2px;");

        // Initialize the game manager with the selected difficulty
        gameManager = new GameManager(selectedDifficulty);

        // Initialize the game
        gameManager.initializeGame(gameField);

        // Check for win condition after each move
        gameField.setOnMouseClicked(event -> {
            if (gameManager.isGameWon()) {
                showWinMessage();
            }
        });
    }

    private void showWinMessage() {
        // Pause the game
        isPaused = true;
        timeline.pause();

        // Create a popup to show the win message
        Stage winPopup = new Stage();
        winPopup.initModality(Modality.APPLICATION_MODAL);
        winPopup.initStyle(StageStyle.UNDECORATED);
        winPopup.initOwner(pauseButton.getScene().getWindow());

        VBox winLayout = new VBox(20);
        winLayout.setAlignment(Pos.CENTER);
        winLayout.setStyle("-fx-background-color: rgb(43, 45, 48); -fx-padding: 20px;");

        Text winTitle = new Text("Congratulations!");
        winTitle.setStyle("-fx-fill: rgb(255, 215, 0); -fx-font-size: 24px;");
        winTitle.setFont(new Font("Papyrus", 24));

        Text winMessage = new Text("You've completed the puzzle!");
        winMessage.setStyle("-fx-fill: rgb(205, 205, 205); -fx-font-size: 16px;");
        winMessage.setFont(new Font("Papyrus", 16));

        Text timeText = new Text("Time: " + timerText.getText());
        timeText.setStyle("-fx-fill: rgb(205, 205, 205); -fx-font-size: 16px;");
        timeText.setFont(new Font("Papyrus", 16));

        Button mainMenuButton = createStyledButton("Main Menu");
        Button newGameButton = createStyledButton("New Game");

        mainMenuButton.setOnAction(e -> {
            Stage primaryStage = (Stage) winPopup.getOwner();
            SceneTransitionManager.switchScene(primaryStage.getScene().getRoot(), "main-view.fxml");
            winPopup.close();
        });

        newGameButton.setOnAction(e -> {
            Stage primaryStage = (Stage) winPopup.getOwner();
            SceneTransitionManager.switchScene(primaryStage.getScene().getRoot(), "difficulty-view.fxml");
            winPopup.close();
        });

        winLayout.getChildren().addAll(winTitle, winMessage, timeText, newGameButton, mainMenuButton);

        Scene winScene = new Scene(winLayout, 350, 300);
        winPopup.setScene(winScene);

        winPopup.setX(pauseButton.getScene().getWindow().getX() +
                     (pauseButton.getScene().getWindow().getWidth() - 350) / 2);
        winPopup.setY(pauseButton.getScene().getWindow().getY() + 
                     (pauseButton.getScene().getWindow().getHeight() - 300) / 2);

        winPopup.show();
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
