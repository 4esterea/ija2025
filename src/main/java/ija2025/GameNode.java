package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public abstract class GameNode extends StackPane {
    protected int row;
    protected int col;
    protected int rotation = 0; // 0, 90, 180, 270 degrees
    protected boolean isPowered = false;
    protected GameManager gameManager;

    public GameNode(int row, int col) {
        this.row = row;
        this.col = col;

        // Set up click handler for rotation
        this.setOnMouseClicked(event -> rotate());
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public abstract void draw(GraphicsContext gc);

    public void rotate() {
        int prevRotation = this.rotation;
        this.rotation = (this.rotation + 90) % 360;
        if (gameManager != null) {
            gameManager.logNodeRotation(this, prevRotation);
        }
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

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public boolean isPowered() {
        return isPowered;
    }

    public void setPowered(boolean powered) {
        isPowered = powered;
    }
}
