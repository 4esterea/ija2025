/*
 * SolutionController.java
 *
 * Authors: Zhdanovich Iaroslav (xzhdan00)
 *          Malytskyi Denys     (xmalytd00)
 *
 * Description: Controller class for the solution view in the "lightbulb" project.
 * Manages the visual representation of the game solution, including a color-coded
 * grid that indicates how many rotations each node needs to reach its correct position.
 * Provides intuitive visual feedback with different colors and symbols for proper
 * game element alignment.
 */


package ija2025;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.effect.BlendMode;

public class SolutionController {
    @FXML
    private Canvas solutionCanvas;

    private GameManager gameManager;


    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;

        int gridSize = gameManager.getGridSize();
        double cellSize = Math.min(350, 350) / gridSize;
        solutionCanvas.setWidth(gridSize * cellSize);
        solutionCanvas.setHeight(gridSize * cellSize);

        updateSolution();
    }

    public void updateSolution() {
        if (solutionCanvas != null && gameManager != null) {
            GraphicsContext gc = solutionCanvas.getGraphicsContext2D();
            int gridSize = gameManager.getGridSize();
            double cellSize = solutionCanvas.getWidth() / gridSize;
            drawSolutionGrid(gc, gridSize, cellSize);
        }
    }

    private void drawSolutionGrid(GraphicsContext gc, int gridSize, double cellSize) {
        double margin = 10.0;

        double effectiveCellSize = (solutionCanvas.getWidth() - 2 * margin) / gridSize;

        gc.clearRect(0, 0, solutionCanvas.getWidth(), solutionCanvas.getHeight());

        gc.setStroke(Color.rgb(60, 63, 65));
        gc.setLineWidth(1);

        for (int i = 0; i <= gridSize; i++) {
            double y = margin + i * effectiveCellSize;
            double x = margin + i * effectiveCellSize;
            gc.strokeLine(margin, y, margin + gridSize * effectiveCellSize, y);
            gc.strokeLine(x, margin, x, margin + gridSize * effectiveCellSize);
        }

        GameNode[][] grid = gameManager.getGrid();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != null) {
                    GameNode node = grid[row][col];

                    double x = margin + col * effectiveCellSize;
                    double y = margin + row * effectiveCellSize;

                    gc.setFill(Color.rgb(30, 31, 34));
                    gc.fillRect(x, y, effectiveCellSize, effectiveCellSize);

                    String nodeInfo = getNodeInfo(node);
                    int rotationsNeeded = gameManager.getRotationsToOriginal(row, col);

                    Color baseColor;
                    switch (rotationsNeeded) {
                        case 0:
                            baseColor = Color.rgb(81, 143, 44);
                            break;
                        case 1:
                            baseColor = Color.rgb(254, 241, 10);
                            break;
                        case 2:
                            baseColor = Color.rgb(239, 154, 25);
                            break;
                        case 3:
                        default:
                            baseColor = Color.rgb(255, 15, 0);
                            break;
                    }

                    gc.setFill(baseColor);
                    gc.fillRect(x, y, effectiveCellSize, effectiveCellSize);

                    gc.setStroke(Color.rgb(30, 30, 30, 0.7));
                    gc.setLineWidth(1.0);
                    gc.strokeRect(x, y, effectiveCellSize, effectiveCellSize);

                    RadialGradient vignette = new RadialGradient(
                            0,
                            0,
                            0.5,
                            0.5,
                            0.7,
                            true,
                            CycleMethod.NO_CYCLE,
                            new Stop(0, Color.TRANSPARENT),
                            new Stop(1, Color.rgb(0, 0, 0, 0.5))
                    );

                    gc.save();
                    gc.setGlobalBlendMode(BlendMode.MULTIPLY);
                    gc.translate(x, y);
                    gc.setFill(vignette);
                    gc.fillRect(0, 0, effectiveCellSize, effectiveCellSize);
                    gc.restore();

                    gc.setFill(Color.WHITE);
                    gc.setFont(Font.font("Cascadia Code", FontWeight.BOLD, effectiveCellSize / 2.5));
                    double textWidth = gc.getFont().getSize() * nodeInfo.length() * 0.6;
                    double textHeight = gc.getFont().getSize();
                    double textX = x + (effectiveCellSize - textWidth) / 2;
                    double textY = y + (effectiveCellSize + textHeight) / 2;

                    gc.fillText(nodeInfo, textX, textY);
                }
            }
        }
    }

    private String getNodeInfo(GameNode node) {
        int row = node.getRow();
        int col = node.getCol();
        int rotationsNeeded = gameManager.getRotationsToOriginal(row, col);

        return rotationsNeeded != 0 ? String.valueOf(rotationsNeeded) : "✔";
    }
}