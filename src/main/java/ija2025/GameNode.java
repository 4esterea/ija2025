package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public abstract class GameNode extends StackPane {
    protected int row;
    protected int col;
    protected int rotation = 0; // 0, 90, 180, 270 degrees
    protected boolean isPowered = false;

    public GameNode(int row, int col) {
        this.row = row;
        this.col = col;

        // Set up click handler for rotation
        this.setOnMouseClicked(event -> rotate());
    }

    public abstract void draw(GraphicsContext gc);

    public void rotate() {
        // Rotate 90 degrees clockwise
        rotation = (rotation + 90) % 360;
        // Redraw after rotation
        //this.getParent().requestLayout();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getRotation() {
        return rotation;
    }

    public boolean isPowered() {
        return isPowered;
    }

    public void setPowered(boolean powered) {
        isPowered = powered;
    }
}
