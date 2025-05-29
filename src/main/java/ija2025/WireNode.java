/*
 * WireNode.java
 *
 * Authors: Zhdanovich Iaroslav (xzhdan00)
 *          Malytskyi Denys     (xmalytd00)
 *
 * Description: Game node class that represents wire elements in the game grid,
 * extending GameNode with functionality for handling wire connections in multiple
 * directions. Implements drawing logic for connected wire segments with visual
 * indicators for power status and disconnected ends, and rotation behavior that
 * properly updates connection directions.
 */


package ija2025;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.HashSet;
import java.util.Set;

// Class representing a wire element on the game grid
public class WireNode extends GameNode {
    // Set of directions where wire has connections
    private Set<Direction> connections = new HashSet<>();

    // Enum for connection directions
    public enum Direction {
        UP(0),
        RIGHT(90),
        DOWN(180),
        LEFT(270);

        private final int degrees;

        Direction(int degrees) {
            this.degrees = degrees;
        }

        // Get angle in degrees
        public int getDegrees() {
            return degrees;
        }

        // Get opposite direction
        public Direction getOpposite() {
            switch (this) {
                case UP: return DOWN;
                case RIGHT: return LEFT;
                case DOWN: return UP;
                case LEFT: return RIGHT;
                default: return UP;
            }
        }

        // Convert angle to direction
        public static Direction fromDegrees(int degrees) {
            degrees = (degrees % 360 + 360) % 360; // Normalize angle
            switch (degrees) {
                case 0: return UP;
                case 90: return RIGHT;
                case 180: return DOWN;
                case 270: return LEFT;
                default: return UP;
            }
        }
    }

    // Constructor
    public WireNode(int row, int col) {
        super(row, col);
    }

    // Add connection in specified direction
    public void addConnection(Direction direction) {
        connections.add(direction);
    }

    // Get all connected directions
    public Set<Direction> getConnectedDirections() {
        return new HashSet<>(connections);
    }

    // Check if direction is connected
    public boolean isDirectionConnected(Direction direction) {
        return connections.contains(direction);
    }

    @Override
    public void draw(GraphicsContext gc) {
        double cellSize = gameManager.getCellSize();
        double x = col * cellSize;
        double y = row * cellSize;
        double centerX = x + cellSize / 2;
        double centerY = y + cellSize / 2;

        // Wire thickness
        double connectionWidth = cellSize * 0.1;
        // Small overlap between elements
        double overlap = 1.0; // 1 pixel overlap

        // Draw transparent background
        gc.clearRect(x, y, cellSize, cellSize);

        // Draw wires in connected directions
        for (Direction dir : connections) {
            double wireX = 0;
            double wireY = 0;
            double wireWidth = 0;
            double wireHeight = 0;

            switch (dir) {
                case UP:
                    wireX = centerX - connectionWidth / 2;
                    wireY = y - overlap; // Extend beyond current cell
                    wireWidth = connectionWidth;
                    wireHeight = cellSize / 2 + overlap;
                    break;
                case RIGHT:
                    wireX = centerX;
                    wireY = centerY - connectionWidth / 2;
                    wireWidth = cellSize / 2 + overlap;
                    wireHeight = connectionWidth;
                    break;
                case DOWN:
                    wireX = centerX - connectionWidth / 2;
                    wireY = centerY;
                    wireWidth = connectionWidth;
                    wireHeight = cellSize / 2 + overlap;
                    break;
                case LEFT:
                    wireX = x - overlap; // Extend beyond current cell
                    wireY = centerY - connectionWidth / 2;
                    wireWidth = cellSize / 2 + overlap;
                    wireHeight = connectionWidth;
                    break;
            }

            // Determine wire color
            Color innerWireColor;
            if (hasDisconnectedEnd) {
                innerWireColor = Color.RED;
            } else {
                innerWireColor = isPowered ? Color.rgb(255, 185, 1) : Color.rgb(150, 150, 150);
            }

            gc.setFill(innerWireColor);
            gc.fillRect(wireX, wireY, wireWidth, wireHeight);
        }

        // Draw connection node in center if more than one connection
        if (connections.size() > 1) {
            // Determine node color, same as wires
            Color nodeColor;
            if (hasDisconnectedEnd) {
                nodeColor = Color.RED;
            } else {
                nodeColor = isPowered ? Color.rgb(255, 185, 1) : Color.rgb(150, 150, 150);
            }

            // Connection node size same as wire thickness
            double jointSize = connectionWidth;

            // Corner radius (equal to node size for round shape)
            double cornerRadius = jointSize;

            gc.setFill(nodeColor);
            gc.fillRoundRect(centerX - jointSize/2, centerY - jointSize/2,
                    jointSize, jointSize, cornerRadius, cornerRadius);
        }
    }

    @Override
    public void rotate() {
        super.rotate();

        // Update connections when rotating
        Set<Direction> newConnections = new HashSet<>();
        for (Direction dir : connections) {
            // Rotate each direction by 90 degrees
            int newDegrees = (dir.getDegrees() + 90) % 360;
            newConnections.add(Direction.fromDegrees(newDegrees));
        }

        connections = newConnections;
    }

    private boolean hasDisconnectedEnd = false;

    // Check if wire has disconnected end
    public boolean hasDisconnectedEnd() {
        return hasDisconnectedEnd;
    }

    // Set disconnected end state
    public void setDisconnectedEnd(boolean disconnected) {
        this.hasDisconnectedEnd = disconnected;
    }

    // Remove connection in specified direction
    public boolean removeConnection(Direction direction) {
        if (connections.contains(direction)) {
            connections.remove(direction);
            return true;
        }
        return false;
    }
}