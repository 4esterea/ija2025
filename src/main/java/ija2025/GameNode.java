/*
 * GameNode.java
 *
 * Authors: Zhdanovich Iaroslav (xzhdan00)
 *          Malytskyi Denys     (xmalytd00)
 *
 * Description: Abstract base class that represents game board elements (nodes)
 * with common properties such as position, rotation and power state. Provides
 * core functionality for all game elements including drawing, rotation and
 * power state management in the "lightbulb" project.
 */


package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;

// Abstract base class for all game nodes
public abstract class GameNode extends StackPane {
    protected int row; // Row position on grid
    protected int col; // Column position on grid
    protected int rotation = 0; // Rotation angle in degrees
    protected boolean isPowered = false; // Node power state
    protected GameManager gameManager; // Game manager reference

    // Constructor with position parameters
    public GameNode(int row, int col) {
        this.row = row;
        this.col = col;

        // Set up click handler for rotation
        this.setOnMouseClicked(event -> rotate());
    }

    // Sets reference to game manager
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    // Abstract method to render the node
    public abstract void draw(GraphicsContext gc);

    // Rotates node by 90 degrees
    public void rotate() {
        int prevRotation = this.rotation;
        this.rotation = (this.rotation + 90) % 360;
        if (gameManager != null) {
            gameManager.logNodeRotation(this, prevRotation);
        }
    }

    // Gets row coordinate
    public int getRow() {
        return row;
    }

    // Gets column coordinate
    public int getCol() {
        return col;
    }

    // Gets current rotation angle
    public int getRotation() {
        return rotation;
    }

    // Sets rotation angle
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    // Checks power state
    public boolean isPowered() {
        return isPowered;
    }

    // Sets power state
    public void setPowered(boolean powered) {
        isPowered = powered;
    }
}