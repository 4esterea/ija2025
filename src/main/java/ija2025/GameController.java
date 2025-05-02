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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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
    private Stage solutionStage;
    private Canvas solutionCanvas;
    private boolean isSolutionShowing = false;
    private List<ObjectNode> movesList = new ArrayList<>();
    private int currentMoveIndex = -1;

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
        setupWindowCloseHandler();
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

    private void setupWindowCloseHandler() {
        // Добавляем слушателя для сцены, чтобы получить доступ к Stage
        gameWindow.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                stage.setOnCloseRequest(event -> {
                    // Удаляем лог при закрытии окна
                    if (gameManager != null && gameManager.getGameLogger() != null) {
                        gameManager.getGameLogger().deleteLogFile();
                    }
                });
            }
        });
    }

    private void setupButtonTransitions() {
        // Список всех кнопок с иконками
        Button[] buttons = {pauseButton, stepBackButton, stepForwardButton, solutionButton};

        // Определение цветов для разных состояний
        String defaultStyle = "-fx-background-color: rgb(43, 45, 48); " +
                "-fx-text-fill: rgb(205, 205, 205); " +
                "-fx-border-color: rgb(30, 31, 34); " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px;";

        String hoverStyle = "-fx-background-color: rgb(30, 31, 34); " +
                "-fx-text-fill: rgb(255, 255, 255); " +
                "-fx-border-color: rgb(60, 63, 65); " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px;";

        String pressedStyle = "-fx-background-color: rgb(80, 82, 85); " +
                "-fx-text-fill: rgb(230, 230, 230); " +
                "-fx-border-color: rgb(100, 103, 105); " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px;";

        for (Button button : buttons) {
            if (button == null) continue;

            // Устанавливаем начальный стиль
            button.setStyle(defaultStyle);

            // При наведении мыши
            button.setOnMouseEntered(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
                scaleTransition.setToX(1.05);
                scaleTransition.setToY(1.05);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);
                button.setStyle(hoverStyle);
                scaleTransition.play();
            });

            // При нажатии
            button.setOnMousePressed(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
                scaleTransition.setToX(0.95);
                scaleTransition.setToY(0.95);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);
                button.setStyle(pressedStyle);
                scaleTransition.play();
            });

            // При отпускании
            button.setOnMouseReleased(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
                scaleTransition.setToX(button.isHover() ? 1.05 : 1.0);
                scaleTransition.setToY(button.isHover() ? 1.05 : 1.0);
                button.setStyle(button.isHover() ? hoverStyle : defaultStyle);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);
                scaleTransition.play();
            });

            // При уходе мыши
            button.setOnMouseExited(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
                scaleTransition.setToX(1.0);
                scaleTransition.setToY(1.0);
                button.setStyle(defaultStyle);
                scaleTransition.setInterpolator(Interpolator.EASE_BOTH);
                scaleTransition.play();
            });
        }
    }


    private void closeSolutionWindow() {
        if (solutionStage != null) {
            System.out.println("Закрытие окна с решением");
            solutionStage.close();
            solutionStage = null;
            isSolutionShowing = false;
        }
    }

    private void loadMoveHistory() {
        if (gameManager != null && gameManager.getGameLogger() != null) {
            File logFile = gameManager.getGameLogger().getLogFile();
            if (logFile != null && logFile.exists()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(logFile);
                    JsonNode movesNode = rootNode.get("moves");

                    movesList.clear();
                    if (movesNode != null && movesNode.isArray()) {
                        for (JsonNode moveNode : movesNode) {
                            movesList.add((ObjectNode) moveNode);
                        }
                        // Устанавливаем индекс в положение ПЕРЕД первым ходом
                        currentMoveIndex = movesList.size() - 1;
                        System.out.println("Загружено ходов: " + movesList.size());
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка при загрузке истории ходов: " + e.getMessage());
                }
            }
        }
    }

    public void reloadMoveHistory() {
        if (gameManager != null && gameManager.getGameLogger() != null) {
            File logFile = gameManager.getGameLogger().getLogFile();
            if (logFile != null && logFile.exists()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(logFile);
                    JsonNode movesNode = rootNode.get("moves");

                    movesList.clear();
                    if (movesNode != null && movesNode.isArray()) {
                        for (JsonNode moveNode : movesNode) {
                            movesList.add((ObjectNode) moveNode);
                        }
                        // Устанавливаем текущий индекс в конец истории
                        currentMoveIndex = movesList.size() - 1;
                        System.out.println("Загружена история: " + movesList.size() + " ходов");
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка при загрузке истории ходов: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("Файл журнала не найден или недоступен");
            }
        } else {
            System.err.println("GameManager или GameLogger не инициализированы");
        }
    }

    private void stepBack() {
        System.out.println("Step Back");

        if (movesList.isEmpty()) {
            System.out.println("История ходов пуста");
            return;
        }

        if (currentMoveIndex >= 0) {
            // Находим текущий ход
            ObjectNode currentMove = movesList.get(currentMoveIndex);

            // Получаем данные хода
            int row = currentMove.get("row").asInt();
            int col = currentMove.get("col").asInt();
            int prevRotation = currentMove.get("prevRotation").asInt();

            // Получаем узел из сетки
            GameNode node = gameManager.getGrid()[row][col];

            // Временно отключаем логирование
            boolean wasLoggingEnabled = gameManager.isLoggingEnabled();
            gameManager.setLoggingEnabled(false);

            // Устанавливаем предыдущее вращение
            while (node.getRotation() != prevRotation) {
                node.rotate();
            }

            // Восстанавливаем исходное состояние логирования
            gameManager.setLoggingEnabled(wasLoggingEnabled);

            // Уменьшаем индекс после применения хода
            currentMoveIndex--;
            System.out.println("Возврат к ходу " + (currentMoveIndex + 1) + " из " + movesList.size());

            // Обновляем сетку и поток энергии
            gameManager.redrawGrid();
            gameManager.updatePowerFlow();
            updateSolutionIfShowing();
        } else {
            System.out.println("Нет предыдущих ходов");
        }
    }

    private void stepForward() {
        System.out.println("Step Forward");

        if (movesList.isEmpty()) {
            System.out.println("История ходов пуста");
            return;
        }

        if (currentMoveIndex < movesList.size() - 1) {
            // Увеличиваем индекс на 1, чтобы перейти к следующему ходу
            currentMoveIndex++;

            // Выводим информацию для отладки
            System.out.println("Переход к ходу " + (currentMoveIndex + 1) + " из " + movesList.size());

            // Находим следующий ход
            ObjectNode nextMove = movesList.get(currentMoveIndex);

            // Получаем данные хода
            int row = nextMove.get("row").asInt();
            int col = nextMove.get("col").asInt();
            int newRotation = nextMove.get("newRotation").asInt();

            // Получаем узел из сетки
            GameNode node = gameManager.getGrid()[row][col];

            // Временно отключаем логирование
            boolean wasLoggingEnabled = gameManager.isLoggingEnabled();
            gameManager.setLoggingEnabled(false);

            // Устанавливаем новое вращение
            while (node.getRotation() != newRotation) {
                node.rotate();
            }

            // Восстанавливаем исходное состояние логирования
            gameManager.setLoggingEnabled(wasLoggingEnabled);

            // Обновляем сетку и поток энергии
            gameManager.redrawGrid();
            gameManager.updatePowerFlow();
            updateSolutionIfShowing();
        } else {
            System.out.println("Нет следующих ходов");
        }
    }

    public void updateSolutionIfShowing() {
        if (isSolutionShowing && solutionStage != null && solutionStage.isShowing()) {
            Scene scene = solutionStage.getScene();
            if (scene != null && scene.getRoot() instanceof BorderPane) {
                BorderPane rootPane = (BorderPane) scene.getRoot();
                SolutionController controller = (SolutionController) rootPane.getUserData();
                if (controller != null) {
                    controller.updateSolution();
                }
            }
        }
    }
    private void showSolution() {
        if (solutionStage != null && solutionStage.isShowing()) {
            updateSolutionIfShowing();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("solution-view.fxml"));
            Parent root = loader.load();

            SolutionController controller = loader.getController();
            controller.setGameManager(gameManager);

            ((BorderPane)root).setUserData(controller);

            solutionStage = new Stage();
            solutionStage.initStyle(StageStyle.DECORATED);
            solutionStage.initOwner(solutionButton.getScene().getWindow());
            solutionStage.setResizable(false);
            solutionStage.setOnCloseRequest(event -> isSolutionShowing = false);

            Scene scene = new Scene(root);
            solutionStage.setScene(scene);
            solutionStage.setTitle("Решение");

            int gridSize = gameManager.getGridSize();
            double cellSize = Math.min(350, 350) / gridSize;
            solutionStage.setWidth(gridSize * cellSize + 50);
            solutionStage.setHeight(gridSize * cellSize + 120);

            Stage primaryStage = (Stage) solutionButton.getScene().getWindow();
            solutionStage.setX(primaryStage.getX() +
                    (primaryStage.getWidth() - solutionStage.getWidth()) / 2);
            solutionStage.setY(primaryStage.getY() +
                    (primaryStage.getHeight() - solutionStage.getHeight()) / 2);

            solutionStage.show();
            isSolutionShowing = true;

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void setupGameField() {
        gameField.setStyle("-fx-border-color: rgb(60, 63, 65); -fx-border-width: 2px;");

        // Initialize the game manager with the selected difficulty
        gameManager = new GameManager(selectedDifficulty);
        gameManager.setGameController(this);

        // Initialize the game
        gameManager.initializeGame(gameField);

        // Загружаем историю ходов
        loadMoveHistory();

        // Check for win condition after each move
        gameField.setOnMouseClicked(event -> {
            if (gameManager.isGameWon()) {
                showWinMessage();
            }
            reloadMoveHistory();
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
        winLayout.setStyle("-fx-background-color: rgb(30, 31, 34); -fx-padding: 20px; -fx-border-width: 2px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 2);");

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
            closeSolutionWindow();
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
            closeSolutionWindow();
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
