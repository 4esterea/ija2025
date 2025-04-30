package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PowerNode extends GameNode {

    public PowerNode(int row, int col) {
        super(row, col);
        this.isPowered = true; // Power nodes are always powered
    }

    @Override
    public void draw(GraphicsContext gc) {
        double cellSize = 50; // Default cell size
        double x = col * cellSize;
        double y = row * cellSize;

        // Draw background
        gc.setFill(Color.YELLOW);
        gc.fillRect(x, y, cellSize, cellSize);

        // Draw border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, cellSize, cellSize);

        // Draw power symbol
        gc.setFill(Color.BLACK);
        // Draw a lightning bolt or power symbol based on rotation
        double centerX = x + cellSize / 2;
        double centerY = y + cellSize / 2;

        // Save the current state
        gc.save();

        // Translate to the center of the cell
        gc.translate(centerX, centerY);

        // Rotate according to the rotation value
        gc.rotate(rotation);

        // Draw the power symbol (a simple lightning bolt)
        gc.beginPath();
        gc.moveTo(0, -15);
        gc.lineTo(-5, 0);
        gc.lineTo(0, 0);
        gc.lineTo(0, 15);
        gc.lineTo(5, 0);
        gc.lineTo(0, 0);
        gc.closePath();
        gc.fill();

        // Restore the graphics context
        gc.restore();
    }

    @Override
    public void rotate() {
        super.rotate();
        // Additional logic specific to PowerNode rotation if needed
    }
}
