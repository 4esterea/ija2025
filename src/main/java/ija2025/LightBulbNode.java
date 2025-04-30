package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LightBulbNode extends GameNode {

    public LightBulbNode(int row, int col) {
        super(row, col);
    }

    @Override
    public void draw(GraphicsContext gc) {
        double cellSize = 50; // Default cell size
        double x = col * cellSize;
        double y = row * cellSize;
        double centerX = x + cellSize / 2;
        double centerY = y + cellSize / 2;
        double wireWidth = 10;
        double bulbRadius = 15;

        // Draw background
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(x, y, cellSize, cellSize);

        // Draw border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, cellSize, cellSize);

        // Save the current state
        gc.save();

        // Translate to the center of the cell
        gc.translate(centerX, centerY);

        // Rotate according to the rotation value
        gc.rotate(rotation);

        // Draw the wire (only in one direction - from center to top)
        gc.setFill(isPowered ? Color.GREEN : Color.GRAY);
        gc.fillRect(-wireWidth/2, -cellSize/2, wireWidth, cellSize/4);

        // Draw the light bulb
        if (isPowered) {
            // Glowing bulb when powered
            gc.setFill(Color.YELLOW);
            gc.fillOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);

            // Add a glow effect
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(2);
            gc.strokeOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);
        } else {
            // Dim bulb when not powered
            gc.setFill(Color.DARKGRAY);
            gc.fillOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeOval(-bulbRadius, -bulbRadius, bulbRadius*2, bulbRadius*2);
        }

        // Restore the graphics context
        gc.restore();
    }

    @Override
    public void rotate() {
        super.rotate();
        // Additional logic specific to LightBulbNode rotation if needed
    }
}
