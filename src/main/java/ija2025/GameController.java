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
    private void applyMove(ObjectNode moveNode, boolean isPrevMove) {
        try {
            int row = moveNode.get("row").asInt();
            int col = moveNode.get("col").asInt();
            int targetRotation;

            // Выбираем нужный поворот в зависимости от направления
            if (isPrevMove) {
                targetRotation = moveNode.get("prevRotation").asInt();
            } else {
                targetRotation = moveNode.get("newRotation").asInt();
            }

            GameNode[][] grid = gameManager.getGrid();
            GameNode node = grid[row][col];

            if (node != null) {
                // Поворачиваем узел до нужного положения
                while (node.getRotation() != targetRotation) {
                    node.rotate();
                }

                // Обновляем поток энергии
                gameManager.updatePowerFlow();

                // Обновляем окно решения если оно открыто
                updateSolutionIfShowing();
            }
        } catch (Exception e) {
            System.err.println("Ошибка при применении хода: " + e.getMessage());
        }
    }
    public void updateMoveHistory() {
        loadMoveHistory(); // Перезагружаем историю из файла
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

    public void updateSolutionIfShowing() {
        if (isSolutionShowing && solutionStage != null && solutionStage.isShowing()) {
            drawSolutionGrid(solutionCanvas.getGraphicsContext2D(),
                    gameManager.getGridSize(),
                    solutionCanvas.getWidth() / gameManager.getGridSize());
        }
    }
    private void updateSolution() {
        if (solutionStage != null && solutionStage.isShowing() && solutionCanvas != null) {
            GraphicsContext gc = solutionCanvas.getGraphicsContext2D();
            int gridSize = gameManager.getGridSize();
            double cellSize = solutionCanvas.getWidth() / gridSize;
            drawSolutionGrid(gc, gridSize, cellSize);
        }
    }
    private void showSolution() {
        if (solutionStage != null && solutionStage.isShowing()) {
            // Если окно уже отображается, просто обновляем его
            updateSolution();
            return;
        }

        // Создаем новое окно (не модальное)
        solutionStage = new Stage();
        solutionStage.initStyle(StageStyle.DECORATED);

        // Устанавливаем владельца
        solutionStage.initOwner(solutionButton.getScene().getWindow());

        // Обработчик закрытия основного окна
        Stage primaryStage = (Stage) solutionButton.getScene().getWindow();
        primaryStage.setOnCloseRequest(event -> {
            if (solutionStage != null && solutionStage.isShowing()) {
                solutionStage.close();
                isSolutionShowing = false;
            }
        });

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
        solutionCanvas = new Canvas(gridSize * cellSize, gridSize * cellSize);
        GraphicsContext gc = solutionCanvas.getGraphicsContext2D();

        // Рисуем сетку с цифрами
        drawSolutionGrid(gc, gridSize, cellSize);

        // Кнопка закрытия
        Button closeButton = createStyledButton("Закрыть");
        closeButton.setOnAction(e -> {
            solutionStage.close();
            isSolutionShowing = false;
        });

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
        solutionStage.setTitle("Решение головоломки");

        // Устанавливаем обработчик закрытия окна решения
        solutionStage.setOnCloseRequest(event -> isSolutionShowing = false);

        // Позиционируем окно
        solutionStage.setWidth(gridSize * cellSize + 100);
        solutionStage.setHeight(gridSize * cellSize + 200);
        solutionStage.setX(solutionButton.getScene().getWindow().getX() +
                (solutionButton.getScene().getWindow().getWidth() - solutionStage.getWidth()) / 2);
        solutionStage.setY(solutionButton.getScene().getWindow().getY() +
                (solutionButton.getScene().getWindow().getHeight() - solutionStage.getHeight()) / 2);

        // Показываем окно и устанавливаем флаг
        solutionStage.show();
        isSolutionShowing = true;
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
