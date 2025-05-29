/*
 * LightBulbNode.java
 *
 * Authors: Zhdanovich Iaroslav (xzhdan00)
 *          Malytskyi Denys     (xmalytd00)
 *
 * Description: Game node class that represents light bulbs in the game grid,
 * extending GameNode with specific drawing and state management for bulbs.
 * Handles rendering of connected/disconnected and powered/unpowered bulb states
 * with visual effects in the "lightbulb" project.
 */


package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LightBulbNode extends GameNode {

    // Flag indicating if the bulb is disconnected from the circuit
    private boolean isDisconnected = false;

    public LightBulbNode(int row, int col) {
        super(row, col);
    }

    @Override
    public void draw(GraphicsContext gc) {
        double cellSize = gameManager.getCellSize();
        double x = col * cellSize;
        double y = row * cellSize;
        double centerX = x + cellSize / 2;
        double centerY = y + cellSize / 2;

        double overlap = 1.0; // Small overlap for elements

        // Connection and bulb parameters
        double connectionWidth = cellSize * 0.1;
        double connectionJointWidth = connectionWidth * 1.5;
        double connectionJointLength = cellSize * 0.07;
        double bulbRadius = cellSize * 0.25;

        gc.clearRect(x, y, cellSize, cellSize);

        gc.save();

        // Rotate the context for drawing the element
        gc.translate(centerX, centerY);
        gc.rotate(rotation);

        // Draw wire
        if (isDisconnected()) {
            gc.setFill(Color.RED); // Red for disconnected bulb
        } else {
            gc.setFill(isPowered() ? Color.rgb(255, 185, 1) : Color.rgb(150, 150, 150));
        }

        gc.fillRect(-connectionWidth/2, -cellSize/2 - overlap, connectionWidth,
                cellSize/2 - bulbRadius - connectionJointLength + overlap);

        // Draw connection joint
        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRect(-connectionJointWidth/2, -bulbRadius - connectionJointLength,
                connectionJointWidth, connectionJointLength);

        // Draw outer bulb shape
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.5);
        gc.strokeOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);

        // Inner part of the bulb
        double innerRadius = bulbRadius * 0.7;

        if (isPowered()) {
            // Bulb is powered on
            gc.setFill(Color.YELLOW);
            gc.fillOval(-innerRadius, -innerRadius, innerRadius*2, innerRadius*2);

            // Glow effect
            gc.setGlobalAlpha(0.15);
            gc.setFill(Color.YELLOW);
            gc.fillOval(-bulbRadius*1.5, -bulbRadius*1.5, bulbRadius*3, bulbRadius*3);
            gc.setGlobalAlpha(1.0);
        } else if (isDisconnected()) {
            // Disconnected bulb
            gc.setFill(Color.RED);
            gc.fillOval(-innerRadius, -innerRadius, innerRadius*2, innerRadius*2);
        } else {
            // Bulb is off
            gc.setFill(Color.rgb(43, 45, 48));
            gc.fillOval(-innerRadius, -innerRadius, innerRadius*2, innerRadius*2);
        }

        gc.restore();
    }

    @Override
    public void rotate() {
        super.rotate(); // Call parent rotation method
    }

    public boolean isDisconnected() {
        return isDisconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.isDisconnected = disconnected;
    }
}