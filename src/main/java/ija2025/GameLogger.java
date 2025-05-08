package ija2025;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameLogger {
    private final ObjectMapper mapper;
    private final ObjectNode gameLog;
    private final ArrayNode movesLog;
    private final File logFile;
    private int moveCounter;
    private boolean isLoggingEnabled = true;

    public GameLogger() {
        mapper = new ObjectMapper();
        gameLog = mapper.createObjectNode();
        movesLog = mapper.createArrayNode();
        moveCounter = 0;

        // Create logs directory if it doesn't exist
        File logDir = new File("src/logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // Create a unique file name with timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormat.format(new Date());
        logFile = new File(logDir, "game_log_" + timestamp + ".json");
    }
    public File getLogFile() {
        return logFile;
    }
    public void setLoggingEnabled(boolean enabled) {
        this.isLoggingEnabled = enabled;
    }
    // Method for logging the initial game state
    public void logInitialState(GameManager gameManager) {
        ObjectNode initialState = mapper.createObjectNode();
        initialState.put("timestamp", new Date().toString());
        initialState.put("difficulty", gameManager.getDifficulty().toString());
        initialState.put("gridSize", gameManager.getGridSize());

        // Array for all cell states
        ArrayNode gridState = mapper.createArrayNode();

        GameNode[][] grid = gameManager.getGrid();
        for (int row = 0; row < gameManager.getGridSize(); row++) {
            for (int col = 0; col < gameManager.getGridSize(); col++) {
                GameNode node = grid[row][col];
                if (node != null) {
                    ObjectNode nodeState = mapper.createObjectNode();
                    nodeState.put("row", row);
                    nodeState.put("col", col);
                    nodeState.put("type", getNodeType(node));
                    nodeState.put("rotation", node.getRotation());
                    nodeState.put("powered", node.isPowered());

                    // Add specific information for different node types
                    if (node instanceof WireNode wireNode) {
                        ArrayNode connections = mapper.createArrayNode();
                        for (WireNode.Direction dir : wireNode.getConnectedDirections()) {
                            connections.add(dir.toString());
                        }
                        nodeState.set("connections", connections);
                    }

                    gridState.add(nodeState);
                }
            }
        }

        initialState.set("grid", gridState);
        gameLog.set("initialState", initialState);
        gameLog.set("moves", movesLog);

        // Save the initial state to file
        saveLogToFile();
    }

    // Method for logging player's move
    public void logMove(GameNode node, int prevRotation) {
        if (!isLoggingEnabled) return;
        moveCounter++;
        ObjectNode move = mapper.createObjectNode();

        move.put("moveNumber", moveCounter);
        move.put("timestamp", new Date().toString());
        move.put("row", node.getRow());
        move.put("col", node.getCol());
        move.put("type", getNodeType(node));
        move.put("prevRotation", prevRotation);
        move.put("newRotation", node.getRotation());
        move.put("powered", node.isPowered());

        // Add information about wire connections
        if (node instanceof WireNode wireNode) {
            ArrayNode connections = mapper.createArrayNode();
            for (WireNode.Direction dir : wireNode.getConnectedDirections()) {
                connections.add(dir.toString());
            }
            move.set("connections", connections);
        }

        movesLog.add(move);
        saveLogToFile();
    }

    // Save log to file
    private void saveLogToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(logFile, gameLog);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении лога игры: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Determine node type
    private String getNodeType(GameNode node) {
        if (node instanceof PowerNode) {
            return "PowerNode";
        } else if (node instanceof LightBulbNode) {
            return "LightBulbNode";
        } else if (node instanceof WireNode) {
            return "WireNode";
        }
        return "UnknownNode";
    }

    public void deleteLogFile() {
        if (logFile != null && logFile.exists()) {
            try {
                if (logFile.delete()) {
                    System.out.println("Лог игры удален: " + logFile.getPath());
                } else {
                    System.err.println("Не удалось удалить лог игры: " + logFile.getPath());
                }
            } catch (Exception e) {
                System.err.println("Ошибка при удалении лога игры: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}