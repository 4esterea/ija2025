package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WireNode extends GameNode {

    public enum WireType {
        L_SHAPE,    // Connects adjacent sides (L)
        I_SHAPE,    // Connects opposite sides (I)
        T_SHAPE,    // Connects opposite sides and one side (T)
        X_SHAPE     // Connects all sides (X)
    }

    private WireType wireType;

    public WireNode(int row, int col, WireType wireType) {
        super(row, col);
        this.wireType = wireType;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double cellSize = 50; // Default cell size
        double x = col * cellSize;
        double y = row * cellSize;
        double centerX = x + cellSize / 2;
        double centerY = y + cellSize / 2;
        double wireWidth = 10;

        // Draw background
        gc.setFill(isPowered ? Color.LIGHTGREEN : Color.LIGHTGRAY);
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

        // Set wire color
        gc.setFill(isPowered ? Color.GREEN : Color.GRAY);

        // Draw the wire based on its type
        switch (wireType) {
            case L_SHAPE:
                // L-shape wire (connects adjacent sides)
                gc.fillRect(-wireWidth/2, -wireWidth/2, cellSize/2, wireWidth);
                gc.fillRect(-wireWidth/2, -wireWidth/2, wireWidth, cellSize/2);
                break;

            case I_SHAPE:
                // I-shape wire (connects opposite sides)
                gc.fillRect(-cellSize/2, -wireWidth/2, cellSize, wireWidth);
                break;

            case T_SHAPE:
                // T-shape wire (connects opposite sides and one side)
                gc.fillRect(-cellSize/2, -wireWidth/2, cellSize, wireWidth);
                gc.fillRect(-wireWidth/2, -wireWidth/2, wireWidth, cellSize/2);
                break;

            case X_SHAPE:
                // X-shape wire (connects all sides)
                gc.fillRect(-cellSize/2, -wireWidth/2, cellSize, wireWidth);
                gc.fillRect(-wireWidth/2, -cellSize/2, wireWidth, cellSize);
                break;
        }

        // Restore the graphics context
        gc.restore();
    }

    public WireType getWireType() {
        return wireType;
    }

    @Override
    public void rotate() {
        super.rotate();
        // Additional logic specific to WireNode rotation if needed
    }
}
