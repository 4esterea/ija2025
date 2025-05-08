/*
 * GameManager.java
 *
 * Authors: Zhdanovich Iaroslav (xzhdan00)
 *          Malytskyi Denys     (xmalytd00)
 *
 * Description: Core game logic class that manages the game board, game elements,
 * power flow calculations, board generation, and handles interactions between game
 * components for the "lightbulb" project.
 */


package ija2025;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.util.*;

public class GameManager {

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private Difficulty difficulty;
    private int gridSize;
    private GameNode[][] grid;
    private PowerNode _powerNode;
    private List<LightBulbNode> lightBulbNodes;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private Random random;
    private Map<String, Integer> originalRotations = new HashMap<>();
    private GameLogger gameLogger;
    private boolean loggingEnabled = false;

    private double cellSize;

    public GameManager(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.lightBulbNodes = new ArrayList<>();
        this.random = new Random();
        this.gameLogger = new GameLogger();

        // Set grid size based on selected difficulty
        switch (difficulty) {
            case EASY:
                gridSize = 5;                // Smaller grid for easy difficulty
                break;
            case MEDIUM:
                gridSize = 7;                // Medium-sized grid
                break;
            case HARD:
                gridSize = 10;               // Larger grid for hard difficulty
                break;
            default:
                gridSize = 5;                // Default to easy grid size
        }

        grid = new GameNode[gridSize][gridSize];
        cellSize = 50;
    }
    public double getCellSize() {
        return cellSize;
    }
    public GameNode[][] getGrid() {
        return grid;
    }
    public GameLogger getGameLogger() {
        return gameLogger;
    }
    public void enableLogging() {
        if (gameLogger != null) {
            loggingEnabled = true;
            gameLogger.setLoggingEnabled(true);
        }
    }

