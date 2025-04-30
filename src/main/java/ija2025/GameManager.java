package ija2025;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private Difficulty difficulty;
    private int gridSize;
    private GameNode[][] grid;
    private List<PowerNode> powerNodes;
    private List<LightBulbNode> lightBulbNodes;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private Random random;

    public GameManager(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.powerNodes = new ArrayList<>();
        this.lightBulbNodes = new ArrayList<>();
        this.random = new Random();

        // Set grid size based on difficulty
        switch (difficulty) {
            case EASY:
                gridSize = 5;
                break;
            case MEDIUM:
                gridSize = 7;
                break;
            case HARD:
                gridSize = 10;
                break;
            default:
                gridSize = 5;
        }

        // Initialize grid
        grid = new GameNode[gridSize][gridSize];
    }

    public void generateGameBoard() {}

    /**
     * Создает игровое поле с корректным подключением всех элементов
     */


    public void initializeGame(Pane gamePane) {
        // Create canvas for drawing
        double cellSize = 50;
        double canvasSize = gridSize * cellSize;
        gameCanvas = new Canvas(canvasSize, canvasSize);
        gc = gameCanvas.getGraphicsContext2D();

        // Add canvas to the game pane
        gamePane.getChildren().add(gameCanvas);

        // Create grid with random nodes
        generateGameBoard();

        // Draw the initial state
        drawGrid();

        // Set up click handlers for nodes
        setupClickHandlers(gamePane);
    }



    private void drawGrid() {
        // Clear the canvas
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw each node
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != null) {
                    grid[row][col].draw(gc);
                }
            }
        }
    }

    private void setupClickHandlers(Pane gamePane) {
        gameCanvas.setOnMouseClicked(event -> {
            double cellSize = gameCanvas.getWidth() / gridSize;
            int col = (int) (event.getX() / cellSize);
            int row = (int) (event.getY() / cellSize);

            // Check if click is within grid bounds
            if (row >= 0 && row < gridSize && col >= 0 && col < gridSize) {
                GameNode node = grid[row][col];
                if (node != null) {
                    // Rotate the node
                    node.rotate();

                    // Redraw the grid
                    drawGrid();

                    // Update power flow
                    updatePowerFlow();
                }
            }
        });
    }

    public boolean updatePowerFlow() {
        // Сбрасываем питание для всех узлов кроме PowerNode
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != null && !(grid[row][col] instanceof PowerNode)) {
                    grid[row][col].setPowered(false);
                }
            }
        }

        // Распространяем питание от каждого источника
        for (PowerNode powerNode : powerNodes) {
            propagatePower(powerNode.getRow(), powerNode.getCol(), powerNode.getRotation());
        }

        // Считаем количество подключенных проводов
        int poweredWires = 0;
        int totalWires = 0;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] instanceof WireNode) {
                    totalWires++;
                    if (grid[row][col].isPowered()) {
                        poweredWires++;
                    }
                }
            }
        }

        // Перерисовываем сетку
        drawGrid();

        // Возвращаем true только если все провода подключены
        return poweredWires == totalWires;
    }

    private void propagatePower(int row, int col, int fromDirection) {
        // Check if position is valid
        if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) {
            return;
        }

        GameNode node = grid[row][col];
        if (node == null || node.isPowered()) {
            return; // Skip if no node or already powered
        }

        // Set node as powered
        node.setPowered(true);

        // Determine which directions to propagate based on node type and rotation
        if (node instanceof PowerNode) {
            // Power nodes propagate in all directions
            propagatePower(row - 1, col, 180); // Up
            propagatePower(row + 1, col, 0);   // Down
            propagatePower(row, col - 1, 90);  // Left
            propagatePower(row, col + 1, 270); // Right
        } else if (node instanceof WireNode) {
            WireNode wireNode = (WireNode) node;
            int rotation = node.getRotation();

            switch (wireNode.getWireType()) {
                case L_SHAPE:
                    // L-shape connects adjacent sides
                    if (rotation == 0) {
                        if (fromDirection != 90) propagatePower(row, col - 1, 90);  // Left
                        if (fromDirection != 0) propagatePower(row + 1, col, 0);    // Down
                    } else if (rotation == 90) {
                        if (fromDirection != 0) propagatePower(row + 1, col, 0);    // Down
                        if (fromDirection != 270) propagatePower(row, col + 1, 270); // Right
                    } else if (rotation == 180) {
                        if (fromDirection != 270) propagatePower(row, col + 1, 270); // Right
                        if (fromDirection != 180) propagatePower(row - 1, col, 180); // Up
                    } else { // rotation == 270
                        if (fromDirection != 180) propagatePower(row - 1, col, 180); // Up
                        if (fromDirection != 90) propagatePower(row, col - 1, 90);   // Left
                    }
                    break;

                case I_SHAPE:
                    // I-shape connects opposite sides
                    if (rotation == 0 || rotation == 180) {
                        if (fromDirection != 0) propagatePower(row + 1, col, 0);    // Down
                        if (fromDirection != 180) propagatePower(row - 1, col, 180); // Up
                    } else { // rotation == 90 || rotation == 270
                        if (fromDirection != 90) propagatePower(row, col - 1, 90);   // Left
                        if (fromDirection != 270) propagatePower(row, col + 1, 270); // Right
                    }
                    break;

                case T_SHAPE:
                    // T-shape connects opposite sides and one side
                    if (rotation == 0) {
                        if (fromDirection != 0) propagatePower(row + 1, col, 0);    // Down
                        if (fromDirection != 90) propagatePower(row, col - 1, 90);   // Left
                        if (fromDirection != 270) propagatePower(row, col + 1, 270); // Right
                    } else if (rotation == 90) {
                        if (fromDirection != 0) propagatePower(row + 1, col, 0);    // Down
                        if (fromDirection != 180) propagatePower(row - 1, col, 180); // Up
                        if (fromDirection != 270) propagatePower(row, col + 1, 270); // Right
                    } else if (rotation == 180) {
                        if (fromDirection != 90) propagatePower(row, col - 1, 90);   // Left
                        if (fromDirection != 180) propagatePower(row - 1, col, 180); // Up
                        if (fromDirection != 270) propagatePower(row, col + 1, 270); // Right
                    } else { // rotation == 270
                        if (fromDirection != 0) propagatePower(row + 1, col, 0);    // Down
                        if (fromDirection != 90) propagatePower(row, col - 1, 90);   // Left
                        if (fromDirection != 180) propagatePower(row - 1, col, 180); // Up
                    }
                    break;

                case X_SHAPE:
                    // X-shape connects all sides
                    if (fromDirection != 0) propagatePower(row + 1, col, 0);    // Down
                    if (fromDirection != 90) propagatePower(row, col - 1, 90);   // Left
                    if (fromDirection != 180) propagatePower(row - 1, col, 180); // Up
                    if (fromDirection != 270) propagatePower(row, col + 1, 270); // Right
                    break;
            }
        } else if (node instanceof LightBulbNode) {
            // LightBulbNode only receives power, doesn't propagate
            // The direction it accepts power from depends on its rotation
            // No propagation needed
        }
    }

    public boolean isGameWon() {
        // Проверяем, что все лампочки подключены
        for (LightBulbNode bulb : lightBulbNodes) {
            if (!bulb.isPowered()) {
                return false;
            }
        }

        // Проверяем, что все провода подключены
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] instanceof WireNode && !grid[row][col].isPowered()) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getGridSize() {
        return gridSize;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}