    public void disableLogging() {
        loggingEnabled = false;
        if (gameLogger != null) {
            gameLogger.setLoggingEnabled(false);
        }
    }
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean enabled) {
        this.loggingEnabled = enabled;
        if (gameLogger != null) {
            gameLogger.setLoggingEnabled(enabled);
        }
    }

    private GameController gameController;

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    public void generateGameBoard() {
        disableLogging();                // Temporarily disable move logging during generation
        placePowerNode();                // Place power source on the board

        boolean hasEmptyCell = true;

        while (hasEmptyCell) {
            // Find an empty cell on the board
            hasEmptyCell = false;
            int emptyRow = -1;
            int emptyCol = -1;

            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    if (grid[row][col] == null) {
                        hasEmptyCell = true;
                        emptyRow = row;
                        emptyCol = col;
                        break;
                    }
                }
                if (hasEmptyCell) break;
            }

            // Place a light bulb and connect it to the power source
            if (hasEmptyCell) {
                LightBulbNode node = placeLightBulbNode();
                connectToPower(node);
            }
        }
        List<WireNode> wiresToReplace = checkDisconnectedWires();
        while (!wiresToReplace.isEmpty()) {
            replaceDisconnectedWires(wiresToReplace);     // Replace disconnected wires with light bulbs
            wiresToReplace = checkDisconnectedWires();    // Recheck for any remaining disconnected wires
        }
        checkAllLightBulbsConnected();                    // Verify all bulbs are connected to power
        finalizePowerNodeConnections();                   // Set active directions for power node
        saveOriginalNodePositions();                      // Store the solution state
        shuffleAllNodes();                                // Randomize node rotations
        updatePowerFlow();                                // Update power state of all elements
        gameLogger.logInitialState(this);                 // Log the initial game state
        enableLogging();                                  // Enable move logging for player actions
    }

    private void shuffleAllNodes(){

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != null) {
                    GameNode node = grid[row][col];
                    if (node instanceof PowerNode) {
                        continue;
                    }
                    int rotations = random.nextInt(4);
                    for (int i = 0; i < rotations; i++) {
                        node.rotate();
                    }
                }
            }
        }
    }

    private void saveOriginalNodePositions() {
        originalRotations.clear();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                GameNode node = grid[row][col];
                if (node != null) {
                    String key = row + "," + col;
                    originalRotations.put(key, node.getRotation());
                }
            }
        }
    }

    public int getRotationsToOriginal(int row, int col)
    {
        String key = row + "," + col;
        GameNode node = grid[row][col];

        if (node == null || !originalRotations.containsKey(key)) {
            return 0;  // Return 0 if node doesn't exist or has no original position
        }

        int currentRotation = node.getRotation();
        int originalRotation = originalRotations.get(key);

        if (currentRotation == originalRotation) {
            return 0;  // Already in correct position
        }

        int rotationsNeeded = (4 + (originalRotation - currentRotation) / 90) % 4;  // Calculate standard rotations needed

        if (node instanceof WireNode) {
            WireNode wire = (WireNode) node;
            int connections = wire.getConnectedDirections().size();

            if (connections == 4) {
                return 0;  // Cross-shaped wire doesn't need rotation
            }

            if (connections == 2) {
                Set<WireNode.Direction> directions = wire.getConnectedDirections();

                boolean isIType = (directions.contains(WireNode.Direction.UP) && directions.contains(WireNode.Direction.DOWN)) ||
                        (directions.contains(WireNode.Direction.LEFT) && directions.contains(WireNode.Direction.RIGHT));

                if (isIType) {
                    return rotationsNeeded % 2;  // Straight wire has only 2 distinct states
                }
            }
        }
        return rotationsNeeded;
    }

    public void finalizePowerNodeConnections() {
        int powerRow = _powerNode.getRow();
        int powerCol = _powerNode.getCol();

        // Store active directions for power source
        Set<WireNode.Direction> activeDirections = new HashSet<>();

        // Check all four possible directions
        if (isValidConnection(powerRow - 1, powerCol)) { // Up
            activeDirections.add(WireNode.Direction.UP);
        }
        if (isValidConnection(powerRow, powerCol + 1)) { // Right
            activeDirections.add(WireNode.Direction.RIGHT);
        }
        if (isValidConnection(powerRow + 1, powerCol)) { // Down
            activeDirections.add(WireNode.Direction.DOWN);
        }
        if (isValidConnection(powerRow, powerCol - 1)) { // Left
            activeDirections.add(WireNode.Direction.LEFT);
        }

        // Set active directions in power source
        _powerNode.setActiveDirections(activeDirections);

        System.out.println("Active Powernode Directions: " + activeDirections);
        drawGrid(); // Redraw grid with updated power source
    }

    private boolean isValidConnection(int row, int col) {
        // Check grid boundaries
        if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) {
            return false;
        }

        GameNode node = grid[row][col];

        // No connection if cell is empty
        if (node == null) {
            return false;
        }

        // For wire nodes - check if it accepts power from this direction
        if (node instanceof WireNode) {
            WireNode wire = (WireNode) node;
            WireNode.Direction directionFromPower;

            // Determine direction from power source to wire
            if (row < _powerNode.getRow()) {
                directionFromPower = WireNode.Direction.UP;
            } else if (row > _powerNode.getRow()) {
                directionFromPower = WireNode.Direction.DOWN;
            } else if (col < _powerNode.getCol()) {
                directionFromPower = WireNode.Direction.LEFT;
            } else {
                directionFromPower = WireNode.Direction.RIGHT;
            }

            return wire.isDirectionConnected(directionFromPower.getOpposite());
        }

        // For light bulbs - check if it's facing the power source
        if (node instanceof LightBulbNode) {
            LightBulbNode bulb = (LightBulbNode) node;
            WireNode.Direction bulbDirection = WireNode.Direction.fromDegrees(bulb.getRotation());

            if (row < _powerNode.getRow() && bulbDirection == WireNode.Direction.UP) return true;
            if (row > _powerNode.getRow() && bulbDirection == WireNode.Direction.DOWN) return true;
            if (col < _powerNode.getCol() && bulbDirection == WireNode.Direction.LEFT) return true;
            if (col > _powerNode.getCol() && bulbDirection == WireNode.Direction.RIGHT) return true;
        }

        return false;
    }

    public boolean checkAllLightBulbsConnected() {
        updatePowerFlow();                // Update power state from source first

        boolean allConnected = true;
        List<LightBulbNode> disconnectedBulbs = new ArrayList<>();

        // Check if all bulbs receive power
        for (LightBulbNode lightBulb : lightBulbNodes) {
            if (!lightBulb.isPowered()) {
                System.out.println("Found disconnected bulb on [" +
                        lightBulb.getRow() + "," + lightBulb.getCol() + "]");
                lightBulb.setDisconnected(true);
                disconnectedBulbs.add(lightBulb);
                allConnected = false;
            } else {
                lightBulb.setDisconnected(false);
            }
        }

        // Try to connect disconnected bulbs
        if (!disconnectedBulbs.isEmpty()) {
            System.out.println("Trying to connect " + disconnectedBulbs.size() + " bulbs");

            // Try standard connection first
            for (LightBulbNode lightBulb : new ArrayList<>(disconnectedBulbs)) {
                connectToPower(lightBulb);

                updatePowerFlow();
                if (lightBulb.isPowered()) {
                    lightBulb.setDisconnected(false);
                    disconnectedBulbs.remove(lightBulb);
                    System.out.println("Successfully connected bulb on [" +
                            lightBulb.getRow() + "," + lightBulb.getCol() + "]");
                }
            }

            // If any bulbs still disconnected, try replacing neighbors
            if (!disconnectedBulbs.isEmpty()) {
                tryConnectWithNeighbors(disconnectedBulbs);
            }

            // Final check if all bulbs connected
            updatePowerFlow();
            allConnected = true;
            for (LightBulbNode lightBulb : lightBulbNodes) {
                if (!lightBulb.isPowered()) {
                    allConnected = false;
                    break;
                }
            }
        }

        if (allConnected) {
            System.out.println("All bulbs are connected to power!");
        }

        return allConnected;
    }

    private void tryConnectWithNeighbors(List<LightBulbNode> disconnectedBulbs) {
        for (LightBulbNode disconnectedBulb : new ArrayList<>(disconnectedBulbs)) {
            int row = disconnectedBulb.getRow();
            int col = disconnectedBulb.getCol();
            boolean connected = false;

            // Check adjacent cells
            for (WireNode.Direction dir : WireNode.Direction.values()) {
                int neighborRow = row;
                int neighborCol = col;

                switch (dir) {
                    case UP: neighborRow--; break;
                    case RIGHT: neighborCol++; break;
                    case DOWN: neighborRow++; break;
                    case LEFT: neighborCol--; break;
                }

                // Check grid boundaries
                if (neighborRow < 0 || neighborRow >= gridSize || neighborCol < 0 || neighborCol >= gridSize) {
                    continue;
                }

                GameNode neighbor = grid[neighborRow][neighborCol];

                // If neighbor is a connected light bulb
                if (neighbor instanceof LightBulbNode && neighbor.isPowered()) {
                    System.out.println("Найдена подключенная лампочка-сосед в [" +
                            neighborRow + "," + neighborCol + "]");

                    // Replace connected bulb with a wire
                    LightBulbNode connectedBulb = (LightBulbNode) grid[neighborRow][neighborCol];
                    lightBulbNodes.remove(connectedBulb);

                    // Determine connected bulb direction
                    WireNode.Direction connectedBulbDir = WireNode.Direction.fromDegrees(connectedBulb.getRotation());

                    // Create new wire with connections
                    WireNode wireNode = new WireNode(neighborRow, neighborCol);
                    wireNode.setGameManager(this);

                    // Add connection toward disconnected bulb
                    wireNode.addConnection(dir.getOpposite());

                    // Add connection in same direction as connected bulb
                    wireNode.addConnection(connectedBulbDir);

                    // Place wire in grid
                    grid[neighborRow][neighborCol] = wireNode;

                    // Rotate disconnected bulb toward new wire
                    while (WireNode.Direction.fromDegrees(disconnectedBulb.getRotation()) != dir) {
                        disconnectedBulb.rotate();
                    }

                    // Update power flow
                    updatePowerFlow();

                    // Check if bulb is now powered
                    if (disconnectedBulb.isPowered()) {
                        System.out.println("Successfully replaced bulb on [" + row + "," + col +
                                "] with wire and connected to power");
                        disconnectedBulb.setDisconnected(false);
                        connected = true;
                        break;
                    }
                }
            }

            if (!connected) {
                System.out.println("Replacing of bulb on [" + row + "," + col +
                        "] with wire failed");
            }
        }
    }

    private void connectToPower(LightBulbNode lightBulb) {
        System.out.println("\n===== STARTING LIGHT BULB CONNECTION TO POWER SOURCE =====");
        System.out.println("Light bulb: [" + lightBulb.getRow() + "," + lightBulb.getCol() +
                "], rotation: " + lightBulb.getRotation());
        System.out.println("Power source: [" + _powerNode.getRow() + "," + _powerNode.getCol() + "]");

        // Get the direction the light bulb is facing
        WireNode.Direction bulbDirection = WireNode.Direction.fromDegrees(lightBulb.getRotation());
        System.out.println("Light bulb direction: " + bulbDirection);

        // Try all possible light bulb orientations to find a direction to connect to power source
        int originalRotation = lightBulb.getRotation();
        boolean connectionSuccess = false;

        // Try all 4 possible light bulb orientations
        for (int attempts = 0; attempts < 4; attempts++) {
            // Get current light bulb direction
            bulbDirection = WireNode.Direction.fromDegrees(lightBulb.getRotation());

            // Calculate coordinates for first wire cell
            int nextRow = lightBulb.getRow();
            int nextCol = lightBulb.getCol();

            // Shift coordinates in the direction from light bulb
            switch (bulbDirection) {
                case UP: nextRow--; break;
                case RIGHT: nextCol++; break;
                case DOWN: nextRow++; break;
                case LEFT: nextCol--; break;
            }

            System.out.println("Trying to place first wire at [" + nextRow + "," + nextCol + "]");

            // Check grid boundaries and cell occupancy
            if (nextRow < 0 || nextRow >= gridSize || nextCol < 0 || nextCol >= gridSize) {
                System.out.println("Cannot build path: outside grid boundaries");
                lightBulb.rotate(); // Rotate and try next orientation
                continue;
            }

            // If cell is already occupied (not by a wire), try another orientation
            if (grid[nextRow][nextCol] != null && !(grid[nextRow][nextCol] instanceof WireNode)) {
                System.out.println("Cannot build path: cell occupied by " + grid[nextRow][nextCol].getClass().getSimpleName());
                lightBulb.rotate(); // Rotate and try next orientation
                continue;
            }

            // Recursively build path from first cell
            boolean pathBuilt = buildPath(nextRow, nextCol, bulbDirection.getOpposite(),
                    _powerNode.getRow(), _powerNode.getCol(), new boolean[gridSize][gridSize]);

            if (pathBuilt) {
                // Path successfully built
                connectionSuccess = true;
                System.out.println("Successfully built path from light bulb to power source!");
                break;
            } else {
                System.out.println("Failed to build path in this direction. Trying another.");
                lightBulb.rotate(); // Rotate and try next orientation
            }
        }

        // If connection failed after trying all orientations
        if (!connectionSuccess) {
            System.out.println("Failed to connect light bulb to power source in any direction.");
            // Restore original orientation
            while (lightBulb.getRotation() != originalRotation) {
                lightBulb.rotate();
            }
        } else {
            // Update power flow
            updatePowerFlow();
        }

        System.out.println("===== COMPLETED LIGHT BULB CONNECTION TO POWER SOURCE =====\n");
    }

    private boolean buildPath(int row, int col, WireNode.Direction fromDirection,
                            int targetRow, int targetCol, boolean[][] visited) {

        System.out.println("Building path from [" + row + "," + col + "] with direction " +
                          fromDirection + " to [" + targetRow + "," + targetCol + "]");

        // Check grid boundaries and visited cells
        if (row < 0 || row >= gridSize || col < 0 || col >= gridSize || visited[row][col]) {
            System.out.println("  → Outside grid or cell already visited");
            return false;
        }

        // Mark cell as visited
        visited[row][col] = true;

        // Check if target reached (power source)
        if (row == targetRow && col == targetCol) {
            System.out.println("  → Reached power source!");
            return true;
        }

        // Check cell - empty or wire
        WireNode wireNode;
        if (grid[row][col] == null) {
            System.out.println("  → Creating new wire at [" + row + "," + col + "]");
            wireNode = new WireNode(row, col);
            wireNode.setGameManager(this);
            grid[row][col] = wireNode;
        } else if (grid[row][col] instanceof WireNode) {
            System.out.println("  → Using existing wire at [" + row + "," + col + "]");
            wireNode = (WireNode) grid[row][col];
        } else {
            System.out.println("  → Cell occupied by another object, cannot continue");
            return false;
        }

        // Add connection from the direction we came from
        wireNode.addConnection(fromDirection);
        System.out.println("  → Added connection " + fromDirection);

        // Calculate deltas to determine direction
        int rowDelta = targetRow - row;
        int colDelta = targetCol - col;

        // List of directions to try in priority order
        List<WireNode.Direction> directionsToTry = new ArrayList<>();

        // Prioritize directions based on relative target position
        if (Math.abs(rowDelta) > Math.abs(colDelta)) {
            // Vertical movement priority
            if (rowDelta < 0) directionsToTry.add(WireNode.Direction.UP);
            else if (rowDelta > 0) directionsToTry.add(WireNode.Direction.DOWN);

            if (colDelta < 0) directionsToTry.add(WireNode.Direction.LEFT);
            else if (colDelta > 0) directionsToTry.add(WireNode.Direction.RIGHT);
        } else {
            // Horizontal movement priority
            if (colDelta < 0) directionsToTry.add(WireNode.Direction.LEFT);
            else if (colDelta > 0) directionsToTry.add(WireNode.Direction.RIGHT);

            if (rowDelta < 0) directionsToTry.add(WireNode.Direction.UP);
            else if (rowDelta > 0) directionsToTry.add(WireNode.Direction.DOWN);
        }

        // Add remaining directions if not already in the list
        for (WireNode.Direction dir : WireNode.Direction.values()) {
            if (!directionsToTry.contains(dir) && dir != fromDirection) {
                directionsToTry.add(dir);
            }
        }

        // Try directions in priority order
        for (WireNode.Direction nextDirection : directionsToTry) {
            // Skip direction we came from
            if (nextDirection == fromDirection) {
                continue;
            }

            System.out.println("  → Trying direction: " + nextDirection);

            // Calculate next position
            int nextRow = row;
            int nextCol = col;
            switch (nextDirection) {
                case UP: nextRow--; break;
                case RIGHT: nextCol++; break;
                case DOWN: nextRow++; break;
                case LEFT: nextCol--; break;
            }

            // Recursively build path from next cell
            if (buildPath(nextRow, nextCol, nextDirection.getOpposite(), targetRow, targetCol, visited)) {
                // Path found, add connection on this side
                wireNode.addConnection(nextDirection);
                System.out.println("  → Success! Adding connection " + nextDirection + " to [" + row + "," + col + "]");
                return true;
            }
        }

        // If no path could be built in any direction
        System.out.println("  → Failed to build path from [" + row + "," + col + "], going back");

        // If this is a wire we created but no path found - remove the wire
        if (grid[row][col] == wireNode && wireNode.getConnectedDirections().size() <= 1) {
            System.out.println("  → Removing useless wire from [" + row + "," + col + "]");
            grid[row][col] = null;
        }

        return false;
    }
    private void placePowerNode() {
        int row = random.nextInt(gridSize);
        int col = random.nextInt(gridSize);

        // Ensure the position is empty
        while (grid[row][col] != null) {
            row = random.nextInt(gridSize);
            col = random.nextInt(gridSize);
        }

        PowerNode powerNode = new PowerNode(row, col);
        powerNode.setGameManager(this);
        grid[row][col] = powerNode;
        _powerNode = powerNode;
    }

    private LightBulbNode placeLightBulbNode() {
        int row = random.nextInt(gridSize);
        int col = random.nextInt(gridSize);

        // Ensure the position is empty
        while (grid[row][col] != null) {
            row = random.nextInt(gridSize);
            col = random.nextInt(gridSize);
        }

        LightBulbNode lightBulbNode = new LightBulbNode(row, col);
        lightBulbNode.setGameManager(this);
        grid[row][col] = lightBulbNode;
        int rotations = random.nextInt(4); // 0-3 rotations (0, 90, 180, 270 degrees)
        for(int i = 0; i < rotations; i++) lightBulbNode.rotate();

        // Check if light bulb is facing the grid boundaries, rotate if true
        while (isFacingBounds(lightBulbNode)) {
            lightBulbNode.rotate();
        }

        lightBulbNodes.add(lightBulbNode);

        return lightBulbNode;
    }

    private boolean isFacingBounds(LightBulbNode node) {
        int rotation = node.getRotation();
        int row = node.getRow();
        int col = node.getCol();

        // Check light bulb direction
        switch (rotation) {
            case 0: // Up
                return row == 0;
            case 90: // Right
                return col == gridSize - 1;
            case 180: // Down
                return row == gridSize - 1;
            case 270: // Left
                return col == 0;
            default:
                return false;
        }
    }

    public void initializeGame(Pane gamePane) {
        // Calculate optimal cell size
        double windowSize = Math.min(gamePane.getPrefWidth(), gamePane.getPrefHeight());
        cellSize = windowSize / gridSize;

        // Create canvas with required dimensions
        double canvasSize = gridSize * cellSize;
        gameCanvas = new Canvas(canvasSize, canvasSize);
        gc = gameCanvas.getGraphicsContext2D();

        // Add canvas to game panel
        gamePane.getChildren().add(gameCanvas);

        // Create game board
        generateGameBoard();

        // Draw initial state
        drawGrid();

        // Set up click handlers
        setupClickHandlers(gamePane);
    }

    private void drawGrid() {
        // Clear the canvas
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw each node
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != null) {
                    // The node's draw method should account for its state
                    grid[row][col].draw(gc);
                }
            }
        }
    }

    public void redrawGrid() {
        drawGrid();
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

                    if (gameController != null) {
                        gameController.updateSolutionIfShowing();
                    }
                }
            }
        });
    }

    public List<WireNode> checkDisconnectedWires() {
        List<WireNode> disconnectedWires = new ArrayList<>();

        // Reset wire states
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] instanceof WireNode) {
                    ((WireNode) grid[row][col]).setDisconnectedEnd(false);
                }
            }
        }

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] instanceof WireNode) {
                    WireNode wire = (WireNode) grid[row][col];
                    Set<WireNode.Direction> validConnections = new HashSet<>();
                    Set<WireNode.Direction> invalidConnections = new HashSet<>();

                    // Check each connection direction
                    for (WireNode.Direction dir : wire.getConnectedDirections()) {
                        int nextRow = row;
                        int nextCol = col;

                        switch (dir) {
                            case UP: nextRow--; break;
                            case RIGHT: nextCol++; break;
                            case DOWN: nextRow++; break;
                            case LEFT: nextCol--; break;
                        }

                        boolean validConnection = false;

                        if (nextRow >= 0 && nextRow < gridSize && nextCol >= 0 && nextCol < gridSize) {
                            GameNode nextNode = grid[nextRow][nextCol];

                            if (nextNode != null) {
                                if (nextNode instanceof WireNode) {
                                    WireNode nextWire = (WireNode) nextNode;
                                    if (nextWire.isDirectionConnected(dir.getOpposite())) {
                                        validConnection = true;
                                    }
                                } else if (nextNode instanceof PowerNode) {
                                    validConnection = true;
                                } else if (nextNode instanceof LightBulbNode) {
                                    if (dir.getOpposite() == WireNode.Direction.fromDegrees(nextNode.getRotation())) {
                                        validConnection = true;
                                    }
                                }
                            }
                        }

                        if (validConnection) {
                            validConnections.add(dir);
                        } else {
                            invalidConnections.add(dir);
                        }
                    }

                    // If wire has at least 2 valid connections, only remove the invalid ones
                    if (validConnections.size() >= 2) {
                        // Remove invalid connections
                        for (WireNode.Direction dir : invalidConnections) {
                            System.out.println("Removing invalid connection " + dir +
                                    " from wire at [" + row + "," + col + "]");
                            wire.removeConnection(dir);
                        }
                    }
                    // If fewer than 2 valid connections but at least one invalid
                    else if (!invalidConnections.isEmpty()) {
                        wire.setDisconnectedEnd(true);
                        disconnectedWires.add(wire);
                        System.out.println("Found disconnected wire at [" + row + "," + col + "] - " +
                                validConnections.size() + " valid, " + invalidConnections.size() + " invalid");
                    }
                }
            }
        }

        System.out.println("Found disconnected wires: " + disconnectedWires.size());
        return disconnectedWires;
    }

    private void replaceDisconnectedWires(List<WireNode> wiresToReplace) {
        for (WireNode wire : wiresToReplace) {
            int row = wire.getRow();
            int col = wire.getCol();

            // Create a new light bulb
            LightBulbNode lightBulb = new LightBulbNode(row, col);
            lightBulb.setGameManager(this);
            grid[row][col] = lightBulb;

            // Orient the light bulb and connect to power source
            orientLightBulb(lightBulb, row, col);
            lightBulbNodes.add(lightBulb);
        }

        // Redraw the grid
        drawGrid();
    }

    private void orientLightBulb(LightBulbNode lightBulb, int row, int col) {
        // Check adjacent cells to find a connected wire or power source
        for (WireNode.Direction dir : WireNode.Direction.values()) {
            int nextRow = row;
            int nextCol = col;

            switch (dir) {
                case UP: nextRow--; break;
                case RIGHT: nextCol++; break;
                case DOWN: nextRow++; break;
                case LEFT: nextCol--; break;
            }

            // Check boundaries
            if (nextRow < 0 || nextRow >= gridSize || nextCol < 0 || nextCol >= gridSize) {
                continue;
            }

            GameNode nextNode = grid[nextRow][nextCol];
            if (nextNode != null) {
                // If we found a wire that's connected in our direction, or a power source
                if ((nextNode instanceof WireNode &&
                        ((WireNode)nextNode).isDirectionConnected(dir.getOpposite())) ||
                        nextNode instanceof PowerNode) {

                    // Rotate the light bulb to "face" the opposite direction
                    while (WireNode.Direction.fromDegrees(lightBulb.getRotation()) != dir) {
                        lightBulb.rotate();
                    }
                    return;
                }
            }
        }

        // If no suitable direction was found, set a random orientation
        int randomRotations = random.nextInt(4);
        for (int i = 0; i < randomRotations; i++) {
            lightBulb.rotate();
        }

        // Check if the light bulb is facing beyond the grid boundaries
        while (isFacingBounds(lightBulb)) {
            lightBulb.rotate();
        }

        // Connect the light bulb to a power source
        connectToPower(lightBulb);
    }

    public boolean updatePowerFlow() {
        // Reset power for all nodes except PowerNode
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] != null && !(grid[row][col] instanceof PowerNode)) {
                    grid[row][col].setPowered(false);
                }
            }
        }

        // Get power source coordinates
        int powerRow = _powerNode.getRow();
        int powerCol = _powerNode.getCol();

        // Get active directions from the power source
        Set<WireNode.Direction> activeDirections = _powerNode.getActiveDirections();

        // Propagate power only in active directions
        if (activeDirections.contains(WireNode.Direction.UP)) {
            propagatePower(powerRow - 1, powerCol, 180); // up
        }

        if (activeDirections.contains(WireNode.Direction.RIGHT)) {
            propagatePower(powerRow, powerCol + 1, 270); // right
        }

        if (activeDirections.contains(WireNode.Direction.DOWN)) {
            propagatePower(powerRow + 1, powerCol, 0); // down
        }

        if (activeDirections.contains(WireNode.Direction.LEFT)) {
            propagatePower(powerRow, powerCol - 1, 90); // left
        }

        // Count connected wires
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

        // Redraw the grid
        drawGrid();

        // Return true only if all wires are connected
        return poweredWires == totalWires;
    }

    private void propagatePower(int row, int col, int fromDirection) {
        // Check grid boundaries
        if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) {
            return;
        }

        GameNode node = grid[row][col];
        if (node == null) {
            return;
        }

        // If node is already powered, no need to propagate further
        if (node.isPowered()) {
            return;
        }

        // For light bulb - check if power is coming from the correct side
        if (node instanceof LightBulbNode) {
            WireNode.Direction bulbDirection = WireNode.Direction.fromDegrees(node.getRotation());
            WireNode.Direction powerDirection = WireNode.Direction.fromDegrees(fromDirection);

            // Light bulb receives power only if it's facing the direction
            // from which power is coming (i.e., toward the wire)
            if (bulbDirection != powerDirection) {
                System.out.println("Light bulb at [" + row + "," + col + "] not powered. " +
                        "Its direction: " + bulbDirection + ", power direction: " + powerDirection);
                return; // Light bulb is facing the wrong way
            }
            System.out.println("Light bulb at [" + row + "," + col + "] powered! " +
                    "Its direction: " + bulbDirection + ", power direction: " + powerDirection);
        }

        // For wire - check if it accepts power from the current direction
        if (node instanceof WireNode) {
            WireNode wireNode = (WireNode) node;
            WireNode.Direction fromDir = WireNode.Direction.fromDegrees(fromDirection);

            if (!wireNode.isDirectionConnected(fromDir)) {
                return; // Wire doesn't accept power from this direction
            }
        }

        // Apply power to the node
        node.setPowered(true);

        // Propagate power further only for wires
        if (node instanceof WireNode) {
            WireNode wireNode = (WireNode) node;

            // Propagate energy across all connected directions
            for (WireNode.Direction dir : wireNode.getConnectedDirections()) {
                // Don't propagate energy back in the direction we came from
                if (dir.getDegrees() == fromDirection) {
                    continue;
                }

                // Calculate new coordinates based on direction
                int newRow = row;
                int newCol = col;

                switch (dir) {
                    case UP:
                        newRow--;
                        break;
                    case RIGHT:
                        newCol++;
                        break;
                    case DOWN:
                        newRow++;
                        break;
                    case LEFT:
                        newCol--;
                        break;
                }

                // Propagate energy in the new direction
                propagatePower(newRow, newCol, dir.getOpposite().getDegrees());
            }
        }
    }

    public boolean isGameWon() {
        // Check that all light bulbs are connected
        for (LightBulbNode bulb : lightBulbNodes) {
            if (!bulb.isPowered()) {
                return false;
            }
        }

        // Check that all wires are connected
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
    public void logNodeRotation(GameNode node, int prevRotation) {
        if (loggingEnabled && gameLogger != null) {
            gameLogger.logMove(node, prevRotation);
        }
    }

}
